package io.github.simpleauth0.core.token;

import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/6/19 22:26
 */
public class JwtTokenContext implements AuthTokenContext {
    private final Map<Object, Object> context;

    private JwtTokenContext(Map<Object, Object> context) {
        this.context = Collections.unmodifiableMap(context);
    }

    @Override
    @Nullable
    public <V> V get(Object key) {
        return this.hasKey(key) ? (V) this.context.get(key) : null;
    }

    public boolean hasKey(Object key) {
        Assert.notNull(key, "key cannot be null");
        return this.context.containsKey(key);
    }

    public JwsHeader.Builder getJwsHeader() {
        return this.get(JwsHeader.Builder.class);
    }

    public JwtClaimsSet.Builder getClaims() {
        return this.get(JwtClaimsSet.Builder.class);
    }

    public static JwtTokenContext.Builder with(JwsHeader.Builder jwsHeaderBuilder, JwtClaimsSet.Builder claimsBuilder) {
        return new JwtTokenContext.Builder(jwsHeaderBuilder, claimsBuilder);
    }

    public static final class Builder extends AuthTokenContext.AbstractBuilder<JwtTokenContext, JwtTokenContext.Builder> {
        private Builder(JwsHeader.Builder jwsHeaderBuilder, JwtClaimsSet.Builder claimsBuilder) {
            Assert.notNull(jwsHeaderBuilder, "jwsHeaderBuilder cannot be null");
            Assert.notNull(claimsBuilder, "claimsBuilder cannot be null");
            this.put(JwsHeader.Builder.class, jwsHeaderBuilder);
            this.put(JwtClaimsSet.Builder.class, claimsBuilder);
        }

        public JwtTokenContext build() {
            return new JwtTokenContext(this.getContext());
        }
    }
}
