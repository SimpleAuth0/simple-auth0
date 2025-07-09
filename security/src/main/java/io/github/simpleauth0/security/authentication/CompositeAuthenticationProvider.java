package io.github.simpleauth0.security.authentication;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/7/9 22:22
 */
public class CompositeAuthenticationProvider implements AuthenticationProvider {

    private final List<AuthenticationProvider> authenticationProviders;

    public CompositeAuthenticationProvider(List<AuthenticationProvider> authenticationProviders) {
        Assert.notEmpty(authenticationProviders, "authenticationProviders cannot be empty");
        this.authenticationProviders = authenticationProviders;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CompositeAuthenticationToken authenticationToken = (CompositeAuthenticationToken) authentication;

        boolean allSuccess = true;
        Authentication authenticationResult = null;
        for (Authentication auth : authenticationToken.getAuthentications()) {
            for (AuthenticationProvider step : authenticationProviders) {
                if (step.supports(auth.getClass())) {
                    try {
                        authenticationResult = step.authenticate(auth);
                        if (!authenticationResult.isAuthenticated()) {
                            allSuccess = false;
                        }
                    } catch (AuthenticationException e) {
                        allSuccess = false;
                    }
                    break;
                }
            }
        }
        if (authenticationResult == null || !allSuccess) {
            throw new BadCredentialsException("Composite authentication failed.");
        }
        return new CompositeAuthenticationToken(
                authenticationResult.getPrincipal(), null, authenticationResult.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return CompositeAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
