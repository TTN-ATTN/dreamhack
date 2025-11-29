package freemarker.ext.dom;

import freemarker.core.Environment;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/DomStringUtil.class */
final class DomStringUtil {
    private DomStringUtil() {
    }

    static boolean isXMLNameLike(String name) {
        return isXMLNameLike(name, 0);
    }

    static boolean isXMLNameLike(String name, int firstCharIdx) {
        int ln = name.length();
        for (int i = firstCharIdx; i < ln; i++) {
            char c = name.charAt(i);
            if (i == firstCharIdx && (c == '-' || c == '.' || Character.isDigit(c))) {
                return false;
            }
            if (!Character.isLetterOrDigit(c) && c != '_' && c != '-' && c != '.') {
                if (c == ':') {
                    if (i + 1 < ln && name.charAt(i + 1) == ':') {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }
        return true;
    }

    static boolean matchesName(String qname, String nodeName, String nsURI, Environment env) {
        String defaultNS = env.getDefaultNS();
        if (defaultNS != null && defaultNS.equals(nsURI)) {
            return qname.equals(nodeName) || qname.equals(new StringBuilder().append("D:").append(nodeName).toString());
        }
        if ("".equals(nsURI)) {
            if (defaultNS != null) {
                return qname.equals("N:" + nodeName);
            }
            return qname.equals(nodeName) || qname.equals(new StringBuilder().append("N:").append(nodeName).toString());
        }
        String prefix = env.getPrefixForNamespace(nsURI);
        if (prefix == null) {
            return false;
        }
        return qname.equals(prefix + ":" + nodeName);
    }
}
