package org.springframework.beans;

import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/BeanMetadataElement.class */
public interface BeanMetadataElement {
    @Nullable
    default Object getSource() {
        return null;
    }
}
