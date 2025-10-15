package cn.com.wysha.e_mail_master.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailManagerTests {
    private final BasicAuthMailManager basicAuthMailManager;

    @Autowired
    public MailManagerTests(BasicAuthMailManager basicAuthMailManager) {
        this.basicAuthMailManager = basicAuthMailManager;
    }
}
