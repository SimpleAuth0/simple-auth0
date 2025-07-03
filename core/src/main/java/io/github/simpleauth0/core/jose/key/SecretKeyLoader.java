package io.github.simpleauth0.core.jose.key;

import io.github.simpleauth0.core.SimpleAuth0Properties;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:49
 */
public class SecretKeyLoader implements KeyLoader {

    private final SimpleAuth0Properties properties;

    public SecretKeyLoader(SimpleAuth0Properties properties) {
        this.properties = properties;
    }

    @Override
    public RSAPrivateKey loadPrivateKey() {
        return null;
    }

    @Override
    public RSAPublicKey loadPublicKey() {
        return null;
    }

    @Override
    public String loadSecretKey() {
        return properties.getToken().getJwt().getSecret();
    }

    @Override
    public String signatureAlgorithm() {
        return properties.getToken().getJwt().getSignatureAlgorithm();
    }

    @Override
    public boolean supports(KeySource source) {
        return source == KeySource.SECRET;
    }
}

