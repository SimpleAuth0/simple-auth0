package io.github.simpleauth0.security.convert;

import io.github.simpleauth0.security.AuthenticationType;
import io.github.simpleauth0.security.AuthenticationTypeRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/6/2 20:45
 */
public class UsernamePasswordAuthenticationConvert extends AbstractAuthenticationConverter {

    public UsernamePasswordAuthenticationConvert(AuthenticationTypeRepository repository) {
        super(repository);
    }

    @Override
    protected Authentication convert(Map<String, Object> body) {
        //TODO 密码解密
        String username = body.get("username") != null ? this.trimBlankCharacter((String) body.get("username")) : "";
        String password = body.get("password") != null ? this.trimBlankCharacter((String) body.get("password")) : "";

        return UsernamePasswordAuthenticationToken.unauthenticated(username, password);
    }

    @Override
    protected boolean supports(String authType) {
        return AuthenticationType.PASSWORD.getAuthType().equals(authType);
    }
}
