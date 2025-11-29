package freemarker.template.utility;

import freemarker.log.Logger;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/SecurityUtilities.class */
public class SecurityUtilities {
    private static final Logger LOG = Logger.getLogger("freemarker.security");

    private SecurityUtilities() {
    }

    public static String getSystemProperty(final String key) {
        return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: freemarker.template.utility.SecurityUtilities.1
            @Override // java.security.PrivilegedAction
            public Object run() {
                return System.getProperty(key);
            }
        });
    }

    public static String getSystemProperty(final String key, final String defValue) {
        try {
            return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: freemarker.template.utility.SecurityUtilities.2
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return System.getProperty(key, defValue);
                }
            });
        } catch (AccessControlException e) {
            LOG.warn("Insufficient permissions to read system property " + StringUtil.jQuoteNoXSS(key) + ", using default value " + StringUtil.jQuoteNoXSS(defValue));
            return defValue;
        }
    }

    public static Integer getSystemProperty(final String key, final int defValue) {
        try {
            return (Integer) AccessController.doPrivileged(new PrivilegedAction() { // from class: freemarker.template.utility.SecurityUtilities.3
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return Integer.getInteger(key, defValue);
                }
            });
        } catch (AccessControlException e) {
            LOG.warn("Insufficient permissions to read system property " + StringUtil.jQuote(key) + ", using default value " + defValue);
            return Integer.valueOf(defValue);
        }
    }
}
