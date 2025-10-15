package cn.com.wysha.e_mail_master;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "e-mail-master")
@Data @NoArgsConstructor @AllArgsConstructor
public class EMailMasterProperties {
    /**
     * 确保devtools能正常重启程序,便于前端开发
     * <p>
     * 当其为{@code true}时:
     * <p>
     * - JavaFX客户端不会启动
     * <p>
     * - {@code MailServiceImpl.MailServiceRunner.run()}将跳过"对所有邮箱初始化"
     */
    private boolean devtools_mode;
    /**
     * 缓存目录
     */
    private String cache_dir;
}
