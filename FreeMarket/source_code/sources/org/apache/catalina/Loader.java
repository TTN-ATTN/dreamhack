package org.apache.catalina;

import java.beans.PropertyChangeListener;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/Loader.class */
public interface Loader {
    void backgroundProcess();

    ClassLoader getClassLoader();

    Context getContext();

    void setContext(Context context);

    boolean getDelegate();

    void setDelegate(boolean z);

    @Deprecated
    boolean getReloadable();

    @Deprecated
    void setReloadable(boolean z);

    void addPropertyChangeListener(PropertyChangeListener propertyChangeListener);

    boolean modified();

    void removePropertyChangeListener(PropertyChangeListener propertyChangeListener);
}
