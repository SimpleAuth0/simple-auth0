package io.github.simpleauth0.core.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:45
 */

public class PemUtils {

    public static RSAPrivateKey loadPrivateKey(String location) throws Exception {
        String pem = readPem(location, "PRIVATE");
        byte[] decoded = Base64.getDecoder().decode(pem);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        return (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(spec);
    }

    public static RSAPublicKey loadPublicKey(String location) throws Exception {
        String pem = readPem(location, "PUBLIC");
        byte[] decoded = Base64.getDecoder().decode(pem);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    private static String readPem(String location, String type) throws Exception {
        Resource resource = new DefaultResourceLoader().getResource(location);
        try (InputStream is = resource.getInputStream()) {
            String raw = IOUtils.toString(is, StandardCharsets.UTF_8);
            return raw.replaceAll("-----BEGIN " + type + " KEY-----", "")
                    .replaceAll("-----END " + type + " KEY-----", "")
                    .replaceAll("\\s+", "");
        }
    }
}

