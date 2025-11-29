package org.apache.tomcat.util.modeler.modules;

import java.util.List;
import javax.management.ObjectName;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/modeler/modules/ModelerSource.class */
public abstract class ModelerSource {
    protected static final StringManager sm = StringManager.getManager((Class<?>) Registry.class);
    protected Object source;

    public abstract List<ObjectName> loadDescriptors(Registry registry, String str, Object obj) throws Exception;
}
