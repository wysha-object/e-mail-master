package cn.com.wysha.e_mail_master.model.constant;

public enum FieldName {
    NAME("name");
    private final String name;

    FieldName(String name) {
        this.name = name;
    }

    @Override
    public String toString () {
        return name;
    }
}
