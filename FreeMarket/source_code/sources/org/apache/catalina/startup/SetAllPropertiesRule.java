package org.apache.catalina.startup;

import org.apache.tomcat.util.digester.SetPropertiesRule;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/SetAllPropertiesRule.class */
public class SetAllPropertiesRule extends SetPropertiesRule {
    public SetAllPropertiesRule() {
    }

    public SetAllPropertiesRule(String[] exclude) {
        super(exclude);
    }
}
