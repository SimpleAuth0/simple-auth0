package io.github.simpleauth0.security.convert;

import io.github.simpleauth0.core.http.RepeatedlyRequestWrapper;
import io.github.simpleauth0.security.authentication.CompositeAuthenticationToken;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        List<Authentication> authentications = new ArrayList<>(converters.size());
        for (AuthenticationConverter converter : this.converters) {
            Authentication authentication = converter.convert(requestWrapper);
            if (authentication != null) {
                authentications.add(authentication);
            }
        }

        if (CollectionUtils.isEmpty(authentications)) {
            return null;
        }
        if (authentications.size() == 1) {
            return authentications.get(0);
        }
        return new CompositeAuthenticationToken(authentications);
    }
}
