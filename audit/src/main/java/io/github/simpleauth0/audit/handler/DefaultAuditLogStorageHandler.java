package io.github.simpleauth0.audit.handler;

import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import io.github.simpleauth0.audit.annotation.AuditLog;
import io.github.simpleauth0.audit.expression.AuditLogExpressionAttribute;
import io.github.simpleauth0.audit.repository.AuditLogEvent;
import io.github.simpleauth0.audit.repository.AuditLogEventRepository;
import io.github.simpleauth0.audit.repository.Field;
import io.github.simpleauth0.core.utils.IPUtils;
import io.github.simpleauth0.core.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import reactor.core.publisher.Sinks;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.*;

/**
 * @author: ReLive27
 * @date: 2025/5/24 21:42
 */
@Slf4j
public class DefaultAuditLogStorageHandler implements AuditLogStorageHandler<AuditLogExpressionAttribute> {

    private static final Sinks.Many<AuditLogEvent> sink = Sinks.many()
            .unicast()
            .onBackpressureBuffer();

    private final AuditLogEventRepository auditLogRepository;

    public DefaultAuditLogStorageHandler(AuditLogEventRepository auditLogRepository) {
        Assert.notNull(auditLogRepository, "auditLogRepository can not be null");
        this.auditLogRepository = auditLogRepository;
        // 异步订阅处理，批量保存
        sink.asFlux()
                .bufferTimeout(10, Duration.ofSeconds(3))
                .flatMap(this.auditLogRepository::save) // 异步保存
                .doOnNext(batch -> log.debug("已保存日志数量：" + batch))
                .doOnError(e -> log.error("日志流异常：" + e.getMessage()))
                .onErrorContinue((e, o) -> log.error("单条日志保存失败: " + o))
                .subscribe();
    }

    @Override
    public void handler(AuditLogExpressionAttribute expressionAttribute) {
        AuditLogEvent auditEvent = new AuditLogEvent();
        AuditLog auditLog = expressionAttribute.getAuditLog();
        auditEvent.setEventType(auditLog.resourceType());
        auditEvent.setEventDetail(auditLog.actionName());

        EvaluationContext context = new StandardEvaluationContext();
        Map<String, Object> requestParams = new HashMap<>();
        String[] params = expressionAttribute.getParams();
        if (params != null && params.length > 0) {
            Object[] args = expressionAttribute.getArgs();
            for (int i = 0; i < params.length; i++) {
                context.setVariable(params[i], args[i]);
                requestParams.put(params[i], args[i]);
            }
        }
        String username = expressionAttribute.getExpression().getValue(context, String.class);
        auditEvent.setUsername(username);

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String ipAddress = IPUtils.getIpAddress(request);
        auditEvent.setIpAddress(ipAddress);
        String uaString = request.getHeader("User-Agent");
        if (StringUtils.hasText(uaString)) {
            UserAgent userAgent = UserAgent.parseUserAgentString(uaString);
            OperatingSystem os = userAgent.getOperatingSystem();

            auditEvent.setDeviceType(os.getDeviceType().getName());
            auditEvent.setDeviceOs(os.getName());
        }
        auditEvent.setSuccess(expressionAttribute.getSuccess());
        List<Field> fieldList = new ArrayList<>();
        fieldList.add(new Field("http_method", request.getMethod(), null));
        fieldList.add(new Field("request_uri", request.getServletPath(), null));
        fieldList.add(new Field("request_param", JsonUtils.toJson(requestParams), null));
        fieldList.add(new Field("response_body", expressionAttribute.getResult() != null ? JsonUtils.toJson(expressionAttribute.getResult()) : "", null));
        auditEvent.setFields(fieldList);

        save(auditEvent);
    }

    public void save(AuditLogEvent logEvent) {
        sink.emitNext(logEvent, (signalType, emitResult) -> {
            if (emitResult == Sinks.EmitResult.FAIL_NON_SERIALIZED) {
                // Reactor 自身会重试；返回 true 表示可重试
                return true;
            }
            if (emitResult.isFailure()) {
                log.warn("日志发送失败: " + emitResult);
            }
            // 返回 false 表示不重试
            return false;
        });
    }
}
