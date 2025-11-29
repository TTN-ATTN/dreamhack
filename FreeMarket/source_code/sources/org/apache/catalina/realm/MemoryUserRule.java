package org.apache.catalina.realm;

import org.apache.tomcat.util.digester.Rule;
import org.xml.sax.Attributes;

/* compiled from: MemoryRuleSet.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/realm/MemoryUserRule.class */
final class MemoryUserRule extends Rule {
    MemoryUserRule() {
    }

    @Override // org.apache.tomcat.util.digester.Rule
    public void begin(String namespace, String name, Attributes attributes) throws Exception {
        String username = attributes.getValue("username");
        if (username == null) {
            username = attributes.getValue("name");
        }
        String password = attributes.getValue("password");
        String roles = attributes.getValue("roles");
        MemoryRealm realm = (MemoryRealm) this.digester.peek(this.digester.getCount() - 1);
        realm.addUser(username, password, roles);
    }
}
