package ch.qos.logback.classic.jmx;

import ch.qos.logback.core.joran.spi.JoranException;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-classic-1.2.12.jar:ch/qos/logback/classic/jmx/JMXConfiguratorMBean.class */
public interface JMXConfiguratorMBean {
    void reloadDefaultConfiguration() throws JoranException;

    void reloadByFileName(String str) throws JoranException, FileNotFoundException;

    void reloadByURL(URL url) throws JoranException;

    void setLoggerLevel(String str, String str2);

    String getLoggerLevel(String str);

    String getLoggerEffectiveLevel(String str);

    List<String> getLoggerList();

    List<String> getStatuses();
}
