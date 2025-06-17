package io.github.simpleauth0.security;

import io.github.simpleauth0.core.i18n.SimpleAuth0MessageSource;
import io.github.simpleauth0.security.exception.CompositeLoginAuthenticationException;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: ReLive27
 * @date: 2025/5/25 20:00
 */
public class CompositeLoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter implements MessageSourceAware {
    private MessageSourceAccessor messageSource = SimpleAuth0MessageSource.getAccessor();

    private AuthenticationConverter authenticationConverter;

    public CompositeLoginAuthenticationFilter(RequestMatcher requiresAuthenticationRequestMatcher, AuthenticationManager authenticationManager) {
        super(requiresAuthenticationRequestMatcher, authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {
            Authentication authentication = this.authenticationConverter.convert(request);
            if (authentication == null) {
                throw new CompositeLoginAuthenticationException(this.messageSource.getMessage("auth.login.unsupportedType", "不支持的登录类型", LocaleContextHolder.getLocale()));
            }
            return this.getAuthenticationManager().authenticate(authentication);

        }
    }

    public void setAuthenticationConverter(AuthenticationConverter authenticationConverter) {
        this.authenticationConverter = authenticationConverter;
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messageSource = new MessageSourceAccessor(messageSource);
    }
}
