package org.springframework.boot.autoconfigure.h2;

import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.server.web.WebServlet;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.h2.H2ConsoleProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

@EnableConfigurationProperties({H2ConsoleProperties.class})
@AutoConfiguration(after = {DataSourceAutoConfiguration.class})
@ConditionalOnClass({WebServlet.class})
@ConditionalOnProperty(prefix = "spring.h2.console", name = {"enabled"}, havingValue = "true")
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/h2/H2ConsoleAutoConfiguration.class */
public class H2ConsoleAutoConfiguration {
    private static final Log logger = LogFactory.getLog((Class<?>) H2ConsoleAutoConfiguration.class);

    @Bean
    public ServletRegistrationBean<WebServlet> h2Console(H2ConsoleProperties properties, ObjectProvider<DataSource> dataSource) {
        String path = properties.getPath();
        String urlMapping = path + (path.endsWith("/") ? "*" : "/*");
        ServletRegistrationBean<WebServlet> registration = new ServletRegistrationBean<>(new WebServlet(), urlMapping);
        configureH2ConsoleSettings(registration, properties.getSettings());
        if (logger.isInfoEnabled()) {
            withThreadContextClassLoader(getClass().getClassLoader(), () -> {
                logDataSources(dataSource, path);
            });
        }
        return registration;
    }

    private void withThreadContextClassLoader(ClassLoader classLoader, Runnable action) {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(classLoader);
            action.run();
            Thread.currentThread().setContextClassLoader(previous);
        } catch (Throwable th) {
            Thread.currentThread().setContextClassLoader(previous);
            throw th;
        }
    }

    private void logDataSources(ObjectProvider<DataSource> dataSource, String path) {
        List<String> urls = (List) dataSource.orderedStream().map(available -> {
            try {
                Connection connection = available.getConnection();
                Throwable th = null;
                try {
                    String str = "'" + connection.getMetaData().getURL() + "'";
                    if (connection != null) {
                        if (0 != 0) {
                            try {
                                connection.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            connection.close();
                        }
                    }
                    return str;
                } finally {
                }
            } catch (Exception e) {
                return null;
            }
        }).filter((v0) -> {
            return Objects.nonNull(v0);
        }).collect(Collectors.toList());
        if (!urls.isEmpty()) {
            StringBuilder sb = new StringBuilder("H2 console available at '").append(path).append("'. ");
            String tmp = urls.size() > 1 ? "Databases" : "Database";
            sb.append(tmp).append(" available at ");
            sb.append(String.join(", ", urls));
            logger.info(sb.toString());
        }
    }

    private void configureH2ConsoleSettings(ServletRegistrationBean<WebServlet> registration, H2ConsoleProperties.Settings settings) {
        if (settings.isTrace()) {
            registration.addInitParameter("trace", "");
        }
        if (settings.isWebAllowOthers()) {
            registration.addInitParameter("webAllowOthers", "");
        }
        if (settings.getWebAdminPassword() != null) {
            registration.addInitParameter("webAdminPassword", settings.getWebAdminPassword());
        }
    }
}
