package cn.com.wysha.e_mail_master;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EMailMasterApplication{
    public static ConfigurableApplicationContext context;

    public static void main(String[] args) {
        context = SpringApplication.run(EMailMasterApplication.class, args);
        EMailMasterProperties eMailMasterProperties = context.getBean(EMailMasterProperties.class);
        if (!eMailMasterProperties.isDevtools_mode()) Application.launch(JavaFXWebApplication.class, args);
    }
}