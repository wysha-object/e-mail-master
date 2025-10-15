package cn.com.wysha.e_mail_master.model.dto;

import cn.com.wysha.e_mail_master.model.entity.MailEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

@Data @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode(callSuper = true)
public class Mail extends MailPart {
    private String id;
    private String of;
    private Collection<String> from;
    private Collection<String> replyTo;
    private Collection<String> to;
    private Collection<String> cc;
    private Collection<String> bcc;
    private Date sentDate;
    private String subject;

    public Mail(MailEntity entity) {
        this(
                entity.getId(),
                entity.getOf(),
                entity.getFrom(),
                new HashSet<>(entity.getReplyTO()),
                new HashSet<>(entity.getTo()),
                new HashSet<>(entity.getCc()),
                new HashSet<>(entity.getBcc()),
                entity.getSentDate(),
                entity.getSubject()
        );
    }
}
