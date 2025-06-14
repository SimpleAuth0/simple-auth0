package io.github.simpleauth0.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author: ReLive27
 * @date: 2025/6/6 22:48
 */
public class CompositeLoginAuthenticationException extends AuthenticationException {

    public CompositeLoginAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public CompositeLoginAuthenticationException(String msg) {
        super(msg);
    }
}
