package cn.com.wysha.e_mail_master.service.impl;

import cn.com.wysha.e_mail_master.EMailMasterProperties;
import cn.com.wysha.e_mail_master.ex.MailServiceException;
import cn.com.wysha.e_mail_master.manager.BasicAuthMailManager;
import cn.com.wysha.e_mail_master.manager.MailManager;
import cn.com.wysha.e_mail_master.manager.MailboxManager;
import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import cn.com.wysha.e_mail_master.model.dto.Mail;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.service.MailService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class MailServiceImpl implements MailService {
    private final BasicAuthMailManager basicAuthMailManager;
    private final MailManager mailManager;
    private final MailboxManager mailboxManager;

    @Autowired
    public MailServiceImpl(BasicAuthMailManager basicAuthMailManager, MailManager mailManager, MailboxManager mailboxManager) {
        this.basicAuthMailManager = basicAuthMailManager;
        this.mailManager = mailManager;
        this.mailboxManager = mailboxManager;
    }

    private void saveError(MailServiceException exception) {
        Mailbox mailbox = exception.getMailBox();
        Throwable cause = exception.getCause();
        mailbox.addError(cause);
        mailboxManager.save(mailbox);
    }

    private void addMailboxReceiver(Mailbox mailBox) {
        switch (mailBox.getAuthType()) {
            case BASIC -> {
                if (!(mailBox instanceof BasicAuthMailbox basicAuthMailbox)) throw new IllegalArgumentException();
                basicAuthMailManager.addReceiver(basicAuthMailbox, mailManager::save, mailManager::existsById, this::saveError);
            }
            case OAUTH -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
        }
    }

    private void initialMailbox (Mailbox mailbox) {
        switch (mailbox.getAuthType()) {
            case BASIC -> {
                if (!(mailbox instanceof BasicAuthMailbox basicAuthMailbox)) throw new IllegalArgumentException();
                try {
                    basicAuthMailbox.initializeFolders();
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }
            }
            case OAUTH -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void addMailBox(Mailbox mailbox) {
        initialMailbox(mailbox);
        addMailboxReceiver(mailbox);
        mailboxManager.save(mailbox);
    }

    private void removeMailboxReceiver(Mailbox mailbox) {
        switch (mailbox.getAuthType()) {
            case BASIC -> {
                if (!(mailbox instanceof BasicAuthMailbox basicAuthMailbox)) throw new IllegalArgumentException();
                try {
                    basicAuthMailManager.removeReceiver(basicAuthMailbox);
                }catch (IllegalArgumentException _) {
                }
            }
            case OAUTH -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeMailBox(Mailbox mailbox) {
        removeMailboxReceiver(mailbox);
        for (String folder : mailbox.getFolders()) {
            mailManager.deleteAllByOf(mailbox, folder);
        }
        mailboxManager.del(mailbox);
    }

    @Override
    public void reinitialMailBox(Mailbox mailbox) {
        //移除接收者
        removeMailboxReceiver(mailbox);

        //暂存
        Collection<String> tmp = new HashSet<>(mailbox.getFolders());

        //初始化
        initialMailbox(mailbox);

        //如果文件夹被删除,则清空其中的邮件
        tmp.stream()
                .filter(folder -> !mailbox.getFolders().contains(folder))
                .forEach(folder -> mailManager.deleteAllByOf(mailbox, folder));
        mailboxManager.save(mailbox);

        //重新添加接收者
        addMailboxReceiver(mailbox);
    }

    @Override
    public void resyncMailBox(Mailbox mailbox) {
        //移除接收者
        removeMailboxReceiver(mailbox);

        //清空邮件
        for (String folder : mailbox.getFolders()) {
            mailManager.deleteAllByOf(mailbox, folder);
        }

        //初始化
        initialMailbox(mailbox);

        //重新添加接收者
        addMailboxReceiver(mailbox);
    }

    @Override
    public void clearErrors(Mailbox mailbox) {
        mailbox.clearError();
        mailboxManager.save(mailbox);
    }

    @Override
    public void sendMail(Mailbox mailbox, Mail mail) throws MailServiceException {
        switch (mailbox.getAuthType()) {
            case BASIC -> {
                if (mailbox instanceof BasicAuthMailbox basicAuthMailbox) {
                    basicAuthMailManager.send(basicAuthMailbox, mail);
                } else {
                    throw new IllegalArgumentException();
                }
            }
            case OAUTH -> throw new IllegalArgumentException();
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public Collection<Mail> getAllMailByOf(Mailbox mailbox, String folder) {
        return mailManager.findAllByOf(mailbox, folder);
    }

    @Override
    public Collection<Mail> getAllMail() {
        return mailManager.findAll();
    }

    @Override
    public Mail getMailById(String id) {
        return mailManager.findById(id);
    }

    @Override
    public Collection<Mailbox> getAllMailbox() {
        return mailboxManager.findAll();
    }

    @Override
    public long getMailCount() {
        return mailManager.mailCount();
    }

    @Configuration
    public static class MailServiceRunner implements ApplicationRunner {
        private final EMailMasterProperties eMailMasterProperties;
        private final MailServiceImpl mailServiceImpl;

        @Autowired
        public MailServiceRunner(EMailMasterProperties eMailMasterProperties, MailServiceImpl mailServiceImpl) {
            this.eMailMasterProperties = eMailMasterProperties;
            this.mailServiceImpl = mailServiceImpl;
        }

        @Override
        public void run(ApplicationArguments args) {
            if (!eMailMasterProperties.isDevtools_mode()) {
                //对所有邮箱初始化
                mailServiceImpl.getAllMailbox().forEach(mailServiceImpl::addMailboxReceiver);
            }
        }
    }
}
