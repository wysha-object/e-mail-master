package cn.com.wysha.e_mail_master.manager;

import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.model.dto.Mail;

import java.util.Collection;

public interface MailManager {
    Collection<Mail> findAll();
    Collection<Mail> findAllByOf(Mailbox mailbox, String folder);
    Mail findById(String id);
    void save(Mail mail);
    void deleteAllByOf(Mailbox mailbox, String folder);
    boolean existsById(String id);
    long mailCount();
}
