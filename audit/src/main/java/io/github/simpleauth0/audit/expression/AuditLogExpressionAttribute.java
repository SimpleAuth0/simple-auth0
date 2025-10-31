package io.github.simpleauth0.audit.expression;

import io.github.simpleauth0.audit.annotation.AuditLog;
import lombok.Data;
import org.springframework.expression.Expression;

/**
 * @author: ReLive27
 * @date: 2025/4/28 20:53
 */
@Data
public class AuditLogExpressionAttribute {

    public static final AuditLogExpressionAttribute NULL_ATTRIBUTE = new AuditLogExpressionAttribute(null, null);

    private final Expression expression;

    private Object[] args;

    private String[] params;

    private final AuditLog auditLog;

    private Object result;

    private Boolean success;

    public AuditLogExpressionAttribute(Expression expression, AuditLog auditLog) {
        this.expression = expression;
        this.auditLog = auditLog;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getResult() {
        return result;
    }

    public Boolean getSuccess() {
        return success;
    }

    public AuditLog getAuditLog() {
        return auditLog;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}
