package io.github.simpleauth0.security.handler;

import io.github.simpleauth0.core.token.AuthToken;
import io.github.simpleauth0.core.token.TokenGenerator;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: ReLive27
 * @date: 2025/5/25 20:57
 */
public class CompositeLoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private HttpMessageConverter<Object> httpMessageConverter = new MappingJackson2HttpMessageConverter();

    private final TokenGenerator<? extends AuthToken> tokenGenerator;

    public CompositeLoginAuthenticationSuccessHandler(TokenGenerator<? extends AuthToken> tokenGenerator) {
        Assert.notNull(tokenGenerator, "tokenGenerator cannot be null");
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        AuthToken token = tokenGenerator.generate(authentication);
        ServletServerHttpResponse httpResponse = new ServletServerHttpResponse(response);
        this.httpMessageConverter.write(token.toJSONString(), MediaType.APPLICATION_JSON, httpResponse);
    }
}
