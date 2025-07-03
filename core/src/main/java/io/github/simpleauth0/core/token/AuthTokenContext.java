package io.github.simpleauth0.core.token;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:30
 */
public interface AuthTokenContext {

    @Nullable
    <V> V get(Object key);

    default <T extends Authentication> T getPrincipal() {
        return this.get(AuthTokenContext.AbstractBuilder.PRINCIPAL_AUTHENTICATION_KEY);
    }

    abstract class AbstractBuilder<T extends AuthTokenContext, B extends AuthTokenContext.AbstractBuilder<T, B>> {
        private static final String PRINCIPAL_AUTHENTICATION_KEY = Authentication.class.getName().concat(".PRINCIPAL");
        private final Map<Object, Object> context = new HashMap<>();

        public AbstractBuilder() {
        }

        public B principal(Authentication principal) {
            return this.put(PRINCIPAL_AUTHENTICATION_KEY, principal);
        }

        public B put(Object key, Object value) {
            Assert.notNull(key, "key cannot be null");
            Assert.notNull(value, "value cannot be null");
            this.context.put(key, value);
            return this.getThis();
        }

        public B context(Consumer<Map<Object, Object>> contextConsumer) {
            contextConsumer.accept(this.context);
            return this.getThis();
        }

        protected <V> V get(Object key) {
            return (V) this.context.get(key);
        }

        protected Map<Object, Object> getContext() {
            return this.context;
        }

        protected final B getThis() {
            return (B) this;
        }

        public abstract T build();

    }
}
