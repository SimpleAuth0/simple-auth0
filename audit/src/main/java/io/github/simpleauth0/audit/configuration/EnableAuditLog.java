package io.github.simpleauth0.audit.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: ReLive27
 * @date: 2025/4/25 23:20
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AuditLogMethodSelector.class)
@Configuration
public @interface EnableAuditLog {

    boolean method() default true;
}
