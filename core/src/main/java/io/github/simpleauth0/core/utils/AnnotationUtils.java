package io.github.simpleauth0.core.utils;

import org.springframework.core.annotation.AnnotationConfigurationException;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author: ReLive27
 * @date: 2025/4/28 21:13
 */
public class AnnotationUtils {

    public static <A extends Annotation> A findUniqueAnnotation(Method method, Class<A> annotationType) {
        MergedAnnotations mergedAnnotations = MergedAnnotations.from(method,
                MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
        if (hasDuplicate(mergedAnnotations, annotationType)) {
            throw new AnnotationConfigurationException("Found more than one annotation of type " + annotationType
                    + " attributed to " + method
                    + " Please remove the duplicate annotations and publish a bean to handle your authorization logic.");
        }
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(method, annotationType);
    }


   public static <A extends Annotation> A findUniqueAnnotation(Class<?> type, Class<A> annotationType) {
        MergedAnnotations mergedAnnotations = MergedAnnotations.from(type,
                MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
        if (hasDuplicate(mergedAnnotations, annotationType)) {
            throw new AnnotationConfigurationException("Found more than one annotation of type " + annotationType
                    + " attributed to " + type
                    + " Please remove the duplicate annotations and publish a bean to handle your authorization logic.");
        }
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(type, annotationType);
    }

    private static <A extends Annotation> boolean hasDuplicate(MergedAnnotations mergedAnnotations,
                                                               Class<A> annotationType) {
        boolean alreadyFound = false;
        for (MergedAnnotation<Annotation> mergedAnnotation : mergedAnnotations) {
            if (mergedAnnotation.getType() == annotationType) {
                if (alreadyFound) {
                    return true;
                }
                alreadyFound = true;
            }
        }
        return false;
    }

}
