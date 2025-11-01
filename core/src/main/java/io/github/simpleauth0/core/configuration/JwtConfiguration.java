package io.github.simpleauth0.core.configuration;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import io.github.simpleauth0.core.SimpleAuth0Properties;
import io.github.simpleauth0.core.jose.key.FileRsaKeyLoader;
import io.github.simpleauth0.core.jose.key.KeyLoader;
import io.github.simpleauth0.core.jose.key.SecretKeyLoader;
import io.github.simpleauth0.core.token.JwtToken;
import io.github.simpleauth0.core.token.JwtTokenGenerator;
import io.github.simpleauth0.core.token.TokenGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author: ReLive27
 * @date: 2025/7/6 23:07
 */
@Conditional(JwtConfiguredCondition.class)
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SimpleAuth0Properties.class)
public class JwtConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "simpleauth0.token.jwt", name = "key-source", havingValue = "file")
    static class FileKeySourceJwtConfiguration {


        @Bean
        public JWKSource<SecurityContext> jwkSource(SimpleAuth0Properties properties) throws JOSEException {
            KeyLoader keyLoader = new FileRsaKeyLoader(properties);
            String alg = keyLoader.signatureAlgorithm();

            if (!"RS256".equalsIgnoreCase(alg)) {
                throw new IllegalArgumentException("Unsupported signature algorithm: " + alg + ". Only RSA is supported.");
            }

            RSAKey rsaKey = new RSAKey.Builder(keyLoader.loadPublicKey())
                    .privateKey(keyLoader.loadPrivateKey())
                    .keyID(UUID.randomUUID().toString()) // 可以考虑用公钥的 thumbprint 做唯一标识
                    .build();

            JWKSet jwkSet = new JWKSet(rsaKey);
            return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
        }


        @Bean
        public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) throws Exception {
            return new NimbusJwtEncoder(jwkSource);
        }


        @Bean
        public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) throws Exception {
            Set<JWSAlgorithm> jwsAlgs = new HashSet();
            jwsAlgs.addAll(JWSAlgorithm.Family.RSA);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor();
            JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector(jwsAlgs, jwkSource);
            jwtProcessor.setJWSKeySelector(jwsKeySelector);
            jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
            });
            return new NimbusJwtDecoder(jwtProcessor);
        }
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "simpleauth0.token.jwt", name = "key-source", havingValue = "secret")
    static class SecretKeySourceJwtConfiguration {


        @Bean
        public JWKSource<SecurityContext> jwkSource(SimpleAuth0Properties properties) throws JOSEException {
            KeyLoader keyLoader = new SecretKeyLoader(properties);
            String alg = keyLoader.signatureAlgorithm();

            if (!"HS256".equalsIgnoreCase(alg)) {
                throw new IllegalArgumentException("Unsupported signature algorithm: " + alg + ". Only HMAC_SHA is supported.");
            }
            SecretKey secretKey = new SecretKeySpec(properties.getToken().getJwt().getSecret().getBytes(StandardCharsets.UTF_8), "HmacSha256");

            OctetSequenceKey hmacJwk = new OctetSequenceKey.Builder(secretKey)
                    .algorithm(JWSAlgorithm.HS256)
                    .keyUse(KeyUse.SIGNATURE)
                    .keyID(UUID.randomUUID().toString())
                    .build();
            JWKSet jwkSet = new JWKSet(hmacJwk);
            return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
        }

        @Bean
        public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) throws Exception {
            return new NimbusJwtEncoder(jwkSource);
        }

        @Bean
        public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) throws Exception {
            Set<JWSAlgorithm> jwsAlgs = new HashSet();
            jwsAlgs.addAll(JWSAlgorithm.Family.HMAC_SHA);
            ConfigurableJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor();
            JWSKeySelector<SecurityContext> jwsKeySelector = new JWSVerificationKeySelector(jwsAlgs, jwkSource);
            jwtProcessor.setJWSKeySelector(jwsKeySelector);
            jwtProcessor.setJWTClaimsSetVerifier((claims, context) -> {
            });
            return new NimbusJwtDecoder(jwtProcessor);
        }

        @Bean
        public TokenGenerator<JwtToken> tokenGenerator(JwtEncoder jwtEncoder) {
            return new JwtTokenGenerator(jwtEncoder);
        }
    }
}
