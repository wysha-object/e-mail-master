package cn.com.wysha.e_mail_master.manager.impl;

import cn.com.wysha.e_mail_master.dao.BasicAuthMailboxDao;
import cn.com.wysha.e_mail_master.manager.MailboxManager;
import cn.com.wysha.e_mail_master.model.entity.BasicAuthMailboxEntity;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.locks.ReadWriteLock;

@Component
public class MailboxManagerImpl implements MailboxManager {
    private final ReadWriteLock lock;
    private final BasicAuthMailboxDao basicAuthMailboxDao;

    @Autowired
    public MailboxManagerImpl(@Qualifier("database_lock") ReadWriteLock lock, BasicAuthMailboxDao basicAuthMailboxDao) {
        this.lock = lock;
        this.basicAuthMailboxDao = basicAuthMailboxDao;
    }

    @Override
    public Collection<Mailbox> findAll() {
        lock.readLock().lock();
        try {
            return new HashSet<>(basicAuthMailboxDao.findAll().stream().map(BasicAuthMailbox::new).toList());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void save(Mailbox mailbox) {
        lock.writeLock().lock();
        try {
            switch (mailbox.getAuthType()) {
                case BASIC -> {
                    if (!(mailbox instanceof BasicAuthMailbox basicAuthMailbox)) throw new IllegalArgumentException();
                    basicAuthMailboxDao.save(new BasicAuthMailboxEntity(basicAuthMailbox));
                }
                case OAUTH -> throw new IllegalArgumentException();
                default -> throw new IllegalArgumentException();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void del(Mailbox mailbox) {
        lock.writeLock().lock();
        try {
            switch (mailbox.getAuthType()) {
                case BASIC -> {
                    if (!(mailbox instanceof BasicAuthMailbox basicAuthMailbox)) throw new IllegalArgumentException();
                    basicAuthMailboxDao.delete(new BasicAuthMailboxEntity(basicAuthMailbox));
                }
                case OAUTH -> throw new IllegalArgumentException();
                default -> throw new IllegalArgumentException();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }
}
