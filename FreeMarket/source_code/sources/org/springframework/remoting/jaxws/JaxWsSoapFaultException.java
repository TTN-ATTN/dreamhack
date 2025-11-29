package org.springframework.remoting.jaxws;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;
import org.springframework.remoting.soap.SoapFaultException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/remoting/jaxws/JaxWsSoapFaultException.class */
public class JaxWsSoapFaultException extends SoapFaultException {
    public JaxWsSoapFaultException(SOAPFaultException original) {
        super(original.getMessage(), original);
    }

    public final SOAPFault getFault() {
        return getCause().getFault();
    }

    @Override // org.springframework.remoting.soap.SoapFaultException
    public String getFaultCode() {
        return getFault().getFaultCode();
    }

    @Override // org.springframework.remoting.soap.SoapFaultException
    public QName getFaultCodeAsQName() {
        return getFault().getFaultCodeAsQName();
    }

    @Override // org.springframework.remoting.soap.SoapFaultException
    public String getFaultString() {
        return getFault().getFaultString();
    }

    @Override // org.springframework.remoting.soap.SoapFaultException
    public String getFaultActor() {
        return getFault().getFaultActor();
    }
}
