package com.acsc2025.config;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.TemplateExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/config/FreemarkerConfig.class */
public class FreemarkerConfig {
    private static freemarker.template.Configuration freemarkerConfiguration;

    @Bean
    public freemarker.template.Configuration freemarkerConfiguration() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_33);
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setDefaultEncoding("UTF-8");
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setTemplateLoader(new ClassTemplateLoader(getClass(), "/templates"));
        cfg.setAPIBuiltinEnabled(false);
        cfg.setClassicCompatible(false);
        cfg.setFallbackOnNullLoopVariable(false);
        freemarkerConfiguration = cfg;
        return cfg;
    }

    public static freemarker.template.Configuration getConfiguration() {
        return freemarkerConfiguration;
    }
}
