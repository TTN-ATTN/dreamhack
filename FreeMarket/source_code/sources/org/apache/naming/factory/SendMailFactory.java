package org.apache.naming.factory;

import java.security.AccessController;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimePartDataSource;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/naming/factory/SendMailFactory.class */
public class SendMailFactory implements ObjectFactory {
    protected static final String DataSourceClassName = "javax.mail.internet.MimePartDataSource";

    public Object getObjectInstance(Object refObj, Name name, Context ctx, Hashtable<?, ?> env) throws Exception {
        Reference ref = (Reference) refObj;
        if (ref.getClassName().equals(DataSourceClassName)) {
            return AccessController.doPrivileged(() -> {
                Properties props = new Properties();
                Enumeration<RefAddr> list = ref.getAll();
                props.put("mail.transport.protocol", JavaMailSenderImpl.DEFAULT_PROTOCOL);
                while (list.hasMoreElements()) {
                    RefAddr refaddr = list.nextElement();
                    props.put(refaddr.getType(), refaddr.getContent());
                }
                MimeMessage message = new MimeMessage(Session.getInstance(props));
                try {
                    RefAddr fromAddr = ref.get("mail.from");
                    String from = null;
                    if (fromAddr != null) {
                        from = (String) ref.get("mail.from").getContent();
                    }
                    if (from != null) {
                        message.setFrom(new InternetAddress(from));
                    }
                    message.setSubject("");
                } catch (Exception e) {
                }
                MimePartDataSource mds = new MimePartDataSource(message);
                return mds;
            });
        }
        return null;
    }
}
