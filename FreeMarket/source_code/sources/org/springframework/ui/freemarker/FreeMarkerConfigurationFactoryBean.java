package org.springframework.ui.freemarker;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/ui/freemarker/FreeMarkerConfigurationFactoryBean.class */
public class FreeMarkerConfigurationFactoryBean extends FreeMarkerConfigurationFactory implements FactoryBean<Configuration>, InitializingBean, ResourceLoaderAware {

    @Nullable
    private Configuration configuration;

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws TemplateException, IOException {
        this.configuration = createConfiguration();
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Configuration getObject() {
        return this.configuration;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<? extends Configuration> getObjectType() {
        return Configuration.class;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
