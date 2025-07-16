package io.github.simpleauth0.core;

import io.github.simpleauth0.core.jose.key.KeySource;
import io.github.simpleauth0.core.token.OpaqueTokenFormat;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:20
 */
@Data
@ConfigurationProperties(prefix = "simpleauth0")
public class SimpleAuth0Properties {

    private Token token = new Token();

    @Data
    public class Token {

        /**
         * JWT配置
         */
        private final Jwt jwt = new Jwt();
        /**
         * 不透明令牌配置
         */
        private final Opaque opaque = new Opaque();
    }


    @Data
    public static class Jwt {

        private KeySource keySource = KeySource.FILE;
        private FileKey file;

        /**
         * 默认签发者
         */
        private String issuer = "simpleauth0";

        /**
         * 默认过期时间（单位：秒）
         */
        private long expiresIn = 3600;

        /**
         * 对称签名密钥
         */
        private String secret;

        /**
         * 签名算法，默认 HS256，可扩展 RS256 等
         */
        private String signatureAlgorithm = "HS256";
    }

    @Data
    public static class FileKey {
        private String privateKeyLocation;
        private String publicKeyLocation;
    }


    @Data
    public static class Opaque {
        /**
         * 令牌随机长度，默认 32
         */
        private int length = 32;

        /**
         * 控制令牌格式生成器
         */
        private OpaqueTokenFormat format = OpaqueTokenFormat.UUID;
    }
}
