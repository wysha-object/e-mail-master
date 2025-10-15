package cn.com.wysha.e_mail_master.dao;

import cn.com.wysha.e_mail_master.model.entity.MailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailDao extends JpaRepository<MailEntity, String> {
    List<MailEntity> findAllByOf(String of);
}
