package org.springframework.boot.web.servlet.server;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.server.AbstractConfigurableWebServerFactory;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/server/AbstractServletWebServerFactory.class */
public abstract class AbstractServletWebServerFactory extends AbstractConfigurableWebServerFactory implements ConfigurableServletWebServerFactory {
    protected final Log logger;
    private String contextPath;
    private String displayName;
    private Session session;
    private boolean registerDefaultServlet;
    private MimeMappings mimeMappings;
    private List<ServletContextInitializer> initializers;
    private Jsp jsp;
    private Map<Locale, Charset> localeCharsetMappings;
    private Map<String, String> initParameters;
    private List<CookieSameSiteSupplier> cookieSameSiteSuppliers;
    private final DocumentRoot documentRoot;
    private final StaticResourceJars staticResourceJars;
    private final Set<String> webListenerClassNames;

    public AbstractServletWebServerFactory() {
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = false;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.cookieSameSiteSuppliers = new ArrayList();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
        this.webListenerClassNames = new HashSet();
    }

    public AbstractServletWebServerFactory(int port) {
        super(port);
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = false;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.cookieSameSiteSuppliers = new ArrayList();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
        this.webListenerClassNames = new HashSet();
    }

    public AbstractServletWebServerFactory(String contextPath, int port) {
        super(port);
        this.logger = LogFactory.getLog(getClass());
        this.contextPath = "";
        this.session = new Session();
        this.registerDefaultServlet = false;
        this.mimeMappings = new MimeMappings(MimeMappings.DEFAULT);
        this.initializers = new ArrayList();
        this.jsp = new Jsp();
        this.localeCharsetMappings = new HashMap();
        this.initParameters = Collections.emptyMap();
        this.cookieSameSiteSuppliers = new ArrayList();
        this.documentRoot = new DocumentRoot(this.logger);
        this.staticResourceJars = new StaticResourceJars();
        this.webListenerClassNames = new HashSet();
        checkContextPath(contextPath);
        this.contextPath = contextPath;
    }

