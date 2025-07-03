package io.github.simpleauth0.core.jose.key;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author: ReLive27
 * @date: 2025/6/23 22:38
 */
public interface KeyLoader {
    RSAPrivateKey loadPrivateKey();

    RSAPublicKey loadPublicKey();

    String signatureAlgorithm();

    boolean supports(KeySource source);

    default String loadSecretKey() {
        return null;
    }
}
