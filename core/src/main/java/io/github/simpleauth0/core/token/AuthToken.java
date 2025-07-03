package io.github.simpleauth0.core.token;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.Instant;

/**
 * @author: ReLive27
 * @date: 2025/6/18 21:55
 */
public abstract class AuthToken {

    private final String tokenValue;
    private final String tokenType;
    private final Instant issuedAt;
    private final Instant expiresAt;

    protected AuthToken(String tokenValue, String tokenType, @Nullable Instant issuedAt, @Nullable Instant expiresAt) {
        Assert.hasText(tokenValue, "tokenValue cannot be empty");
        if (issuedAt != null && expiresAt != null) {
            Assert.isTrue(expiresAt.isAfter(issuedAt), "expiresAt must be after issuedAt");
        }

        this.tokenValue = tokenValue;
        this.tokenType = tokenType;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public String getTokenType() {
        return tokenType;
    }

    @Nullable
    public Instant getIssuedAt() {
        return this.issuedAt;
    }

    @Nullable
    public Instant getExpiresAt() {
        return this.expiresAt;
    }
}