    public String getContextPath() {
        return this.contextPath;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setContextPath(String contextPath) {
        checkContextPath(contextPath);
        this.contextPath = contextPath;
    }

    private void checkContextPath(String contextPath) {
        Assert.notNull(contextPath, "ContextPath must not be null");
        if (!contextPath.isEmpty()) {
            if ("/".equals(contextPath)) {
                throw new IllegalArgumentException("Root ContextPath must be specified using an empty string");
            }
            if (!contextPath.startsWith("/") || contextPath.endsWith("/")) {
                throw new IllegalArgumentException("ContextPath must start with '/' and not end with '/'");
            }
        }
    }

    public String getDisplayName() {
        return this.displayName;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isRegisterDefaultServlet() {
        return this.registerDefaultServlet;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setRegisterDefaultServlet(boolean registerDefaultServlet) {
        this.registerDefaultServlet = registerDefaultServlet;
    }

    public MimeMappings getMimeMappings() {
        return this.mimeMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setMimeMappings(MimeMappings mimeMappings) {
        this.mimeMappings = new MimeMappings(mimeMappings);
    }

    public File getDocumentRoot() {
        return this.documentRoot.getDirectory();
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setDocumentRoot(File documentRoot) {
        this.documentRoot.setDirectory(documentRoot);
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setInitializers(List<? extends ServletContextInitializer> initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers = new ArrayList(initializers);
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void addInitializers(ServletContextInitializer... initializers) {
        Assert.notNull(initializers, "Initializers must not be null");
        this.initializers.addAll(Arrays.asList(initializers));
    }

    public Jsp getJsp() {
        return this.jsp;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setJsp(Jsp jsp) {
        this.jsp = jsp;
    }

    public Session getSession() {
        return this.session;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setSession(Session session) {
        this.session = session;
    }

    public Map<Locale, Charset> getLocaleCharsetMappings() {
        return this.localeCharsetMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setLocaleCharsetMappings(Map<Locale, Charset> localeCharsetMappings) {
        Assert.notNull(localeCharsetMappings, "localeCharsetMappings must not be null");
        this.localeCharsetMappings = localeCharsetMappings;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setInitParameters(Map<String, String> initParameters) {
        this.initParameters = initParameters;
    }

    public Map<String, String> getInitParameters() {
        return this.initParameters;
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void setCookieSameSiteSuppliers(List<? extends CookieSameSiteSupplier> cookieSameSiteSuppliers) {
        Assert.notNull(cookieSameSiteSuppliers, "CookieSameSiteSuppliers must not be null");
        this.cookieSameSiteSuppliers = new ArrayList(cookieSameSiteSuppliers);
    }

    @Override // org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
    public void addCookieSameSiteSuppliers(CookieSameSiteSupplier... cookieSameSiteSuppliers) {
        Assert.notNull(cookieSameSiteSuppliers, "CookieSameSiteSuppliers must not be null");
        this.cookieSameSiteSuppliers.addAll(Arrays.asList(cookieSameSiteSuppliers));
    }

    public List<CookieSameSiteSupplier> getCookieSameSiteSuppliers() {
        return this.cookieSameSiteSuppliers;
    }

    protected final ServletContextInitializer[] mergeInitializers(ServletContextInitializer... initializers) {
        List<ServletContextInitializer> mergedInitializers = new ArrayList<>();
        mergedInitializers.add(servletContext -> {
            Map<String, String> map = this.initParameters;
            servletContext.getClass();
            map.forEach(servletContext::setInitParameter);
        });
        mergedInitializers.add(new SessionConfiguringInitializer(this.session));
        mergedInitializers.addAll(Arrays.asList(initializers));
        mergedInitializers.addAll(this.initializers);
        return (ServletContextInitializer[]) mergedInitializers.toArray(new ServletContextInitializer[0]);
    }

    protected boolean shouldRegisterJspServlet() {
        return this.jsp != null && this.jsp.getRegistered() && ClassUtils.isPresent(this.jsp.getClassName(), getClass().getClassLoader());
    }

    protected final File getValidDocumentRoot() {
        return this.documentRoot.getValidDirectory();
    }

    protected final List<URL> getUrlsOfJarsWithMetaInfResources() {
        return this.staticResourceJars.getUrls();
    }

    protected final File getValidSessionStoreDir() {
        return getValidSessionStoreDir(true);
    }

    protected final File getValidSessionStoreDir(boolean mkdirs) {
        return this.session.getSessionStoreDirectory().getValidDirectory(mkdirs);
    }

    @Override // org.springframework.boot.web.servlet.WebListenerRegistry
    public void addWebListeners(String... webListenerClassNames) {
        this.webListenerClassNames.addAll(Arrays.asList(webListenerClassNames));
    }

    protected final Set<String> getWebListenerClassNames() {
        return this.webListenerClassNames;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/server/AbstractServletWebServerFactory$SessionConfiguringInitializer.class */
    private static class SessionConfiguringInitializer implements ServletContextInitializer {
        private final Session session;

        SessionConfiguringInitializer(Session session) {
            this.session = session;
        }

        @Override // org.springframework.boot.web.servlet.ServletContextInitializer
        public void onStartup(ServletContext servletContext) throws ServletException {
            if (this.session.getTrackingModes() != null) {
                servletContext.setSessionTrackingModes(unwrap(this.session.getTrackingModes()));
            }
            configureSessionCookie(servletContext.getSessionCookieConfig());
        }

        private void configureSessionCookie(SessionCookieConfig config) {
            Session.Cookie cookie = this.session.getCookie();
            PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
            cookie.getClass();
            PropertyMapper.Source sourceFrom = map.from(cookie::getName);
            config.getClass();
            sourceFrom.to(config::setName);
            cookie.getClass();
            PropertyMapper.Source sourceFrom2 = map.from(cookie::getDomain);
            config.getClass();
            sourceFrom2.to(config::setDomain);
            cookie.getClass();
            PropertyMapper.Source sourceFrom3 = map.from(cookie::getPath);
            config.getClass();
            sourceFrom3.to(config::setPath);
            cookie.getClass();
            PropertyMapper.Source sourceFrom4 = map.from(cookie::getComment);
            config.getClass();
            sourceFrom4.to(config::setComment);
            cookie.getClass();
            PropertyMapper.Source sourceFrom5 = map.from(cookie::getHttpOnly);
            config.getClass();
            sourceFrom5.to((v1) -> {
                r1.setHttpOnly(v1);
            });
            cookie.getClass();
            PropertyMapper.Source sourceFrom6 = map.from(cookie::getSecure);
            config.getClass();
            sourceFrom6.to((v1) -> {
                r1.setSecure(v1);
            });
            cookie.getClass();
            PropertyMapper.Source<Integer> sourceAsInt = map.from(cookie::getMaxAge).asInt((v0) -> {
                return v0.getSeconds();
            });
            config.getClass();
            sourceAsInt.to((v1) -> {
                r1.setMaxAge(v1);
            });
        }

        private Set<SessionTrackingMode> unwrap(Set<Session.SessionTrackingMode> modes) {
            if (modes == null) {
                return null;
            }
            Set<SessionTrackingMode> result = new LinkedHashSet<>();
            for (Session.SessionTrackingMode mode : modes) {
                result.add(SessionTrackingMode.valueOf(mode.name()));
            }
            return result;
        }
    }
}
