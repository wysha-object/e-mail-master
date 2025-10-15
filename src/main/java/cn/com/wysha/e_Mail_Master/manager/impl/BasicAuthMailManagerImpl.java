package cn.com.wysha.e_mail_master.manager.impl;

import cn.com.wysha.e_mail_master.EMailMasterProperties;
import cn.com.wysha.e_mail_master.ex.MailServiceException;
import cn.com.wysha.e_mail_master.manager.BasicAuthMailManager;
import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import cn.com.wysha.e_mail_master.model.dto.Mail;
import cn.com.wysha.e_mail_master.util.MailUtils;
import jakarta.annotation.PreDestroy;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.context.IntegrationFlowContext;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.transformer.AbstractMailMessageTransformer;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

@Component
public class BasicAuthMailManagerImpl implements BasicAuthMailManager {
    private final Map<String, Registration> mailboxMap = new HashMap<>();

    private final EMailMasterProperties eMailMasterProperties;
    private final IntegrationFlowContext integrationFlowContext;

    @Autowired
    public BasicAuthMailManagerImpl(EMailMasterProperties eMailMasterProperties, IntegrationFlowContext integrationFlowContext) {
        this.eMailMasterProperties = eMailMasterProperties;
        this.integrationFlowContext = integrationFlowContext;
    }

    @Override
    public void send(BasicAuthMailbox mailbox, Mail mail) throws MailServiceException {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailbox.getSmtpHost());
        mailSender.setPort(587);
        mailSender.setUsername(mailbox.getUsername());
        mailSender.setPassword(mailbox.getPassword());

        MimeMessage message;
        try {
            message = MailUtils.toMimeMessage(mail);
        } catch (Exception e) {
            throw new MailServiceException(e, mailbox);
        }

        assert message != null;//给IDEA看的
        mailSender.send(message);
    }

    @Override
    public void addReceiver(BasicAuthMailbox mailbox, Consumer<Mail> consumer, Function<String, Boolean> isExists, Consumer<MailServiceException> onError) {
        Assert.notNull(mailbox, "mailbox must not be null");
        Assert.notNull(consumer, "consumer must not be null");
        Assert.notNull(isExists, "isExists must not be null");

        String key = mailbox.getAddress();
        Assert.isTrue(!mailboxMap.containsKey(key), "mailbox already exists");

        Registration registration = new Registration(
                new HashMap<>(),
                Executors.newVirtualThreadPerTaskExecutor()
        );
        mailboxMap.put(key, registration);

        mailbox.getFolders().forEach(folder -> addReceiverForFolder(mailbox, consumer, isExists, onError, folder, registration));
    }

    private void addReceiverForFolder(
            BasicAuthMailbox mailbox,
            Consumer<Mail> consumer,
            Function<String, Boolean> isExists,
            Consumer<MailServiceException> onError,
            String folder,
            Registration registration
    ) {
        String imapURL = mailbox.getImapURLWithFolder(folder);

        MailReceiver mailReceiver = new MailReceiver(imapURL);
        mailReceiver.setAutoCloseFolder(false);
        mailReceiver.setFlaggedAsFallback(false);

        ExecutorService executor = registration.getExecutorService();
        executor.submit(() -> {
            try {
                if (Thread.currentThread().isInterrupted()) return;
                Message[] messages = mailReceiver.receiveAllMessages();
                if (Thread.currentThread().isInterrupted()) return;
                Arrays.stream(messages).forEach(
                        message -> {
                            if (Thread.currentThread().isInterrupted()) return;
                            executor.submit(() -> {
                                if (Thread.currentThread().isInterrupted()) return;

                                try {
                                    if (!(message instanceof MimeMessage mimeMessage)) throw new IllegalArgumentException();

                                    try {
                                        if (isExists.apply(MailUtils.toMailId(mimeMessage.getMessageID(), mailbox.getFolderId(folder)))) return;

                                        if (Thread.currentThread().isInterrupted()) return;

                                        consumer.accept(MailUtils.toMail(mimeMessage, mailbox.getFolderId(folder), eMailMasterProperties.getCache_dir()));
                                    } catch (MessagingException | IOException e) {
                                        if (Thread.currentThread().isInterrupted()) return;
                                        throw new MailServiceException(e, mailbox);
                                    }

                                } catch (MailServiceException e) {
                                    onError.accept(e);
                                }
                            });
                        }
                );
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        });

        IntegrationFlow flow = IntegrationFlow
                .from(org.springframework.integration.mail.dsl.Mail.imapInboundAdapter(imapURL).autoCloseFolder(false).flaggedAsFallback(false))
                .transform(new AbstractMailMessageTransformer<Mail>() {
                    @Override
                    protected AbstractIntegrationMessageBuilder<Mail> doTransform(Message mailMessage) {
                        try {
                            if (!(mailMessage instanceof MimeMessage mimeMessage)) throw new IllegalArgumentException();

                            if (isExists.apply(MailUtils.toMailId(mimeMessage.getMessageID(), mailbox.getFolderId(folder)))) return null;
                            return MessageBuilder.withPayload(MailUtils.toMail(mimeMessage, mailbox.getFolderId(folder), eMailMasterProperties.getCache_dir()));

                        } catch (MessagingException | IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .handle(consumer)
                .get();
        IntegrationFlowContext.IntegrationFlowRegistration flowRegistration = integrationFlowContext.registration(flow).register();
        registration.getFolderFlowIdMap().put(folder, flowRegistration.getId());
    }

    private void destroyExecutor (ExecutorService executor) {
        if (executor != null) {
            executor.shutdownNow();
            boolean isTermination;
            do {
                try {
                    isTermination = executor.awaitTermination(60, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    return;
                }
            } while (!isTermination);
        }
    }

    @Override
    public void removeReceiver(BasicAuthMailbox mailbox) {
        Assert.notNull(mailbox, "mailbox must not be null");

        String key = mailbox.getAddress();
        Assert.isTrue(mailboxMap.containsKey(key), "no such mailbox");

        Registration registration = mailboxMap.get(key);
        registration.getFolderFlowIdMap().forEach((_, flowId) -> integrationFlowContext.remove(flowId));
        destroyExecutor(registration.getExecutorService());
        mailboxMap.remove(key);
    }

    @PreDestroy
    public void destroy() {
        mailboxMap.forEach((_, registration) -> destroyExecutor(registration.getExecutorService()));
    }

    private static class MailReceiver extends ImapMailReceiver {
        public MailReceiver(String url) {
            super(url);
        }

        public Message[] receiveAllMessages() throws MessagingException {
            openFolder();
            Folder folderToUse = getFolder();
            if (folderToUse.isOpen()) {
                return folderToUse.getMessages();
            }
            throw new MessagingException("Folder is closed");
        }
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    private static class Registration {
        Map<String, String> folderFlowIdMap;
        ExecutorService executorService;
    }
}

