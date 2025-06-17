package io.github.simpleauth0.core.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author: ReLive27
 * @date: 2025/6/15 22:02
 */
public class SimpleAuth0MessageSource extends ResourceBundleMessageSource {

    public SimpleAuth0MessageSource() {
        setBasename("io.github.simpleauth0.messages");
        setDefaultEncoding("UTF-8");
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(new SimpleAuth0MessageSource());
    }
}

