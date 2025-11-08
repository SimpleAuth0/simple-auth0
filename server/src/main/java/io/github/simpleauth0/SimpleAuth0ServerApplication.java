package io.github.simpleauth0;

import io.github.simpleauth0.audit.configuration.EnableAuditLog;
import io.github.simpleauth0.security.configuration.EnableAuthentication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: ReLive27
 * @date: 2025/3/5 23:08
 */
@EnableAuditLog
@EnableAuthentication
@SpringBootApplication
public class SimpleAuth0ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleAuth0ServerApplication.class, args);
    }
}
