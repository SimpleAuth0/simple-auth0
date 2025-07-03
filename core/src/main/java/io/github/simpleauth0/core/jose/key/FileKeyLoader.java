package io.github.simpleauth0.core.jose.key;

import io.github.simpleauth0.core.SimpleAuth0Properties;
import io.github.simpleauth0.core.utils.PemUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * @author: ReLive27
 * @date: 2025/6/23 22:38
 */
@Slf4j
public class FileKeyLoader implements KeyLoader {

    private final SimpleAuth0Properties properties;

    public FileKeyLoader(SimpleAuth0Properties properties) {
        this.properties = properties;
    }

    @Override
    public boolean supports(KeySource source) {
        return source == KeySource.FILE;
    }

    @Override
    public RSAPrivateKey loadPrivateKey() {
        var location = properties.getToken().getJwt().getFile().getPrivateKeyLocation();
        try {
            return PemUtils.loadPrivateKey(location);
        } catch (Exception e) {
            log.error("读取私钥失败", e);
            return null;
        }
    }

    @Override
    public RSAPublicKey loadPublicKey() {
        var location = properties.getToken().getJwt().getFile().getPublicKeyLocation();
        try {
            return PemUtils.loadPublicKey(location);
        } catch (Exception e) {
            log.error("读取公钥失败", e);
            return null;
        }
    }

    @Override
    public String signatureAlgorithm() {
        return this.properties.getToken().getJwt().getSignatureAlgorithm();
    }
}

