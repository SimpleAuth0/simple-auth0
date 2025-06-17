package io.github.simpleauth0.core.handler;

import io.github.simpleauth0.core.AuthnError;
import io.github.simpleauth0.core.i18n.SimpleAuth0MessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/5/25 10:10
 */
public class ExceptionMappingAuthenticationEntryPoint implements AuthenticationEntryPoint, MessageSourceAware {
    private final Map<String, AuthnError> failureMessageMap = new HashMap<>();
    private HttpMessageConverter<Object> httpMessageConverter = new MappingJackson2HttpMessageConverter();
    private MessageSourceAccessor messageSource = SimpleAuth0MessageSource.getAccessor();

    public ExceptionMappingAuthenticationEntryPoint() {
        Locale locale = LocaleContextHolder.getLocale();

        failureMessageMap.put(AccountExpiredException.class.getName(), new AuthnError(HttpServletResponse.SC_BAD_REQUEST, this.messageSource.getMessage("auth.account.expired", "用户账号已过期", locale)));
        failureMessageMap.put(BadCredentialsException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.bad.credentials", "用户名或密码错误", locale)));
        failureMessageMap.put(UsernameNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.bad.credentials", "用户名或密码错误", locale)));
        failureMessageMap.put(DisabledException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.account.disabled", "用户账号已被禁用", locale)));
        failureMessageMap.put(LockedException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.account.locked", "用户账号已被锁定", locale)));
        failureMessageMap.put(CredentialsExpiredException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.credentials.expired", "用户凭证已过期，请重新登录", locale)));
        failureMessageMap.put(AuthenticationServiceException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.service.error", "登录服务内部异常", locale)));
        failureMessageMap.put(InsufficientAuthenticationException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.insufficient.auth", "当前访问需要更高权限，请重新认证", locale)));
        failureMessageMap.put(AuthenticationCredentialsNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.credentials.missing", "未提供认证凭证，禁止访问", locale)));
        failureMessageMap.put(ProviderNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.provider.not-found", "登录类型错误", locale)));
        failureMessageMap.put(InternalAuthenticationServiceException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, this.messageSource.getMessage("auth.service.internal-error", "认证服务内部异常，请稍后重试", locale)));
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        AuthnError error = this.failureMessageMap.get(authException.getClass().getName());

        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        if (error != null) {
            this.httpMessageConverter.write(error, null, httpResponse);
        } else {
            this.httpMessageConverter.write(new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage()), null, httpResponse);
        }
    }

    public void setExceptionMappings(Map<?, ?> failureMessageMap) {
        this.failureMessageMap.clear();
        for (Map.Entry<?, ?> entry : failureMessageMap.entrySet()) {
            Object exception = entry.getKey();
            Object message = entry.getValue();
            Assert.isInstanceOf(String.class, exception, "Exception key must be a String (the exception classname).");
            Assert.isInstanceOf(AuthnError.class, message, "message must be a AuthError");
            this.failureMessageMap.put((String) exception, (AuthnError) message);
        }
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        Assert.notNull(messageSource, "messageSource cannot be null");
        this.messageSource = new MessageSourceAccessor(messageSource);
    }
}
