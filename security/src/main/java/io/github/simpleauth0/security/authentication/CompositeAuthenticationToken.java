package io.github.simpleauth0.security.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/7/9 21:04
 */
public class CompositeAuthenticationToken extends AbstractAuthenticationToken {

    private List<Authentication> authentications;

    private Object principal;
    private Object credentials;

    public CompositeAuthenticationToken(List<Authentication> authentications) {
        super(null);
        this.authentications = authentications;
        setAuthenticated(false);
    }

    public CompositeAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return this.credentials;
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }

    public List<Authentication> getAuthentications() {
        return authentications;
    }
}
