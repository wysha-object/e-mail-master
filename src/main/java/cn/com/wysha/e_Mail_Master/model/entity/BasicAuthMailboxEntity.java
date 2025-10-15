package cn.com.wysha.e_mail_master.model.entity;

import cn.com.wysha.e_mail_master.model.constant.ErrorType;
import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@IdClass(BasicAuthMailboxEntity.AddressKey.class)
@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "mailbox")
public class BasicAuthMailboxEntity {
    @Id
    @Column(name = "host")
    private String host;
    @Id
    @Column(name = "username")
    private String username;
    @Column(name = "smtp_host")
    private String smtpHost;
    @Column(name = "imap_host")
    private String imapHost;
    @Column(name = "password")
    private String password;
    @Column(name = "folders", columnDefinition = "text[]")
    private Collection<String> folders = new ArrayList<>();
    @Column(name = "errors", columnDefinition = "text[]")
    private List<ErrorType> errors;

    public BasicAuthMailboxEntity(BasicAuthMailbox mailBox){
        this(
                mailBox.getHost(),
                mailBox.getUsername(),
                mailBox.getSmtpHost(),
                mailBox.getImapHost(),
                mailBox.getPassword(),
                new HashSet<>(mailBox.getFolders()),
                new ArrayList<>(mailBox.getErrors())
        );
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressKey{
        private String host;
        private String username;
    }
}