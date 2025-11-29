package org.apache.tomcat.util.modeler;

import javax.management.MBeanParameterInfo;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/modeler/ParameterInfo.class */
public class ParameterInfo extends FeatureInfo {
    private static final long serialVersionUID = 2222796006787664020L;

    public MBeanParameterInfo createParameterInfo() {
        if (this.info == null) {
            this.info = new MBeanParameterInfo(getName(), getType(), getDescription());
        }
        return this.info;
    }
}
