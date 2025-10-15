package cn.com.wysha.e_mail_master.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "headers")
public class HeadersEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "mail_part_id")
    private String mailPartId;

    @Column(name = "key")
    private String key;

    @Column(name = "value")
    private String value;
}
