package io.github.simpleauth0.core.handler;

import io.github.simpleauth0.core.AuthnError;
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
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/5/25 10:10
 */
public class ExceptionMappingAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final Map<String, AuthnError> failureMessageMap = new HashMap<>();
    private HttpMessageConverter<Object> httpMessageConverter = new MappingJackson2HttpMessageConverter();

    public ExceptionMappingAuthenticationEntryPoint() {
        //TODO i18n
        failureMessageMap.put(AccountExpiredException.class.getName(), new AuthnError(HttpServletResponse.SC_BAD_REQUEST, "用户账号已过期"));
        failureMessageMap.put(AuthenticationCredentialsNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, "用户名密码错误"));
        failureMessageMap.put(AuthenticationCredentialsNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, "用户名密码错误"));
        failureMessageMap.put(BadCredentialsException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, "用户名密码错误"));
        failureMessageMap.put(CredentialsExpiredException.class.getName(), new AuthnError(HttpServletResponse.SC_BAD_REQUEST, "密码已过期"));
        failureMessageMap.put(ProviderNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_BAD_REQUEST, "当前认证方式未开通"));
        failureMessageMap.put(UsernameNotFoundException.class.getName(), new AuthnError(HttpServletResponse.SC_UNAUTHORIZED, "用户名密码错误"));
        failureMessageMap.put(InternalAuthenticationServiceException.class.getName(), new AuthnError(HttpServletResponse.SC_BAD_REQUEST, "内部服务异常"));
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
}
