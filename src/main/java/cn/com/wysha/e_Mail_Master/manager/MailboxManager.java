package cn.com.wysha.e_mail_master.manager;

import cn.com.wysha.e_mail_master.model.intf.Mailbox;

import java.util.Collection;

public interface MailboxManager {
    Collection<Mailbox> findAll();
    void save(Mailbox mailbox);
    void del(Mailbox mailbox);
}
