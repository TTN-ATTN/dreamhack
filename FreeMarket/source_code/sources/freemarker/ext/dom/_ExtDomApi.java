package freemarker.ext.dom;

import freemarker.core.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/_ExtDomApi.class */
public final class _ExtDomApi {
    private _ExtDomApi() {
    }

    public static boolean isXMLNameLike(String name) {
        return DomStringUtil.isXMLNameLike(name);
    }

    public static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
        return DomStringUtil.matchesName(qname, nodeName, nsURI, env);
    }
}
