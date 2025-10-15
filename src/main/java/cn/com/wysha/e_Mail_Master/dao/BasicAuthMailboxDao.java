package cn.com.wysha.e_mail_master.dao;

import cn.com.wysha.e_mail_master.model.entity.BasicAuthMailboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BasicAuthMailboxDao extends JpaRepository<BasicAuthMailboxEntity, BasicAuthMailboxEntity.AddressKey> {
}
