package org.apache.naming;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/naming/ResourceEnvRef.class */
public class ResourceEnvRef extends AbstractRef {
    private static final long serialVersionUID = 1;
    public static final String DEFAULT_FACTORY = "org.apache.naming.factory.ResourceEnvFactory";

    public ResourceEnvRef(String resourceType) {
        super(resourceType);
    }

    @Override // org.apache.naming.AbstractRef
    protected String getDefaultFactoryClassName() {
        return "org.apache.naming.factory.ResourceEnvFactory";
    }
}
