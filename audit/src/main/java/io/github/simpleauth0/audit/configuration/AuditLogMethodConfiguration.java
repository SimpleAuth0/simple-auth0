package io.github.simpleauth0.audit.configuration;

import io.github.simpleauth0.audit.AuditLogMethodInterceptor;
import io.github.simpleauth0.audit.handler.AuditLogStorageHandler;
import io.github.simpleauth0.audit.handler.DefaultAuditLogStorageHandler;
import io.github.simpleauth0.audit.repository.AuditLogEventRepository;
import org.springframework.aop.Advisor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: ReLive27
 * @date: 2025/4/26 22:32
 */
@Configuration(proxyBeanMethods = false)
public class AuditLogMethodConfiguration {

    @Bean("auditLogAdvisor")
    @ConditionalOnMissingBean(name = "auditLogAdvisor")
    Advisor auditLogAdvisor(AuditLogStorageHandler auditLogStorageHandler) {
        return new AuditLogMethodInterceptor(auditLogStorageHandler);
    }

    @Bean
    @ConditionalOnMissingBean(AuditLogStorageHandler.class)
    AuditLogStorageHandler auditLogStorageHandler(AuditLogEventRepository auditLogRepository) {
        return new DefaultAuditLogStorageHandler(auditLogRepository);
    }
}
