package io.github.simpleauth0.repository.internal;

import io.github.simpleauth0.audit.repository.AuditLogEvent;
import io.github.simpleauth0.audit.repository.AuditLogEventRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/11/3 23:06
 */
@Component
public class DefaultAuditLogEventRepository implements AuditLogEventRepository {

    @Override
    public Flux<Integer> save(List<AuditLogEvent> auditLogEvents) {
        return null;
    }
}
