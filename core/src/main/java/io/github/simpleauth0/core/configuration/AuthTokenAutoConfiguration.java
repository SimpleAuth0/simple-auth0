package io.github.simpleauth0.core.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:42
 */
@AutoConfiguration(before = SecurityAutoConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({AuthTokenConfiguration.JwtConfigurer.class,
        AuthTokenConfiguration.OpaqueTokenConfigurer.class})
public class AuthTokenAutoConfiguration {

}
