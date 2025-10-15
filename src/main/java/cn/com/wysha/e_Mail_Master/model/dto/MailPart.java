package cn.com.wysha.e_mail_master.model.dto;

import cn.com.wysha.e_mail_master.util.MimeContentTypeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data @NoArgsConstructor @AllArgsConstructor
public class MailPart {
    private Map<String, String> headers;
    private String contentType;
    private Object content;
    public String getStringContent() {
        if (MimeContentTypeUtils.isMultipart(contentType)) throw new IllegalArgumentException();
        if (!(content instanceof String str)) throw new IllegalStateException();
        return str;
    }
}
