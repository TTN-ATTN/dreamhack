package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/BeanInfoFactory.class */
public interface BeanInfoFactory {
    @Nullable
    BeanInfo getBeanInfo(Class<?> cls) throws IntrospectionException;
}
