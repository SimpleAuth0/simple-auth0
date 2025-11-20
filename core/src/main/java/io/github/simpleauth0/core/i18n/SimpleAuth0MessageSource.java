package io.github.simpleauth0.core.i18n;

import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author: ReLive27
 * @date: 2025/6/15 22:02
 */
public class SimpleAuth0MessageSource extends ResourceBundleMessageSource {

    private SimpleAuth0MessageSource() {
        setBasename("io.github.simpleauth0.messages");
        setDefaultEncoding("UTF-8");
    }

    public static SimpleAuth0MessageSource getInstance() {
        return SimpleAuth0MessageSourceHolder.instance;
    }

    public static MessageSourceAccessor getAccessor() {
        return new MessageSourceAccessor(getInstance());
    }

    private static class SimpleAuth0MessageSourceHolder {
        private static SimpleAuth0MessageSource instance = new SimpleAuth0MessageSource();
    }
}

