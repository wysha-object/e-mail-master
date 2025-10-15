package cn.com.wysha.e_mail_master.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "setting")
public class SettingEntity {
    @Id
    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
