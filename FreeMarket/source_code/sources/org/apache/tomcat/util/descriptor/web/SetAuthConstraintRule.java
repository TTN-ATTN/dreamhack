package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: WebRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/SetAuthConstraintRule.class */
final class SetAuthConstraintRule extends Rule {
    SetAuthConstraintRule() {
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        SecurityConstraint securityConstraint = (SecurityConstraint) this.digester.peek();
        securityConstraint.setAuthConstraint(true);
        if (this.digester.getLogger().isDebugEnabled()) {
            this.digester.getLogger().debug("Calling SecurityConstraint.setAuthConstraint(true)");
        }
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(securityConstraint)).append(".setAuthConstraint(true);");
            code.append(System.lineSeparator());
        }
    }
}
