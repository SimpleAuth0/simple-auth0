package io.github.simpleauth0.audit;

import io.github.simpleauth0.audit.annotation.AuditLog;
import io.github.simpleauth0.audit.expression.AuditLogExpressionAttribute;
import io.github.simpleauth0.audit.handler.AuditLogStorageHandler;
import io.github.simpleauth0.core.utils.AnnotationUtils;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.Pointcut;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.support.AopUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.Ordered;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * @author: ReLive27
 * @date: 2025/4/25 22:54
 */
public class AuditLogMethodInterceptor implements Ordered, MethodInterceptor, PointcutAdvisor, AopInfrastructureBean {

    private final AuditLogExpressionAttributeRegistry registry = new AuditLogExpressionAttributeRegistry();

    private int order = 100;

    private final Pointcut pointcut;

    private AuditLogStorageHandler auditLogStorageHandler;

    public AuditLogMethodInterceptor(AuditLogStorageHandler<? extends AuditLogExpressionAttribute> auditLogStorageHandler) {
        Assert.notNull(auditLogStorageHandler, "auditLogStorageHandler cannot be null");
        this.auditLogStorageHandler = auditLogStorageHandler;
        this.pointcut = AuditLogMethodPointcuts.forAnnotations(AuditLog.class);
    }

    private ParameterNameDiscoverer discoverer = new LocalVariableTableParameterNameDiscoverer();


    @Override
    public Object invoke(MethodInvocation mi) throws Throwable {
        Object returnedObject = null;
        Boolean success = true;
        try {
            returnedObject = mi.proceed();
            return returnedObject;
        } catch (Exception e) {
            returnedObject = e.getMessage();
            success = false;
            throw e;
        } finally {
            AuditLogExpressionAttribute attribute = this.registry.getAttribute(mi);
            if (attribute != AuditLogExpressionAttribute.NULL_ATTRIBUTE) {
                String[] parameterNames = discoverer.getParameterNames(mi.getMethod());
                if (parameterNames != null && parameterNames.length > 0) {
                    attribute.setParams(parameterNames);
                    attribute.setArgs(mi.getArguments());
                }
                attribute.setResult(returnedObject);
                attribute.setSuccess(success);
                this.auditLogStorageHandler.handler(attribute);
            }
        }

    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    @Override
    public boolean isPerInstance() {
        return true;
    }

    @Override
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int ordering) {
        this.order = ordering;
    }

    public void setAuditLogStorageHandler(AuditLogStorageHandler auditLogStorageHandler) {
        this.auditLogStorageHandler = auditLogStorageHandler;
    }

    private final class AuditLogExpressionAttributeRegistry {

        private final ExpressionParser parser = new SpelExpressionParser();

        public final AuditLogExpressionAttribute getAttribute(MethodInvocation mi) {
            Method method = mi.getMethod();
            Object target = mi.getThis();
            Class<?> targetClass = (target != null) ? target.getClass() : null;
            return resolveAttribute(method, targetClass);
        }

        @NonNull
        AuditLogExpressionAttribute resolveAttribute(Method method, Class<?> targetClass) {
            Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            AuditLog auditLog = findPostFilterAnnotation(specificMethod);
            if (auditLog == null) {
                return AuditLogExpressionAttribute.NULL_ATTRIBUTE;
            }
            Expression operatorExpression = parser.parseExpression(auditLog.operator());
            return new AuditLogExpressionAttribute(operatorExpression, auditLog);
        }

        private AuditLog findPostFilterAnnotation(Method method) {
            AuditLog auditLog = AnnotationUtils.findUniqueAnnotation(method, AuditLog.class);
            return (auditLog != null) ? auditLog
                    : AnnotationUtils.findUniqueAnnotation(method.getDeclaringClass(), AuditLog.class);
        }

    }

}


