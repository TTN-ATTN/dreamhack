package freemarker.ext.jakarta.jsp;

import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.jakarta.servlet.FreemarkerServlet;
import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.log.Logger;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.Tag;
import java.beans.IntrospectionException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.springframework.util.ResourceUtils;
import org.xml.sax.Attributes;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory.class */
public class TaglibFactory implements TemplateHashModel {
    private static final int URL_TYPE_FULL = 0;
    private static final int URL_TYPE_ABSOLUTE = 1;
    private static final int URL_TYPE_RELATIVE = 2;
    private static final String META_INF_REL_PATH = "META-INF/";
    private static final String META_INF_ABS_PATH = "/META-INF/";
    private static final String DEFAULT_TLD_RESOURCE_PATH = "/META-INF/taglib.tld";
    private static final String JAR_URL_ENTRY_PATH_START = "!/";
    private final ServletContext servletContext;
    private ObjectWrapper objectWrapper;
    private List metaInfTldSources = DEFAULT_META_INF_TLD_SOURCES;
    private List classpathTlds = DEFAULT_CLASSPATH_TLDS;
    boolean test_emulateNoUrlToFileConversions = false;
    boolean test_emulateNoJarURLConnections = false;
    boolean test_emulateJarEntryUrlOpenStreamFails = false;
    private final Object lock = new Object();
    private final Map taglibs = new HashMap();
    private final Map tldLocations = new HashMap();
    private List failedTldLocations = new ArrayList();
    private int nextTldLocationLookupPhase = 0;
    public static final List DEFAULT_CLASSPATH_TLDS = Collections.EMPTY_LIST;
    public static final List DEFAULT_META_INF_TLD_SOURCES = Collections.singletonList(WebInfPerLibJarMetaInfTldSource.INSTANCE);
    private static final Logger LOG = Logger.getLogger("freemarker.jsp");
    private static final String PLATFORM_FILE_ENCODING = SecurityUtilities.getSystemProperty("file.encoding", "utf-8");

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$InputStreamFactory.class */
    private interface InputStreamFactory {
        InputStream getInputStream();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$TldLocation.class */
    private interface TldLocation {
        InputStream getInputStream() throws IOException;

        String getXmlSystemId() throws IOException;
    }

    public TaglibFactory(ServletContext ctx) {
        this.servletContext = ctx;
    }

