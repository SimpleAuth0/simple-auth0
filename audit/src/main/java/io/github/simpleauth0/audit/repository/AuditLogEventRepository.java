package io.github.simpleauth0.audit.repository;

import reactor.core.publisher.Mono;

/**
 * @author: ReLive27
 * @date: 2025/6/30 22:56
 */
public interface AuditLogEventRepository {

    Mono<Void> save(AuditLogEvent event);

}
