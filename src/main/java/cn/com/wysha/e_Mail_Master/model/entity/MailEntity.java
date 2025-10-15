package cn.com.wysha.e_mail_master.model.entity;

import cn.com.wysha.e_mail_master.model.dto.Mail;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "mail")
public class MailEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "of")
    private String of;

    @Column(name = "mail_from", columnDefinition = "text[]")
    private Collection<String> from;

    @Column(name = "mail_reply_to", columnDefinition = "text[]")
    private Collection<String> replyTO;

    @Column(name = "mail_to", columnDefinition = "text[]")
    private Collection<String> to;

    @Column(name = "mail_cc", columnDefinition = "text[]")
    private Collection<String> cc;

    @Column(name = "mail_bcc", columnDefinition = "text[]")
    private Collection<String> bcc;

    @Column(name = "sent_date")
    private Date sentDate;

    @Column(name = "subject")
    private String subject;

    public MailEntity(Mail mail) {
        this(
                mail.getId(),
                mail.getOf(),
                new HashSet<>(mail.getFrom()),
                new HashSet<>(mail.getReplyTo()),
                new HashSet<>(mail.getTo()),
                new HashSet<>(mail.getCc()),
                new HashSet<>(mail.getBcc()),
                mail.getSentDate(),
                mail.getSubject()
        );
    }
}
