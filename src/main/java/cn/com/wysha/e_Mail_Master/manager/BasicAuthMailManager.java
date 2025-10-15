package cn.com.wysha.e_mail_master.manager;

import cn.com.wysha.e_mail_master.ex.MailServiceException;
import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import cn.com.wysha.e_mail_master.model.dto.Mail;

import java.util.function.Consumer;
import java.util.function.Function;

public interface BasicAuthMailManager {
    void send(BasicAuthMailbox mailbox, Mail mail) throws MailServiceException ;

    /**
     * 接收已有邮件并添加监听新邮件
     * @param mailbox 目标邮箱
     * @param consumer 邮件消费者
     * @param isExists 邮件是否存在,已存在的邮件不会给消费者
     * @param onError 发生错误时
     */
    void addReceiver(BasicAuthMailbox mailbox, Consumer<Mail> consumer, Function<String, Boolean> isExists, Consumer<MailServiceException> onError);

    void removeReceiver(BasicAuthMailbox mailbox);
}
