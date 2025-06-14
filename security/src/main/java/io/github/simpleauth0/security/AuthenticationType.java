package io.github.simpleauth0.security;

import lombok.Getter;

/**
 * @author: ReLive27
 * @date: 2025/6/6 22:54
 */
@Getter
public enum AuthenticationType {
    PASSWORD("password"),
    SMS_CODE("sms_code"),
    TOTP("TOTP"),
    BIOMETRIC("biometric"),
    PASSKEY("passkey"),
    OAUTH2("oauth2"),
    SAML("saml");

    String authType;

    private AuthenticationType(String authType) {
        this.authType = authType;
    }
}
