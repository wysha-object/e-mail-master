package cn.com.wysha.e_mail_master.util;

import cn.com.wysha.e_mail_master.model.dto.Mail;
import cn.com.wysha.e_mail_master.model.dto.MailFilePart;
import cn.com.wysha.e_mail_master.model.dto.MailMultipart;
import cn.com.wysha.e_mail_master.model.dto.MailPart;
import jakarta.mail.*;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;

@Slf4j
public class MailUtils {
    public static String toMailId(String messageId, String of) {
        return Base64.getEncoder().encodeToString(String.format("{\"message-id\":\"%s\",\"of\":\"%s\"}", messageId, of).getBytes());
    }

    public static Mail toMail(MimeMessage message, String of, String cacheDir) throws MessagingException, IOException {
        Mail mail = new Mail(
                toMailId(message.getMessageID(), of),
                of,
                toStringCollection(message.getFrom()),
                toStringCollection(message.getReplyTo()),
                toStringCollection(message.getRecipients(Message.RecipientType.TO)),
                toStringCollection(message.getRecipients(Message.RecipientType.CC)),
                toStringCollection(message.getRecipients(Message.RecipientType.BCC)),
                message.getSentDate(),
                message.getSubject()
        );

        String contentType = message.getContentType();
        Object content = message.getContent();

        mail.setHeaders(new HashMap<>());
        mail.setContentType(contentType);
        mail.setContent(toContent(contentType, content, cacheDir));

        return mail;
    }

    /**
     * 根据{@code contentType}将{@code content}转换成对应的对象
     *
     * @param contentType MIME Content-Type
     * @param content     MIME Content
     * @return 对应的对象
     * @throws MessagingException 可能的错误
     * @throws IOException        可能的错误
     */
    public static Object toContent(String contentType, Object content, String cacheDir) throws MessagingException, IOException {
        Object rs;

        if (MimeContentTypeUtils.isMultipart(contentType)) {
            if (!(content instanceof Multipart multipart)) throw new IllegalArgumentException();

            MailMultipart mailMultipart = new MailMultipart();

            HashSet<MailPart> parts = new HashSet<>();
            for (int i = 0; i < multipart.getCount(); i++) {
                Part part = multipart.getBodyPart(i);

                HashMap<String, String> headers = new HashMap<>();
                part.getAllHeaders().asIterator().forEachRemaining(header -> headers.put(header.getName(), header.getValue()));
                String partContentType = part.getContentType();
                Object partContent = part.getContent();

                MailPart mailPart = new MailPart();

                mailPart.setHeaders(headers);
                mailPart.setContentType(partContentType);
                mailPart.setContent(toContent(partContentType, partContent, cacheDir));

                parts.add(mailPart);
            }
            mailMultipart.setParts(parts);

            rs = mailMultipart;
        } else {
            if (content instanceof InputStream inputStream) {

                File file = writeToFile(contentType, cacheDir, inputStream);
                MailFilePart mailFilePart = new MailFilePart();
                mailFilePart.setFilePath(file.getAbsolutePath());
                rs = mailFilePart;

            }else if (!(content instanceof String)) {
                throw new IllegalStateException();
            }else {
                rs = content;
            }
        }

        return rs;
    }

    public static MimeMessage toMimeMessage(Mail mail) {
        throw new UnsupportedOperationException();
        /*
        code
         */
    }

    private static Collection<String> toStringCollection(Address[] addresses) {
        if (addresses == null) return new HashSet<>();
        return Arrays.stream(addresses).map(Address::toString).toList();
    }

    private static File randomFile(String contentType, String cacheDir) {
        File dir = new File (cacheDir);
        if (!dir.isDirectory()) throw new IllegalArgumentException();

        File file;
        do {
            file = new File(String.format(dir.getAbsolutePath() + "\\%d_%s",
                    new Random().nextInt(Integer.MAX_VALUE),
                    MimeContentTypeUtils.getName(contentType).replace(" ", "_")
            ));
        } while (file.exists());
        return file;
    }

    /**
     * 把流写入文件
     * @param contentType 用以生成文件名
     * @param inputStream 源
     * @return 生成的文件
     */
    public static File writeToFile (String contentType, String cacheDir, InputStream inputStream) throws IOException {
        File file = randomFile(contentType, cacheDir);

        try (
                InputStream in = new BufferedInputStream(inputStream);
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        ) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
        }

        return file;
    }

    /**
     * 把流写入文件
     * @param contentType 用以生成文件名
     * @param value 源
     * @return 生成的文件
     */
    public static File writeToFile (String contentType, String cacheDir, byte[] value) throws IOException {
        File file = randomFile(contentType, cacheDir);

        try (
                OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        ) {
            for (byte b : value) {
                out.write(b);
            }
        }

        return file;
    }
}