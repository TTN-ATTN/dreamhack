package freemarker.cache;

import freemarker.core.TemplateConfiguration;
import freemarker.template.Configuration;
import freemarker.template.utility.StringUtil;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/FirstMatchTemplateConfigurationFactory.class */
public class FirstMatchTemplateConfigurationFactory extends TemplateConfigurationFactory {
    private final TemplateConfigurationFactory[] templateConfigurationFactories;
    private boolean allowNoMatch;
    private String noMatchErrorDetails;

    public FirstMatchTemplateConfigurationFactory(TemplateConfigurationFactory... templateConfigurationFactories) {
        this.templateConfigurationFactories = templateConfigurationFactories;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    public TemplateConfiguration get(String sourceName, Object templateSource) throws IOException, TemplateConfigurationFactoryException {
        for (TemplateConfigurationFactory tcf : this.templateConfigurationFactories) {
            TemplateConfiguration tc = tcf.get(sourceName, templateSource);
            if (tc != null) {
                return tc;
            }
        }
        if (!this.allowNoMatch) {
            throw new TemplateConfigurationFactoryException(FirstMatchTemplateConfigurationFactory.class.getSimpleName() + " has found no matching choice for source name " + StringUtil.jQuote(sourceName) + ". " + (this.noMatchErrorDetails != null ? "Error details: " + this.noMatchErrorDetails : "(Set the noMatchErrorDetails property of the factory bean to give a more specific error message. Set allowNoMatch to true if this shouldn't be an error.)"));
        }
        return null;
    }

    public boolean getAllowNoMatch() {
        return this.allowNoMatch;
    }

    public void setAllowNoMatch(boolean allowNoMatch) {
        this.allowNoMatch = allowNoMatch;
    }

    public String getNoMatchErrorDetails() {
        return this.noMatchErrorDetails;
    }

    public void setNoMatchErrorDetails(String noMatchErrorDetails) {
        this.noMatchErrorDetails = noMatchErrorDetails;
    }

    public FirstMatchTemplateConfigurationFactory allowNoMatch(boolean allow) {
        setAllowNoMatch(allow);
        return this;
    }

    public FirstMatchTemplateConfigurationFactory noMatchErrorDetails(String message) {
        setNoMatchErrorDetails(message);
        return this;
    }

    @Override // freemarker.cache.TemplateConfigurationFactory
    protected void setConfigurationOfChildren(Configuration cfg) {
        for (TemplateConfigurationFactory templateConfigurationFactory : this.templateConfigurationFactories) {
            templateConfigurationFactory.setConfiguration(cfg);
        }
    }
}
