package io.github.simpleauth0.core.token;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.util.Assert;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:04
 */
public class JwtTokenGenerator implements TokenGenerator<JwtToken> {

    private final JwtEncoder jwtEncoder;
    private AuthTokenCustomizer<JwtTokenContext> tokenCustomizer;

    public JwtTokenGenerator(JwtEncoder jwtEncoder) {
        Assert.notNull(jwtEncoder, "jwtEncoder can not be null");
        this.jwtEncoder = jwtEncoder;
    }

    public JwtToken generate(Authentication authentication) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(30, ChronoUnit.MINUTES);
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder();
        claimsBuilder.subject(authentication.getName())
                .issuer("https://github.com/SimpleAuth0/simple-auth0")
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .notBefore(issuedAt)
                .claim("authorize", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .claim(JwtClaimNames.JTI, UUID.randomUUID().toString());

        JwsHeader.Builder headersBuilder = JwsHeader.with(MacAlgorithm.HS256);

        if (this.tokenCustomizer != null) {
            JwtTokenContext jwtTokenContext = JwtTokenContext.with(headersBuilder, claimsBuilder)
                    .principal(authentication)
                    .build();
            this.tokenCustomizer.customize(jwtTokenContext);
        }
        JwsHeader headers = headersBuilder.build();
        JwtClaimsSet claims = claimsBuilder.build();

        Jwt jwt = this.jwtEncoder.encode(JwtEncoderParameters.from(headers, claims));
        return new JwtToken(jwt.getTokenValue(), jwt.getIssuedAt(), jwt.getExpiresAt(), jwt.getHeaders(), jwt.getClaims());
    }

    public void setTokenCustomizer(AuthTokenCustomizer<JwtTokenContext> tokenCustomizer) {
        this.tokenCustomizer = tokenCustomizer;
    }
}


