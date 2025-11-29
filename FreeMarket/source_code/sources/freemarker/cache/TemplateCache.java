package freemarker.cache;

import freemarker.cache.MultiTemplateLoader;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.TemplateConfiguration;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateCache.class */
public class TemplateCache {
    public static final long DEFAULT_TEMPLATE_UPDATE_DELAY_MILLIS = 5000;
    private static final String ASTERISKSTR = "*";
    private static final char ASTERISK = '*';
    private static final char SLASH = '/';
    private static final String LOCALE_PART_SEPARATOR = "_";
    private final TemplateLoader templateLoader;
    private final CacheStorage storage;
    private final TemplateLookupStrategy templateLookupStrategy;
    private final TemplateNameFormat templateNameFormat;
    private final TemplateConfigurationFactory templateConfigurations;
    private final boolean isStorageConcurrent;
    private long updateDelay;
    private boolean localizedLookup;
    private Configuration config;
    private static final Logger LOG = Logger.getLogger("freemarker.cache");
    private static final Method INIT_CAUSE = getInitCauseMethod();

    @Deprecated
    public TemplateCache() {
        this(_TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0));
    }

    @Deprecated
    public TemplateCache(TemplateLoader templateLoader) {
        this(templateLoader, (Configuration) null);
    }

    @Deprecated
    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage) {
        this(templateLoader, cacheStorage, null);
    }

    public TemplateCache(TemplateLoader templateLoader, Configuration config) {
        this(templateLoader, _TemplateAPI.createDefaultCacheStorage(Configuration.VERSION_2_3_0), config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, Configuration config) {
        this(templateLoader, cacheStorage, _TemplateAPI.getDefaultTemplateLookupStrategy(Configuration.VERSION_2_3_0), _TemplateAPI.getDefaultTemplateNameFormat(Configuration.VERSION_2_3_0), config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, Configuration config) {
        this(templateLoader, cacheStorage, templateLookupStrategy, templateNameFormat, null, config);
    }

    public TemplateCache(TemplateLoader templateLoader, CacheStorage cacheStorage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations, Configuration config) {
        this.updateDelay = 5000L;
        this.localizedLookup = true;
        this.templateLoader = templateLoader;
        NullArgumentException.check(Configuration.CACHE_STORAGE_KEY_CAMEL_CASE, cacheStorage);
        this.storage = cacheStorage;
        this.isStorageConcurrent = (cacheStorage instanceof ConcurrentCacheStorage) && ((ConcurrentCacheStorage) cacheStorage).isConcurrent();
        NullArgumentException.check(Configuration.TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE, templateLookupStrategy);
        this.templateLookupStrategy = templateLookupStrategy;
        NullArgumentException.check(Configuration.TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE, templateNameFormat);
        this.templateNameFormat = templateNameFormat;
        this.templateConfigurations = templateConfigurations;
        this.config = config;
    }

    @Deprecated
    public void setConfiguration(Configuration config) {
        this.config = config;
        clear();
    }

    public TemplateLoader getTemplateLoader() {
        return this.templateLoader;
    }

    public CacheStorage getCacheStorage() {
        return this.storage;
    }

    public TemplateLookupStrategy getTemplateLookupStrategy() {
        return this.templateLookupStrategy;
    }

    public TemplateNameFormat getTemplateNameFormat() {
        return this.templateNameFormat;
    }

    public TemplateConfigurationFactory getTemplateConfigurations() {
        return this.templateConfigurations;
    }

    public MaybeMissingTemplate getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL) throws IOException {
        NullArgumentException.check("name", name);
        NullArgumentException.check("locale", locale);
        NullArgumentException.check("encoding", encoding);
        try {
            String name2 = this.templateNameFormat.normalizeRootBasedName(name);
            if (this.templateLoader == null) {
                return new MaybeMissingTemplate(name2, "The TemplateLoader was null.");
            }
            Template template = getTemplateInternal(name2, locale, customLookupCondition, encoding, parseAsFTL);
            return template != null ? new MaybeMissingTemplate(template) : new MaybeMissingTemplate(name2, (String) null);
        } catch (MalformedTemplateNameException e) {
            if (this.templateNameFormat != TemplateNameFormat.DEFAULT_2_3_0 || this.config.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_4_0) {
                throw e;
            }
            return new MaybeMissingTemplate((String) null, e);
        }
    }

    @Deprecated
    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws IOException {
        return getTemplate(name, locale, null, encoding, parseAsFTL).getTemplate();
    }

    @Deprecated
    protected static TemplateLoader createLegacyDefaultTemplateLoader() {
        return _TemplateAPI.createDefaultTemplateLoader(Configuration.VERSION_2_3_0);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private Template getTemplateInternal(String name, Locale locale, Object obj, String encoding, boolean parseAsFTL) throws IOException {
        CachedTemplate cachedTemplate;
        CachedTemplate cachedTemplate2;
        TemplateLookupResult newLookupResult;
        long lastModified;
        boolean debug = LOG.isDebugEnabled();
        String debugName = debug ? buildDebugName(name, locale, obj, encoding, parseAsFTL) : null;
        TemplateKey tk = new TemplateKey(name, locale, obj, encoding, parseAsFTL);
        if (this.isStorageConcurrent) {
            cachedTemplate = (CachedTemplate) this.storage.get(tk);
        } else {
            synchronized (this.storage) {
                cachedTemplate = (CachedTemplate) this.storage.get(tk);
            }
        }
        long now = System.currentTimeMillis();
        TemplateLookupResult newLookupResult2 = null;
        try {
            try {
                if (cachedTemplate == null) {
                    if (debug) {
                        LOG.debug("Couldn't find template in cache for " + debugName + "; will try to load it.");
                    }
                    cachedTemplate2 = new CachedTemplate();
                    cachedTemplate2.lastChecked = now;
                    newLookupResult = lookupTemplate(name, locale, obj);
                    if (!newLookupResult.isPositive()) {
                        storeNegativeLookup(tk, cachedTemplate2, null);
                        if (newLookupResult != null && newLookupResult.isPositive()) {
                            this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
                        }
                        return null;
                    }
                    lastModified = obj;
                    cachedTemplate2.lastModified = Long.MIN_VALUE;
                } else {
                    if (now - cachedTemplate.lastChecked < this.updateDelay) {
                        if (debug) {
                            LOG.debug(debugName + " cached copy not yet stale; using cached.");
                        }
                        Object t = cachedTemplate.templateOrException;
                        if ((t instanceof Template) || t == null) {
                            Template template = (Template) t;
                            if (0 != 0 && newLookupResult2.isPositive()) {
                                this.templateLoader.closeTemplateSource(newLookupResult2.getTemplateSource());
                            }
                            return template;
                        }
                        if (t instanceof RuntimeException) {
                            throwLoadFailedException((RuntimeException) t);
                        } else if (t instanceof IOException) {
                            throwLoadFailedException((IOException) t);
                        }
                        throw new BugException("t is " + t.getClass().getName());
                    }
                    cachedTemplate2 = cachedTemplate.cloneCachedTemplate();
                    cachedTemplate2.lastChecked = now;
                    newLookupResult = lookupTemplate(name, locale, obj);
                    if (!newLookupResult.isPositive()) {
                        if (debug) {
                            LOG.debug(debugName + " no source found.");
                        }
                        storeNegativeLookup(tk, cachedTemplate2, null);
                        if (newLookupResult != null && newLookupResult.isPositive()) {
                            this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
                        }
                        return null;
                    }
                    Object newLookupResultSource = newLookupResult.getTemplateSource();
                    lastModified = this.templateLoader.getLastModified(newLookupResultSource);
                    boolean lastModifiedNotChanged = lastModified == cachedTemplate2.lastModified;
                    boolean sourceEquals = newLookupResultSource.equals(cachedTemplate2.source);
                    if (lastModifiedNotChanged && sourceEquals) {
                        if (debug) {
                            LOG.debug(debugName + ": using cached since " + newLookupResultSource + " hasn't changed.");
                        }
                        storeCached(tk, cachedTemplate2);
                        Template template2 = (Template) cachedTemplate2.templateOrException;
                        if (newLookupResult != null && newLookupResult.isPositive()) {
                            this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
                        }
                        return template2;
                    }
                    if (debug) {
                        if (!sourceEquals) {
                            LOG.debug("Updating source because: sourceEquals=" + sourceEquals + ", newlyFoundSource=" + StringUtil.jQuoteNoXSS(newLookupResultSource) + ", cached.source=" + StringUtil.jQuoteNoXSS(cachedTemplate2.source));
                        } else if (!lastModifiedNotChanged) {
                            LOG.debug("Updating source because: lastModifiedNotChanged=" + lastModifiedNotChanged + ", cached.lastModified=" + cachedTemplate2.lastModified + " != source.lastModified=" + lastModified);
                        }
                    }
                }
                Object source = newLookupResult.getTemplateSource();
                cachedTemplate2.source = source;
                if (debug) {
                    LOG.debug("Loading template for " + debugName + " from " + StringUtil.jQuoteNoXSS(source));
                }
                long lastModified2 = lastModified == Long.MIN_VALUE ? this.templateLoader.getLastModified(source) : lastModified;
                Template template3 = loadTemplate(this.templateLoader, source, name, newLookupResult.getTemplateSourceName(), locale, obj, encoding, parseAsFTL);
                cachedTemplate2.templateOrException = template3;
                cachedTemplate2.lastModified = lastModified2;
                storeCached(tk, cachedTemplate2);
                if (newLookupResult != null && newLookupResult.isPositive()) {
                    this.templateLoader.closeTemplateSource(newLookupResult.getTemplateSource());
                }
                return template3;
            } catch (IOException e) {
                if (0 == 0) {
                    storeNegativeLookup(tk, cachedTemplate, e);
                }
                throw e;
            } catch (RuntimeException e2) {
                if (cachedTemplate != null) {
                    storeNegativeLookup(tk, cachedTemplate, e2);
                }
                throw e2;
            }
        } catch (Throwable th) {
            if (0 != 0 && newLookupResult2.isPositive()) {
                this.templateLoader.closeTemplateSource(newLookupResult2.getTemplateSource());
            }
            throw th;
        }
    }

    private static final Method getInitCauseMethod() {
        try {
            return Throwable.class.getMethod("initCause", Throwable.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private IOException newIOException(String message, Throwable cause) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        IOException ioe;
        if (cause == null) {
            return new IOException(message);
        }
        if (INIT_CAUSE != null) {
            ioe = new IOException(message);
            try {
                INIT_CAUSE.invoke(ioe, cause);
            } catch (RuntimeException ex) {
                throw ex;
            } catch (Exception ex2) {
                throw new UndeclaredThrowableException(ex2);
            }
        } else {
            ioe = new IOException(message + "\nCaused by: " + cause.getClass().getName() + ": " + cause.getMessage());
        }
        return ioe;
    }

    private void throwLoadFailedException(Throwable e) throws IOException {
        throw newIOException("There was an error loading the template on an earlier attempt; see cause exception.", e);
    }

    private void storeNegativeLookup(TemplateKey tk, CachedTemplate cachedTemplate, Exception e) {
        cachedTemplate.templateOrException = e;
        cachedTemplate.source = null;
        cachedTemplate.lastModified = 0L;
        storeCached(tk, cachedTemplate);
    }

    private void storeCached(TemplateKey tk, CachedTemplate cachedTemplate) {
        if (this.isStorageConcurrent) {
            this.storage.put(tk, cachedTemplate);
            return;
        }
        synchronized (this.storage) {
            this.storage.put(tk, cachedTemplate);
        }
    }

    private Template loadTemplate(TemplateLoader templateLoader, Object source, String name, String sourceName, Locale locale, Object customLookupCondition, String initialEncoding, boolean parseAsFTL) throws IOException {
        Reader reader;
        Template template;
        try {
            TemplateConfiguration tc = this.templateConfigurations != null ? this.templateConfigurations.get(sourceName, source) : null;
            if (tc != null) {
                if (tc.isEncodingSet()) {
                    initialEncoding = tc.getEncoding();
                }
                if (tc.isLocaleSet()) {
                    locale = tc.getLocale();
                }
            }
            if (parseAsFTL) {
                try {
                    Reader reader2 = templateLoader.getReader(source, initialEncoding);
                    Throwable th = null;
                    try {
                        template = new Template(name, sourceName, reader2, this.config, tc, initialEncoding);
                        if (reader2 != null) {
                            if (0 != 0) {
                                try {
                                    reader2.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            } else {
                                reader2.close();
                            }
                        }
                    } finally {
                    }
                } catch (Template.WrongEncodingException wee) {
                    String actualEncoding = wee.getTemplateSpecifiedEncoding();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Initial encoding \"" + initialEncoding + "\" was incorrect, re-reading with \"" + actualEncoding + "\". Template: " + sourceName);
                    }
                    reader = templateLoader.getReader(source, actualEncoding);
                    Throwable th3 = null;
                    try {
                        try {
                            template = new Template(name, sourceName, reader, this.config, tc, actualEncoding);
                            if (reader != null) {
                                if (0 != 0) {
                                    try {
                                        reader.close();
                                    } catch (Throwable th4) {
                                        th3.addSuppressed(th4);
                                    }
                                } else {
                                    reader.close();
                                }
                            }
                        } finally {
                        }
                    } finally {
                    }
                }
            } else {
                StringWriter sw = new StringWriter();
                char[] buf = new char[4096];
                reader = templateLoader.getReader(source, initialEncoding);
                Throwable th5 = null;
                while (true) {
                    try {
                        try {
                            int charsRead = reader.read(buf);
                            if (charsRead > 0) {
                                sw.write(buf, 0, charsRead);
                            } else if (charsRead < 0) {
                                break;
                            }
                        } finally {
                        }
                    } finally {
                    }
                }
                if (reader != null) {
                    if (0 != 0) {
                        try {
                            reader.close();
                        } catch (Throwable th6) {
                            th5.addSuppressed(th6);
                        }
                    } else {
                        reader.close();
                    }
                }
                template = Template.getPlainTextTemplate(name, sourceName, sw.toString(), this.config);
                template.setEncoding(initialEncoding);
            }
            if (tc != null) {
                tc.apply(template);
            }
            template.setLocale(locale);
            template.setCustomLookupCondition(customLookupCondition);
            return template;
        } catch (TemplateConfigurationFactoryException e) {
            throw newIOException("Error while getting TemplateConfiguration; see cause exception.", e);
        }
    }

    public long getDelay() {
        long j;
        synchronized (this) {
            j = this.updateDelay;
        }
        return j;
    }

    public void setDelay(long delay) {
        synchronized (this) {
            this.updateDelay = delay;
        }
    }

    public boolean getLocalizedLookup() {
        boolean z;
        synchronized (this) {
            z = this.localizedLookup;
        }
        return z;
    }

    public void setLocalizedLookup(boolean localizedLookup) {
        synchronized (this) {
            if (this.localizedLookup != localizedLookup) {
                this.localizedLookup = localizedLookup;
                clear();
            }
        }
    }

    public void clear() {
        synchronized (this.storage) {
            this.storage.clear();
            if (this.templateLoader instanceof StatefulTemplateLoader) {
                ((StatefulTemplateLoader) this.templateLoader).resetState();
            }
        }
    }

    public void removeTemplate(String name, Locale locale, String encoding, boolean parse) throws IOException {
        removeTemplate(name, locale, null, encoding, parse);
    }

    public void removeTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
        if (name == null) {
            throw new IllegalArgumentException("Argument \"name\" can't be null");
        }
        if (locale == null) {
            throw new IllegalArgumentException("Argument \"locale\" can't be null");
        }
        if (encoding == null) {
            throw new IllegalArgumentException("Argument \"encoding\" can't be null");
        }
        String name2 = this.templateNameFormat.normalizeRootBasedName(name);
        if (name2 != null && this.templateLoader != null) {
            boolean debug = LOG.isDebugEnabled();
            String debugName = debug ? buildDebugName(name2, locale, customLookupCondition, encoding, parse) : null;
            TemplateKey tk = new TemplateKey(name2, locale, customLookupCondition, encoding, parse);
            if (this.isStorageConcurrent) {
                this.storage.remove(tk);
            } else {
                synchronized (this.storage) {
                    this.storage.remove(tk);
                }
            }
            if (debug) {
                LOG.debug(debugName + " was removed from the cache, if it was there");
            }
        }
    }

    private String buildDebugName(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
        return StringUtil.jQuoteNoXSS(name) + "(" + StringUtil.jQuoteNoXSS(locale) + (customLookupCondition != null ? ", cond=" + StringUtil.jQuoteNoXSS(customLookupCondition) : "") + ", " + encoding + (parse ? ", parsed)" : ", unparsed]");
    }

    @Deprecated
    public static String getFullTemplatePath(Environment env, String baseName, String targetName) {
        try {
            return env.toFullTemplateName(baseName, targetName);
        } catch (MalformedTemplateNameException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private TemplateLookupResult lookupTemplate(String name, Locale locale, Object customLookupCondition) throws IOException {
        TemplateLookupResult lookupResult = this.templateLookupStrategy.lookup(new TemplateCacheTemplateLookupContext(name, locale, customLookupCondition));
        if (lookupResult == null) {
            throw new NullPointerException("Lookup result shouldn't be null");
        }
        return lookupResult;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TemplateLookupResult lookupTemplateWithAcquisitionStrategy(String path) throws IOException {
        int asterisk = path.indexOf(42);
        if (asterisk == -1) {
            return TemplateLookupResult.from(path, findTemplateSource(path));
        }
        StringTokenizer tok = new StringTokenizer(path, "/");
        int lastAsterisk = -1;
        List tokpath = new ArrayList();
        while (tok.hasMoreTokens()) {
            String pathToken = tok.nextToken();
            if (pathToken.equals("*")) {
                if (lastAsterisk != -1) {
                    tokpath.remove(lastAsterisk);
                }
                lastAsterisk = tokpath.size();
            }
            tokpath.add(pathToken);
        }
        if (lastAsterisk == -1) {
            return TemplateLookupResult.from(path, findTemplateSource(path));
        }
        String basePath = concatPath(tokpath, 0, lastAsterisk);
        String resourcePath = concatPath(tokpath, lastAsterisk + 1, tokpath.size());
        if (resourcePath.endsWith("/")) {
            resourcePath = resourcePath.substring(0, resourcePath.length() - 1);
        }
        StringBuilder buf = new StringBuilder(path.length()).append(basePath);
        int l = basePath.length();
        while (true) {
            String fullPath = buf.append(resourcePath).toString();
            Object templateSource = findTemplateSource(fullPath);
            if (templateSource != null) {
                return TemplateLookupResult.from(fullPath, templateSource);
            }
            if (l == 0) {
                return TemplateLookupResult.createNegativeResult();
            }
            l = basePath.lastIndexOf(47, l - 2) + 1;
            buf.setLength(l);
        }
    }

    private Object findTemplateSource(String path) throws IOException {
        Object result = this.templateLoader.findTemplateSource(path);
        if (LOG.isDebugEnabled()) {
            LOG.debug("TemplateLoader.findTemplateSource(" + StringUtil.jQuote(path) + "): " + (result == null ? "Not found" : "Found"));
        }
        return modifyForConfIcI(result);
    }

    private Object modifyForConfIcI(Object templateSource) {
        if (templateSource == null) {
            return null;
        }
        if (this.config.getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_21) {
            return templateSource;
        }
        if (templateSource instanceof URLTemplateSource) {
            URLTemplateSource urlTemplateSource = (URLTemplateSource) templateSource;
            if (urlTemplateSource.getUseCaches() == null) {
                urlTemplateSource.setUseCaches(false);
            }
        } else if (templateSource instanceof MultiTemplateLoader.MultiSource) {
            modifyForConfIcI(((MultiTemplateLoader.MultiSource) templateSource).getWrappedSource());
        }
        return templateSource;
    }

    private String concatPath(List path, int from, int to) {
        StringBuilder buf = new StringBuilder((to - from) * 16);
        for (int i = from; i < to; i++) {
            buf.append(path.get(i)).append('/');
        }
        return buf.toString();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateCache$TemplateKey.class */
    private static final class TemplateKey {
        private final String name;
        private final Locale locale;
        private final Object customLookupCondition;
        private final String encoding;
        private final boolean parse;

        TemplateKey(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) {
            this.name = name;
            this.locale = locale;
            this.customLookupCondition = customLookupCondition;
            this.encoding = encoding;
            this.parse = parse;
        }

        public boolean equals(Object o) {
            if (o instanceof TemplateKey) {
                TemplateKey tk = (TemplateKey) o;
                return this.parse == tk.parse && this.name.equals(tk.name) && this.locale.equals(tk.locale) && nullSafeEquals(this.customLookupCondition, tk.customLookupCondition) && this.encoding.equals(tk.encoding);
            }
            return false;
        }

        private boolean nullSafeEquals(Object o1, Object o2) {
            if (o1 == null) {
                return o2 == null;
            }
            if (o2 != null) {
                return o1.equals(o2);
            }
            return false;
        }

        public int hashCode() {
            return (((this.name.hashCode() ^ this.locale.hashCode()) ^ this.encoding.hashCode()) ^ (this.customLookupCondition != null ? this.customLookupCondition.hashCode() : 0)) ^ Boolean.valueOf(!this.parse).hashCode();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateCache$CachedTemplate.class */
    private static final class CachedTemplate implements Cloneable, Serializable {
        private static final long serialVersionUID = 1;
        Object templateOrException;
        Object source;
        long lastChecked;
        long lastModified;

        private CachedTemplate() {
        }

        public CachedTemplate cloneCachedTemplate() {
            try {
                return (CachedTemplate) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateCache$TemplateCacheTemplateLookupContext.class */
    private class TemplateCacheTemplateLookupContext extends TemplateLookupContext {
        TemplateCacheTemplateLookupContext(String templateName, Locale templateLocale, Object customLookupCondition) {
            super(templateName, TemplateCache.this.localizedLookup ? templateLocale : null, customLookupCondition);
        }

        @Override // freemarker.cache.TemplateLookupContext
        public TemplateLookupResult lookupWithAcquisitionStrategy(String name) throws IOException {
            if (!name.startsWith("/")) {
                return TemplateCache.this.lookupTemplateWithAcquisitionStrategy(name);
            }
            throw new IllegalArgumentException("Non-normalized name, starts with \"/\": " + name);
        }

        @Override // freemarker.cache.TemplateLookupContext
        public TemplateLookupResult lookupWithLocalizedThenAcquisitionStrategy(String templateName, Locale templateLocale) throws IOException {
            if (templateLocale == null) {
                return lookupWithAcquisitionStrategy(templateName);
            }
            int lastDot = templateName.lastIndexOf(46);
            String prefix = lastDot == -1 ? templateName : templateName.substring(0, lastDot);
            String suffix = lastDot == -1 ? "" : templateName.substring(lastDot);
            String localeName = "_" + templateLocale.toString();
            StringBuilder buf = new StringBuilder(templateName.length() + localeName.length());
            buf.append(prefix);
            while (true) {
                buf.setLength(prefix.length());
                String path = buf.append(localeName).append(suffix).toString();
                TemplateLookupResult lookupResult = lookupWithAcquisitionStrategy(path);
                if (lookupResult.isPositive()) {
                    return lookupResult;
                }
                int lastUnderscore = localeName.lastIndexOf(95);
                if (lastUnderscore != -1) {
                    localeName = localeName.substring(0, lastUnderscore);
                } else {
                    return createNegativeLookupResult();
                }
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateCache$MaybeMissingTemplate.class */
    public static final class MaybeMissingTemplate {
        private final Template template;
        private final String missingTemplateNormalizedName;
        private final String missingTemplateReason;
        private final MalformedTemplateNameException missingTemplateCauseException;

        private MaybeMissingTemplate(Template template) {
            this.template = template;
            this.missingTemplateNormalizedName = null;
            this.missingTemplateReason = null;
            this.missingTemplateCauseException = null;
        }

        private MaybeMissingTemplate(String normalizedName, MalformedTemplateNameException missingTemplateCauseException) {
            this.template = null;
            this.missingTemplateNormalizedName = normalizedName;
            this.missingTemplateReason = null;
            this.missingTemplateCauseException = missingTemplateCauseException;
        }

        private MaybeMissingTemplate(String normalizedName, String missingTemplateReason) {
            this.template = null;
            this.missingTemplateNormalizedName = normalizedName;
            this.missingTemplateReason = missingTemplateReason;
            this.missingTemplateCauseException = null;
        }

        public Template getTemplate() {
            return this.template;
        }

        public String getMissingTemplateReason() {
            if (this.missingTemplateReason != null) {
                return this.missingTemplateReason;
            }
            if (this.missingTemplateCauseException != null) {
                return this.missingTemplateCauseException.getMalformednessDescription();
            }
            return null;
        }

        public String getMissingTemplateNormalizedName() {
            return this.missingTemplateNormalizedName;
        }
    }
}
