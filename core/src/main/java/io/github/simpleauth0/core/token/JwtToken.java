package io.github.simpleauth0.core.token;

import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/6/18 21:58
 */
public class JwtToken extends AuthToken {

    private final Map<String, Object> headers;
    private final Map<String, Object> claims;

    public JwtToken(String tokenValue, Instant issuedAt, Instant expiresAt, Map<String, Object> headers, Map<String, Object> claims) {
        super(tokenValue, "bearer", issuedAt, expiresAt);
        Assert.notEmpty(headers, "headers cannot be empty");
        Assert.notEmpty(claims, "claims cannot be empty");
        this.headers = Collections.unmodifiableMap(headers);
        this.claims = Collections.unmodifiableMap(claims);
    }

    public Map<String, Object> getHeaders() {
        return this.headers;
    }

    public Map<String, Object> getClaims() {
        return this.claims;
    }
}

