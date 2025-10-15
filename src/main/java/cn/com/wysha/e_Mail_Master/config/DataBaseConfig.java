package cn.com.wysha.e_mail_master.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Configuration
public class DataBaseConfig {
    @Bean("database_lock")
    public ReadWriteLock database_lock() {
        return new ReentrantReadWriteLock();
    }
}
