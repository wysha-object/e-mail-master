package cn.com.wysha.e_mail_master.model.dto;

import cn.com.wysha.e_mail_master.model.constant.AuthType;
import cn.com.wysha.e_mail_master.model.constant.ErrorType;
import cn.com.wysha.e_mail_master.model.entity.BasicAuthMailboxEntity;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.valid.DomainValid;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import jakarta.mail.URLName;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPSSLStore;

import java.util.*;

@Slf4j
@Data @NoArgsConstructor @AllArgsConstructor
public class BasicAuthMailbox implements Mailbox {
    @DomainValid
    private String host;

    @NotBlank
    private String username;

    @DomainValid
    private String smtpHost;

    @DomainValid
    private String imapHost;

    @NotBlank
    private String password;

    private final Collection<String> folders = new HashSet<>();

    private final List<ErrorType> errors = new LinkedList<>();

    @Override
    public void addError(Throwable e){
        log.error(e.toString());
        errors.addLast(ErrorType.ERROR);
        if (errors.size() > 8) errors.removeFirst();
    }

    @Override
    public void clearError() {
        errors.clear();
    }

    public BasicAuthMailbox(BasicAuthMailboxEntity entity){
        this(entity.getHost(), entity.getUsername(), entity.getSmtpHost(), entity.getImapHost(), entity.getPassword());
        folders.addAll(entity.getFolders());
        errors.addAll(entity.getErrors());
    }

    @Override
    public AuthType getAuthType() {
        return AuthType.BASIC;
    }

    @Override
    public String getAddress() {
        return String.format("%s@%s",
                getUsername(), getHost()
        );
    }

    public String getImapURLWithFolder(String folder){
        return String.format("imaps://%s:%s@%s/%s",
                getUsername(), getPassword(), getImapHost(), folder
        );
    }

    public String getImapURL(){
        return String.format("imaps://%s:%s@%s",
                getUsername(), getPassword(), getImapHost()
        );
    }

    public void initializeFolders() throws MessagingException {
        Store store = new IMAPSSLStore(Session.getInstance(new Properties()), new URLName(getImapURL()));
        store.connect();
        Arrays.stream(store.getDefaultFolder().list("*")).forEach(o -> getFolders().add(o.getName()));
        store.close();
    }
}
