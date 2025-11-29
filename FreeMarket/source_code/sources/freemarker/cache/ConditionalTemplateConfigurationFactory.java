package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/ConditionalTemplateConfigurationFactory.class */
public class ConditionalTemplateConfigurationFactory extends TemplateConfigurationFactory {
    private final TemplateSourceMatcher matcher;
    private final TemplateConfiguration templateConfiguration;
    private final TemplateConfigurationFactory templateConfigurationFactory;

    public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfigurationFactory templateConfigurationFactory) {
        this.matcher = matcher;
        this.templateConfiguration = null;
        this.templateConfigurationFactory = templateConfigurationFactory;
    }

    public ConditionalTemplateConfigurationFactory(TemplateSourceMatcher matcher, TemplateConfiguration templateConfiguration) {
        this.matcher = matcher;
        this.templateConfiguration = templateConfiguration;
        this.templateConfigurationFactory = null;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        if (this.matcher.matches(sourceName, templateSource)) {
            if (this.templateConfigurationFactory != null) {
                return this.templateConfigurationFactory.get(sourceName, templateSource);
            }
            return this.templateConfiguration;
        }
        return null;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    protected void setConfigurationOfChildren(Configuration cfg) {
        if (this.templateConfiguration != null) {
            this.templateConfiguration.setParentConfiguration(cfg);
        }
        if (this.templateConfigurationFactory != null) {
            this.templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}
