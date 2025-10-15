package cn.com.wysha.e_mail_master.service;

import cn.com.wysha.e_mail_master.ex.MailServiceException;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import cn.com.wysha.e_mail_master.model.dto.Mail;

import java.util.Collection;

public interface MailService {
    /**
     * 添加邮箱,开始自动收取其邮件
     * @param mailbox 邮箱
     * @throws MailServiceException 第一次连接失败
     */
    void addMailBox(Mailbox mailbox) throws MailServiceException;

    /**
     * 移除邮箱
     * @param mailbox 邮箱
     */
    void removeMailBox(Mailbox mailbox);

    /**
     * 重新初始化邮箱
     */
    void reinitialMailBox(Mailbox mailbox);

    /**
     * 重新同步邮箱
     */
    void resyncMailBox(Mailbox mailbox);

    /**
     * 清空已存储的异常
     */
    void clearErrors(Mailbox mailbox);

    /**
     * 发送邮件
     * @param mailbox 源邮箱
     * @param mail 邮件
     * @throws MailServiceException 发送失败
     */
    void sendMail(Mailbox mailbox, Mail mail) throws MailServiceException;

    /**
     * 获取指定邮箱的所有邮件
     * @param mailbox 邮箱
     * @param folder 文件夹
     * @return 邮件集合
     */
    Collection<Mail> getAllMailByOf(Mailbox mailbox, String folder);

    /**
     * 获取所有邮箱的所有邮件
     * @return 邮件集合
     */
    Collection<Mail> getAllMail();

    /**
     * 根据ID获取邮件
     * @return 邮件
     */
    Mail getMailById(String id);

    /**
     * 获取所有邮箱
     * @return 邮箱集合
     */
    Collection<Mailbox> getAllMailbox();

    /**
     * 获取邮件总数
     * @return 邮件总数
     */
    long getMailCount();
}
