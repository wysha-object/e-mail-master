package cn.com.wysha.e_mail_master.ex;


import cn.com.wysha.e_mail_master.model.dto.BasicAuthMailbox;
import lombok.Getter;

@Getter
public class MailServiceException extends Exception{
    private final BasicAuthMailbox mailBox;
    public MailServiceException(Exception e, BasicAuthMailbox mailBox){
        super(e);
        this.mailBox = mailBox;
    }
}
