package cn.com.wysha.e_mail_master.model.constant;

public enum ContentType {
    MULTIPART("multipart"),
    TEXT("text"),
    TEXT_HTML("text/html"),
    TEXT_PLAIN("text/plain");

    private final String contentType;

    ContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString () {
        return contentType;
    }
}
