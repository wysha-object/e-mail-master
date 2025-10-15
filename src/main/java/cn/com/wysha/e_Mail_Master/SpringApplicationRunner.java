package cn.com.wysha.e_mail_master;

import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class SpringApplicationRunner {

    private final EMailMasterProperties eMailMasterProperties;

    public SpringApplicationRunner(EMailMasterProperties eMailMasterProperties) {
        this.eMailMasterProperties = eMailMasterProperties;
    }

    @PreDestroy
    public void destroy() {
        String cacheDir = eMailMasterProperties.getCache_dir();
        File file = new File(cacheDir);
        for (File f : file.listFiles()) {
            if (f.isFile()) f.delete();
        }
    }
}
