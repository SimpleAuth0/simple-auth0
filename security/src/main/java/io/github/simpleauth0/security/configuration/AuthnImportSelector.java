package io.github.simpleauth0.security.configuration;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: ReLive27
 * @date: 2025/5/25 21:16
 */
public class AuthnImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importMetadata) {
        if (!importMetadata.hasAnnotation(EnableAuthentication.class.getName())) {
            return new String[0];
        }
        EnableAuthentication annotation = importMetadata.getAnnotations().get(EnableAuthentication.class).synthesize();
        List<String> imports = new ArrayList<>();
        imports.add(AuthnSecurityConfiguration.class.getName());
        return imports.toArray(new String[0]);
    }
}
