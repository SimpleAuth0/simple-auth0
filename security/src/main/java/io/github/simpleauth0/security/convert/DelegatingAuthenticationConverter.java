package io.github.simpleauth0.security.convert;

import io.github.simpleauth0.core.http.RepeatedlyRequestWrapper;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/6/2 21:05
 */
public class DelegatingAuthenticationConverter implements AuthenticationConverter {
    private final List<AuthenticationConverter> converters;

    public DelegatingAuthenticationConverter(List<AuthenticationConverter> converters) {
        Assert.notEmpty(converters, "converters cannot be empty");
        this.converters = Collections.unmodifiableList(new LinkedList<>(converters));
    }

    @SneakyThrows
    @Nullable
    @Override
    public Authentication convert(HttpServletRequest request) {
        Assert.notNull(request, "request cannot be null");
        RepeatedlyRequestWrapper requestWrapper = new RepeatedlyRequestWrapper(request);
        for (AuthenticationConverter converter : this.converters) {
            Authentication authentication = converter.convert(requestWrapper);
            if (authentication != null) {
                return authentication;
            }
        }
        return null;
    }
}
