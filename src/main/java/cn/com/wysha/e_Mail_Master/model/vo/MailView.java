package cn.com.wysha.e_mail_master.model.vo;

import cn.com.wysha.e_mail_master.model.constant.ContentType;
import cn.com.wysha.e_mail_master.model.dto.Mail;
import cn.com.wysha.e_mail_master.model.dto.MailPart;
import cn.com.wysha.e_mail_master.util.MailPartUtils;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailView implements Comparable<MailView> {
    private String id;
    private String folder;
    private String fromOverview;
    private List<String> from;
    private List<String> replyTo;
    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String overview;
    private String text;
    private Date sentDate;

    public MailView(Mail mail) {
        this();
        id = mail.getId();
        folder = mail.getOf();
        subject = mail.getSubject();
        overview = getOverview(mail);
        text = getText(mail);
        sentDate = mail.getSentDate();

        from = toList(mail.getFrom());
        replyTo = toList(mail.getReplyTo());
        to = toList(mail.getTo());
        cc = toList(mail.getCc());
        bcc = toList(mail.getBcc());

        if (from.isEmpty()) {
            fromOverview = "(无)";
        } else {
            fromOverview = from.getFirst();
        }

        if (!StringUtils.hasText(getSubject())) setSubject("(无主题)");
        if (!StringUtils.hasText(getOverview())) setOverview("(无内容)");
    }

    private static List<String> toList(Collection<String> addresses) {
        return addresses.stream().map(
                o -> {
                    try {
                        return new InternetAddress(o).toUnicodeString();
                    } catch (AddressException e) {
                        return o;
                    }
                }
        ).sorted().toList();
    }

    private static String getOverview (MailPart mailPart) {
        String rs = MailPartUtils.getContentByType(mailPart, ContentType.TEXT_PLAIN.toString());
        if (rs == null) rs = MailPartUtils.getContentByType(mailPart, ContentType.TEXT_HTML.toString());
        if (rs == null) rs = "";
        return rs;
    }

    private static String getText (MailPart mailPart) {
        String rs = MailPartUtils.getContentByType(mailPart, ContentType.TEXT_HTML.toString());
        if (rs == null) rs = MailPartUtils.getContentByType(mailPart, ContentType.TEXT_PLAIN.toString());
        if (rs == null) rs = "";
        return rs;
    }

    @Override
    public int compareTo(MailView o) {
        return this.sentDate.compareTo(o.sentDate);
    }
}
