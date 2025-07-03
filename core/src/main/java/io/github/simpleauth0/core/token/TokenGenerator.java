package io.github.simpleauth0.core.token;

import org.springframework.security.core.Authentication;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:02
 */
@FunctionalInterface
public interface TokenGenerator<T extends AuthToken> {
    T generate(Authentication authentication);
}

