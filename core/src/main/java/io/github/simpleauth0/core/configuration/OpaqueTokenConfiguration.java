package io.github.simpleauth0.core.configuration;

import io.github.simpleauth0.core.SimpleAuth0Properties;
import io.github.simpleauth0.core.token.OpaqueToken;
import io.github.simpleauth0.core.token.OpaqueTokenGenerator;
import io.github.simpleauth0.core.token.TokenGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ReLive27
 * @date: 2025/7/6 22:58
 */
@Conditional({OpaqueConfiguredCondition.class})
@Configuration(proxyBeanMethods = false)
public class OpaqueTokenConfiguration {

    @Bean
    public TokenGenerator<OpaqueToken> opaqueTokenGenerator(SimpleAuth0Properties properties) {
        return new OpaqueTokenGenerator(properties);
    }
}
