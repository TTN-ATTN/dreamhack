package org.springframework.beans.factory.support;

import java.security.AccessControlContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/SecurityContextProvider.class */
public interface SecurityContextProvider {
    AccessControlContext getAccessControlContext();
}
