package io.github.simpleauth0.security.handler;

import io.github.simpleauth0.core.handler.ExceptionMappingAuthenticationEntryPoint;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: ReLive27
 * @date: 2025/5/25 20:49
 */
public class CompositeLoginAuthenticationFailureHandler implements AuthenticationFailureHandler {
    AuthenticationEntryPoint authenticationEntryPoint = new ExceptionMappingAuthenticationEntryPoint();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        this.authenticationEntryPoint.commence(request, response, exception);
    }
}
