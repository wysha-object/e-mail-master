package cn.com.wysha.e_mail_master.manager.impl;

import cn.com.wysha.e_mail_master.EMailMasterProperties;
import cn.com.wysha.e_mail_master.dao.HeadersDao;
import cn.com.wysha.e_mail_master.dao.MailDao;
import cn.com.wysha.e_mail_master.dao.MailPartDao;
import cn.com.wysha.e_mail_master.manager.MailManager;
import cn.com.wysha.e_mail_master.model.dto.Mail;
import cn.com.wysha.e_mail_master.model.dto.MailFilePart;
import cn.com.wysha.e_mail_master.model.dto.MailMultipart;
import cn.com.wysha.e_mail_master.model.dto.MailPart;
import cn.com.wysha.e_mail_master.model.entity.HeadersEntity;
import cn.com.wysha.e_mail_master.model.entity.MailEntity;
import cn.com.wysha.e_mail_master.model.entity.MailPartEntity;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.util.Base64Util;
import cn.com.wysha.e_mail_master.util.MailUtils;
import cn.com.wysha.e_mail_master.util.MimeContentTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Function;

@Component
@Slf4j
public class MailManagerImpl implements MailManager {
    private final EMailMasterProperties eMailMasterProperties;
    private final ReadWriteLock lock;
    private final MailDao mailDao;
    private final MailPartDao mailPartDao;
    private final HeadersDao headersDao;

    @Autowired
    public MailManagerImpl(EMailMasterProperties eMailMasterProperties, ReadWriteLock lock, MailDao mailDao, MailPartDao mailPartDao, HeadersDao headersDao) {
        this.eMailMasterProperties = eMailMasterProperties;
        this.lock = lock;
        this.mailDao = mailDao;
        this.mailPartDao = mailPartDao;
        this.headersDao = headersDao;
    }

