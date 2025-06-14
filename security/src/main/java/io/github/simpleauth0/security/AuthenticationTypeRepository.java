package io.github.simpleauth0.security;

/**
 * @author: ReLive27
 * @date: 2025/6/2 20:58
 */
public interface AuthenticationTypeRepository {

    Boolean supports(String authType);

}
