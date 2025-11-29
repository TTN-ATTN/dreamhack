package org.apache.tomcat.util.descriptor.web;

import org.apache.tomcat.util.digester.Rule;

/* compiled from: WebRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/MappedNameRule.class */
final class MappedNameRule extends Rule {
    MappedNameRule() {
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void body(String namespace, String name, String text) throws Exception {
        ResourceBase resourceBase = (ResourceBase) this.digester.peek();
        resourceBase.setProperty("mappedName", text.trim());
        StringBuilder code = this.digester.getGeneratedCode();
        if (code != null) {
            code.append(System.lineSeparator());
            code.append(this.digester.toVariableName(resourceBase));
            code.append(".setProperty(\"mappedName\", \"").append(text.trim()).append("\");");
            code.append(System.lineSeparator());
        }
    }
}
