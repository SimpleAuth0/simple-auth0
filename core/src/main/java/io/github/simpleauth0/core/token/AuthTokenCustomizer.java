package io.github.simpleauth0.core.token;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:30
 */
@FunctionalInterface
public interface AuthTokenCustomizer<T extends AuthTokenContext> {

    void customize(T context);

}
