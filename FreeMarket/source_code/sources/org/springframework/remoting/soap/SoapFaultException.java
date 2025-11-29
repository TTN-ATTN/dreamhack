package org.springframework.remoting.soap;

import javax.xml.namespace.QName;
import org.springframework.remoting.RemoteInvocationFailureException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/remoting/soap/SoapFaultException.class */
public abstract class SoapFaultException extends RemoteInvocationFailureException {
    public abstract String getFaultCode();

    public abstract QName getFaultCodeAsQName();

    public abstract String getFaultString();

    public abstract String getFaultActor();

    protected SoapFaultException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
