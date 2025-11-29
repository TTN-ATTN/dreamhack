package org.springframework.boot.autoconfigure.freemarker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerTemplateAvailabilityProvider.class */
public class FreeMarkerTemplateAvailabilityProvider extends PathBasedTemplateAvailabilityProvider {
    public FreeMarkerTemplateAvailabilityProvider() {
        super("freemarker.template.Configuration", FreeMarkerTemplateAvailabilityProperties.class, "spring.freemarker");
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/freemarker/FreeMarkerTemplateAvailabilityProvider$FreeMarkerTemplateAvailabilityProperties.class */
    protected static final class FreeMarkerTemplateAvailabilityProperties extends PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties {
        private List<String> templateLoaderPath;

        FreeMarkerTemplateAvailabilityProperties() {
            super("", FreeMarkerProperties.DEFAULT_SUFFIX);
            this.templateLoaderPath = new ArrayList(Arrays.asList("classpath:/templates/"));
        }

        @Override // org.springframework.boot.autoconfigure.template.PathBasedTemplateAvailabilityProvider.TemplateAvailabilityProperties
        protected List<String> getLoaderPath() {
            return this.templateLoaderPath;
        }

        public List<String> getTemplateLoaderPath() {
            return this.templateLoaderPath;
        }

        public void setTemplateLoaderPath(List<String> templateLoaderPath) {
            this.templateLoaderPath = templateLoaderPath;
        }
    }
}
