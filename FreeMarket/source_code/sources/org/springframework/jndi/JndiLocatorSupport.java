package org.springframework.jndi;

import javax.naming.NamingException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/jndi/JndiLocatorSupport.class */
public abstract class JndiLocatorSupport extends JndiAccessor {
    public static final String CONTAINER_PREFIX = "java:comp/env/";
    private boolean resourceRef = false;

    public void setResourceRef(boolean resourceRef) {
        this.resourceRef = resourceRef;
    }

    public boolean isResourceRef() {
        return this.resourceRef;
    }

    protected Object lookup(String jndiName) throws NamingException {
        return lookup(jndiName, null);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.NamingException */
    protected <T> T lookup(String str, @Nullable Class<T> cls) throws NamingException {
        Object objLookup;
        Assert.notNull(str, "'jndiName' must not be null");
        String strConvertJndiName = convertJndiName(str);
        try {
            objLookup = getJndiTemplate().lookup(strConvertJndiName, cls);
        } catch (NamingException e) {
            if (!strConvertJndiName.equals(str)) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Converted JNDI name [" + strConvertJndiName + "] not found - trying original name [" + str + "]. " + e);
                }
                objLookup = getJndiTemplate().lookup(str, cls);
            } else {
                throw e;
            }
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Located object with JNDI name [" + strConvertJndiName + "]");
        }
        return (T) objLookup;
    }

    protected String convertJndiName(String jndiName) {
        if (isResourceRef() && !jndiName.startsWith(CONTAINER_PREFIX) && jndiName.indexOf(58) == -1) {
            jndiName = CONTAINER_PREFIX + jndiName;
        }
        return jndiName;
    }
}
