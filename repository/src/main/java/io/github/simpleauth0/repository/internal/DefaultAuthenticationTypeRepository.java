package io.github.simpleauth0.repository.internal;

import io.github.simpleauth0.security.AuthenticationTypeRepository;
import org.springframework.stereotype.Component;

/**
 * @author: ReLive27
 * @date: 2025/11/5 23:20
 */
@Component
public class DefaultAuthenticationTypeRepository implements AuthenticationTypeRepository {
    @Override
    public Boolean supports(String authType) {
        return null;
    }
}
