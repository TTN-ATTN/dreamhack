package org.apache.tomcat.websocket;

import java.util.List;
import javax.websocket.Extension;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:org/apache/tomcat/websocket/TransformationFactory.class */
public class TransformationFactory {
    private static final StringManager sm = StringManager.getManager((Class<?>) TransformationFactory.class);
    private static final TransformationFactory factory = new TransformationFactory();

    private TransformationFactory() {
    }

    public static TransformationFactory getInstance() {
        return factory;
    }

    public Transformation create(String name, List<List<Extension.Parameter>> preferences, boolean isServer) {
        if (PerMessageDeflate.NAME.equals(name)) {
            return PerMessageDeflate.negotiate(preferences, isServer);
        }
        if (Constants.ALLOW_UNSUPPORTED_EXTENSIONS) {
            return null;
        }
        throw new IllegalArgumentException(sm.getString("transformerFactory.unsupportedExtension", name));
    }
}
