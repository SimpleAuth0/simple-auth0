package io.github.simpleauth0.core.token;

import java.time.Instant;

/**
 * @author: ReLive27
 * @date: 2025/6/18 21:56
 */
public class OpaqueToken extends AuthToken {
    public OpaqueToken(String tokenValue, String tokenType, Instant issuedAt, Instant expiresAt) {
        super(tokenValue, tokenType, issuedAt, expiresAt);
    }
}
