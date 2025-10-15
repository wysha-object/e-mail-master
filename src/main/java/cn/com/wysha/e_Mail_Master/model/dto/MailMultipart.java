package cn.com.wysha.e_mail_master.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * {@code MailPart}的集合
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class MailMultipart {
    Collection<MailPart> parts;
}
