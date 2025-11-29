package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.SetPropertiesRule;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/SetContextPropertiesRule.class */
public class SetContextPropertiesRule extends SetPropertiesRule {
    public SetContextPropertiesRule() {
        super(new String[]{"path", "docBase"});
    }
}
