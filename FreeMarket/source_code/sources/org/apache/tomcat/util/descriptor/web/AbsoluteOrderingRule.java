package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/AbsoluteOrderingRule.class */
final class AbsoluteOrderingRule extends Rule {
    boolean isAbsoluteOrderingSet = false;
    private final boolean fragment;

    AbsoluteOrderingRule(boolean fragment) {
        this.fragment = fragment;
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        if (this.fragment) {
            this.digester.getLogger().warn(WebRuleSet.sm.getString("webRuleSet.absoluteOrdering"));
        }
        if (this.isAbsoluteOrderingSet) {
            throw new IllegalArgumentException(WebRuleSet.sm.getString("webRuleSet.absoluteOrderingCount"));
        }
        this.isAbsoluteOrderingSet = true;
        WebXml webXml = (WebXml) this.digester.peek();
        webXml.createAbsoluteOrdering();
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug(webXml.getClass().getName() + ".setAbsoluteOrdering()");
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(webXml)).append(".createAbsoluteOrdering();");
            code.append(System.lineSeparator());
        }
    }
}
