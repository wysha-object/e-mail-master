package cn.com.wysha.e_mail_master.dao;

import cn.com.wysha.e_mail_master.model.entity.HeadersEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HeadersDao extends JpaRepository<HeadersEntity, String> {
    void deleteAllByMailPartId(String mailPartId);
}
