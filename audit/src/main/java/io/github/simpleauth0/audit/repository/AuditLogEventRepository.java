package io.github.simpleauth0.audit.repository;

import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:56
 */
public interface AuditLogEventRepository {

    Flux<Integer> save(List<AuditLogEvent> auditLogEvents);

}
