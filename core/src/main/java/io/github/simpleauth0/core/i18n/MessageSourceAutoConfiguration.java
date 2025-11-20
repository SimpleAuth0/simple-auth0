package io.github.simpleauth0.core.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ReLive27
 * @date: 2025/11/19 23:16
 */
@Configuration(proxyBeanMethods = false)
public class MessageSourceAutoConfiguration {

    @Bean
    public MessageSource messageSource() {
        return new SimpleAuth0MessageSource();
    }

}
