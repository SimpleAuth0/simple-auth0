package io.github.simpleauth0.audit.annotation;

import java.lang.annotation.*;

/**
 * @author: ReLive27
 * @date: 2025/4/26 22:46
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditLog {
    /**
     * 操作名称，比如 "用户登录"、"创建角色"，支持 SpEL 表达式。
     */
    String actionName();

    /**
     * 资源类型，比如 "用户"、"角色"、"菜单"、"接口"。
     */
    String resourceType();

    /**
     * 是否记录请求参数。
     */
    boolean recordRequest() default false;

    /**
     * 是否记录响应结果。
     */
    boolean recordResponse() default true;

    /**
     * 当前操作用户，支持 SpEL 表达式，比如 "#{#user.username}" 或 "#{@securityContext.getCurrentUser()}"。
     */
    String operator() default "";
}
