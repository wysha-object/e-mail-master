package cn.com.wysha.e_mail_master.util;

import cn.com.wysha.e_mail_master.model.constant.ContentType;
import cn.com.wysha.e_mail_master.model.constant.FieldName;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MimeContentTypeUtils {
    public static String getField (String contentType, String fieldName) {
        String pattern = String.format("((?<=%s=\")[^\\r\\n]+(?=\")|(?<=%1$s=)[^\"\\n]+(?=))",
                fieldName
        );
        Matcher matcher = Pattern.compile(pattern).matcher(contentType);
        if (matcher.find()) {
            return matcher.group();
        }else {
            log.error(contentType);
            throw new IllegalArgumentException();
        }
    }

    public static String getName (String contentType) {
        return getField(contentType, FieldName.NAME.toString());
    }

    public static boolean is (String contentType, String type) {
        return contentType.toLowerCase().matches(String.format("%s/[\\s\\S]*", type.toLowerCase()));
    }

    public static boolean isMultipart (String contentType) {
        return is(contentType, ContentType.MULTIPART.toString());
    }
    public static boolean isText (String contentType) {
        return is(contentType, ContentType.TEXT.toString());
    }

    public static boolean isWithSubType (String contentType, String target) {
        return contentType.toLowerCase().matches(String.format("%s[\\s\\S]*", target.toLowerCase()));
    }

    public static boolean isTextPlain (String contentType) {
        return is(contentType, ContentType.TEXT_PLAIN.toString());
    }

    public static boolean isTextHTML (String contentType) {
        return is(contentType, ContentType.TEXT_HTML.toString());
    }
}
