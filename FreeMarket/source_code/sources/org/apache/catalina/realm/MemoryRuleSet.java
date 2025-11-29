package org.apache.catalina.realm;

import ch.qos.logback.classic.ClassicConstants;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.RuleSet;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/realm/MemoryRuleSet.class */
public class MemoryRuleSet implements RuleSet {
    protected final String prefix;

    public MemoryRuleSet() {
        this("tomcat-users/");
    }

    public MemoryRuleSet(String prefix) {
        this.prefix = prefix;
    }

    @Override // org.apache.tomcat.util.digester.RuleSet
    public void addRuleInstances(Digester digester) {
        digester.addRule(this.prefix + ClassicConstants.USER_MDC_KEY, new MemoryUserRule());
    }
}
