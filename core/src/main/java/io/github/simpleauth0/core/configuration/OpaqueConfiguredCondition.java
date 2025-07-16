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
public class OpaqueConfiguredCondition extends SpringBootCondition {

    private static final String PROPERTY_KEY = "simpleauth0.token.opaque.format";

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        ConditionMessage.Builder message = ConditionMessage.forCondition("OpaqueTokenFormatConfigured");

        Environment environment = context.getEnvironment();
        String format = environment.getProperty(PROPERTY_KEY);

        if (!StringUtils.hasText(format)) {
            return ConditionOutcome.noMatch(message.didNotFind(PROPERTY_KEY).atAll());
        }

        return ConditionOutcome.match(message.foundExactly(PROPERTY_KEY + "=" + format.trim()));
    }
}
