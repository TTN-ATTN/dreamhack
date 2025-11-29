package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateConfigurationFactory.class */
public abstract class TemplateConfigurationFactory {
    private Configuration cfg;

    public abstract TemplateConfiguration get(String str, Object obj) throws IOException, TemplateConfigurationFactoryException;

    protected abstract void setConfigurationOfChildren(Configuration configuration);

    public final void setConfiguration(Configuration cfg) {
        if (this.cfg != null) {
            if (cfg != this.cfg) {
                throw new IllegalStateException("The TemplateConfigurationFactory is already bound to another Configuration");
            }
        } else {
            this.cfg = cfg;
            setConfigurationOfChildren(cfg);
        }
    }

    public Configuration getConfiguration() {
        return this.cfg;
    }
}