    @Override // freemarker.template.TemplateHashModel
    public TemplateModel get(String taglibUri) throws TemplateModelException {
        String normalizedTaglibUri;
        TldLocation tldLocation;
        Taglib taglib;
        synchronized (this.lock) {
            Taglib taglib2 = (Taglib) this.taglibs.get(taglibUri);
            if (taglib2 != null) {
                return taglib2;
            }
            try {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Locating TLD for taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + ".");
                }
                TldLocation explicitlyMappedTldLocation = getExplicitlyMappedTldLocation(taglibUri);
                if (explicitlyMappedTldLocation != null) {
                    tldLocation = explicitlyMappedTldLocation;
                    normalizedTaglibUri = taglibUri;
                } else {
                    try {
                        int urlType = getUriType(taglibUri);
                        if (urlType == 2) {
                            normalizedTaglibUri = resolveRelativeUri(taglibUri);
                        } else if (urlType == 1) {
                            normalizedTaglibUri = taglibUri;
                        } else {
                            if (urlType == 0) {
                                String failedTLDsList = getFailedTLDsList();
                                throw new TaglibGettingException("No TLD was found for the " + StringUtil.jQuoteNoXSS(taglibUri) + " JSP taglib URI. (TLD-s are searched according the JSP 2.2 specification. In development- and embedded-servlet-container setups you may also need the \"MetaInfTldSources\" and \"ClasspathTlds\" " + FreemarkerServlet.class.getName() + " init-params or the similar system properites." + (failedTLDsList == null ? "" : " Also note these TLD-s were skipped earlier due to errors; see error in the log: " + failedTLDsList) + ")");
                            }
                            throw new BugException();
                        }
                        if (!normalizedTaglibUri.equals(taglibUri) && (taglib = (Taglib) this.taglibs.get(normalizedTaglibUri)) != null) {
                            return taglib;
                        }
                        tldLocation = isJarPath(normalizedTaglibUri) ? new ServletContextJarEntryTldLocation(normalizedTaglibUri, DEFAULT_TLD_RESOURCE_PATH) : new ServletContextTldLocation(normalizedTaglibUri);
                    } catch (MalformedURLException e) {
                        throw new TaglibGettingException("Malformed taglib URI: " + StringUtil.jQuote(taglibUri), e);
                    }
                }
                try {
                    return loadTaglib(tldLocation, normalizedTaglibUri);
                } catch (Exception e2) {
                    throw new TemplateModelException("Error while loading tag library for URI " + StringUtil.jQuoteNoXSS(normalizedTaglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation) + "; see cause exception.", e2);
                }
            } catch (Exception e3) {
                String failedTLDsList2 = 0 != 0 ? null : getFailedTLDsList();
                throw new TemplateModelException("Error while looking for TLD file for " + StringUtil.jQuoteNoXSS(taglibUri) + "; see cause exception." + (failedTLDsList2 == null ? "" : " (Note: These TLD-s were skipped earlier due to errors; see errors in the log: " + failedTLDsList2 + ")"), e3);
            }
        }
    }

    private String getFailedTLDsList() {
        synchronized (this.failedTldLocations) {
            if (this.failedTldLocations.isEmpty()) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < this.failedTldLocations.size(); i++) {
                if (i != 0) {
                    sb.append(", ");
                }
                sb.append(StringUtil.jQuote(this.failedTldLocations.get(i)));
            }
            return sb.toString();
        }
    }

    @Override // freemarker.template.TemplateHashModel
    public boolean isEmpty() {
        return false;
    }

    public ObjectWrapper getObjectWrapper() {
        return this.objectWrapper;
    }

    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        checkNotStarted();
        this.objectWrapper = objectWrapper;
    }

    public List getMetaInfTldSources() {
        return this.metaInfTldSources;
    }

    public void setMetaInfTldSources(List metaInfTldSources) {
        checkNotStarted();
        NullArgumentException.check("metaInfTldSources", metaInfTldSources);
        this.metaInfTldSources = metaInfTldSources;
    }

    public List getClasspathTlds() {
        return this.classpathTlds;
    }

    public void setClasspathTlds(List classpathTlds) {
        checkNotStarted();
        NullArgumentException.check("classpathTlds", classpathTlds);
        this.classpathTlds = classpathTlds;
    }

    private void checkNotStarted() {
        synchronized (this.lock) {
            if (this.nextTldLocationLookupPhase != 0) {
                throw new IllegalStateException(TaglibFactory.class.getName() + " object was already in use.");
            }
        }
    }

    private TldLocation getExplicitlyMappedTldLocation(String uri) throws SAXException, IOException, TaglibGettingException {
        while (true) {
            TldLocation tldLocation = (TldLocation) this.tldLocations.get(uri);
            if (tldLocation != null) {
                return tldLocation;
            }
            switch (this.nextTldLocationLookupPhase) {
                case 0:
                    addTldLocationsFromClasspathTlds();
                    break;
                case 1:
                    addTldLocationsFromWebXml();
                    break;
                case 2:
                    addTldLocationsFromWebInfTlds();
                    break;
                case 3:
                    addTldLocationsFromMetaInfTlds();
                    break;
                case 4:
                    return null;
                default:
                    throw new BugException();
            }
            this.nextTldLocationLookupPhase++;
        }
    }

    private void addTldLocationsFromWebXml() throws SAXException, IOException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/web.xml");
        WebXmlParser webXmlParser = new WebXmlParser();
        InputStream in = this.servletContext.getResourceAsStream("/WEB-INF/web.xml");
        if (in == null) {
            LOG.debug("No web.xml was found in servlet context");
            return;
        }
        try {
            parseXml(in, this.servletContext.getResource("/WEB-INF/web.xml").toExternalForm(), webXmlParser);
        } finally {
            in.close();
        }
    }

    private void addTldLocationsFromWebInfTlds() throws SAXException, IOException {
        LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/**/*.tld");
        addTldLocationsFromServletContextResourceTlds("/WEB-INF");
    }

    private void addTldLocationsFromServletContextResourceTlds(String basePath) throws SAXException, IOException {
        Set unsortedResourcePaths = this.servletContext.getResourcePaths(basePath);
        if (unsortedResourcePaths != null) {
            List<String> resourcePaths = new ArrayList(unsortedResourcePaths);
            Collections.sort(resourcePaths);
            for (String resourcePath : resourcePaths) {
                if (resourcePath.endsWith(".tld")) {
                    addTldLocationFromTld(new ServletContextTldLocation(resourcePath));
                }
            }
            for (String resourcePath2 : resourcePaths) {
                if (resourcePath2.endsWith("/")) {
                    addTldLocationsFromServletContextResourceTlds(resourcePath2);
                }
            }
        }
    }

    private void addTldLocationsFromMetaInfTlds() throws SAXException, IOException {
        String rootContainerUrl;
        if (this.metaInfTldSources == null || this.metaInfTldSources.isEmpty()) {
            return;
        }
        Set<URLWithExternalForm> cpMetaInfDirUrlsWithEF = null;
        int srcIdxStart = 0;
        int i = this.metaInfTldSources.size() - 1;
        while (true) {
            if (i < 0) {
                break;
            }
            if (this.metaInfTldSources.get(i) instanceof ClearMetaInfTldSource) {
                srcIdxStart = i + 1;
                break;
            }
            i--;
        }
        for (int srcIdx = srcIdxStart; srcIdx < this.metaInfTldSources.size(); srcIdx++) {
            MetaInfTldSource miTldSource = (MetaInfTldSource) this.metaInfTldSources.get(srcIdx);
            if (miTldSource == WebInfPerLibJarMetaInfTldSource.INSTANCE) {
                addTldLocationsFromWebInfPerLibJarMetaInfTlds();
            } else if (miTldSource instanceof ClasspathMetaInfTldSource) {
                ClasspathMetaInfTldSource cpMiTldLocation = (ClasspathMetaInfTldSource) miTldSource;
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Looking for TLD-s in classpathRoots[" + cpMiTldLocation.getRootContainerPattern() + "]" + META_INF_ABS_PATH + "**/*.tld");
                }
                if (cpMetaInfDirUrlsWithEF == null) {
                    cpMetaInfDirUrlsWithEF = collectMetaInfUrlsFromClassLoaders();
                }
                for (URLWithExternalForm urlWithEF : cpMetaInfDirUrlsWithEF) {
                    URL url = urlWithEF.getUrl();
                    boolean isJarUrl = isJarUrl(url);
                    String urlEF = urlWithEF.externalForm;
                    if (isJarUrl) {
                        int sep = urlEF.indexOf("!/");
                        rootContainerUrl = sep != -1 ? urlEF.substring(0, sep) : urlEF;
                    } else {
                        rootContainerUrl = urlEF.endsWith(META_INF_ABS_PATH) ? urlEF.substring(0, urlEF.length() - META_INF_REL_PATH.length()) : urlEF;
                    }
                    if (cpMiTldLocation.getRootContainerPattern().matcher(rootContainerUrl).matches()) {
                        File urlAsFile = urlToFileOrNull(url);
                        if (urlAsFile != null) {
                            addTldLocationsFromFileDirectory(urlAsFile);
                        } else if (isJarUrl) {
                            addTldLocationsFromJarDirectoryEntryURL(url);
                        } else if (LOG.isDebugEnabled()) {
                            LOG.debug("Can't list entries under this URL; TLD-s won't be discovered here: " + urlWithEF.getExternalForm());
                        }
                    }
                }
            } else {
                throw new BugException();
            }
        }
    }

    private void addTldLocationsFromWebInfPerLibJarMetaInfTlds() throws SAXException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Looking for TLD locations in servletContext:/WEB-INF/lib/*.{jar,zip}/META-INF/*.tld");
        }
        Set<String> libEntPaths = this.servletContext.getResourcePaths("/WEB-INF/lib");
        if (libEntPaths != null) {
            for (String libEntryPath : libEntPaths) {
                if (isJarPath(libEntryPath)) {
                    addTldLocationsFromServletContextJar(libEntryPath);
                }
            }
        }
    }

    /* JADX WARN: Finally extract failed */
    private void addTldLocationsFromClasspathTlds() throws SAXException, IOException, TaglibGettingException {
        InputStream in;
        if (this.classpathTlds == null || this.classpathTlds.size() == 0) {
            return;
        }
        LOG.debug("Looking for TLD locations in TLD-s specified in cfg.classpathTlds");
        for (String tldResourcePath : this.classpathTlds) {
            if (tldResourcePath.trim().length() == 0) {
                throw new TaglibGettingException("classpathTlds can't contain empty item");
            }
            if (!tldResourcePath.startsWith("/")) {
                tldResourcePath = "/" + tldResourcePath;
            }
            if (tldResourcePath.endsWith("/")) {
                throw new TaglibGettingException("classpathTlds can't specify a directory: " + tldResourcePath);
            }
            ClasspathTldLocation tldLocation = new ClasspathTldLocation(tldResourcePath);
            try {
                in = tldLocation.getInputStream();
            } catch (IOException e) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Ignored classpath TLD location " + StringUtil.jQuoteNoXSS(tldResourcePath) + " because of error", e);
                }
                in = null;
            }
            if (in != null) {
                try {
                    addTldLocationFromTld(in, tldLocation);
                    in.close();
                } catch (Throwable th) {
                    in.close();
                    throw th;
                }
            }
        }
    }

    private void addTldLocationsFromServletContextJar(String jarResourcePath) throws SAXException, IOException {
        String metaInfEntryPath = normalizeJarEntryPath(META_INF_ABS_PATH, true);
        JarFile jarFile = servletContextResourceToFileOrNull(jarResourcePath);
        if (jarFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/*.tld-s in JarFile: servletContext:" + jarResourcePath);
            }
            Enumeration entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                String curEntryPath = normalizeJarEntryPath(entries.nextElement().getName(), false);
                if (curEntryPath.startsWith(metaInfEntryPath) && curEntryPath.endsWith(".tld")) {
                    addTldLocationFromTld(new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath));
                }
            }
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/*.tld-s in ZipInputStream (slow): servletContext:" + jarResourcePath);
        }
        InputStream in = this.servletContext.getResourceAsStream(jarResourcePath);
        if (in == null) {
            throw new IOException("ServletContext resource not found: " + jarResourcePath);
        }
        try {
            ZipInputStream zipIn = new ZipInputStream(in);
            Throwable th = null;
            while (true) {
                try {
                    try {
                        ZipEntry curEntry = zipIn.getNextEntry();
                        if (curEntry == null) {
                            break;
                        }
                        String curEntryPath2 = normalizeJarEntryPath(curEntry.getName(), false);
                        if (curEntryPath2.startsWith(metaInfEntryPath) && curEntryPath2.endsWith(".tld")) {
                            addTldLocationFromTld(zipIn, new ServletContextJarEntryTldLocation(jarResourcePath, curEntryPath2));
                        }
                    } finally {
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th2;
                }
            }
            if (zipIn != null) {
                if (0 != 0) {
                    try {
                        zipIn.close();
                    } catch (Throwable th3) {
                        th.addSuppressed(th3);
                    }
                } else {
                    zipIn.close();
                }
            }
        } finally {
            in.close();
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [java.io.InputStream, java.net.URLConnection] */
    /* JADX WARN: Type inference failed for: r0v27 */
    /* JADX WARN: Type inference failed for: r0v28, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r15v6, types: [java.net.JarURLConnection] */
    private void addTldLocationsFromJarDirectoryEntryURL(URL url) throws SAXException, IOException {
        String strSubstring;
        String strNormalizeJarEntryPath;
        JarFile jarFile;
        String str;
        ?? OpenConnection = url.openConnection();
        if (!this.test_emulateNoJarURLConnections && (OpenConnection instanceof JarURLConnection)) {
            ?? r15 = (JarURLConnection) OpenConnection;
            jarFile = r15.getJarFile();
            strSubstring = null;
            strNormalizeJarEntryPath = normalizeJarEntryPath(r15.getEntryName(), true);
            str = r15;
            if (strNormalizeJarEntryPath == null) {
                throw newFailedToExtractEntryPathException(url);
            }
        } else {
            String externalForm = url.toExternalForm();
            int iIndexOf = externalForm.indexOf("!/");
            if (iIndexOf == -1) {
                throw newFailedToExtractEntryPathException(url);
            }
            strSubstring = externalForm.substring(externalForm.indexOf(58) + 1, iIndexOf);
            strNormalizeJarEntryPath = normalizeJarEntryPath(externalForm.substring(iIndexOf + "!/".length()), true);
            File fileUrlToFileOrNull = urlToFileOrNull(new URL(strSubstring));
            jarFile = fileUrlToFileOrNull != null ? new JarFile(fileUrlToFileOrNull) : null;
            str = externalForm;
        }
        if (jarFile != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for /META-INF/**/*.tld-s in random access mode: " + url);
            }
            Enumeration<JarEntry> enumerationEntries = jarFile.entries();
            while (enumerationEntries.hasMoreElements()) {
                String strNormalizeJarEntryPath2 = normalizeJarEntryPath(enumerationEntries.nextElement().getName(), false);
                if (strNormalizeJarEntryPath2.startsWith(strNormalizeJarEntryPath) && strNormalizeJarEntryPath2.endsWith(".tld")) {
                    addTldLocationFromTld(new JarEntryUrlTldLocation(createJarEntryUrl(url, strNormalizeJarEntryPath2.substring(strNormalizeJarEntryPath.length())), null));
                }
            }
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Scanning for /META-INF/**/*.tld-s in stream mode (slow): " + strSubstring);
        }
        try {
            try {
                InputStream inputStreamOpenStream = new URL(strSubstring).openStream();
                Throwable th = null;
                ZipInputStream zipInputStream = new ZipInputStream(inputStreamOpenStream);
                while (true) {
                    try {
                        ZipEntry nextEntry = zipInputStream.getNextEntry();
                        if (nextEntry == null) {
                            break;
                        }
                        String strNormalizeJarEntryPath3 = normalizeJarEntryPath(nextEntry.getName(), false);
                        if (strNormalizeJarEntryPath3.startsWith(strNormalizeJarEntryPath) && strNormalizeJarEntryPath3.endsWith(".tld")) {
                            addTldLocationFromTld(zipInputStream, new JarEntryUrlTldLocation(createJarEntryUrl(url, strNormalizeJarEntryPath3.substring(strNormalizeJarEntryPath.length())), null));
                        }
                    } catch (Throwable th2) {
                        zipInputStream.close();
                        throw th2;
                    }
                }
                zipInputStream.close();
                if (inputStreamOpenStream != null) {
                    if (0 != 0) {
                        try {
                            inputStreamOpenStream.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        inputStreamOpenStream.close();
                    }
                }
            } finally {
            }
        } catch (ZipException e) {
            IOException iOException = new IOException("Error reading ZIP (see cause excepetion) from: " + strSubstring);
            try {
                iOException.initCause(e);
                throw iOException;
            } catch (Exception e2) {
                throw e;
            }
        }
    }

    private void addTldLocationsFromFileDirectory(File dir) throws SAXException, IOException {
        if (dir.isDirectory()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Scanning for *.tld-s in File directory: " + StringUtil.jQuoteNoXSS(dir));
            }
            File[] tldFiles = dir.listFiles(new FilenameFilter() { // from class: freemarker.ext.jakarta.jsp.TaglibFactory.1
                @Override // java.io.FilenameFilter
                public boolean accept(File urlAsFile, String name) {
                    return TaglibFactory.isTldFileNameIgnoreCase(name);
                }
            });
            if (tldFiles == null) {
                throw new IOException("Can't list this directory for some reason: " + dir);
            }
            for (File file : tldFiles) {
                addTldLocationFromTld(new FileTldLocation(file));
            }
            return;
        }
        LOG.warn("Skipped scanning for *.tld for non-existent directory: " + StringUtil.jQuoteNoXSS(dir));
    }

    private void addTldLocationFromTld(TldLocation tldLocation) throws SAXException, IOException {
        InputStream in = tldLocation.getInputStream();
        Throwable th = null;
        try {
            try {
                addTldLocationFromTld(in, tldLocation);
                if (in != null) {
                    if (0 != 0) {
                        try {
                            in.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    in.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (in != null) {
                if (th != null) {
                    try {
                        in.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    in.close();
                }
            }
            throw th4;
        }
    }

    private void addTldLocationFromTld(InputStream reusedIn, TldLocation tldLocation) throws SAXException, IOException {
        String taglibUri;
        try {
            taglibUri = getTaglibUriFromTld(reusedIn, tldLocation.getXmlSystemId());
        } catch (SAXException e) {
            LOG.error("Error while parsing TLD; skipping: " + tldLocation, e);
            synchronized (this.failedTldLocations) {
                this.failedTldLocations.add(tldLocation.toString());
                taglibUri = null;
            }
        }
        if (taglibUri != null) {
            addTldLocation(tldLocation, taglibUri);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addTldLocation(TldLocation tldLocation, String taglibUri) {
        if (this.tldLocations.containsKey(taglibUri)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Ignored duplicate mapping of taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        } else {
            this.tldLocations.put(taglibUri, tldLocation);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Mapped taglib URI " + StringUtil.jQuoteNoXSS(taglibUri) + " to TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
            }
        }
    }

    private static Set collectMetaInfUrlsFromClassLoaders() throws IOException {
        Set metainfDirUrls = new TreeSet();
        ClassLoader tccl = tryGetThreadContextClassLoader();
        if (tccl != null) {
            collectMetaInfUrlsFromClassLoader(tccl, metainfDirUrls);
        }
        ClassLoader cccl = TaglibFactory.class.getClassLoader();
        if (!isDescendantOfOrSameAs(tccl, cccl)) {
            collectMetaInfUrlsFromClassLoader(cccl, metainfDirUrls);
        }
        return metainfDirUrls;
    }

    private static void collectMetaInfUrlsFromClassLoader(ClassLoader cl, Set metainfDirUrls) throws IOException {
        Enumeration urls = cl.getResources(META_INF_REL_PATH);
        if (urls != null) {
            while (urls.hasMoreElements()) {
                metainfDirUrls.add(new URLWithExternalForm(urls.nextElement()));
            }
        }
    }

    private String getTaglibUriFromTld(InputStream tldFileIn, String tldFileXmlSystemId) throws SAXException, IOException {
        TldParserForTaglibUriExtraction tldParser = new TldParserForTaglibUriExtraction();
        parseXml(tldFileIn, tldFileXmlSystemId, tldParser);
        return tldParser.getTaglibUri();
    }

    private TemplateHashModel loadTaglib(TldLocation tldLocation, String taglibUri) throws SAXException, IOException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Loading taglib for URI " + StringUtil.jQuoteNoXSS(taglibUri) + " from TLD location " + StringUtil.jQuoteNoXSS(tldLocation));
        }
        Taglib taglib = new Taglib(this.servletContext, tldLocation, this.objectWrapper);
        this.taglibs.put(taglibUri, taglib);
        this.tldLocations.remove(taglibUri);
        return taglib;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void parseXml(InputStream in, String systemId, DefaultHandler handler) throws SAXException, IOException {
        InputSource inSrc = new InputSource();
        inSrc.setSystemId(systemId);
        inSrc.setByteStream(toCloseIgnoring(in));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setNamespaceAware(false);
        factory.setValidating(false);
        try {
            XMLReader reader = factory.newSAXParser().getXMLReader();
            reader.setEntityResolver(new EmptyContentEntityResolver());
            reader.setContentHandler(handler);
            reader.setErrorHandler(handler);
            reader.parse(inSrc);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("XML parser setup failed", e);
        }
    }

    private static String resolveRelativeUri(String uri) throws TaglibGettingException {
        try {
            TemplateModel reqHash = Environment.getCurrentEnvironment().getVariable("__FreeMarkerServlet.Request__");
            if (reqHash instanceof HttpRequestHashModel) {
                HttpServletRequest req = ((HttpRequestHashModel) reqHash).getRequest();
                String pi = req.getPathInfo();
                String reqPath = req.getServletPath();
                if (reqPath == null) {
                    reqPath = "";
                }
                String reqPath2 = reqPath + (pi == null ? "" : pi);
                int lastSlash = reqPath2.lastIndexOf(47);
                if (lastSlash != -1) {
                    return reqPath2.substring(0, lastSlash + 1) + uri;
                }
                return '/' + uri;
            }
            throw new TaglibGettingException("Can't resolve relative URI " + uri + " as request URL information is unavailable.");
        } catch (TemplateModelException e) {
            throw new TaglibGettingException("Failed to get FreemarkerServlet request information", e);
        }
    }

    private static FilterInputStream toCloseIgnoring(InputStream in) {
        return new FilterInputStream(in) { // from class: freemarker.ext.jakarta.jsp.TaglibFactory.2
            @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
            public void close() {
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getUriType(String uri) throws MalformedURLException {
        int colon;
        if (uri == null) {
            throw new IllegalArgumentException("null is not a valid URI");
        }
        if (uri.length() == 0) {
            throw new MalformedURLException("empty string is not a valid URI");
        }
        char c0 = uri.charAt(0);
        if (c0 == '/') {
            return 1;
        }
        if (c0 < 'a' || c0 > 'z' || (colon = uri.indexOf(58)) == -1) {
            return 2;
        }
        for (int i = 1; i < colon; i++) {
            char c = uri.charAt(i);
            if ((c < 'a' || c > 'z') && ((c < '0' || c > '9') && c != '+' && c != '-' && c != '.')) {
                return 2;
            }
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isJarPath(String uriPath) {
        return uriPath.endsWith(".jar") || uriPath.endsWith(".zip");
    }

    private static boolean isJarUrl(URL url) {
        String scheme = url.getProtocol();
        return ResourceUtils.URL_PROTOCOL_JAR.equals(scheme) || ResourceUtils.URL_PROTOCOL_ZIP.equals(scheme) || ResourceUtils.URL_PROTOCOL_VFSZIP.equals(scheme) || ResourceUtils.URL_PROTOCOL_WSJAR.equals(scheme);
    }

    private static URL createJarEntryUrl(URL jarBaseEntryUrl, String relativeEntryPath) throws MalformedURLException {
        if (relativeEntryPath.startsWith("/")) {
            relativeEntryPath = relativeEntryPath.substring(1);
        }
        try {
            return new URL(jarBaseEntryUrl, StringUtil.URLPathEnc(relativeEntryPath, PLATFORM_FILE_ENCODING));
        } catch (UnsupportedEncodingException e) {
            throw new BugException();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String normalizeJarEntryPath(String jarEntryDirPath, boolean directory) {
        if (!jarEntryDirPath.startsWith("/")) {
            jarEntryDirPath = "/" + jarEntryDirPath;
        }
        if (directory && !jarEntryDirPath.endsWith("/")) {
            jarEntryDirPath = jarEntryDirPath + "/";
        }
        return jarEntryDirPath;
    }

    private static MalformedURLException newFailedToExtractEntryPathException(URL url) {
        return new MalformedURLException("Failed to extract jar entry path from: " + url);
    }

    private File urlToFileOrNull(URL url) throws UnsupportedEncodingException {
        String filePath;
        if (this.test_emulateNoUrlToFileConversions || !"file".equals(url.getProtocol())) {
            return null;
        }
        try {
            filePath = url.toURI().getSchemeSpecificPart();
        } catch (URISyntaxException e) {
            try {
                filePath = URLDecoder.decode(url.getFile(), PLATFORM_FILE_ENCODING);
            } catch (UnsupportedEncodingException e2) {
                throw new BugException(e2);
            }
        }
        return new File(filePath);
    }

    private JarFile servletContextResourceToFileOrNull(String jarResourcePath) throws IOException {
        URL jarResourceUrl = this.servletContext.getResource(jarResourcePath);
        if (jarResourceUrl == null) {
            LOG.error("ServletContext resource URL was null (missing resource?): " + jarResourcePath);
            return null;
        }
        File jarResourceAsFile = urlToFileOrNull(jarResourceUrl);
        if (jarResourceAsFile == null) {
            return null;
        }
        if (!jarResourceAsFile.isFile()) {
            LOG.error("Jar file doesn't exist - falling back to stream mode: " + jarResourceAsFile);
            return null;
        }
        return new JarFile(jarResourceAsFile);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static URL tryCreateServletContextJarEntryUrl(ServletContext servletContext, String servletContextJarFilePath, String entryPath) throws IOException {
        try {
            URL jarFileUrl = servletContext.getResource(servletContextJarFilePath);
            if (jarFileUrl == null) {
                throw new IOException("Servlet context resource not found: " + servletContextJarFilePath);
            }
            return new URL(ResourceUtils.JAR_URL_PREFIX + jarFileUrl.toURI() + "!/" + URLEncoder.encode(entryPath.startsWith("/") ? entryPath.substring(1) : entryPath, PLATFORM_FILE_ENCODING));
        } catch (Exception e) {
            LOG.error("Couldn't get URL for serlvetContext resource " + StringUtil.jQuoteNoXSS(servletContextJarFilePath) + " / jar entry " + StringUtil.jQuoteNoXSS(entryPath), e);
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isTldFileNameIgnoreCase(String name) {
        int dotIdx = name.lastIndexOf(46);
        if (dotIdx < 0) {
            return false;
        }
        String extension = name.substring(dotIdx + 1).toLowerCase();
        return extension.equalsIgnoreCase("tld");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ClassLoader tryGetThreadContextClassLoader() {
        ClassLoader tccl;
        try {
            tccl = Thread.currentThread().getContextClassLoader();
        } catch (SecurityException e) {
            tccl = null;
            LOG.warn("Can't access Thread Context ClassLoader", e);
        }
        return tccl;
    }

    private static boolean isDescendantOfOrSameAs(ClassLoader descendant, ClassLoader parent) {
        while (descendant != null) {
            if (descendant == parent) {
                return true;
            }
            descendant = descendant.getParent();
        }
        return false;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$MetaInfTldSource.class */
    public static abstract class MetaInfTldSource {
        private MetaInfTldSource() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$WebInfPerLibJarMetaInfTldSource.class */
    public static final class WebInfPerLibJarMetaInfTldSource extends MetaInfTldSource {
        public static final WebInfPerLibJarMetaInfTldSource INSTANCE = new WebInfPerLibJarMetaInfTldSource();

        private WebInfPerLibJarMetaInfTldSource() {
            super();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$ClasspathMetaInfTldSource.class */
    public static final class ClasspathMetaInfTldSource extends MetaInfTldSource {
        private final Pattern rootContainerPattern;

        public ClasspathMetaInfTldSource(Pattern rootContainerPattern) {
            super();
            this.rootContainerPattern = rootContainerPattern;
        }

        public Pattern getRootContainerPattern() {
            return this.rootContainerPattern;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$ClearMetaInfTldSource.class */
    public static final class ClearMetaInfTldSource extends MetaInfTldSource {
        public static final ClearMetaInfTldSource INSTANCE = new ClearMetaInfTldSource();

        private ClearMetaInfTldSource() {
            super();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$ServletContextTldLocation.class */
    private class ServletContextTldLocation implements TldLocation {
        private final String fileResourcePath;

        public ServletContextTldLocation(String fileResourcePath) {
            this.fileResourcePath = fileResourcePath;
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public InputStream getInputStream() throws IOException {
            InputStream in = TaglibFactory.this.servletContext.getResourceAsStream(this.fileResourcePath);
            if (in == null) {
                throw newResourceNotFoundException();
            }
            return in;
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public String getXmlSystemId() throws IOException {
            URL url = TaglibFactory.this.servletContext.getResource(this.fileResourcePath);
            if (url != null) {
                return url.toExternalForm();
            }
            return null;
        }

        private IOException newResourceNotFoundException() {
            return new IOException("Resource not found: servletContext:" + this.fileResourcePath);
        }

        public final String toString() {
            return "servletContext:" + this.fileResourcePath;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$ClasspathTldLocation.class */
    private static class ClasspathTldLocation implements TldLocation {
        private final String resourcePath;

        public ClasspathTldLocation(String resourcePath) {
            if (!resourcePath.startsWith("/")) {
                throw new IllegalArgumentException("\"resourcePath\" must start with /");
            }
            this.resourcePath = resourcePath;
        }

        public String toString() {
            return "classpath:" + this.resourcePath;
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public InputStream getInputStream() throws IOException {
            InputStream ins;
            ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
            if (tccl != null && (ins = ClassUtil.getReasourceAsStream(tccl, this.resourcePath, true)) != null) {
                return ins;
            }
            return ClassUtil.getReasourceAsStream(getClass(), this.resourcePath, false);
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public String getXmlSystemId() throws IOException {
            URL url;
            ClassLoader tccl = TaglibFactory.tryGetThreadContextClassLoader();
            if (tccl != null && (url = tccl.getResource(this.resourcePath)) != null) {
                return url.toExternalForm();
            }
            URL url2 = getClass().getResource(this.resourcePath);
            if (url2 == null) {
                return null;
            }
            return url2.toExternalForm();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$JarEntryTldLocation.class */
    private abstract class JarEntryTldLocation implements TldLocation {
        private final URL entryUrl;
        private final InputStreamFactory fallbackRawJarContentInputStreamFactory;
        private final String entryPath;

        public JarEntryTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory, String entryPath) {
            if (entryUrl == null) {
                NullArgumentException.check(fallbackRawJarContentInputStreamFactory);
                NullArgumentException.check(entryPath);
            }
            this.entryUrl = entryUrl;
            this.fallbackRawJarContentInputStreamFactory = fallbackRawJarContentInputStreamFactory;
            this.entryPath = entryPath != null ? TaglibFactory.normalizeJarEntryPath(entryPath, false) : null;
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public InputStream getInputStream() throws IOException {
            String entryPath;
            ZipEntry macthedJarEntry;
            if (this.entryUrl != null) {
                try {
                    if (TaglibFactory.this.test_emulateJarEntryUrlOpenStreamFails) {
                        throw new RuntimeException("Test only");
                    }
                    return this.entryUrl.openStream();
                } catch (Exception e) {
                    if (this.fallbackRawJarContentInputStreamFactory != null) {
                        TaglibFactory.LOG.error("Failed to open InputStream for URL (will try fallback stream): " + this.entryUrl);
                    } else {
                        if (e instanceof IOException) {
                            throw ((IOException) e);
                        }
                        if (e instanceof RuntimeException) {
                            throw ((RuntimeException) e);
                        }
                        throw new RuntimeException(e);
                    }
                }
            }
            if (this.entryPath != null) {
                entryPath = this.entryPath;
            } else {
                if (this.entryUrl == null) {
                    throw new IOException("Nothing to deduce jar entry path from.");
                }
                String urlEF = this.entryUrl.toExternalForm();
                int sepIdx = urlEF.indexOf("!/");
                if (sepIdx != -1) {
                    entryPath = TaglibFactory.normalizeJarEntryPath(URLDecoder.decode(urlEF.substring(sepIdx + "!/".length()), TaglibFactory.PLATFORM_FILE_ENCODING), false);
                } else {
                    throw new IOException("Couldn't extract jar entry path from: " + urlEF);
                }
            }
            InputStream rawIn = null;
            ZipInputStream zipIn = null;
            boolean returnedZipIn = false;
            try {
                rawIn = this.fallbackRawJarContentInputStreamFactory.getInputStream();
                if (rawIn == null) {
                    throw new IOException("Jar's InputStreamFactory (" + this.fallbackRawJarContentInputStreamFactory + ") says the resource doesn't exist.");
                }
                zipIn = new ZipInputStream(rawIn);
                do {
                    macthedJarEntry = zipIn.getNextEntry();
                    if (macthedJarEntry == null) {
                        throw new IOException("Could not find JAR entry " + StringUtil.jQuoteNoXSS(entryPath) + ".");
                    }
                } while (!entryPath.equals(TaglibFactory.normalizeJarEntryPath(macthedJarEntry.getName(), false)));
                returnedZipIn = true;
                return zipIn;
            } finally {
                if (!returnedZipIn) {
                    if (zipIn != null) {
                        zipIn.close();
                    }
                    if (rawIn != null) {
                        rawIn.close();
                    }
                }
            }
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public String getXmlSystemId() {
            if (this.entryUrl != null) {
                return this.entryUrl.toExternalForm();
            }
            return null;
        }

        public String toString() {
            return this.entryUrl != null ? this.entryUrl.toExternalForm() : "jar:{" + this.fallbackRawJarContentInputStreamFactory + "}!" + this.entryPath;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$JarEntryUrlTldLocation.class */
    private class JarEntryUrlTldLocation extends JarEntryTldLocation {
        private JarEntryUrlTldLocation(URL entryUrl, InputStreamFactory fallbackRawJarContentInputStreamFactory) {
            super(entryUrl, fallbackRawJarContentInputStreamFactory, null);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$ServletContextJarEntryTldLocation.class */
    private class ServletContextJarEntryTldLocation extends JarEntryTldLocation {
        private ServletContextJarEntryTldLocation(final String servletContextJarFilePath, String entryPath) {
            super(TaglibFactory.tryCreateServletContextJarEntryUrl(TaglibFactory.this.servletContext, servletContextJarFilePath, entryPath), new InputStreamFactory() { // from class: freemarker.ext.jakarta.jsp.TaglibFactory.ServletContextJarEntryTldLocation.1
                @Override // freemarker.ext.jakarta.jsp.TaglibFactory.InputStreamFactory
                public InputStream getInputStream() {
                    return taglibFactory.servletContext.getResourceAsStream(servletContextJarFilePath);
                }

                public String toString() {
                    return "servletContext:" + servletContextJarFilePath;
                }
            }, entryPath);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$FileTldLocation.class */
    private static class FileTldLocation implements TldLocation {
        private final File file;

        public FileTldLocation(File file) {
            this.file = file;
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public InputStream getInputStream() throws IOException {
            return new FileInputStream(this.file);
        }

        @Override // freemarker.ext.jakarta.jsp.TaglibFactory.TldLocation
        public String getXmlSystemId() throws IOException {
            return this.file.toURI().toURL().toExternalForm();
        }

        public String toString() {
            return this.file.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$Taglib.class */
    private static final class Taglib implements TemplateHashModel {
        private final Map tagsAndFunctions;

        Taglib(ServletContext ctx, TldLocation tldPath, ObjectWrapper wrapper) throws SAXException, IOException {
            this.tagsAndFunctions = parseToTagsAndFunctions(ctx, tldPath, wrapper);
        }

        @Override // freemarker.template.TemplateHashModel
        public TemplateModel get(String key) {
            return (TemplateModel) this.tagsAndFunctions.get(key);
        }

        @Override // freemarker.template.TemplateHashModel
        public boolean isEmpty() {
            return this.tagsAndFunctions.isEmpty();
        }

        private static final Map parseToTagsAndFunctions(ServletContext ctx, TldLocation tldLocation, ObjectWrapper objectWrapper) throws SAXException, IOException {
            TldParserForTaglibBuilding tldParser = new TldParserForTaglibBuilding(objectWrapper);
            InputStream in = tldLocation.getInputStream();
            Throwable th = null;
            try {
                try {
                    TaglibFactory.parseXml(in, tldLocation.getXmlSystemId(), tldParser);
                    if (in != null) {
                        if (0 != 0) {
                            try {
                                in.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            in.close();
                        }
                    }
                    EventForwarding eventForwarding = EventForwarding.getInstance(ctx);
                    if (eventForwarding != null) {
                        eventForwarding.addListeners(tldParser.getListeners());
                    } else if (tldParser.getListeners().size() > 0) {
                        throw new TldParsingSAXException("Event listeners specified in the TLD could not be  registered since the web application doesn't have a listener of class " + EventForwarding.class.getName() + ". To remedy this, add this element to web.xml:\n| <listener>\n|   <listener-class>" + EventForwarding.class.getName() + "</listener-class>\n| </listener>", null);
                    }
                    return tldParser.getTagsAndFunctions();
                } finally {
                }
            } catch (Throwable th3) {
                if (in != null) {
                    if (th != null) {
                        try {
                            in.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        in.close();
                    }
                }
                throw th3;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$WebXmlParser.class */
    private class WebXmlParser extends DefaultHandler {
        private static final String E_TAGLIB = "taglib";
        private static final String E_TAGLIB_LOCATION = "taglib-location";
        private static final String E_TAGLIB_URI = "taglib-uri";
        private StringBuilder cDataCollector;
        private String taglibUriCData;
        private String taglibLocationCData;
        private Locator locator;

        private WebXmlParser() {
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String nsuri, String localName, String qName, Attributes atts) {
            if (E_TAGLIB_URI.equals(qName) || E_TAGLIB_LOCATION.equals(qName)) {
                this.cDataCollector = new StringBuilder();
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String nsUri, String localName, String qName) throws TldParsingSAXException {
            if (E_TAGLIB_URI.equals(qName)) {
                this.taglibUriCData = this.cDataCollector.toString().trim();
                this.cDataCollector = null;
                return;
            }
            if (E_TAGLIB_LOCATION.equals(qName)) {
                this.taglibLocationCData = this.cDataCollector.toString().trim();
                if (this.taglibLocationCData.length() != 0) {
                    try {
                        if (TaglibFactory.getUriType(this.taglibLocationCData) == 2) {
                            this.taglibLocationCData = "/WEB-INF/" + this.taglibLocationCData;
                        }
                        this.cDataCollector = null;
                        return;
                    } catch (MalformedURLException e) {
                        throw new TldParsingSAXException("Failed to detect URI type for: " + this.taglibLocationCData, this.locator, e);
                    }
                }
                throw new TldParsingSAXException("Required \"taglib-uri\" element was missing or empty", this.locator);
            }
            if (E_TAGLIB.equals(qName)) {
                TaglibFactory.this.addTldLocation(TaglibFactory.isJarPath(this.taglibLocationCData) ? new ServletContextJarEntryTldLocation(this.taglibLocationCData, TaglibFactory.DEFAULT_TLD_RESOURCE_PATH) : TaglibFactory.this.new ServletContextTldLocation(this.taglibLocationCData), this.taglibUriCData);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$TldParserForTaglibUriExtraction.class */
    private static class TldParserForTaglibUriExtraction extends DefaultHandler {
        private static final String E_URI = "uri";
        private StringBuilder cDataCollector;
        private String uri;

        TldParserForTaglibUriExtraction() {
        }

        String getTaglibUri() {
            return this.uri;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String nsuri, String localName, String qName, Attributes atts) {
            if (E_URI.equals(qName)) {
                this.cDataCollector = new StringBuilder();
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String nsuri, String localName, String qName) {
            if (E_URI.equals(qName)) {
                this.uri = this.cDataCollector.toString().trim();
                this.cDataCollector = null;
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$TldParserForTaglibBuilding.class */
    static final class TldParserForTaglibBuilding extends DefaultHandler {
        private static final String E_TAG = "tag";
        private static final String E_NAME = "name";
        private static final String E_TAG_CLASS = "tag-class";
        private static final String E_TAG_CLASS_LEGACY = "tagclass";
        private static final String E_FUNCTION = "function";
        private static final String E_FUNCTION_CLASS = "function-class";
        private static final String E_FUNCTION_SIGNATURE = "function-signature";
        private static final String E_LISTENER = "listener";
        private static final String E_LISTENER_CLASS = "listener-class";
        private final BeansWrapper beansWrapper;
        private Locator locator;
        private StringBuilder cDataCollector;
        private String tagNameCData;
        private String tagClassCData;
        private String functionNameCData;
        private String functionClassCData;
        private String functionSignatureCData;
        private String listenerClassCData;
        private final Map<String, TemplateModel> tagsAndFunctions = new HashMap();
        private final List listeners = new ArrayList();
        private Stack stack = new Stack();

        TldParserForTaglibBuilding(ObjectWrapper wrapper) {
            if (wrapper instanceof BeansWrapper) {
                this.beansWrapper = (BeansWrapper) wrapper;
                return;
            }
            this.beansWrapper = null;
            if (TaglibFactory.LOG.isWarnEnabled()) {
                TaglibFactory.LOG.warn("Custom EL functions won't be loaded because " + (wrapper == null ? "no ObjectWrapper was specified for the TaglibFactory (via TaglibFactory.setObjectWrapper(...), exists since 2.3.22)" : "the ObjectWrapper wasn't instance of " + BeansWrapper.class.getName()) + ".");
            }
        }

        Map<String, TemplateModel> getTagsAndFunctions() {
            return this.tagsAndFunctions;
        }

        List getListeners() {
            return this.listeners;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void setDocumentLocator(Locator locator) {
            this.locator = locator;
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void startElement(String nsUri, String localName, String qName, Attributes atts) {
            this.stack.push(qName);
            if (this.stack.size() == 3) {
                if ("name".equals(qName) || E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName) || E_LISTENER_CLASS.equals(qName) || E_FUNCTION_CLASS.equals(qName) || E_FUNCTION_SIGNATURE.equals(qName)) {
                    this.cDataCollector = new StringBuilder();
                }
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void characters(char[] chars, int off, int len) {
            if (this.cDataCollector != null) {
                this.cDataCollector.append(chars, off, len);
            }
        }

        @Override // org.xml.sax.helpers.DefaultHandler, org.xml.sax.ContentHandler
        public void endElement(String nsuri, String localName, String qName) throws IllegalAccessException, InstantiationException, TldParsingSAXException {
            TemplateModel customTagModel;
            if (!this.stack.peek().equals(qName)) {
                throw new TldParsingSAXException("Unbalanced tag nesting at \"" + qName + "\" end-tag.", this.locator);
            }
            if (this.stack.size() == 3) {
                if ("name".equals(qName)) {
                    if (E_TAG.equals(this.stack.get(1))) {
                        this.tagNameCData = pullCData();
                    } else if (E_FUNCTION.equals(this.stack.get(1))) {
                        this.functionNameCData = pullCData();
                    }
                } else if (E_TAG_CLASS_LEGACY.equals(qName) || E_TAG_CLASS.equals(qName)) {
                    this.tagClassCData = pullCData();
                } else if (E_LISTENER_CLASS.equals(qName)) {
                    this.listenerClassCData = pullCData();
                } else if (E_FUNCTION_CLASS.equals(qName)) {
                    this.functionClassCData = pullCData();
                } else if (E_FUNCTION_SIGNATURE.equals(qName)) {
                    this.functionSignatureCData = pullCData();
                }
            } else if (this.stack.size() == 2) {
                if (E_TAG.equals(qName)) {
                    checkChildElementNotNull(qName, "name", this.tagNameCData);
                    checkChildElementNotNull(qName, E_TAG_CLASS, this.tagClassCData);
                    Class tagClass = resoveClassFromTLD(this.tagClassCData, "custom tag", this.tagNameCData);
                    try {
                        if (Tag.class.isAssignableFrom(tagClass)) {
                            customTagModel = new TagTransformModel(this.tagNameCData, tagClass);
                        } else {
                            customTagModel = new SimpleTagDirectiveModel(this.tagNameCData, tagClass);
                        }
                        TemplateModel replacedTagOrFunction = this.tagsAndFunctions.put(this.tagNameCData, customTagModel);
                        if (replacedTagOrFunction != null) {
                            if (!CustomTagAndELFunctionCombiner.canBeCombinedAsELFunction(replacedTagOrFunction)) {
                                TaglibFactory.LOG.warn("TLD contains multiple tags with name " + StringUtil.jQuote(this.tagNameCData) + "; keeping only the last one.");
                            } else {
                                this.tagsAndFunctions.put(this.tagNameCData, CustomTagAndELFunctionCombiner.combine(customTagModel, (TemplateMethodModelEx) replacedTagOrFunction));
                            }
                        }
                        this.tagNameCData = null;
                        this.tagClassCData = null;
                    } catch (IntrospectionException e) {
                        throw new TldParsingSAXException("JavaBean introspection failed on custom tag class " + this.tagClassCData, this.locator, e);
                    }
                } else if (E_FUNCTION.equals(qName) && this.beansWrapper != null) {
                    checkChildElementNotNull(qName, E_FUNCTION_CLASS, this.functionClassCData);
                    checkChildElementNotNull(qName, E_FUNCTION_SIGNATURE, this.functionSignatureCData);
                    checkChildElementNotNull(qName, "name", this.functionNameCData);
                    Class functionClass = resoveClassFromTLD(this.functionClassCData, "custom EL function", this.functionNameCData);
                    try {
                        Method functionMethod = TaglibMethodUtil.getMethodByFunctionSignature(functionClass, this.functionSignatureCData);
                        int modifiers = functionMethod.getModifiers();
                        if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers)) {
                            throw new TldParsingSAXException("The custom EL function method must be public and static: " + functionMethod, this.locator);
                        }
                        try {
                            TemplateMethodModelEx elFunctionModel = this.beansWrapper.wrap(null, functionMethod);
                            TemplateModel replacedTagOrFunction2 = this.tagsAndFunctions.put(this.functionNameCData, elFunctionModel);
                            if (replacedTagOrFunction2 != null) {
                                if (!CustomTagAndELFunctionCombiner.canBeCombinedAsCustomTag(replacedTagOrFunction2)) {
                                    TaglibFactory.LOG.warn("TLD contains multiple functions with name " + StringUtil.jQuote(this.functionNameCData) + "; keeping only the last one.");
                                } else {
                                    this.tagsAndFunctions.put(this.functionNameCData, CustomTagAndELFunctionCombiner.combine(replacedTagOrFunction2, elFunctionModel));
                                }
                            }
                            this.functionNameCData = null;
                            this.functionClassCData = null;
                            this.functionSignatureCData = null;
                        } catch (Exception e2) {
                            throw new TldParsingSAXException("FreeMarker object wrapping failed on method : " + functionMethod, this.locator);
                        }
                    } catch (Exception e3) {
                        throw new TldParsingSAXException("Error while trying to resolve signature " + StringUtil.jQuote(this.functionSignatureCData) + " on class " + StringUtil.jQuote(functionClass.getName()) + " for custom EL function " + StringUtil.jQuote(this.functionNameCData) + ".", this.locator, e3);
                    }
                } else if (E_LISTENER.equals(qName)) {
                    checkChildElementNotNull(qName, E_LISTENER_CLASS, this.listenerClassCData);
                    Class listenerClass = resoveClassFromTLD(this.listenerClassCData, E_LISTENER, null);
                    try {
                        Object listener = listenerClass.newInstance();
                        this.listeners.add(listener);
                        this.listenerClassCData = null;
                    } catch (Exception e4) {
                        throw new TldParsingSAXException("Failed to create new instantiate from listener class " + this.listenerClassCData, this.locator, e4);
                    }
                }
            }
            this.stack.pop();
        }

        private String pullCData() {
            String r = this.cDataCollector.toString().trim();
            this.cDataCollector = null;
            return r;
        }

        private void checkChildElementNotNull(String parentElementName, String childElementName, String value) throws TldParsingSAXException {
            if (value == null) {
                throw new TldParsingSAXException("Missing required \"" + childElementName + "\" element inside the \"" + parentElementName + "\" element.", this.locator);
            }
        }

        private Class resoveClassFromTLD(String className, String entryType, String entryName) throws TldParsingSAXException {
            try {
                return ClassUtil.forName(className);
            } catch (ClassNotFoundException | LinkageError e) {
                throw newTLDEntryClassLoadingException(e, className, entryType, entryName);
            }
        }

        private TldParsingSAXException newTLDEntryClassLoadingException(Throwable e, String className, String entryType, String entryName) throws TldParsingSAXException {
            int dotIdx = className.lastIndexOf(46);
            if (dotIdx != -1) {
                dotIdx = className.lastIndexOf(46, dotIdx - 1);
            }
            boolean looksLikeNestedClass = dotIdx != -1 && className.length() > dotIdx + 1 && Character.isUpperCase(className.charAt(dotIdx + 1));
            return new TldParsingSAXException((e instanceof ClassNotFoundException ? "Not found class " : "Can't load class ") + StringUtil.jQuote(className) + " for " + entryType + (entryName != null ? " " + StringUtil.jQuote(entryName) : "") + "." + (looksLikeNestedClass ? " Hint: Before nested classes, use \"$\", not \".\"." : ""), this.locator, e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$EmptyContentEntityResolver.class */
    private static final class EmptyContentEntityResolver implements EntityResolver {
        private EmptyContentEntityResolver() {
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) {
            InputSource is = new InputSource(new ByteArrayInputStream(new byte[0]));
            is.setPublicId(publicId);
            is.setSystemId(systemId);
            return is;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$TldParsingSAXException.class */
    private static class TldParsingSAXException extends SAXParseException {
        private final Throwable cause;

        TldParsingSAXException(String message, Locator locator) {
            this(message, locator, null);
        }

        TldParsingSAXException(String message, Locator locator, Throwable e) {
            super(message, locator, e instanceof Exception ? (Exception) e : new Exception("Unchecked exception; see cause", e));
            this.cause = e;
        }

        @Override // org.xml.sax.SAXException, java.lang.Throwable
        public String toString() {
            StringBuilder sb = new StringBuilder(getClass().getName());
            sb.append(": ");
            int startLn = sb.length();
            String systemId = getSystemId();
            String publicId = getPublicId();
            if (systemId != null || publicId != null) {
                sb.append("In ");
                if (systemId != null) {
                    sb.append(systemId);
                }
                if (publicId != null) {
                    if (systemId != null) {
                        sb.append(" (public ID: ");
                    }
                    sb.append(publicId);
                    if (systemId != null) {
                        sb.append(')');
                    }
                }
            }
            int line = getLineNumber();
            if (line != -1) {
                sb.append(sb.length() != startLn ? ", at " : "At ");
                sb.append("line ");
                sb.append(line);
                int col = getColumnNumber();
                if (col != -1) {
                    sb.append(", column ");
                    sb.append(col);
                }
            }
            String message = getLocalizedMessage();
            if (message != null) {
                if (sb.length() != startLn) {
                    sb.append(":\n");
                }
                sb.append(message);
            }
            return sb.toString();
        }

        @Override // java.lang.Throwable
        public Throwable getCause() {
            Throwable superCause = super.getCause();
            return superCause == null ? this.cause : superCause;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$URLWithExternalForm.class */
    private static class URLWithExternalForm implements Comparable {
        private final URL url;
        private final String externalForm;

        public URLWithExternalForm(URL url) {
            this.url = url;
            this.externalForm = url.toExternalForm();
        }

        public URL getUrl() {
            return this.url;
        }

        public String getExternalForm() {
            return this.externalForm;
        }

        public int hashCode() {
            return this.externalForm.hashCode();
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            return (that == null || getClass() != that.getClass() || this.externalForm.equals(((URLWithExternalForm) that).externalForm)) ? false : true;
        }

        public String toString() {
            return "URLWithExternalForm(" + this.externalForm + ")";
        }

        @Override // java.lang.Comparable
        public int compareTo(Object that) {
            return getExternalForm().compareTo(((URLWithExternalForm) that).getExternalForm());
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/TaglibFactory$TaglibGettingException.class */
    private static class TaglibGettingException extends Exception {
        public TaglibGettingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TaglibGettingException(String message) {
            super(message);
        }
    }
}
