package io.github.simpleauth0.core.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * @author: ReLive27
 * @date: 2025/7/6 22:52
 */
public class JwtConfiguredCondition extends SpringBootCondition {

    private static final String PROPERTY_KEY = "simpleauth0.token.jwt.keySource";


    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("JwtKeySourceConfigured");
        Environment environment = context.getEnvironment();
        String keySource = environment.getProperty(PROPERTY_KEY);

        if (!StringUtils.hasText(keySource)) {
            return ConditionOutcome.noMatch(message.didNotFind(PROPERTY_KEY).atAll());
        }

        return ConditionOutcome.match(message.foundExactly(PROPERTY_KEY + "=" + keySource.trim()));
    }
}
