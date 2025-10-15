package cn.com.wysha.e_mail_master.model.vo;

import cn.com.wysha.e_mail_master.model.constant.ErrorType;
import cn.com.wysha.e_mail_master.model.intf.Mailbox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class MailboxView implements Comparable<MailboxView> {
    private String address;

    private List<String> folders;

    private List<String> errors;

    public MailboxView (Mailbox mailbox) {
        this(
                mailbox.getAddress(),
                mailbox.getFolders().stream().sorted().toList(),
                mailbox.getErrors().stream().map(ErrorType::toString).toList()
        );
    }

    @Override
    public int compareTo(MailboxView o) {
        return this.address.compareTo(o.address);
    }
}
