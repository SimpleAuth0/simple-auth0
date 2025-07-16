package io.github.simpleauth0.core.token;

import io.github.simpleauth0.core.SimpleAuth0Properties;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.util.Assert;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:03
 */
public class OpaqueTokenGenerator implements TokenGenerator<OpaqueToken> {
    private final StringKeyGenerator tokenGenerator;

    public OpaqueTokenGenerator(SimpleAuth0Properties properties) {
        StringKeyGenerator keyGenerator = createStringKeyGenerator(properties);
        Assert.notNull(keyGenerator, "Invalid opaque token format: unsupported or missing format type. Please check the configuration property 'simpleauth0.token.opaque.format'.");
        this.tokenGenerator = keyGenerator;
    }

    @Override
    public OpaqueToken generate(Authentication authentication) {
        String opaqueToken = tokenGenerator.generateKey();
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
        return new OpaqueToken(opaqueToken, "bearer", Instant.now(), expiresAt);
    }

    private StringKeyGenerator createStringKeyGenerator(SimpleAuth0Properties properties) {
        SimpleAuth0Properties.Opaque opaque = properties.getToken().getOpaque();
        if (opaque.getFormat().equals(OpaqueTokenFormat.UUID)) {
            return new StringKeyGenerator() {
                @Override
                public String generateKey() {
                    return UUID.randomUUID().toString();
                }
            };
        } else if (opaque.getFormat().equals(OpaqueTokenFormat.BASE64)) {
            return new Base64StringKeyGenerator(opaque.getLength());
        } else if (opaque.getFormat().equals(OpaqueTokenFormat.HEX)) {
            return new StringKeyGenerator() {
                @Override
                public String generateKey() {
                    SecureRandom random = new SecureRandom();
                    byte[] bytes = new byte[opaque.getLength()];
                    random.nextBytes(bytes);
                    return new String(Hex.encode(bytes));
                }
            };
        } else {
            return null;
        }
    }
}

