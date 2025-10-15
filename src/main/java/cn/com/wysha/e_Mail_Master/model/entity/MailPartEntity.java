package cn.com.wysha.e_mail_master.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
@Entity(name = "mail_part")
public class MailPartEntity {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "super_id")
    private String superId;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "content")
    private String content;
}