    private List<MailPart> toPart(List<MailPartEntity> mailPartEntityList, List<HeadersEntity> headersEntityList, String superId) {
        return mailPartEntityList.stream().filter(o -> superId.equals(o.getSuperId())).map(o -> {
            MailPart mailPart = new MailPart();

            mailPart.setContentType(o.getContentType());

            Map<String, String> headers = new HashMap<>();
            headersEntityList.stream().filter(header -> o.getId().equals(header.getMailPartId())).forEach(headersEntity -> headers.put(headersEntity.getKey(), headersEntity.getValue()));
            mailPart.setHeaders(headers);

            if (MimeContentTypeUtils.isMultipart(o.getContentType())) {
                MailMultipart mailMultipart = new MailMultipart(toPart(mailPartEntityList, headersEntityList, o.getId()));
                mailPart.setContent(mailMultipart);
            } else if (MimeContentTypeUtils.isText(o.getContentType())) {
                mailPart.setContent(o.getContent());
            } else {
                File cache;
                try {
                    cache = MailUtils.writeToFile(o.getContentType(), eMailMasterProperties.getCache_dir(), Base64.getDecoder().decode(o.getContent()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                MailFilePart mailFilePart = new MailFilePart(cache.getAbsolutePath());
                mailPart.setContent(mailFilePart);
            }

            return mailPart;
        }).toList();
    }

    private Mail toMail(List<MailPartEntity> mailPartEntityList, List<HeadersEntity> headersEntityList, MailEntity entity) {
        Mail mail = new Mail(entity);
        List<MailPart> mailPartList = toPart(mailPartEntityList, headersEntityList, entity.getId());
        if (mailPartList.size() != 1) {
            log.error("id={},of={},mailPartList.size()={}", entity.getId(), entity.getOf(), mailPartList.size());
            throw new IllegalStateException();
        }
        MailPart part = mailPartList.getFirst();

        mail.setHeaders(part.getHeaders());
        mail.setContentType(part.getContentType());
        mail.setContent(part.getContent());

        return mail;
    }

    private Collection<Mail> findAll(Function<MailDao, Collection<MailEntity>> mailDaoMethod) {
        lock.readLock().lock();
        try {
            List<MailPartEntity> mailPartEntityLis = mailPartDao.findAll();
            List<HeadersEntity> headersEntityList = headersDao.findAll();
            return mailDaoMethod.apply(mailDao).stream().map(o -> toMail(mailPartEntityLis, headersEntityList, o)).toList();
        } finally {
            lock.readLock().unlock();
        }
    }

    private Mail find(Function<MailDao, MailEntity> mailDaoMethod) {
        lock.readLock().lock();
        try {
            List<MailPartEntity> mailPartEntityLis = mailPartDao.findAll();
            List<HeadersEntity> headersEntityList = headersDao.findAll();
            return toMail(mailPartEntityLis, headersEntityList, mailDaoMethod.apply(mailDao));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Collection<Mail> findAll() {
        return findAll(MailDao::findAll);
    }

    @Override
    public Collection<Mail> findAllByOf(Mailbox mailbox, String folder) {
        return findAll(o -> o.findAllByOf(mailbox.getFolderId(folder)));
    }

    @Override
    public Mail findById(String id) {
        return find(o -> o.findById(id).orElseThrow());
    }

    private EntitiesTmp generateEntities(MailPart mailPart, String superId) {
        Collection<MailPartEntity> part_rs = new HashSet<>();
        Collection<HeadersEntity> headers_rs = new HashSet<>();

        Map<String, String> headers = mailPart.getHeaders();
        String contentType = mailPart.getContentType();
        Object content = mailPart.getContent();
        String id = UUID.randomUUID().toString();

        if (MimeContentTypeUtils.isMultipart(contentType)) {
            if (!(content instanceof MailMultipart mailMultipart)) throw new IllegalArgumentException();

            for (MailPart o : mailMultipart.getParts()) {
                EntitiesTmp tmp = generateEntities(o, id);
                part_rs.addAll(tmp.mailPartEntities);
                headers_rs.addAll(tmp.headersEntities);
            }
            part_rs.add(new MailPartEntity(id, superId, contentType, null));

        } else if (MimeContentTypeUtils.isText(contentType)) {

            part_rs.add(new MailPartEntity(id, superId, contentType, mailPart.getStringContent()));

        } else {
            if (!(content instanceof MailFilePart mailFilePart)) {
                log.error("mail part content is not MailFilePart, contentType={}, content.getClass()={}", contentType, content.getClass());
                throw new IllegalArgumentException();
            }

            part_rs.add(new MailPartEntity(id, superId, contentType, Base64Util.fileToBase64(mailFilePart.getFilePath())));
        }

        headers.forEach((name, value) -> headers_rs.add(new HeadersEntity(UUID.randomUUID().toString(), id, name, value)));

        return new EntitiesTmp(part_rs, headers_rs);
    }

    @Override
    public void save(Mail mail) {
        //判断是否存在
        lock.readLock().lock();
        try {
            if (mailDao.existsById(mail.getId())) {//已经存在
                log.error("mail already exists: id={} of={}", mail.getId(), mail.getOf());
                throw new IllegalArgumentException("mail already exists");
            }
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        } finally {
            lock.readLock().unlock();
        }

        //保存
        String id = mail.getId();
        EntitiesTmp tmp = generateEntities(mail, id);
        lock.writeLock().lock();
        try {
            mailDao.save(new MailEntity(mail));
            mailPartDao.saveAll(tmp.mailPartEntities);
            headersDao.saveAll(tmp.headersEntities);
        } catch (Exception e) {
            log.error(e.toString());
            throw e;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private EntitiesTmp getAllEntities(List<MailPartEntity> mailPartEntityList, List<HeadersEntity> headersEntityList, String superId) {
        Collection<MailPartEntity> part_rs = new HashSet<>();
        Collection<HeadersEntity> headers_rs = new HashSet<>();

        mailPartEntityList.stream()
                .filter(o -> superId.equals(o.getSuperId()))
                .forEach(o -> {
                    if (MimeContentTypeUtils.isMultipart(o.getContentType())) {
                        EntitiesTmp tmp = getAllEntities(mailPartEntityList, headersEntityList, o.getId());
                        part_rs.addAll(tmp.mailPartEntities);
                        headers_rs.addAll(tmp.headersEntities);
                    }

                    part_rs.add(o);
                    headersEntityList.stream()
                            .filter(h -> o.getId().equals(h.getMailPartId()))
                            .forEach(headers_rs::add);
                });

        return new EntitiesTmp(part_rs, headers_rs);
    }

    @Override
    public void deleteAllByOf(Mailbox mailbox, String folder) {
        List<MailEntity> mailEntityList;
        List<MailPartEntity> mailPartEntityLis;
        List<HeadersEntity> headersEntityList;
        lock.readLock().lock();
        try {
            mailEntityList = mailDao.findAllByOf(mailbox.getFolderId(folder));
            mailPartEntityLis = mailPartDao.findAll();
            headersEntityList = headersDao.findAll();
        } finally {
            lock.readLock().unlock();
        }

        Collection<MailPartEntity> mailPartEntityTmp = new HashSet<>();
        Collection<HeadersEntity> headersEntityTmp = new HashSet<>();
        mailEntityList.forEach(o -> {
            EntitiesTmp tmp = getAllEntities(mailPartEntityLis, headersEntityList, o.getId());
            mailPartEntityTmp.addAll(tmp.mailPartEntities());
            headersEntityTmp.addAll(tmp.headersEntities());
        });
        lock.writeLock().lock();
        try {
            mailDao.deleteAll(mailEntityList);
            mailPartDao.deleteAll(mailPartEntityTmp);
            headersDao.deleteAll(headersEntityTmp);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean existsById(String id) {
        lock.readLock().lock();
        try {
            return mailDao.existsById(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public long mailCount() {
        lock.readLock().lock();
        try {
            return mailDao.count();
        } finally {
            lock.readLock().unlock();
        }
    }

    private record EntitiesTmp(Collection<MailPartEntity> mailPartEntities, Collection<HeadersEntity> headersEntities) {
    }
}
