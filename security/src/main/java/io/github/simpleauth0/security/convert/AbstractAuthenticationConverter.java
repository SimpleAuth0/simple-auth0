package io.github.simpleauth0.security.convert;

import io.github.simpleauth0.core.utils.JsonUtils;
import io.github.simpleauth0.security.AuthenticationParameterNames;
import io.github.simpleauth0.security.AuthenticationTypeRepository;
import io.github.simpleauth0.security.exception.CompositeLoginAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/6/2 21:13
 */
public abstract class AbstractAuthenticationConverter implements AuthenticationConverter {

    protected AuthenticationTypeRepository repository;

    public AbstractAuthenticationConverter(AuthenticationTypeRepository repository) {
        Assert.notNull(repository, "AuthenticationMethodRepository cannot be null");
        this.repository = repository;
    }

    @Override
    public Authentication convert(HttpServletRequest request) {
        StringBuilder requestBody = new StringBuilder();

        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        } catch (IOException e) {
            throw new CompositeLoginAuthenticationException("登陆请求解析失败");
        }
        Map<String, Object> body = JsonUtils.toMap(requestBody.toString());
        if (!repository.supports(String.valueOf(body.get(AuthenticationParameterNames.AUTH_TYPE)))
                || !supports(String.valueOf(body.get(AuthenticationParameterNames.AUTH_TYPE)))) {
            return null;
        }
        return this.convert(body);
    }

    protected abstract Authentication convert(Map<String, Object> body);

    protected abstract boolean supports(String loginType);

    protected String trimBlankCharacter(String value) {
        return value != null ? value.replaceAll("\\s*", "") : "";
    }

}
