package io.github.simpleauth0.audit.handler;


import io.github.simpleauth0.audit.expression.AuditLogExpressionAttribute;

/**
 * @author: ReLive27
 * @date: 2025/5/23 22:06
 */
public interface AuditLogStorageHandler<T extends AuditLogExpressionAttribute> {

    void handler(T expressionAttribute);
}
