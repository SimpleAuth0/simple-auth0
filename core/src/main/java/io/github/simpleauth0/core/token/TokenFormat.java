package io.github.simpleauth0.core.token;

/**
 * @author: ReLive27
 * @date: 2025/6/18 22:08
 */
public enum TokenFormat {

    /**
     * 不透明令牌（opaque token）
     */
    OPAQUE("opaque"),

    /**
     * JSON Web Token（JWT）
     */
    JWT("jwt");

    private final String value;

    TokenFormat(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static TokenFormat from(String value) {
        for (TokenFormat format : values()) {
            if (format.value.equalsIgnoreCase(value)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported token format: " + value);
    }
}

