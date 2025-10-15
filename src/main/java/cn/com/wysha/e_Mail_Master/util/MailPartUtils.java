package cn.com.wysha.e_mail_master.util;

import cn.com.wysha.e_mail_master.model.dto.MailFilePart;
import cn.com.wysha.e_mail_master.model.dto.MailMultipart;
import cn.com.wysha.e_mail_master.model.dto.MailPart;

import java.util.List;
import java.util.Objects;

public class MailPartUtils {
    /**
     * 根据 {@code target} 在 {@code part} 中递归地寻找复合的字段
     * @param part 邮件段落
     * @param target MIME类型
     * @return 找到的内容,如果没找到返回 {@code null}
     */
    public static String getContentByType (MailPart part, String target) {
        if (MimeContentTypeUtils.isMultipart(target)) throw new IllegalArgumentException();

        if (MimeContentTypeUtils.isMultipart(part.getContentType())) {
            if (!(part.getContent() instanceof MailMultipart multipart)) throw new IllegalArgumentException();

            List<String> list = multipart.getParts().stream().map(o -> getContentByType(o, target)).filter(Objects::nonNull).toList();
            if (list.isEmpty()){
                return null;
            }
            return list.getFirst();
        } else {
            if (MimeContentTypeUtils.isWithSubType(part.getContentType(), target)) {
                if (part.getContent() instanceof String s) {
                    return s;
                } else if (part.getContent() instanceof MailFilePart mailFilePart) {
                    return mailFilePart.getFilePath();
                } else {
                    throw new IllegalArgumentException();
                }
            } else {
                return null;
            }
        }
    }
}
