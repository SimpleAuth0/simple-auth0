package io.github.simpleauth0.core.token;

import org.springframework.security.core.Authentication;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:03
 */
public class OpaqueTokenGenerator implements TokenGenerator<OpaqueToken> {

    @Override
    public OpaqueToken generate(Authentication authentication) {
        String opaqueToken = UUID.randomUUID().toString();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
        return new OpaqueToken(opaqueToken, "bearer", Instant.now(), expiresAt);
    }
}

