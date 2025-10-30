package io.github.simpleauth0.audit.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/4/26 22:29
 */
public class AuditLogMethodSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importMetadata) {
        if (!importMetadata.hasAnnotation(EnableAuditLog.class.getName())) {
            return new String[0];
        }
        EnableAuditLog annotation = importMetadata.getAnnotations().get(EnableAuditLog.class).synthesize();
        List<String> imports = new ArrayList<>();
        if (annotation.method()) {
            imports.add(AuditLogMethodConfiguration.class.getName());
        }

        return imports.toArray(new String[0]);
    }

}
