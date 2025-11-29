package org.apache.coyote.http11;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.net.AprEndpoint;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/coyote/http11/Http11AprProtocol.class */
public class Http11AprProtocol extends AbstractHttp11Protocol<Long> {
    private static final Log log = LogFactory.getLog((Class<?>) Http11AprProtocol.class);

    public Http11AprProtocol() {
        this(new AprEndpoint());
    }

    public Http11AprProtocol(AprEndpoint endpoint) {
        super(endpoint);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected Log getLog() {
        return log;
    }

    @Override // org.apache.coyote.AbstractProtocol, org.apache.coyote.ProtocolHandler
    public boolean isAprRequired() {
        return true;
    }

    public int getPollTime() {
        return ((AprEndpoint) getEndpoint()).getPollTime();
    }

    public void setPollTime(int pollTime) {
        ((AprEndpoint) getEndpoint()).setPollTime(pollTime);
    }

    public int getSendfileSize() {
        return ((AprEndpoint) getEndpoint()).getSendfileSize();
    }

    public void setSendfileSize(int sendfileSize) {
        ((AprEndpoint) getEndpoint()).setSendfileSize(sendfileSize);
    }

    public boolean getDeferAccept() {
        return ((AprEndpoint) getEndpoint()).getDeferAccept();
    }

    public void setDeferAccept(boolean deferAccept) {
        ((AprEndpoint) getEndpoint()).setDeferAccept(deferAccept);
    }

    @Override // org.apache.coyote.AbstractProtocol
    protected String getNamePrefix() {
        if (isSSLEnabled()) {
            return "https-openssl-apr";
        }
        return "http-apr";
    }
}
