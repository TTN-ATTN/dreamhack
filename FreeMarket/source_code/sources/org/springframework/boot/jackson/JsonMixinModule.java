package org.springframework.boot.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.Collection;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jackson/JsonMixinModule.class */
public class JsonMixinModule extends SimpleModule implements InitializingBean {
    private final ApplicationContext context;
    private final Collection<String> basePackages;

    public JsonMixinModule(ApplicationContext context, Collection<String> basePackages) {
        Assert.notNull(context, "Context must not be null");
        this.context = context;
        this.basePackages = basePackages;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws Exception {
        if (ObjectUtils.isEmpty(this.basePackages)) {
            return;
        }
        JsonMixinComponentScanner scanner = new JsonMixinComponentScanner();
        scanner.setEnvironment(this.context.getEnvironment());
        scanner.setResourceLoader(this.context);
        for (String basePackage : this.basePackages) {
            if (StringUtils.hasText(basePackage)) {
                for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
                    addJsonMixin(ClassUtils.forName(candidate.getBeanClassName(), this.context.getClassLoader()));
                }
            }
        }
    }

    private void addJsonMixin(Class<?> mixinClass) throws NoSuchElementException {
        MergedAnnotation<JsonMixin> annotation = MergedAnnotations.from(mixinClass, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(JsonMixin.class);
        for (Class<?> targetType : annotation.getClassArray("type")) {
            setMixInAnnotation(targetType, mixinClass);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/jackson/JsonMixinModule$JsonMixinComponentScanner.class */
    static class JsonMixinComponentScanner extends ClassPathScanningCandidateComponentProvider {
        JsonMixinComponentScanner() {
            addIncludeFilter(new AnnotationTypeFilter(JsonMixin.class));
        }

        @Override // org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
        protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
            return true;
        }
    }
}
