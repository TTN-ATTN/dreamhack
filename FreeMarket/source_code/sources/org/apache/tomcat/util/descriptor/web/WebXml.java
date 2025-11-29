package org.apache.tomcat.util.descriptor.web;

import ch.qos.logback.classic.joran.action.InsertFromJNDIAction;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.descriptor.JspPropertyGroupDescriptor;
import javax.servlet.descriptor.TaglibDescriptor;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.naming.EjbRef;
import org.apache.naming.LookupRef;
import org.apache.tomcat.util.buf.B2CConverter;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.XmlIdentifiers;
import org.apache.tomcat.util.digester.DocumentProperties;
import org.apache.tomcat.util.res.StringManager;
import org.apache.tomcat.util.security.Escape;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/descriptor/web/WebXml.class */
public class WebXml extends XmlEncodingBase implements DocumentProperties.Charset {
    protected static final String ORDER_OTHERS = "org.apache.catalina.order.others";
    private static final StringManager sm = StringManager.getManager(Constants.PACKAGE_NAME);
    private String requestCharacterEncoding;
    private String responseCharacterEncoding;
    private static final String INDENT2 = "  ";
    private static final String INDENT4 = "    ";
    private static final String INDENT6 = "      ";
    private final Log log = LogFactory.getLog((Class<?>) WebXml.class);
    private boolean overridable = false;
    private final List<String> duplicates = new ArrayList();
    private Set<String> absoluteOrdering = null;
    private final Set<String> after = new LinkedHashSet();
    private final Set<String> before = new LinkedHashSet();
    private String publicId = null;
    private boolean metadataComplete = false;
    private String name = null;
    private int majorVersion = 4;
    private int minorVersion = 0;
    private String displayName = null;
    private boolean distributable = false;
    private boolean denyUncoveredHttpMethods = false;
    private final Map<String, String> contextParams = new HashMap();
    private final Map<String, FilterDef> filters = new LinkedHashMap();
    private final Set<FilterMap> filterMaps = new LinkedHashSet();
    private final Set<String> filterMappingNames = new HashSet();
    private final Set<String> listeners = new LinkedHashSet();
    private final Map<String, ServletDef> servlets = new HashMap();
    private final Map<String, String> servletMappings = new HashMap();
    private final Set<String> servletMappingNames = new HashSet();
    private SessionConfig sessionConfig = new SessionConfig();
    private final Map<String, String> mimeMappings = new HashMap();
    private boolean replaceWelcomeFiles = false;
    private boolean alwaysAddWelcomeFiles = true;
    private final Set<String> welcomeFiles = new LinkedHashSet();
    private final Map<String, ErrorPage> errorPages = new HashMap();
    private final Map<String, String> taglibs = new HashMap();
    private final Set<JspPropertyGroup> jspPropertyGroups = new LinkedHashSet();
    private final Set<SecurityConstraint> securityConstraints = new HashSet();
    private LoginConfig loginConfig = null;
    private final Set<String> securityRoles = new HashSet();
    private final Map<String, ContextEnvironment> envEntries = new HashMap();
    private final Map<String, ContextEjb> ejbRefs = new HashMap();
    private final Map<String, ContextLocalEjb> ejbLocalRefs = new HashMap();
    private final Map<String, ContextService> serviceRefs = new HashMap();
    private final Map<String, ContextResource> resourceRefs = new HashMap();
    private final Map<String, ContextResourceEnvRef> resourceEnvRefs = new HashMap();
    private final Map<String, MessageDestinationRef> messageDestinationRefs = new HashMap();
    private final Map<String, MessageDestination> messageDestinations = new HashMap();
    private final Map<String, String> localeEncodingMappings = new HashMap();
    private Map<String, String> postConstructMethods = new HashMap();
    private Map<String, String> preDestroyMethods = new HashMap();
    private URL uRL = null;
    private String jarName = null;
    private boolean webappJar = true;
    private boolean delegate = false;

    public boolean isOverridable() {
        return this.overridable;
    }

    public void setOverridable(boolean overridable) {
        this.overridable = overridable;
    }

    public boolean isDuplicated() {
        return !this.duplicates.isEmpty();
    }

    @Deprecated
    public void setDuplicated(boolean duplicated) {
        if (duplicated) {
            this.duplicates.add("unknown");
        } else {
            this.duplicates.clear();
        }
    }

    public void addDuplicate(String duplicate) {
        this.duplicates.add(duplicate);
    }

    public List<String> getDuplicates() {
        return new ArrayList(this.duplicates);
    }

    public void createAbsoluteOrdering() {
        if (this.absoluteOrdering == null) {
            this.absoluteOrdering = new LinkedHashSet();
        }
    }

    public void addAbsoluteOrdering(String fragmentName) {
        createAbsoluteOrdering();
        this.absoluteOrdering.add(fragmentName);
    }

    public void addAbsoluteOrderingOthers() {
        createAbsoluteOrdering();
        this.absoluteOrdering.add(ORDER_OTHERS);
    }

    public Set<String> getAbsoluteOrdering() {
        return this.absoluteOrdering;
    }

    public void addAfterOrdering(String fragmentName) {
        this.after.add(fragmentName);
    }

    public void addAfterOrderingOthers() {
        if (this.before.contains(ORDER_OTHERS)) {
            throw new IllegalArgumentException(sm.getString("webXml.multipleOther"));
        }
        this.after.add(ORDER_OTHERS);
    }

    public Set<String> getAfterOrdering() {
        return this.after;
    }

    public void addBeforeOrdering(String fragmentName) {
        this.before.add(fragmentName);
    }

    public void addBeforeOrderingOthers() {
        if (this.after.contains(ORDER_OTHERS)) {
            throw new IllegalArgumentException(sm.getString("webXml.multipleOther"));
        }
        this.before.add(ORDER_OTHERS);
    }

    public Set<String> getBeforeOrdering() {
        return this.before;
    }

    public String getVersion() {
        StringBuilder sb = new StringBuilder(3);
        sb.append(this.majorVersion);
        sb.append('.');
        sb.append(this.minorVersion);
        return sb.toString();
    }

    public void setVersion(String version) {
        if (version == null) {
        }
        switch (version) {
            case "2.4":
                this.majorVersion = 2;
                this.minorVersion = 4;
                break;
            case "2.5":
                this.majorVersion = 2;
                this.minorVersion = 5;
                break;
            case "3.0":
                this.majorVersion = 3;
                this.minorVersion = 0;
                break;
            case "3.1":
                this.majorVersion = 3;
                this.minorVersion = 1;
                break;
            case "4.0":
                this.majorVersion = 4;
                this.minorVersion = 0;
                break;
            default:
                this.log.warn(sm.getString("webXml.version.unknown", version));
                break;
        }
    }

    public String getPublicId() {
        return this.publicId;
    }

    public void setPublicId(String publicId) {
        if (publicId == null) {
        }
        switch (publicId) {
            case "-//Sun Microsystems, Inc.//DTD Web Application 2.2//EN":
                this.majorVersion = 2;
                this.minorVersion = 2;
                this.publicId = publicId;
                break;
            case "-//Sun Microsystems, Inc.//DTD Web Application 2.3//EN":
                this.majorVersion = 2;
                this.minorVersion = 3;
                this.publicId = publicId;
                break;
            default:
                this.log.warn(sm.getString("webXml.unrecognisedPublicId", publicId));
                break;
        }
    }

    public boolean isMetadataComplete() {
        return this.metadataComplete;
    }

    public void setMetadataComplete(boolean metadataComplete) {
        this.metadataComplete = metadataComplete;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        if (ORDER_OTHERS.equalsIgnoreCase(name)) {
            this.log.warn(sm.getString("webXml.reservedName", name));
        } else {
            this.name = name;
        }
    }

    public int getMajorVersion() {
        return this.majorVersion;
    }

    public int getMinorVersion() {
        return this.minorVersion;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isDistributable() {
        return this.distributable;
    }

    public void setDistributable(boolean distributable) {
        this.distributable = distributable;
    }

    public boolean getDenyUncoveredHttpMethods() {
        return this.denyUncoveredHttpMethods;
    }

    public void setDenyUncoveredHttpMethods(boolean denyUncoveredHttpMethods) {
        this.denyUncoveredHttpMethods = denyUncoveredHttpMethods;
    }

    public void addContextParam(String param, String value) {
        this.contextParams.put(param, value);
    }

    public Map<String, String> getContextParams() {
        return this.contextParams;
    }

    public void addFilter(FilterDef filter) {
        if (this.filters.containsKey(filter.getFilterName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateFilter", filter.getFilterName()));
        }
        this.filters.put(filter.getFilterName(), filter);
    }

    public Map<String, FilterDef> getFilters() {
        return this.filters;
    }

    public void addFilterMapping(FilterMap filterMap) {
        filterMap.setCharset(getCharset());
        this.filterMaps.add(filterMap);
        this.filterMappingNames.add(filterMap.getFilterName());
    }

    public Set<FilterMap> getFilterMappings() {
        return this.filterMaps;
    }

    public void addListener(String className) {
        this.listeners.add(className);
    }

    public Set<String> getListeners() {
        return this.listeners;
    }

    public void addServlet(ServletDef servletDef) {
        this.servlets.put(servletDef.getServletName(), servletDef);
        if (this.overridable) {
            servletDef.setOverridable(this.overridable);
        }
    }

    public Map<String, ServletDef> getServlets() {
        return this.servlets;
    }

    public void addServletMapping(String urlPattern, String servletName) {
        addServletMappingDecoded(UDecoder.URLDecode(urlPattern, getCharset()), servletName);
    }

    public void addServletMappingDecoded(String urlPattern, String servletName) {
        String oldServletName = this.servletMappings.put(urlPattern, servletName);
        if (oldServletName != null) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateServletMapping", oldServletName, servletName, urlPattern));
        }
        this.servletMappingNames.add(servletName);
    }

    public Map<String, String> getServletMappings() {
        return this.servletMappings;
    }

    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    public SessionConfig getSessionConfig() {
        return this.sessionConfig;
    }

    public void addMimeMapping(String extension, String mimeType) {
        this.mimeMappings.put(extension, mimeType);
    }

    public Map<String, String> getMimeMappings() {
        return this.mimeMappings;
    }

    public void setReplaceWelcomeFiles(boolean replaceWelcomeFiles) {
        this.replaceWelcomeFiles = replaceWelcomeFiles;
    }

    public void setAlwaysAddWelcomeFiles(boolean alwaysAddWelcomeFiles) {
        this.alwaysAddWelcomeFiles = alwaysAddWelcomeFiles;
    }

    public void addWelcomeFile(String welcomeFile) {
        if (this.replaceWelcomeFiles) {
            this.welcomeFiles.clear();
            this.replaceWelcomeFiles = false;
        }
        this.welcomeFiles.add(welcomeFile);
    }

    public Set<String> getWelcomeFiles() {
        return this.welcomeFiles;
    }

    public void addErrorPage(ErrorPage errorPage) {
        errorPage.setCharset(getCharset());
        this.errorPages.put(errorPage.getName(), errorPage);
    }

    public Map<String, ErrorPage> getErrorPages() {
        return this.errorPages;
    }

    public void addTaglib(String uri, String location) {
        if (this.taglibs.containsKey(uri)) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateTaglibUri", uri));
        }
        this.taglibs.put(uri, location);
    }

