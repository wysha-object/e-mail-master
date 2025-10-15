package cn.com.wysha.e_mail_master.model.intf;

import cn.com.wysha.e_mail_master.model.constant.AuthType;
import cn.com.wysha.e_mail_master.model.constant.ErrorType;

import java.util.Collection;
import java.util.List;

public interface Mailbox {
    AuthType getAuthType();

    String getAddress();

    default String getFolderId(String folder) {
        return getAddress() + "/" + folder;
    }

    Collection<String> getFolders();

    void addError(Throwable e);

    void clearError();

    /**
     * 更新等操作时发生的错误
     * 最多记录8个
     * 按添加时间排序,新的在后,旧的在前
     */
    List<ErrorType> getErrors();
}
