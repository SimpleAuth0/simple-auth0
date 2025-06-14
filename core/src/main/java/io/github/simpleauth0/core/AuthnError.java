package io.github.simpleauth0.core;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author: ReLive27
 * @date: 2025/6/11 22:27
 */
public class AuthnError implements Serializable {
    private final Integer errorCode;

    private final String description;

    public AuthnError(Integer errorCode) {
        this(errorCode, null);
    }


    public AuthnError(Integer errorCode, String description) {
        Assert.notNull(errorCode, "errorCode cannot be null");
        this.errorCode = errorCode;
        this.description = description;
    }


    public final Integer getErrorCode() {
        return this.errorCode;
    }

    @Nullable
    public final String getDescription() {
        return this.description;
    }
}
