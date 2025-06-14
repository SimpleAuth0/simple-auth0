package io.github.simpleauth0.core.expression;

import lombok.Data;
import org.springframework.expression.Expression;

/**
 * @author: ReLive27
 * @date: 2025/4/28 20:53
 */
@Data
public class ExpressionAttribute {

    public static final ExpressionAttribute NULL_ATTRIBUTE = new ExpressionAttribute(null);

    private final Expression expression;

    public ExpressionAttribute(Expression expression) {
        this.expression = expression;
    }
}
