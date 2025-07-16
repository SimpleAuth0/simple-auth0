package io.github.simpleauth0.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author: ReLive27
 * @date: 2025/7/7 23:13
 */
public class AuthTokenConfiguration {

    @Configuration(proxyBeanMethods = false)
    @Import({OpaqueTokenConfiguration.class})
    static class OpaqueTokenConfigurer {

    }

    @Configuration(proxyBeanMethods = false)
    @Import(JwtConfiguredCondition.class)
    static class JwtConfigurer {

    }
}
