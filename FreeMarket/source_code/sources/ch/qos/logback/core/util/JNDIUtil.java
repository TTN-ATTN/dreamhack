package ch.qos.logback.core.util;

import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/util/JNDIUtil.class */
public class JNDIUtil {
    static final String RESTRICTION_MSG = "JNDI name must start with java: but was ";

    public static Context getInitialContext() throws NamingException {
        return new InitialContext();
    }

    public static Context getInitialContext(Hashtable<?, ?> props) throws NamingException {
        return new InitialContext(props);
    }

    public static Object lookupObject(Context ctx, String name) throws NamingException {
        if (ctx == null || OptionHelper.isEmpty(name)) {
            return null;
        }
        jndiNameSecurityCheck(name);
        Object lookup = ctx.lookup(name);
        return lookup;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.NamingException */
    private static void jndiNameSecurityCheck(String name) throws NamingException {
        if (!name.startsWith("java:")) {
            throw new NamingException(RESTRICTION_MSG + name);
        }
    }

    public static String lookupString(Context ctx, String name) throws NamingException {
        Object lookup = lookupObject(ctx, name);
        return (String) lookup;
    }
}
