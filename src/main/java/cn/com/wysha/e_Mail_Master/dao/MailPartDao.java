package cn.com.wysha.e_mail_master.dao;

import cn.com.wysha.e_mail_master.model.entity.MailPartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailPartDao extends JpaRepository<MailPartEntity, String> {
    List<MailPartEntity> findAllBySuperId(String superId);
}
