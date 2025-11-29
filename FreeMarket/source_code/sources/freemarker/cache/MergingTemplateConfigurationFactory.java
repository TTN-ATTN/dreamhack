package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MergingTemplateConfigurationFactory.class */
public class MergingTemplateConfigurationFactory extends TemplateConfigurationFactory {
    private final TemplateConfigurationFactory[] templateConfigurationFactories;

    public MergingTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
        this.templateConfigurationFactories = templateConfigurationFactories;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        TemplateConfiguration mergedTC = null;
        TemplateConfiguration resultTC = null;
        for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
            TemplateConfiguration tc = tcf.get(sourceName, templateSource);
            if (tc != null) {
                if (resultTC == null) {
                    resultTC = tc;
                } else {
                    if (mergedTC == null) {
                        Configuration cfg = getConfiguration();
                        if (cfg == null) {
                            throw new IllegalStateException("The TemplateConfigurationFactory wasn't associated to a Configuration yet.");
                        }
                        mergedTC = new TemplateConfiguration();
                        mergedTC.setParentConfiguration(cfg);
                        mergedTC.merge(resultTC);
                        resultTC = mergedTC;
                    }
                    mergedTC.merge(tc);
                }
            }
        }
        return resultTC;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    protected void setConfigurationOfChildren(Configuration cfg) {
        for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories) {
            templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}