    public Map<String, String> getTaglibs() {
        return this.taglibs;
    }

    public void addJspPropertyGroup(JspPropertyGroup propertyGroup) {
        propertyGroup.setCharset(getCharset());
        this.jspPropertyGroups.add(propertyGroup);
    }

    public Set<JspPropertyGroup> getJspPropertyGroups() {
        return this.jspPropertyGroups;
    }

    public void addSecurityConstraint(SecurityConstraint securityConstraint) {
        securityConstraint.setCharset(getCharset());
        this.securityConstraints.add(securityConstraint);
    }

    public Set<SecurityConstraint> getSecurityConstraints() {
        return this.securityConstraints;
    }

    public void setLoginConfig(LoginConfig loginConfig) {
        loginConfig.setCharset(getCharset());
        this.loginConfig = loginConfig;
    }

    public LoginConfig getLoginConfig() {
        return this.loginConfig;
    }

    public void addSecurityRole(String securityRole) {
        this.securityRoles.add(securityRole);
    }

    public Set<String> getSecurityRoles() {
        return this.securityRoles;
    }

    public void addEnvEntry(ContextEnvironment envEntry) {
        if (this.envEntries.containsKey(envEntry.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateEnvEntry", envEntry.getName()));
        }
        this.envEntries.put(envEntry.getName(), envEntry);
    }

    public Map<String, ContextEnvironment> getEnvEntries() {
        return this.envEntries;
    }

    public void addEjbRef(ContextEjb ejbRef) {
        this.ejbRefs.put(ejbRef.getName(), ejbRef);
    }

    public Map<String, ContextEjb> getEjbRefs() {
        return this.ejbRefs;
    }

    public void addEjbLocalRef(ContextLocalEjb ejbLocalRef) {
        this.ejbLocalRefs.put(ejbLocalRef.getName(), ejbLocalRef);
    }

    public Map<String, ContextLocalEjb> getEjbLocalRefs() {
        return this.ejbLocalRefs;
    }

    public void addServiceRef(ContextService serviceRef) {
        this.serviceRefs.put(serviceRef.getName(), serviceRef);
    }

    public Map<String, ContextService> getServiceRefs() {
        return this.serviceRefs;
    }

    public void addResourceRef(ContextResource resourceRef) {
        if (this.resourceRefs.containsKey(resourceRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateResourceRef", resourceRef.getName()));
        }
        this.resourceRefs.put(resourceRef.getName(), resourceRef);
    }

    public Map<String, ContextResource> getResourceRefs() {
        return this.resourceRefs;
    }

    public void addResourceEnvRef(ContextResourceEnvRef resourceEnvRef) {
        if (this.resourceEnvRefs.containsKey(resourceEnvRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateResourceEnvRef", resourceEnvRef.getName()));
        }
        this.resourceEnvRefs.put(resourceEnvRef.getName(), resourceEnvRef);
    }

    public Map<String, ContextResourceEnvRef> getResourceEnvRefs() {
        return this.resourceEnvRefs;
    }

    public void addMessageDestinationRef(MessageDestinationRef messageDestinationRef) {
        if (this.messageDestinationRefs.containsKey(messageDestinationRef.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateMessageDestinationRef", messageDestinationRef.getName()));
        }
        this.messageDestinationRefs.put(messageDestinationRef.getName(), messageDestinationRef);
    }

    public Map<String, MessageDestinationRef> getMessageDestinationRefs() {
        return this.messageDestinationRefs;
    }

    public void addMessageDestination(MessageDestination messageDestination) {
        if (this.messageDestinations.containsKey(messageDestination.getName())) {
            throw new IllegalArgumentException(sm.getString("webXml.duplicateMessageDestination", messageDestination.getName()));
        }
        this.messageDestinations.put(messageDestination.getName(), messageDestination);
    }

    public Map<String, MessageDestination> getMessageDestinations() {
        return this.messageDestinations;
    }

    public void addLocaleEncodingMapping(String locale, String encoding) {
        this.localeEncodingMappings.put(locale, encoding);
    }

    public Map<String, String> getLocaleEncodingMappings() {
        return this.localeEncodingMappings;
    }

    public void addPostConstructMethods(String clazz, String method) {
        if (!this.postConstructMethods.containsKey(clazz)) {
            this.postConstructMethods.put(clazz, method);
        }
    }

    public Map<String, String> getPostConstructMethods() {
        return this.postConstructMethods;
    }

    public void addPreDestroyMethods(String clazz, String method) {
        if (!this.preDestroyMethods.containsKey(clazz)) {
            this.preDestroyMethods.put(clazz, method);
        }
    }

    public Map<String, String> getPreDestroyMethods() {
        return this.preDestroyMethods;
    }

    public JspConfigDescriptor getJspConfigDescriptor() {
        if (this.jspPropertyGroups.isEmpty() && this.taglibs.isEmpty()) {
            return null;
        }
        Collection<JspPropertyGroupDescriptor> descriptors = new ArrayList<>(this.jspPropertyGroups.size());
        for (JspPropertyGroup jspPropertyGroup : this.jspPropertyGroups) {
            JspPropertyGroupDescriptor descriptor = new JspPropertyGroupDescriptorImpl(jspPropertyGroup);
            descriptors.add(descriptor);
        }
        Collection<TaglibDescriptor> tlds = new HashSet<>(this.taglibs.size());
        for (Map.Entry<String, String> entry : this.taglibs.entrySet()) {
            TaglibDescriptor descriptor2 = new TaglibDescriptorImpl(entry.getValue(), entry.getKey());
            tlds.add(descriptor2);
        }
        return new JspConfigDescriptorImpl(descriptors, tlds);
    }

    public String getRequestCharacterEncoding() {
        return this.requestCharacterEncoding;
    }

    public void setRequestCharacterEncoding(String requestCharacterEncoding) {
        if (requestCharacterEncoding != null) {
            try {
                B2CConverter.getCharset(requestCharacterEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.requestCharacterEncoding = requestCharacterEncoding;
    }

    public String getResponseCharacterEncoding() {
        return this.responseCharacterEncoding;
    }

    public void setResponseCharacterEncoding(String responseCharacterEncoding) {
        if (responseCharacterEncoding != null) {
            try {
                B2CConverter.getCharset(responseCharacterEncoding);
            } catch (UnsupportedEncodingException e) {
                throw new IllegalArgumentException(e);
            }
        }
        this.responseCharacterEncoding = responseCharacterEncoding;
    }

    public void setURL(URL url) {
        this.uRL = url;
    }

    public URL getURL() {
        return this.uRL;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public String getJarName() {
        return this.jarName;
    }

    public void setWebappJar(boolean webappJar) {
        this.webappJar = webappJar;
    }

    public boolean getWebappJar() {
        return this.webappJar;
    }

    public boolean getDelegate() {
        return this.delegate;
    }

    public void setDelegate(boolean delegate) {
        this.delegate = delegate;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(32);
        buf.append("Name: ");
        buf.append(getName());
        buf.append(", URL: ");
        buf.append(getURL());
        return buf.toString();
    }

    public String toXml() {
        MultipartDef multipartDef;
        StringBuilder sb = new StringBuilder(2048);
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        if (this.publicId != null) {
            sb.append("<!DOCTYPE web-app PUBLIC\n");
            sb.append("  \"");
            sb.append(this.publicId);
            sb.append("\"\n");
            sb.append("  \"");
            if (XmlIdentifiers.WEB_22_PUBLIC.equals(this.publicId)) {
                sb.append(XmlIdentifiers.WEB_22_SYSTEM);
            } else {
                sb.append(XmlIdentifiers.WEB_23_SYSTEM);
            }
            sb.append("\">\n");
            sb.append("<web-app>");
        } else {
            String javaeeNamespace = null;
            String webXmlSchemaLocation = null;
            String version = getVersion();
            if ("2.4".equals(version)) {
                javaeeNamespace = XmlIdentifiers.JAVAEE_1_4_NS;
                webXmlSchemaLocation = XmlIdentifiers.WEB_24_XSD;
            } else if ("2.5".equals(version)) {
                javaeeNamespace = "http://java.sun.com/xml/ns/javaee";
                webXmlSchemaLocation = XmlIdentifiers.WEB_25_XSD;
            } else if ("3.0".equals(version)) {
                javaeeNamespace = "http://java.sun.com/xml/ns/javaee";
                webXmlSchemaLocation = XmlIdentifiers.WEB_30_XSD;
            } else if ("3.1".equals(version)) {
                javaeeNamespace = "http://xmlns.jcp.org/xml/ns/javaee";
                webXmlSchemaLocation = XmlIdentifiers.WEB_31_XSD;
            } else if ("4.0".equals(version)) {
                javaeeNamespace = "http://xmlns.jcp.org/xml/ns/javaee";
                webXmlSchemaLocation = XmlIdentifiers.WEB_40_XSD;
            }
            sb.append("<web-app xmlns=\"");
            sb.append(javaeeNamespace);
            sb.append("\"\n");
            sb.append("         xmlns:xsi=");
            sb.append("\"http://www.w3.org/2001/XMLSchema-instance\"\n");
            sb.append("         xsi:schemaLocation=\"");
            sb.append(javaeeNamespace);
            sb.append(' ');
            sb.append(webXmlSchemaLocation);
            sb.append("\"\n");
            sb.append("         version=\"");
            sb.append(getVersion());
            sb.append("\"");
            if ("2.4".equals(version)) {
                sb.append(">\n\n");
            } else {
                sb.append("\n         metadata-complete=\"true\">\n\n");
            }
        }
        appendElement(sb, INDENT2, "display-name", this.displayName);
        if (isDistributable()) {
            sb.append("  <distributable/>\n\n");
        }
        for (Map.Entry<String, String> entry : this.contextParams.entrySet()) {
            sb.append("  <context-param>\n");
            appendElement(sb, INDENT4, "param-name", entry.getKey());
            appendElement(sb, INDENT4, "param-value", entry.getValue());
            sb.append("  </context-param>\n");
        }
        sb.append('\n');
        if (getMajorVersion() > 2 || getMinorVersion() > 2) {
            Iterator<Map.Entry<String, FilterDef>> it = this.filters.entrySet().iterator();
            while (it.hasNext()) {
                FilterDef filterDef = it.next().getValue();
                sb.append("  <filter>\n");
                appendElement(sb, INDENT4, "description", filterDef.getDescription());
                appendElement(sb, INDENT4, "display-name", filterDef.getDisplayName());
                appendElement(sb, INDENT4, "filter-name", filterDef.getFilterName());
                appendElement(sb, INDENT4, "filter-class", filterDef.getFilterClass());
                if (getMajorVersion() != 2) {
                    appendElement(sb, INDENT4, "async-supported", filterDef.getAsyncSupported());
                }
                for (Map.Entry<String, String> param : filterDef.getParameterMap().entrySet()) {
                    sb.append("    <init-param>\n");
                    appendElement(sb, INDENT6, "param-name", param.getKey());
                    appendElement(sb, INDENT6, "param-value", param.getValue());
                    sb.append("    </init-param>\n");
                }
                sb.append("  </filter>\n");
            }
            sb.append('\n');
            for (FilterMap filterMap : this.filterMaps) {
                sb.append("  <filter-mapping>\n");
                appendElement(sb, INDENT4, "filter-name", filterMap.getFilterName());
                if (filterMap.getMatchAllServletNames()) {
                    sb.append("    <servlet-name>*</servlet-name>\n");
                } else {
                    for (String servletName : filterMap.getServletNames()) {
                        appendElement(sb, INDENT4, "servlet-name", servletName);
                    }
                }
                if (filterMap.getMatchAllUrlPatterns()) {
                    sb.append("    <url-pattern>*</url-pattern>\n");
                } else {
                    for (String urlPattern : filterMap.getURLPatterns()) {
                        appendElement(sb, INDENT4, "url-pattern", encodeUrl(urlPattern));
                    }
                }
                if (getMajorVersion() > 2 || getMinorVersion() > 3) {
                    for (String dispatcher : filterMap.getDispatcherNames()) {
                        if (getMajorVersion() != 2 || !DispatcherType.ASYNC.name().equals(dispatcher)) {
                            appendElement(sb, INDENT4, AbstractDispatcherServletInitializer.DEFAULT_SERVLET_NAME, dispatcher);
                        }
                    }
                }
                sb.append("  </filter-mapping>\n");
            }
            sb.append('\n');
        }
        if (getMajorVersion() > 2 || getMinorVersion() > 2) {
            for (String listener : this.listeners) {
                sb.append("  <listener>\n");
                appendElement(sb, INDENT4, "listener-class", listener);
                sb.append("  </listener>\n");
            }
            sb.append('\n');
        }
        for (Map.Entry<String, ServletDef> entry2 : this.servlets.entrySet()) {
            ServletDef servletDef = entry2.getValue();
            sb.append("  <servlet>\n");
            appendElement(sb, INDENT4, "description", servletDef.getDescription());
            appendElement(sb, INDENT4, "display-name", servletDef.getDisplayName());
            appendElement(sb, INDENT4, "servlet-name", entry2.getKey());
            appendElement(sb, INDENT4, "servlet-class", servletDef.getServletClass());
            appendElement(sb, INDENT4, "jsp-file", servletDef.getJspFile());
            for (Map.Entry<String, String> param2 : servletDef.getParameterMap().entrySet()) {
                sb.append("    <init-param>\n");
                appendElement(sb, INDENT6, "param-name", param2.getKey());
                appendElement(sb, INDENT6, "param-value", param2.getValue());
                sb.append("    </init-param>\n");
            }
            appendElement(sb, INDENT4, "load-on-startup", servletDef.getLoadOnStartup());
            appendElement(sb, INDENT4, "enabled", servletDef.getEnabled());
            if (getMajorVersion() != 2) {
                appendElement(sb, INDENT4, "async-supported", servletDef.getAsyncSupported());
            }
            if ((getMajorVersion() > 2 || getMinorVersion() > 2) && servletDef.getRunAs() != null) {
                sb.append("    <run-as>\n");
                appendElement(sb, INDENT6, "role-name", servletDef.getRunAs());
                sb.append("    </run-as>\n");
            }
            for (SecurityRoleRef roleRef : servletDef.getSecurityRoleRefs()) {
                sb.append("    <security-role-ref>\n");
                appendElement(sb, INDENT6, "role-name", roleRef.getName());
                appendElement(sb, INDENT6, "role-link", roleRef.getLink());
                sb.append("    </security-role-ref>\n");
            }
            if (getMajorVersion() != 2 && (multipartDef = servletDef.getMultipartDef()) != null) {
                sb.append("    <multipart-config>\n");
                appendElement(sb, INDENT6, "location", multipartDef.getLocation());
                appendElement(sb, INDENT6, "max-file-size", multipartDef.getMaxFileSize());
                appendElement(sb, INDENT6, "max-request-size", multipartDef.getMaxRequestSize());
                appendElement(sb, INDENT6, "file-size-threshold", multipartDef.getFileSizeThreshold());
                sb.append("    </multipart-config>\n");
            }
            sb.append("  </servlet>\n");
        }
        sb.append('\n');
        for (Map.Entry<String, String> entry3 : this.servletMappings.entrySet()) {
            sb.append("  <servlet-mapping>\n");
            appendElement(sb, INDENT4, "servlet-name", entry3.getValue());
            appendElement(sb, INDENT4, "url-pattern", encodeUrl(entry3.getKey()));
            sb.append("  </servlet-mapping>\n");
        }
        sb.append('\n');
        if (this.sessionConfig != null) {
            sb.append("  <session-config>\n");
            appendElement(sb, INDENT4, "session-timeout", this.sessionConfig.getSessionTimeout());
            if (this.majorVersion >= 3) {
                sb.append("    <cookie-config>\n");
                appendElement(sb, INDENT6, "name", this.sessionConfig.getCookieName());
                appendElement(sb, INDENT6, "domain", this.sessionConfig.getCookieDomain());
                appendElement(sb, INDENT6, "path", this.sessionConfig.getCookiePath());
                appendElement(sb, INDENT6, "comment", this.sessionConfig.getCookieComment());
                appendElement(sb, INDENT6, "http-only", this.sessionConfig.getCookieHttpOnly());
                appendElement(sb, INDENT6, "secure", this.sessionConfig.getCookieSecure());
                appendElement(sb, INDENT6, "max-age", this.sessionConfig.getCookieMaxAge());
                sb.append("    </cookie-config>\n");
                Iterator it2 = this.sessionConfig.getSessionTrackingModes().iterator();
                while (it2.hasNext()) {
                    SessionTrackingMode stm = (SessionTrackingMode) it2.next();
                    appendElement(sb, INDENT4, "tracking-mode", stm.name());
                }
            }
            sb.append("  </session-config>\n\n");
        }
        for (Map.Entry<String, String> entry4 : this.mimeMappings.entrySet()) {
            sb.append("  <mime-mapping>\n");
            appendElement(sb, INDENT4, "extension", entry4.getKey());
            appendElement(sb, INDENT4, "mime-type", entry4.getValue());
            sb.append("  </mime-mapping>\n");
        }
        sb.append('\n');
        if (this.welcomeFiles.size() > 0) {
            sb.append("  <welcome-file-list>\n");
            for (String welcomeFile : this.welcomeFiles) {
                appendElement(sb, INDENT4, "welcome-file", welcomeFile);
            }
            sb.append("  </welcome-file-list>\n\n");
        }
        for (ErrorPage errorPage : this.errorPages.values()) {
            String exceptionType = errorPage.getExceptionType();
            int errorCode = errorPage.getErrorCode();
            if (exceptionType != null || errorCode != 0 || getMajorVersion() != 2) {
                sb.append("  <error-page>\n");
                if (errorPage.getExceptionType() != null) {
                    appendElement(sb, INDENT4, "exception-type", exceptionType);
                } else if (errorPage.getErrorCode() > 0) {
                    appendElement(sb, INDENT4, "error-code", Integer.toString(errorCode));
                }
                appendElement(sb, INDENT4, "location", errorPage.getLocation());
                sb.append("  </error-page>\n");
            }
        }
        sb.append('\n');
        if (this.taglibs.size() > 0 || this.jspPropertyGroups.size() > 0) {
            if (getMajorVersion() > 2 || getMinorVersion() > 3) {
                sb.append("  <jsp-config>\n");
            }
            for (Map.Entry<String, String> entry5 : this.taglibs.entrySet()) {
                sb.append("    <taglib>\n");
                appendElement(sb, INDENT6, "taglib-uri", entry5.getKey());
                appendElement(sb, INDENT6, "taglib-location", entry5.getValue());
                sb.append("    </taglib>\n");
            }
            if (getMajorVersion() > 2 || getMinorVersion() > 3) {
                for (JspPropertyGroup jpg : this.jspPropertyGroups) {
                    sb.append("    <jsp-property-group>\n");
                    for (String urlPattern2 : jpg.getUrlPatterns()) {
                        appendElement(sb, INDENT6, "url-pattern", encodeUrl(urlPattern2));
                    }
                    appendElement(sb, INDENT6, "el-ignored", jpg.getElIgnored());
                    appendElement(sb, INDENT6, "page-encoding", jpg.getPageEncoding());
                    appendElement(sb, INDENT6, "scripting-invalid", jpg.getScriptingInvalid());
                    appendElement(sb, INDENT6, "is-xml", jpg.getIsXml());
                    for (String prelude : jpg.getIncludePreludes()) {
                        appendElement(sb, INDENT6, "include-prelude", prelude);
                    }
                    for (String coda : jpg.getIncludeCodas()) {
                        appendElement(sb, INDENT6, "include-coda", coda);
                    }
                    appendElement(sb, INDENT6, "deferred-syntax-allowed-as-literal", jpg.getDeferredSyntax());
                    appendElement(sb, INDENT6, "trim-directive-whitespaces", jpg.getTrimWhitespace());
                    appendElement(sb, INDENT6, "default-content-type", jpg.getDefaultContentType());
                    appendElement(sb, INDENT6, "buffer", jpg.getBuffer());
                    appendElement(sb, INDENT6, "error-on-undeclared-namespace", jpg.getErrorOnUndeclaredNamespace());
                    sb.append("    </jsp-property-group>\n");
                }
                sb.append("  </jsp-config>\n\n");
            }
        }
        if (getMajorVersion() > 2 || getMinorVersion() > 2) {
            for (ContextResourceEnvRef resourceEnvRef : this.resourceEnvRefs.values()) {
                sb.append("  <resource-env-ref>\n");
                appendElement(sb, INDENT4, "description", resourceEnvRef.getDescription());
                appendElement(sb, INDENT4, "resource-env-ref-name", resourceEnvRef.getName());
                appendElement(sb, INDENT4, "resource-env-ref-type", resourceEnvRef.getType());
                appendElement(sb, INDENT4, "mapped-name", resourceEnvRef.getProperty("mappedName"));
                for (InjectionTarget target : resourceEnvRef.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    appendElement(sb, INDENT6, "injection-target-class", target.getTargetClass());
                    appendElement(sb, INDENT6, "injection-target-name", target.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, resourceEnvRef.getLookupName());
                sb.append("  </resource-env-ref>\n");
            }
            sb.append('\n');
        }
        for (ContextResource resourceRef : this.resourceRefs.values()) {
            sb.append("  <resource-ref>\n");
            appendElement(sb, INDENT4, "description", resourceRef.getDescription());
            appendElement(sb, INDENT4, "res-ref-name", resourceRef.getName());
            appendElement(sb, INDENT4, "res-type", resourceRef.getType());
            appendElement(sb, INDENT4, "res-auth", resourceRef.getAuth());
            if (getMajorVersion() > 2 || getMinorVersion() > 2) {
                appendElement(sb, INDENT4, "res-sharing-scope", resourceRef.getScope());
            }
            appendElement(sb, INDENT4, "mapped-name", resourceRef.getProperty("mappedName"));
            for (InjectionTarget target2 : resourceRef.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                appendElement(sb, INDENT6, "injection-target-class", target2.getTargetClass());
                appendElement(sb, INDENT6, "injection-target-name", target2.getTargetName());
                sb.append("    </injection-target>\n");
            }
            appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, resourceRef.getLookupName());
            sb.append("  </resource-ref>\n");
        }
        sb.append('\n');
        for (SecurityConstraint constraint : this.securityConstraints) {
            sb.append("  <security-constraint>\n");
            if (getMajorVersion() > 2 || getMinorVersion() > 2) {
                appendElement(sb, INDENT4, "display-name", constraint.getDisplayName());
            }
            for (SecurityCollection collection : constraint.findCollections()) {
                sb.append("    <web-resource-collection>\n");
                appendElement(sb, INDENT6, "web-resource-name", collection.getName());
                appendElement(sb, INDENT6, "description", collection.getDescription());
                for (String urlPattern3 : collection.findPatterns()) {
                    appendElement(sb, INDENT6, "url-pattern", encodeUrl(urlPattern3));
                }
                for (String method : collection.findMethods()) {
                    appendElement(sb, INDENT6, "http-method", method);
                }
                for (String method2 : collection.findOmittedMethods()) {
                    appendElement(sb, INDENT6, "http-method-omission", method2);
                }
                sb.append("    </web-resource-collection>\n");
            }
            if (constraint.findAuthRoles().length > 0) {
                sb.append("    <auth-constraint>\n");
                for (String role : constraint.findAuthRoles()) {
                    appendElement(sb, INDENT6, "role-name", role);
                }
                sb.append("    </auth-constraint>\n");
            }
            if (constraint.getUserConstraint() != null) {
                sb.append("    <user-data-constraint>\n");
                appendElement(sb, INDENT6, "transport-guarantee", constraint.getUserConstraint());
                sb.append("    </user-data-constraint>\n");
            }
            sb.append("  </security-constraint>\n");
        }
        sb.append('\n');
        if (this.loginConfig != null) {
            sb.append("  <login-config>\n");
            appendElement(sb, INDENT4, "auth-method", this.loginConfig.getAuthMethod());
            appendElement(sb, INDENT4, "realm-name", this.loginConfig.getRealmName());
            if (this.loginConfig.getErrorPage() != null || this.loginConfig.getLoginPage() != null) {
                sb.append("    <form-login-config>\n");
                appendElement(sb, INDENT6, "form-login-page", this.loginConfig.getLoginPage());
                appendElement(sb, INDENT6, "form-error-page", this.loginConfig.getErrorPage());
                sb.append("    </form-login-config>\n");
            }
            sb.append("  </login-config>\n\n");
        }
        for (String roleName : this.securityRoles) {
            sb.append("  <security-role>\n");
            appendElement(sb, INDENT4, "role-name", roleName);
            sb.append("  </security-role>\n");
        }
        for (ContextEnvironment envEntry : this.envEntries.values()) {
            sb.append("  <env-entry>\n");
            appendElement(sb, INDENT4, "description", envEntry.getDescription());
            appendElement(sb, INDENT4, InsertFromJNDIAction.ENV_ENTRY_NAME_ATTR, envEntry.getName());
            appendElement(sb, INDENT4, "env-entry-type", envEntry.getType());
            appendElement(sb, INDENT4, "env-entry-value", envEntry.getValue());
            appendElement(sb, INDENT4, "mapped-name", envEntry.getProperty("mappedName"));
            for (InjectionTarget target3 : envEntry.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                appendElement(sb, INDENT6, "injection-target-class", target3.getTargetClass());
                appendElement(sb, INDENT6, "injection-target-name", target3.getTargetName());
                sb.append("    </injection-target>\n");
            }
            appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, envEntry.getLookupName());
            sb.append("  </env-entry>\n");
        }
        sb.append('\n');
        for (ContextEjb ejbRef : this.ejbRefs.values()) {
            sb.append("  <ejb-ref>\n");
            appendElement(sb, INDENT4, "description", ejbRef.getDescription());
            appendElement(sb, INDENT4, "ejb-ref-name", ejbRef.getName());
            appendElement(sb, INDENT4, "ejb-ref-type", ejbRef.getType());
            appendElement(sb, INDENT4, "home", ejbRef.getHome());
            appendElement(sb, INDENT4, EjbRef.REMOTE, ejbRef.getRemote());
            appendElement(sb, INDENT4, "ejb-link", ejbRef.getLink());
            appendElement(sb, INDENT4, "mapped-name", ejbRef.getProperty("mappedName"));
            for (InjectionTarget target4 : ejbRef.getInjectionTargets()) {
                sb.append("    <injection-target>\n");
                appendElement(sb, INDENT6, "injection-target-class", target4.getTargetClass());
                appendElement(sb, INDENT6, "injection-target-name", target4.getTargetName());
                sb.append("    </injection-target>\n");
            }
            appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, ejbRef.getLookupName());
            sb.append("  </ejb-ref>\n");
        }
        sb.append('\n');
        if (getMajorVersion() > 2 || getMinorVersion() > 2) {
            for (ContextLocalEjb ejbLocalRef : this.ejbLocalRefs.values()) {
                sb.append("  <ejb-local-ref>\n");
                appendElement(sb, INDENT4, "description", ejbLocalRef.getDescription());
                appendElement(sb, INDENT4, "ejb-ref-name", ejbLocalRef.getName());
                appendElement(sb, INDENT4, "ejb-ref-type", ejbLocalRef.getType());
                appendElement(sb, INDENT4, "local-home", ejbLocalRef.getHome());
                appendElement(sb, INDENT4, "local", ejbLocalRef.getLocal());
                appendElement(sb, INDENT4, "ejb-link", ejbLocalRef.getLink());
                appendElement(sb, INDENT4, "mapped-name", ejbLocalRef.getProperty("mappedName"));
                for (InjectionTarget target5 : ejbLocalRef.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    appendElement(sb, INDENT6, "injection-target-class", target5.getTargetClass());
                    appendElement(sb, INDENT6, "injection-target-name", target5.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, ejbLocalRef.getLookupName());
                sb.append("  </ejb-local-ref>\n");
            }
            sb.append('\n');
        }
        if (getMajorVersion() > 2 || getMinorVersion() > 3) {
            for (ContextService serviceRef : this.serviceRefs.values()) {
                sb.append("  <service-ref>\n");
                appendElement(sb, INDENT4, "description", serviceRef.getDescription());
                appendElement(sb, INDENT4, "display-name", serviceRef.getDisplayname());
                appendElement(sb, INDENT4, "service-ref-name", serviceRef.getName());
                appendElement(sb, INDENT4, "service-interface", serviceRef.getInterface());
                appendElement(sb, INDENT4, "service-ref-type", serviceRef.getType());
                appendElement(sb, INDENT4, "wsdl-file", serviceRef.getWsdlfile());
                appendElement(sb, INDENT4, "jaxrpc-mapping-file", serviceRef.getJaxrpcmappingfile());
                String qname = serviceRef.getServiceqnameNamespaceURI();
                if (qname != null) {
                    qname = qname + ":";
                }
                appendElement(sb, INDENT4, "service-qname", qname + serviceRef.getServiceqnameLocalpart());
                Iterator<String> endpointIter = serviceRef.getServiceendpoints();
                while (endpointIter.hasNext()) {
                    String endpoint = endpointIter.next();
                    sb.append("    <port-component-ref>\n");
                    appendElement(sb, INDENT6, "service-endpoint-interface", endpoint);
                    appendElement(sb, INDENT6, "port-component-link", serviceRef.getProperty(endpoint));
                    sb.append("    </port-component-ref>\n");
                }
                Iterator<String> handlerIter = serviceRef.getHandlers();
                while (handlerIter.hasNext()) {
                    String handler = handlerIter.next();
                    sb.append("    <handler>\n");
                    ContextHandler ch2 = serviceRef.getHandler(handler);
                    appendElement(sb, INDENT6, "handler-name", ch2.getName());
                    appendElement(sb, INDENT6, "handler-class", ch2.getHandlerclass());
                    sb.append("    </handler>\n");
                }
                appendElement(sb, INDENT4, "mapped-name", serviceRef.getProperty("mappedName"));
                for (InjectionTarget target6 : serviceRef.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    appendElement(sb, INDENT6, "injection-target-class", target6.getTargetClass());
                    appendElement(sb, INDENT6, "injection-target-name", target6.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, serviceRef.getLookupName());
                sb.append("  </service-ref>\n");
            }
            sb.append('\n');
        }
        if (!this.postConstructMethods.isEmpty()) {
            for (Map.Entry<String, String> entry6 : this.postConstructMethods.entrySet()) {
                sb.append("  <post-construct>\n");
                appendElement(sb, INDENT4, "lifecycle-callback-class", entry6.getKey());
                appendElement(sb, INDENT4, "lifecycle-callback-method", entry6.getValue());
                sb.append("  </post-construct>\n");
            }
            sb.append('\n');
        }
        if (!this.preDestroyMethods.isEmpty()) {
            for (Map.Entry<String, String> entry7 : this.preDestroyMethods.entrySet()) {
                sb.append("  <pre-destroy>\n");
                appendElement(sb, INDENT4, "lifecycle-callback-class", entry7.getKey());
                appendElement(sb, INDENT4, "lifecycle-callback-method", entry7.getValue());
                sb.append("  </pre-destroy>\n");
            }
            sb.append('\n');
        }
        if (getMajorVersion() > 2 || getMinorVersion() > 3) {
            for (MessageDestinationRef mdr : this.messageDestinationRefs.values()) {
                sb.append("  <message-destination-ref>\n");
                appendElement(sb, INDENT4, "description", mdr.getDescription());
                appendElement(sb, INDENT4, "message-destination-ref-name", mdr.getName());
                appendElement(sb, INDENT4, "message-destination-type", mdr.getType());
                appendElement(sb, INDENT4, "message-destination-usage", mdr.getUsage());
                appendElement(sb, INDENT4, "message-destination-link", mdr.getLink());
                appendElement(sb, INDENT4, "mapped-name", mdr.getProperty("mappedName"));
                for (InjectionTarget target7 : mdr.getInjectionTargets()) {
                    sb.append("    <injection-target>\n");
                    appendElement(sb, INDENT6, "injection-target-class", target7.getTargetClass());
                    appendElement(sb, INDENT6, "injection-target-name", target7.getTargetName());
                    sb.append("    </injection-target>\n");
                }
                appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, mdr.getLookupName());
                sb.append("  </message-destination-ref>\n");
            }
            sb.append('\n');
            for (MessageDestination md : this.messageDestinations.values()) {
                sb.append("  <message-destination>\n");
                appendElement(sb, INDENT4, "description", md.getDescription());
                appendElement(sb, INDENT4, "display-name", md.getDisplayName());
                appendElement(sb, INDENT4, "message-destination-name", md.getName());
                appendElement(sb, INDENT4, "mapped-name", md.getProperty("mappedName"));
                appendElement(sb, INDENT4, LookupRef.LOOKUP_NAME, md.getLookupName());
                sb.append("  </message-destination>\n");
            }
            sb.append('\n');
        }
        if ((getMajorVersion() > 2 || getMinorVersion() > 3) && this.localeEncodingMappings.size() > 0) {
            sb.append("  <locale-encoding-mapping-list>\n");
            for (Map.Entry<String, String> entry8 : this.localeEncodingMappings.entrySet()) {
                sb.append("    <locale-encoding-mapping>\n");
                appendElement(sb, INDENT6, "locale", entry8.getKey());
                appendElement(sb, INDENT6, "encoding", entry8.getValue());
                sb.append("    </locale-encoding-mapping>\n");
            }
            sb.append("  </locale-encoding-mapping-list>\n");
            sb.append("\n");
        }
        if ((getMajorVersion() > 3 || (getMajorVersion() == 3 && getMinorVersion() > 0)) && this.denyUncoveredHttpMethods) {
            sb.append("  <deny-uncovered-http-methods/>");
            sb.append("\n");
        }
        if (getMajorVersion() >= 4) {
            appendElement(sb, INDENT2, "request-character-encoding", this.requestCharacterEncoding);
            appendElement(sb, INDENT2, "response-character-encoding", this.responseCharacterEncoding);
        }
        sb.append("</web-app>");
        return sb.toString();
    }

    private String encodeUrl(String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    private static void appendElement(StringBuilder sb, String indent, String elementName, String value) {
        if (value == null) {
            return;
        }
        if (value.length() == 0) {
            sb.append(indent);
            sb.append('<');
            sb.append(elementName);
            sb.append("/>\n");
            return;
        }
        sb.append(indent);
        sb.append('<');
        sb.append(elementName);
        sb.append('>');
        sb.append(Escape.xml(value));
        sb.append("</");
        sb.append(elementName);
        sb.append(">\n");
    }

    private static void appendElement(StringBuilder sb, String indent, String elementName, Object value) {
        if (value == null) {
            return;
        }
        appendElement(sb, indent, elementName, value.toString());
    }

    public boolean merge(Set<WebXml> fragments) {
        WebXml temp = new WebXml();
        for (WebXml fragment : fragments) {
            if (!mergeMap(fragment.getContextParams(), this.contextParams, temp.getContextParams(), fragment, "Context Parameter")) {
                return false;
            }
        }
        this.contextParams.putAll(temp.getContextParams());
        if (this.displayName == null) {
            for (WebXml fragment2 : fragments) {
                String value = fragment2.getDisplayName();
                if (value != null) {
                    if (temp.getDisplayName() == null) {
                        temp.setDisplayName(value);
                    } else {
                        this.log.error(sm.getString("webXml.mergeConflictDisplayName", fragment2.getName(), fragment2.getURL()));
                        return false;
                    }
                }
            }
            this.displayName = temp.getDisplayName();
        }
        if (!this.denyUncoveredHttpMethods) {
            Iterator<WebXml> it = fragments.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                if (it.next().getDenyUncoveredHttpMethods()) {
                    this.denyUncoveredHttpMethods = true;
                    break;
                }
            }
        }
        if (this.requestCharacterEncoding == null) {
            for (WebXml fragment3 : fragments) {
                if (fragment3.getRequestCharacterEncoding() != null) {
                    this.requestCharacterEncoding = fragment3.getRequestCharacterEncoding();
                }
            }
        }
        if (this.responseCharacterEncoding == null) {
            for (WebXml fragment4 : fragments) {
                if (fragment4.getResponseCharacterEncoding() != null) {
                    this.responseCharacterEncoding = fragment4.getResponseCharacterEncoding();
                }
            }
        }
        if (this.distributable) {
            Iterator<WebXml> it2 = fragments.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                }
                if (!it2.next().isDistributable()) {
                    this.distributable = false;
                    break;
                }
            }
        }
        for (WebXml fragment5 : fragments) {
            if (!mergeResourceMap(fragment5.getEjbLocalRefs(), this.ejbLocalRefs, temp.getEjbLocalRefs(), fragment5)) {
                return false;
            }
        }
        this.ejbLocalRefs.putAll(temp.getEjbLocalRefs());
        for (WebXml fragment6 : fragments) {
            if (!mergeResourceMap(fragment6.getEjbRefs(), this.ejbRefs, temp.getEjbRefs(), fragment6)) {
                return false;
            }
        }
        this.ejbRefs.putAll(temp.getEjbRefs());
        for (WebXml fragment7 : fragments) {
            if (!mergeResourceMap(fragment7.getEnvEntries(), this.envEntries, temp.getEnvEntries(), fragment7)) {
                return false;
            }
        }
        this.envEntries.putAll(temp.getEnvEntries());
        for (WebXml fragment8 : fragments) {
            if (!mergeMap(fragment8.getErrorPages(), this.errorPages, temp.getErrorPages(), fragment8, "Error Page")) {
                return false;
            }
        }
        this.errorPages.putAll(temp.getErrorPages());
        List<FilterMap> filterMapsToAdd = new ArrayList<>();
        Iterator<WebXml> it3 = fragments.iterator();
        while (it3.hasNext()) {
            for (FilterMap filterMap : it3.next().getFilterMappings()) {
                if (!this.filterMappingNames.contains(filterMap.getFilterName())) {
                    filterMapsToAdd.add(filterMap);
                }
            }
        }
        Iterator<FilterMap> it4 = filterMapsToAdd.iterator();
        while (it4.hasNext()) {
            addFilterMapping(it4.next());
        }
        for (WebXml fragment9 : fragments) {
            for (Map.Entry<String, FilterDef> entry : fragment9.getFilters().entrySet()) {
                if (this.filters.containsKey(entry.getKey())) {
                    mergeFilter(entry.getValue(), this.filters.get(entry.getKey()), false);
                } else if (temp.getFilters().containsKey(entry.getKey())) {
                    if (!mergeFilter(entry.getValue(), temp.getFilters().get(entry.getKey()), true)) {
                        this.log.error(sm.getString("webXml.mergeConflictFilter", entry.getKey(), fragment9.getName(), fragment9.getURL()));
                        return false;
                    }
                } else {
                    temp.getFilters().put(entry.getKey(), entry.getValue());
                }
            }
        }
        this.filters.putAll(temp.getFilters());
        Iterator<WebXml> it5 = fragments.iterator();
        while (it5.hasNext()) {
            for (JspPropertyGroup jspPropertyGroup : it5.next().getJspPropertyGroups()) {
                addJspPropertyGroup(jspPropertyGroup);
            }
        }
        Iterator<WebXml> it6 = fragments.iterator();
        while (it6.hasNext()) {
            for (String listener : it6.next().getListeners()) {
                addListener(listener);
            }
        }
        for (WebXml fragment10 : fragments) {
            if (!mergeMap(fragment10.getLocaleEncodingMappings(), this.localeEncodingMappings, temp.getLocaleEncodingMappings(), fragment10, "Locale Encoding Mapping")) {
                return false;
            }
        }
        this.localeEncodingMappings.putAll(temp.getLocaleEncodingMappings());
        if (getLoginConfig() == null) {
            LoginConfig tempLoginConfig = null;
            for (WebXml fragment11 : fragments) {
                LoginConfig fragmentLoginConfig = fragment11.loginConfig;
                if (fragmentLoginConfig != null) {
                    if (tempLoginConfig == null || fragmentLoginConfig.equals(tempLoginConfig)) {
                        tempLoginConfig = fragmentLoginConfig;
                    } else {
                        this.log.error(sm.getString("webXml.mergeConflictLoginConfig", fragment11.getName(), fragment11.getURL()));
                    }
                }
            }
            this.loginConfig = tempLoginConfig;
        }
        for (WebXml fragment12 : fragments) {
            if (!mergeResourceMap(fragment12.getMessageDestinationRefs(), this.messageDestinationRefs, temp.getMessageDestinationRefs(), fragment12)) {
                return false;
            }
        }
        this.messageDestinationRefs.putAll(temp.getMessageDestinationRefs());
        for (WebXml fragment13 : fragments) {
            if (!mergeResourceMap(fragment13.getMessageDestinations(), this.messageDestinations, temp.getMessageDestinations(), fragment13)) {
                return false;
            }
        }
        this.messageDestinations.putAll(temp.getMessageDestinations());
        for (WebXml fragment14 : fragments) {
            if (!mergeMap(fragment14.getMimeMappings(), this.mimeMappings, temp.getMimeMappings(), fragment14, "Mime Mapping")) {
                return false;
            }
        }
        this.mimeMappings.putAll(temp.getMimeMappings());
        for (WebXml fragment15 : fragments) {
            if (!mergeResourceMap(fragment15.getResourceEnvRefs(), this.resourceEnvRefs, temp.getResourceEnvRefs(), fragment15)) {
                return false;
            }
        }
        this.resourceEnvRefs.putAll(temp.getResourceEnvRefs());
        for (WebXml fragment16 : fragments) {
            if (!mergeResourceMap(fragment16.getResourceRefs(), this.resourceRefs, temp.getResourceRefs(), fragment16)) {
                return false;
            }
        }
        this.resourceRefs.putAll(temp.getResourceRefs());
        Iterator<WebXml> it7 = fragments.iterator();
        while (it7.hasNext()) {
            for (SecurityConstraint constraint : it7.next().getSecurityConstraints()) {
                addSecurityConstraint(constraint);
            }
        }
        Iterator<WebXml> it8 = fragments.iterator();
        while (it8.hasNext()) {
            for (String role : it8.next().getSecurityRoles()) {
                addSecurityRole(role);
            }
        }
        for (WebXml fragment17 : fragments) {
            if (!mergeResourceMap(fragment17.getServiceRefs(), this.serviceRefs, temp.getServiceRefs(), fragment17)) {
                return false;
            }
        }
        this.serviceRefs.putAll(temp.getServiceRefs());
        List<Map.Entry<String, String>> servletMappingsToAdd = new ArrayList<>();
        Iterator<WebXml> it9 = fragments.iterator();
        while (it9.hasNext()) {
            for (Map.Entry<String, String> servletMap : it9.next().getServletMappings().entrySet()) {
                if (!this.servletMappingNames.contains(servletMap.getValue()) && !this.servletMappings.containsKey(servletMap.getKey())) {
                    servletMappingsToAdd.add(servletMap);
                }
            }
        }
        for (Map.Entry<String, String> mapping : servletMappingsToAdd) {
            addServletMappingDecoded(mapping.getKey(), mapping.getValue());
        }
        for (WebXml fragment18 : fragments) {
            for (Map.Entry<String, ServletDef> entry2 : fragment18.getServlets().entrySet()) {
                if (this.servlets.containsKey(entry2.getKey())) {
                    mergeServlet(entry2.getValue(), this.servlets.get(entry2.getKey()), false);
                } else if (temp.getServlets().containsKey(entry2.getKey())) {
                    if (!mergeServlet(entry2.getValue(), temp.getServlets().get(entry2.getKey()), true)) {
                        this.log.error(sm.getString("webXml.mergeConflictServlet", entry2.getKey(), fragment18.getName(), fragment18.getURL()));
                        return false;
                    }
                } else {
                    temp.getServlets().put(entry2.getKey(), entry2.getValue());
                }
            }
        }
        this.servlets.putAll(temp.getServlets());
        if (this.sessionConfig.getSessionTimeout() == null) {
            for (WebXml fragment19 : fragments) {
                Integer value2 = fragment19.getSessionConfig().getSessionTimeout();
                if (value2 != null) {
                    if (temp.getSessionConfig().getSessionTimeout() == null) {
                        temp.getSessionConfig().setSessionTimeout(value2.toString());
                    } else if (!value2.equals(temp.getSessionConfig().getSessionTimeout())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionTimeout", fragment19.getName(), fragment19.getURL()));
                        return false;
                    }
                }
            }
            if (temp.getSessionConfig().getSessionTimeout() != null) {
                this.sessionConfig.setSessionTimeout(temp.getSessionConfig().getSessionTimeout().toString());
            }
        }
        if (this.sessionConfig.getCookieName() == null) {
            for (WebXml fragment20 : fragments) {
                String value3 = fragment20.getSessionConfig().getCookieName();
                if (value3 != null) {
                    if (temp.getSessionConfig().getCookieName() == null) {
                        temp.getSessionConfig().setCookieName(value3);
                    } else if (!value3.equals(temp.getSessionConfig().getCookieName())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieName", fragment20.getName(), fragment20.getURL()));
                        return false;
                    }
                }
            }
            this.sessionConfig.setCookieName(temp.getSessionConfig().getCookieName());
        }
        if (this.sessionConfig.getCookieDomain() == null) {
            for (WebXml fragment21 : fragments) {
                String value4 = fragment21.getSessionConfig().getCookieDomain();
                if (value4 != null) {
                    if (temp.getSessionConfig().getCookieDomain() == null) {
                        temp.getSessionConfig().setCookieDomain(value4);
                    } else if (!value4.equals(temp.getSessionConfig().getCookieDomain())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieDomain", fragment21.getName(), fragment21.getURL()));
                        return false;
                    }
                }
            }
            this.sessionConfig.setCookieDomain(temp.getSessionConfig().getCookieDomain());
        }
        if (this.sessionConfig.getCookiePath() == null) {
            for (WebXml fragment22 : fragments) {
                String value5 = fragment22.getSessionConfig().getCookiePath();
                if (value5 != null) {
                    if (temp.getSessionConfig().getCookiePath() == null) {
                        temp.getSessionConfig().setCookiePath(value5);
                    } else if (!value5.equals(temp.getSessionConfig().getCookiePath())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookiePath", fragment22.getName(), fragment22.getURL()));
                        return false;
                    }
                }
            }
            this.sessionConfig.setCookiePath(temp.getSessionConfig().getCookiePath());
        }
        if (this.sessionConfig.getCookieComment() == null) {
            for (WebXml fragment23 : fragments) {
                String value6 = fragment23.getSessionConfig().getCookieComment();
                if (value6 != null) {
                    if (temp.getSessionConfig().getCookieComment() == null) {
                        temp.getSessionConfig().setCookieComment(value6);
                    } else if (!value6.equals(temp.getSessionConfig().getCookieComment())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieComment", fragment23.getName(), fragment23.getURL()));
                        return false;
                    }
                }
            }
            this.sessionConfig.setCookieComment(temp.getSessionConfig().getCookieComment());
        }
        if (this.sessionConfig.getCookieHttpOnly() == null) {
            for (WebXml fragment24 : fragments) {
                Boolean value7 = fragment24.getSessionConfig().getCookieHttpOnly();
                if (value7 != null) {
                    if (temp.getSessionConfig().getCookieHttpOnly() == null) {
                        temp.getSessionConfig().setCookieHttpOnly(value7.toString());
                    } else if (!value7.equals(temp.getSessionConfig().getCookieHttpOnly())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieHttpOnly", fragment24.getName(), fragment24.getURL()));
                        return false;
                    }
                }
            }
            if (temp.getSessionConfig().getCookieHttpOnly() != null) {
                this.sessionConfig.setCookieHttpOnly(temp.getSessionConfig().getCookieHttpOnly().toString());
            }
        }
        if (this.sessionConfig.getCookieSecure() == null) {
            for (WebXml fragment25 : fragments) {
                Boolean value8 = fragment25.getSessionConfig().getCookieSecure();
                if (value8 != null) {
                    if (temp.getSessionConfig().getCookieSecure() == null) {
                        temp.getSessionConfig().setCookieSecure(value8.toString());
                    } else if (!value8.equals(temp.getSessionConfig().getCookieSecure())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieSecure", fragment25.getName(), fragment25.getURL()));
                        return false;
                    }
                }
            }
            if (temp.getSessionConfig().getCookieSecure() != null) {
                this.sessionConfig.setCookieSecure(temp.getSessionConfig().getCookieSecure().toString());
            }
        }
        if (this.sessionConfig.getCookieMaxAge() == null) {
            for (WebXml fragment26 : fragments) {
                Integer value9 = fragment26.getSessionConfig().getCookieMaxAge();
                if (value9 != null) {
                    if (temp.getSessionConfig().getCookieMaxAge() == null) {
                        temp.getSessionConfig().setCookieMaxAge(value9.toString());
                    } else if (!value9.equals(temp.getSessionConfig().getCookieMaxAge())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionCookieMaxAge", fragment26.getName(), fragment26.getURL()));
                        return false;
                    }
                }
            }
            if (temp.getSessionConfig().getCookieMaxAge() != null) {
                this.sessionConfig.setCookieMaxAge(temp.getSessionConfig().getCookieMaxAge().toString());
            }
        }
        if (this.sessionConfig.getSessionTrackingModes().size() == 0) {
            for (WebXml fragment27 : fragments) {
                EnumSet<SessionTrackingMode> value10 = fragment27.getSessionConfig().getSessionTrackingModes();
                if (value10.size() > 0) {
                    if (temp.getSessionConfig().getSessionTrackingModes().size() == 0) {
                        temp.getSessionConfig().getSessionTrackingModes().addAll(value10);
                    } else if (!value10.equals(temp.getSessionConfig().getSessionTrackingModes())) {
                        this.log.error(sm.getString("webXml.mergeConflictSessionTrackingMode", fragment27.getName(), fragment27.getURL()));
                        return false;
                    }
                }
            }
            this.sessionConfig.getSessionTrackingModes().addAll(temp.getSessionConfig().getSessionTrackingModes());
        }
        for (WebXml fragment28 : fragments) {
            if (!mergeMap(fragment28.getTaglibs(), this.taglibs, temp.getTaglibs(), fragment28, "Taglibs")) {
                return false;
            }
        }
        this.taglibs.putAll(temp.getTaglibs());
        for (WebXml fragment29 : fragments) {
            if (fragment29.alwaysAddWelcomeFiles || this.welcomeFiles.size() == 0) {
                for (String welcomeFile : fragment29.getWelcomeFiles()) {
                    addWelcomeFile(welcomeFile);
                }
            }
        }
        if (this.postConstructMethods.isEmpty()) {
            for (WebXml fragment30 : fragments) {
                if (!mergeLifecycleCallback(fragment30.getPostConstructMethods(), temp.getPostConstructMethods(), fragment30, "Post Construct Methods")) {
                    return false;
                }
            }
            this.postConstructMethods.putAll(temp.getPostConstructMethods());
        }
        if (this.preDestroyMethods.isEmpty()) {
            for (WebXml fragment31 : fragments) {
                if (!mergeLifecycleCallback(fragment31.getPreDestroyMethods(), temp.getPreDestroyMethods(), fragment31, "Pre Destroy Methods")) {
                    return false;
                }
            }
            this.preDestroyMethods.putAll(temp.getPreDestroyMethods());
            return true;
        }
        return true;
    }

    private <T extends ResourceBase> boolean mergeResourceMap(Map<String, T> fragmentResources, Map<String, T> mainResources, Map<String, T> tempResources, WebXml fragment) {
        for (T resource : fragmentResources.values()) {
            String resourceName = resource.getName();
            if (mainResources.containsKey(resourceName)) {
                mainResources.get(resourceName).getInjectionTargets().addAll(resource.getInjectionTargets());
            } else {
                T existingResource = tempResources.get(resourceName);
                if (existingResource != null) {
                    if (!existingResource.equals(resource)) {
                        this.log.error(sm.getString("webXml.mergeConflictResource", resourceName, fragment.getName(), fragment.getURL()));
                        return false;
                    }
                } else {
                    tempResources.put(resourceName, resource);
                }
            }
        }
        return true;
    }

    private <T> boolean mergeMap(Map<String, T> fragmentMap, Map<String, T> mainMap, Map<String, T> tempMap, WebXml fragment, String mapName) {
        for (Map.Entry<String, T> entry : fragmentMap.entrySet()) {
            String key = entry.getKey();
            if (!mainMap.containsKey(key)) {
                T value = entry.getValue();
                if (tempMap.containsKey(key)) {
                    if (value != null && !value.equals(tempMap.get(key))) {
                        this.log.error(sm.getString("webXml.mergeConflictString", mapName, key, fragment.getName(), fragment.getURL()));
                        return false;
                    }
                } else {
                    tempMap.put(key, value);
                }
            }
        }
        return true;
    }

    private static boolean mergeFilter(FilterDef src, FilterDef dest, boolean failOnConflict) {
        if (dest.getAsyncSupported() == null) {
            dest.setAsyncSupported(src.getAsyncSupported());
        } else if (src.getAsyncSupported() != null && failOnConflict && !src.getAsyncSupported().equals(dest.getAsyncSupported())) {
            return false;
        }
        if (dest.getFilterClass() == null) {
            dest.setFilterClass(src.getFilterClass());
        } else if (src.getFilterClass() != null && failOnConflict && !src.getFilterClass().equals(dest.getFilterClass())) {
            return false;
        }
        for (Map.Entry<String, String> srcEntry : src.getParameterMap().entrySet()) {
            if (dest.getParameterMap().containsKey(srcEntry.getKey())) {
                if (failOnConflict && !dest.getParameterMap().get(srcEntry.getKey()).equals(srcEntry.getValue())) {
                    return false;
                }
            } else {
                dest.addInitParameter(srcEntry.getKey(), srcEntry.getValue());
            }
        }
        return true;
    }

    private static boolean mergeServlet(ServletDef src, ServletDef dest, boolean failOnConflict) {
        if (dest.getServletClass() != null && dest.getJspFile() != null) {
            return false;
        }
        if (src.getServletClass() != null && src.getJspFile() != null) {
            return false;
        }
        if (dest.getServletClass() == null && dest.getJspFile() == null) {
            dest.setServletClass(src.getServletClass());
            dest.setJspFile(src.getJspFile());
        } else if (failOnConflict) {
            if (src.getServletClass() != null && (dest.getJspFile() != null || !src.getServletClass().equals(dest.getServletClass()))) {
                return false;
            }
            if (src.getJspFile() != null && (dest.getServletClass() != null || !src.getJspFile().equals(dest.getJspFile()))) {
                return false;
            }
        }
        for (SecurityRoleRef securityRoleRef : src.getSecurityRoleRefs()) {
            dest.addSecurityRoleRef(securityRoleRef);
        }
        if (dest.getLoadOnStartup() == null) {
            if (src.getLoadOnStartup() != null) {
                dest.setLoadOnStartup(src.getLoadOnStartup().toString());
            }
        } else if (src.getLoadOnStartup() != null && failOnConflict && !src.getLoadOnStartup().equals(dest.getLoadOnStartup())) {
            return false;
        }
        if (dest.getEnabled() == null) {
            if (src.getEnabled() != null) {
                dest.setEnabled(src.getEnabled().toString());
            }
        } else if (src.getEnabled() != null && failOnConflict && !src.getEnabled().equals(dest.getEnabled())) {
            return false;
        }
        for (Map.Entry<String, String> srcEntry : src.getParameterMap().entrySet()) {
            if (dest.getParameterMap().containsKey(srcEntry.getKey())) {
                if (failOnConflict && !dest.getParameterMap().get(srcEntry.getKey()).equals(srcEntry.getValue())) {
                    return false;
                }
            } else {
                dest.addInitParameter(srcEntry.getKey(), srcEntry.getValue());
            }
        }
        if (dest.getMultipartDef() == null) {
            dest.setMultipartDef(src.getMultipartDef());
        } else if (src.getMultipartDef() != null) {
            return mergeMultipartDef(src.getMultipartDef(), dest.getMultipartDef(), failOnConflict);
        }
        if (dest.getAsyncSupported() == null) {
            if (src.getAsyncSupported() != null) {
                dest.setAsyncSupported(src.getAsyncSupported().toString());
                return true;
            }
            return true;
        }
        if (src.getAsyncSupported() != null && failOnConflict && !src.getAsyncSupported().equals(dest.getAsyncSupported())) {
            return false;
        }
        return true;
    }

    private static boolean mergeMultipartDef(MultipartDef src, MultipartDef dest, boolean failOnConflict) {
        if (dest.getLocation() == null) {
            dest.setLocation(src.getLocation());
        } else if (src.getLocation() != null && failOnConflict && !src.getLocation().equals(dest.getLocation())) {
            return false;
        }
        if (dest.getFileSizeThreshold() == null) {
            dest.setFileSizeThreshold(src.getFileSizeThreshold());
        } else if (src.getFileSizeThreshold() != null && failOnConflict && !src.getFileSizeThreshold().equals(dest.getFileSizeThreshold())) {
            return false;
        }
        if (dest.getMaxFileSize() == null) {
            dest.setMaxFileSize(src.getMaxFileSize());
        } else if (src.getMaxFileSize() != null && failOnConflict && !src.getMaxFileSize().equals(dest.getMaxFileSize())) {
            return false;
        }
        if (dest.getMaxRequestSize() == null) {
            dest.setMaxRequestSize(src.getMaxRequestSize());
            return true;
        }
        if (src.getMaxRequestSize() != null && failOnConflict && !src.getMaxRequestSize().equals(dest.getMaxRequestSize())) {
            return false;
        }
        return true;
    }

    private boolean mergeLifecycleCallback(Map<String, String> fragmentMap, Map<String, String> tempMap, WebXml fragment, String mapName) {
        for (Map.Entry<String, String> entry : fragmentMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (tempMap.containsKey(key)) {
                if (value != null && !value.equals(tempMap.get(key))) {
                    this.log.error(sm.getString("webXml.mergeConflictString", mapName, key, fragment.getName(), fragment.getURL()));
                    return false;
                }
            } else {
                tempMap.put(key, value);
            }
        }
        return true;
    }

    public static Set<WebXml> orderWebFragments(WebXml application, Map<String, WebXml> fragments, ServletContext servletContext) {
        return application.orderWebFragments(fragments, servletContext);
    }

    private Set<WebXml> orderWebFragments(Map<String, WebXml> fragments, ServletContext servletContext) {
        WebXml fragment;
        Set<WebXml> orderedFragments = new LinkedHashSet<>();
        boolean absoluteOrdering = getAbsoluteOrdering() != null;
        boolean orderingPresent = false;
        if (absoluteOrdering) {
            orderingPresent = true;
            Set<String> requestedOrder = getAbsoluteOrdering();
            for (String requestedName : requestedOrder) {
                if (ORDER_OTHERS.equals(requestedName)) {
                    for (Map.Entry<String, WebXml> entry : fragments.entrySet()) {
                        if (!requestedOrder.contains(entry.getKey()) && (fragment = entry.getValue()) != null) {
                            orderedFragments.add(fragment);
                        }
                    }
                } else {
                    WebXml fragment2 = fragments.get(requestedName);
                    if (fragment2 != null) {
                        orderedFragments.add(fragment2);
                    } else {
                        this.log.warn(sm.getString("webXml.wrongFragmentName", requestedName));
                    }
                }
            }
        } else {
            for (WebXml fragment3 : fragments.values()) {
                if (fragment3.isDuplicated()) {
                    List<String> duplicates = fragment3.getDuplicates();
                    duplicates.add(0, fragment3.getURL().toString());
                    throw new IllegalArgumentException(sm.getString("webXml.duplicateFragment", fragment3.getName(), duplicates));
                }
            }
            for (WebXml fragment4 : fragments.values()) {
                Iterator<String> before = fragment4.getBeforeOrdering().iterator();
                while (before.hasNext()) {
                    orderingPresent = true;
                    String beforeEntry = before.next();
                    if (!beforeEntry.equals(ORDER_OTHERS)) {
                        WebXml beforeFragment = fragments.get(beforeEntry);
                        if (beforeFragment == null) {
                            before.remove();
                        } else {
                            beforeFragment.addAfterOrdering(fragment4.getName());
                        }
                    }
                }
                Iterator<String> after = fragment4.getAfterOrdering().iterator();
                while (after.hasNext()) {
                    orderingPresent = true;
                    String afterEntry = after.next();
                    if (!afterEntry.equals(ORDER_OTHERS)) {
                        WebXml afterFragment = fragments.get(afterEntry);
                        if (afterFragment == null) {
                            after.remove();
                        } else {
                            afterFragment.addBeforeOrdering(fragment4.getName());
                        }
                    }
                }
            }
            for (WebXml fragment5 : fragments.values()) {
                if (fragment5.getBeforeOrdering().contains(ORDER_OTHERS)) {
                    makeBeforeOthersExplicit(fragment5.getAfterOrdering(), fragments);
                }
                if (fragment5.getAfterOrdering().contains(ORDER_OTHERS)) {
                    makeAfterOthersExplicit(fragment5.getBeforeOrdering(), fragments);
                }
            }
            Set<WebXml> beforeSet = new HashSet<>();
            Set<WebXml> othersSet = new HashSet<>();
            Set<WebXml> afterSet = new HashSet<>();
            for (WebXml fragment6 : fragments.values()) {
                if (fragment6.getBeforeOrdering().contains(ORDER_OTHERS)) {
                    beforeSet.add(fragment6);
                    fragment6.getBeforeOrdering().remove(ORDER_OTHERS);
                } else if (fragment6.getAfterOrdering().contains(ORDER_OTHERS)) {
                    afterSet.add(fragment6);
                    fragment6.getAfterOrdering().remove(ORDER_OTHERS);
                } else {
                    othersSet.add(fragment6);
                }
            }
            decoupleOtherGroups(beforeSet);
            decoupleOtherGroups(othersSet);
            decoupleOtherGroups(afterSet);
            orderFragments(orderedFragments, beforeSet);
            orderFragments(orderedFragments, othersSet);
            orderFragments(orderedFragments, afterSet);
        }
        LinkedHashSet linkedHashSet = new LinkedHashSet();
        for (WebXml fragment7 : fragments.values()) {
            if (!fragment7.getWebappJar()) {
                linkedHashSet.add(fragment7);
                orderedFragments.remove(fragment7);
            }
        }
        if (servletContext != null) {
            List<String> orderedJarFileNames = null;
            if (orderingPresent) {
                orderedJarFileNames = new ArrayList<>();
                Iterator<WebXml> it = orderedFragments.iterator();
                while (it.hasNext()) {
                    orderedJarFileNames.add(it.next().getJarName());
                }
            }
            servletContext.setAttribute(ServletContext.ORDERED_LIBS, orderedJarFileNames);
        }
        if (linkedHashSet.size() > 0) {
            Set<WebXml> result = new LinkedHashSet<>();
            if (((WebXml) linkedHashSet.iterator().next()).getDelegate()) {
                result.addAll(linkedHashSet);
                result.addAll(orderedFragments);
            } else {
                result.addAll(orderedFragments);
                result.addAll(linkedHashSet);
            }
            return result;
        }
        return orderedFragments;
    }

    private static void decoupleOtherGroups(Set<WebXml> group) {
        Set<String> names = new HashSet<>();
        for (WebXml fragment : group) {
            names.add(fragment.getName());
        }
        for (WebXml fragment2 : group) {
            fragment2.getAfterOrdering().removeIf(entry -> {
                return !names.contains(entry);
            });
        }
    }

    private static void orderFragments(Set<WebXml> orderedFragments, Set<WebXml> unordered) {
        HashSet hashSet = new HashSet();
        Set<WebXml> addedLastRound = new HashSet<>();
        while (unordered.size() > 0) {
            Iterator<WebXml> source = unordered.iterator();
            while (source.hasNext()) {
                WebXml fragment = source.next();
                for (WebXml toRemove : addedLastRound) {
                    fragment.getAfterOrdering().remove(toRemove.getName());
                }
                if (fragment.getAfterOrdering().isEmpty()) {
                    hashSet.add(fragment);
                    orderedFragments.add(fragment);
                    source.remove();
                }
            }
            if (hashSet.size() == 0) {
                throw new IllegalArgumentException(sm.getString("webXml.mergeConflictOrder"));
            }
            addedLastRound.clear();
            addedLastRound.addAll(hashSet);
            hashSet.clear();
        }
    }

    private static void makeBeforeOthersExplicit(Set<String> beforeOrdering, Map<String, WebXml> fragments) {
        for (String before : beforeOrdering) {
            if (!before.equals(ORDER_OTHERS)) {
                WebXml webXml = fragments.get(before);
                if (!webXml.getBeforeOrdering().contains(ORDER_OTHERS)) {
                    webXml.addBeforeOrderingOthers();
                    makeBeforeOthersExplicit(webXml.getAfterOrdering(), fragments);
                }
            }
        }
    }

    private static void makeAfterOthersExplicit(Set<String> afterOrdering, Map<String, WebXml> fragments) {
        for (String after : afterOrdering) {
            if (!after.equals(ORDER_OTHERS)) {
                WebXml webXml = fragments.get(after);
                if (!webXml.getAfterOrdering().contains(ORDER_OTHERS)) {
                    webXml.addAfterOrderingOthers();
                    makeAfterOthersExplicit(webXml.getBeforeOrdering(), fragments);
                }
            }
        }
    }
}
