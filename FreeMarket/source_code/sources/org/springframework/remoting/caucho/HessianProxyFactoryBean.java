package org.springframework.remoting.caucho;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.RemoteLookupFailureException;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/caucho/HessianProxyFactoryBean.class */
public class HessianProxyFactoryBean extends HessianClientInterceptor implements FactoryBean<Object> {

    @Nullable
    private Object serviceProxy;

    @Override // org.springframework.remoting.caucho.HessianClientInterceptor, org.springframework.remoting.support.UrlBasedRemoteAccessor, org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws RemoteLookupFailureException {
        super.afterPropertiesSet();
        this.serviceProxy = new ProxyFactory(getServiceInterface(), this).getProxy(getBeanClassLoader());
    }

    @Override // org.springframework.beans.factory.FactoryBean
    @Nullable
    public Object getObject() {
        return this.serviceProxy;
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public Class<?> getObjectType() {
        return getServiceInterface();
    }

    @Override // org.springframework.beans.factory.FactoryBean
    public boolean isSingleton() {
        return true;
    }
}
