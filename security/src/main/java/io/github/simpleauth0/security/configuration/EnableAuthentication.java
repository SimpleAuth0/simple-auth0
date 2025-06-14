package io.github.simpleauth0.security.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: ReLive27
 * @date: 2025/5/25 21:12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(AuthnImportSelector.class)
@Configuration
public @interface EnableAuthentication {

}
