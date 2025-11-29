package freemarker.template;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import ch.qos.logback.core.pattern.color.ANSIConstants;
import freemarker.cache.CacheStorage;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MruCacheStorage;
import freemarker.cache.SoftCacheStorage;
import freemarker.cache.TemplateCache;
import freemarker.cache.TemplateConfigurationFactory;
import freemarker.cache.TemplateLoader;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.cache.TemplateNameFormat;
import freemarker.core.BugException;
import freemarker.core.CFormat;
import freemarker.core.CSSOutputFormat;
import freemarker.core.CombinedMarkupOutputFormat;
import freemarker.core.Configurable;
import freemarker.core.Environment;
import freemarker.core.HTMLOutputFormat;
import freemarker.core.JSONOutputFormat;
import freemarker.core.JavaScriptOrJSONCFormat;
import freemarker.core.JavaScriptOutputFormat;
import freemarker.core.LegacyCFormat;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.OutputFormat;
import freemarker.core.ParseException;
import freemarker.core.ParserConfiguration;
import freemarker.core.PlainTextOutputFormat;
import freemarker.core.RTFOutputFormat;
import freemarker.core.UndefinedOutputFormat;
import freemarker.core.UnregisteredOutputFormatException;
import freemarker.core.XHTMLOutputFormat;
import freemarker.core.XMLOutputFormat;
import freemarker.core._CoreAPI;
import freemarker.core._DelayedJQuote;
import freemarker.core._MiscTemplateException;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.core._SortedArraySet;
import freemarker.core._UnmodifiableCompositeSet;
import freemarker.log.Logger;
import freemarker.template.utility.CaptureOutput;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.HtmlEscape;
import freemarker.template.utility.NormalizeNewlines;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StandardCompress;
import freemarker.template.utility.StringUtil;
import freemarker.template.utility.XmlEscape;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Configuration.class */
public class Configuration extends Configurable implements Cloneable, ParserConfiguration {
    private static final String VERSION_PROPERTIES_PATH = "/freemarker/version.properties";
    public static final String DEFAULT_ENCODING_KEY_SNAKE_CASE = "default_encoding";
    public static final String DEFAULT_ENCODING_KEY = "default_encoding";
    public static final String LOCALIZED_LOOKUP_KEY_SNAKE_CASE = "localized_lookup";
    public static final String LOCALIZED_LOOKUP_KEY = "localized_lookup";
    public static final String STRICT_SYNTAX_KEY_SNAKE_CASE = "strict_syntax";
    public static final String STRICT_SYNTAX_KEY = "strict_syntax";
    public static final String WHITESPACE_STRIPPING_KEY_SNAKE_CASE = "whitespace_stripping";
    public static final String WHITESPACE_STRIPPING_KEY = "whitespace_stripping";
    public static final String OUTPUT_FORMAT_KEY_SNAKE_CASE = "output_format";
    public static final String OUTPUT_FORMAT_KEY = "output_format";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_SNAKE_CASE = "recognize_standard_file_extensions";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY = "recognize_standard_file_extensions";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_SNAKE_CASE = "registered_custom_output_formats";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY = "registered_custom_output_formats";
    public static final String AUTO_ESCAPING_POLICY_KEY_SNAKE_CASE = "auto_escaping_policy";
    public static final String AUTO_ESCAPING_POLICY_KEY = "auto_escaping_policy";
    public static final String CACHE_STORAGE_KEY_SNAKE_CASE = "cache_storage";
    public static final String CACHE_STORAGE_KEY = "cache_storage";
    public static final String TEMPLATE_UPDATE_DELAY_KEY_SNAKE_CASE = "template_update_delay";
    public static final String TEMPLATE_UPDATE_DELAY_KEY = "template_update_delay";
    public static final String AUTO_IMPORT_KEY_SNAKE_CASE = "auto_import";
    public static final String AUTO_IMPORT_KEY_CAMEL_CASE = "autoImport";
    public static final String AUTO_IMPORT_KEY = "auto_import";
    public static final String AUTO_INCLUDE_KEY_SNAKE_CASE = "auto_include";
    public static final String AUTO_INCLUDE_KEY_CAMEL_CASE = "autoInclude";
    public static final String AUTO_INCLUDE_KEY = "auto_include";
    public static final String TAG_SYNTAX_KEY_SNAKE_CASE = "tag_syntax";
    public static final String TAG_SYNTAX_KEY = "tag_syntax";
    public static final String INTERPOLATION_SYNTAX_KEY_SNAKE_CASE = "interpolation_syntax";
    public static final String INTERPOLATION_SYNTAX_KEY = "interpolation_syntax";
    public static final String NAMING_CONVENTION_KEY_SNAKE_CASE = "naming_convention";
    public static final String NAMING_CONVENTION_KEY = "naming_convention";
    public static final String TAB_SIZE_KEY_SNAKE_CASE = "tab_size";
    public static final String TAB_SIZE_KEY = "tab_size";
    public static final String TEMPLATE_LOADER_KEY_SNAKE_CASE = "template_loader";
    public static final String TEMPLATE_LOADER_KEY = "template_loader";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_SNAKE_CASE = "template_lookup_strategy";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY = "template_lookup_strategy";
    public static final String TEMPLATE_NAME_FORMAT_KEY_SNAKE_CASE = "template_name_format";
    public static final String TEMPLATE_NAME_FORMAT_KEY = "template_name_format";
    public static final String TEMPLATE_CONFIGURATIONS_KEY_SNAKE_CASE = "template_configurations";
    public static final String TEMPLATE_CONFIGURATIONS_KEY = "template_configurations";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_SNAKE_CASE = "incompatible_improvements";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY = "incompatible_improvements";

    @Deprecated
    public static final String INCOMPATIBLE_IMPROVEMENTS = "incompatible_improvements";

    @Deprecated
    public static final String INCOMPATIBLE_ENHANCEMENTS = "incompatible_enhancements";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_SNAKE_CASE = "fallback_on_null_loop_variable";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY = "fallback_on_null_loop_variable";
    public static final int AUTO_DETECT_TAG_SYNTAX = 0;
    public static final int ANGLE_BRACKET_TAG_SYNTAX = 1;
    public static final int SQUARE_BRACKET_TAG_SYNTAX = 2;
    public static final int LEGACY_INTERPOLATION_SYNTAX = 20;
    public static final int DOLLAR_INTERPOLATION_SYNTAX = 21;
    public static final int SQUARE_BRACKET_INTERPOLATION_SYNTAX = 22;
    public static final int AUTO_DETECT_NAMING_CONVENTION = 10;
    public static final int LEGACY_NAMING_CONVENTION = 11;
    public static final int CAMEL_CASE_NAMING_CONVENTION = 12;
    public static final int DISABLE_AUTO_ESCAPING_POLICY = 20;
    public static final int ENABLE_IF_DEFAULT_AUTO_ESCAPING_POLICY = 21;
    public static final int ENABLE_IF_SUPPORTED_AUTO_ESCAPING_POLICY = 22;
    public static final int FORCE_AUTO_ESCAPING_POLICY = 23;
    public static final Version VERSION_2_3_0;
    public static final Version VERSION_2_3_19;
    public static final Version VERSION_2_3_20;
    public static final Version VERSION_2_3_21;
    public static final Version VERSION_2_3_22;
    public static final Version VERSION_2_3_23;
    public static final Version VERSION_2_3_24;
    public static final Version VERSION_2_3_25;
    public static final Version VERSION_2_3_26;
    public static final Version VERSION_2_3_27;
    public static final Version VERSION_2_3_28;
    public static final Version VERSION_2_3_29;
    public static final Version VERSION_2_3_30;
    public static final Version VERSION_2_3_31;
    public static final Version VERSION_2_3_32;
    public static final Version VERSION_2_3_33;
    public static final Version DEFAULT_INCOMPATIBLE_IMPROVEMENTS;

    @Deprecated
    public static final String DEFAULT_INCOMPATIBLE_ENHANCEMENTS;

    @Deprecated
    public static final int PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS;
    private static final String NULL = "null";
    private static final String DEFAULT = "default";
    private static final String JVM_DEFAULT = "JVM default";
    private static final Version VERSION;
    private static final String FM_24_DETECTION_CLASS_NAME = "freemarker.core._2_4_OrLaterMarker";
    private static final boolean FM_24_DETECTED;
    private static final Object defaultConfigLock;
    private static volatile Configuration defaultConfig;
    private boolean strictSyntax;
    private volatile boolean localizedLookup;
    private boolean whitespaceStripping;
    private int autoEscapingPolicy;
    private OutputFormat outputFormat;
    private boolean outputFormatExplicitlySet;
    private Boolean recognizeStandardFileExtensions;
    private Map<String, ? extends OutputFormat> registeredCustomOutputFormats;
    private Version incompatibleImprovements;
    private int tagSyntax;
    private int interpolationSyntax;
    private int namingConvention;
    private int tabSize;
    private boolean fallbackOnNullLoopVariable;
    private boolean preventStrippings;
    private TemplateCache cache;
    private boolean templateLoaderExplicitlySet;
    private boolean templateLookupStrategyExplicitlySet;
    private boolean templateNameFormatExplicitlySet;
    private boolean cacheStorageExplicitlySet;
    private boolean objectWrapperExplicitlySet;
    private boolean templateExceptionHandlerExplicitlySet;
    private boolean attemptExceptionReporterExplicitlySet;
    private boolean logTemplateExceptionsExplicitlySet;
    private boolean wrapUncheckedExceptionsExplicitlySet;
    private boolean localeExplicitlySet;
    private boolean defaultEncodingExplicitlySet;
    private boolean timeZoneExplicitlySet;
    private boolean cFormatExplicitlySet;
    private HashMap sharedVariables;
    private HashMap rewrappableSharedVariables;
    private String defaultEncoding;
    private ConcurrentMap localeToCharsetMap;
    private static final Logger CACHE_LOG = Logger.getLogger("freemarker.cache");
    private static final String[] SETTING_NAMES_SNAKE_CASE = {"auto_escaping_policy", "cache_storage", "default_encoding", "fallback_on_null_loop_variable", "incompatible_improvements", "interpolation_syntax", "localized_lookup", "naming_convention", "output_format", "recognize_standard_file_extensions", "registered_custom_output_formats", "strict_syntax", "tab_size", "tag_syntax", "template_configurations", "template_loader", "template_lookup_strategy", "template_name_format", "template_update_delay", "whitespace_stripping"};
    public static final String AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE = "autoEscapingPolicy";
    public static final String CACHE_STORAGE_KEY_CAMEL_CASE = "cacheStorage";
    public static final String DEFAULT_ENCODING_KEY_CAMEL_CASE = "defaultEncoding";
    public static final String FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE = "fallbackOnNullLoopVariable";
    public static final String INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE = "incompatibleImprovements";
    public static final String INTERPOLATION_SYNTAX_KEY_CAMEL_CASE = "interpolationSyntax";
    public static final String LOCALIZED_LOOKUP_KEY_CAMEL_CASE = "localizedLookup";
    public static final String NAMING_CONVENTION_KEY_CAMEL_CASE = "namingConvention";
    public static final String OUTPUT_FORMAT_KEY_CAMEL_CASE = "outputFormat";
    public static final String RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE = "recognizeStandardFileExtensions";
    public static final String REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE = "registeredCustomOutputFormats";
    public static final String STRICT_SYNTAX_KEY_CAMEL_CASE = "strictSyntax";
    public static final String TAB_SIZE_KEY_CAMEL_CASE = "tabSize";
    public static final String TAG_SYNTAX_KEY_CAMEL_CASE = "tagSyntax";
    public static final String TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE = "templateConfigurations";
    public static final String TEMPLATE_LOADER_KEY_CAMEL_CASE = "templateLoader";
    public static final String TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE = "templateLookupStrategy";
    public static final String TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE = "templateNameFormat";
    public static final String TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE = "templateUpdateDelay";
    public static final String WHITESPACE_STRIPPING_KEY_CAMEL_CASE = "whitespaceStripping";
    private static final String[] SETTING_NAMES_CAMEL_CASE = {AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE, CACHE_STORAGE_KEY_CAMEL_CASE, DEFAULT_ENCODING_KEY_CAMEL_CASE, FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE, INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE, INTERPOLATION_SYNTAX_KEY_CAMEL_CASE, LOCALIZED_LOOKUP_KEY_CAMEL_CASE, NAMING_CONVENTION_KEY_CAMEL_CASE, OUTPUT_FORMAT_KEY_CAMEL_CASE, RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE, REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE, STRICT_SYNTAX_KEY_CAMEL_CASE, TAB_SIZE_KEY_CAMEL_CASE, TAG_SYNTAX_KEY_CAMEL_CASE, TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE, TEMPLATE_LOADER_KEY_CAMEL_CASE, TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE, TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE, TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE, WHITESPACE_STRIPPING_KEY_CAMEL_CASE};
    private static final Map<String, OutputFormat> STANDARD_OUTPUT_FORMATS = new HashMap();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Configuration$LegacyDefaultFileTemplateLoader.class */
    private static class LegacyDefaultFileTemplateLoader extends FileTemplateLoader {
    }

    static {
        boolean fm24detected;
        STANDARD_OUTPUT_FORMATS.put(UndefinedOutputFormat.INSTANCE.getName(), UndefinedOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(HTMLOutputFormat.INSTANCE.getName(), HTMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(XHTMLOutputFormat.INSTANCE.getName(), XHTMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(XMLOutputFormat.INSTANCE.getName(), XMLOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(RTFOutputFormat.INSTANCE.getName(), RTFOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(PlainTextOutputFormat.INSTANCE.getName(), PlainTextOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(CSSOutputFormat.INSTANCE.getName(), CSSOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(JavaScriptOutputFormat.INSTANCE.getName(), JavaScriptOutputFormat.INSTANCE);
        STANDARD_OUTPUT_FORMATS.put(JSONOutputFormat.INSTANCE.getName(), JSONOutputFormat.INSTANCE);
        VERSION_2_3_0 = new Version(2, 3, 0);
        VERSION_2_3_19 = new Version(2, 3, 19);
        VERSION_2_3_20 = new Version(2, 3, 20);
        VERSION_2_3_21 = new Version(2, 3, 21);
        VERSION_2_3_22 = new Version(2, 3, 22);
        VERSION_2_3_23 = new Version(2, 3, 23);
        VERSION_2_3_24 = new Version(2, 3, 24);
        VERSION_2_3_25 = new Version(2, 3, 25);
        VERSION_2_3_26 = new Version(2, 3, 26);
        VERSION_2_3_27 = new Version(2, 3, 27);
        VERSION_2_3_28 = new Version(2, 3, 28);
        VERSION_2_3_29 = new Version(2, 3, 29);
        VERSION_2_3_30 = new Version(2, 3, 30);
        VERSION_2_3_31 = new Version(2, 3, 31);
        VERSION_2_3_32 = new Version(2, 3, 32);
        VERSION_2_3_33 = new Version(2, 3, 33);
        DEFAULT_INCOMPATIBLE_IMPROVEMENTS = VERSION_2_3_0;
        DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.toString();
        PARSED_DEFAULT_INCOMPATIBLE_ENHANCEMENTS = DEFAULT_INCOMPATIBLE_IMPROVEMENTS.intValue();
        try {
            Properties props = ClassUtil.loadProperties(Configuration.class, VERSION_PROPERTIES_PATH);
            String versionString = getRequiredVersionProperty(props, "version");
            Boolean gaeCompliant = Boolean.valueOf(getRequiredVersionProperty(props, "isGAECompliant"));
            VERSION = new Version(versionString, gaeCompliant, (Date) null);
            try {
                Class.forName(FM_24_DETECTION_CLASS_NAME);
                fm24detected = true;
            } catch (ClassNotFoundException e) {
                fm24detected = false;
            } catch (LinkageError e2) {
                fm24detected = true;
            } catch (Throwable th) {
                fm24detected = false;
            }
            FM_24_DETECTED = fm24detected;
            defaultConfigLock = new Object();
        } catch (IOException e3) {
            throw new RuntimeException("Failed to load and parse /freemarker/version.properties", e3);
        }
    }

    @Deprecated
    public Configuration() {
        this(DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public Configuration(Version incompatibleImprovements) {
        super(incompatibleImprovements);
        this.strictSyntax = true;
        this.localizedLookup = true;
        this.whitespaceStripping = true;
        this.autoEscapingPolicy = 21;
        this.outputFormat = UndefinedOutputFormat.INSTANCE;
        this.registeredCustomOutputFormats = Collections.emptyMap();
        this.tagSyntax = 1;
        this.interpolationSyntax = 20;
        this.namingConvention = 10;
        this.tabSize = 8;
        this.fallbackOnNullLoopVariable = true;
        this.sharedVariables = new HashMap();
        this.rewrappableSharedVariables = null;
        this.defaultEncoding = getDefaultDefaultEncoding();
        this.localeToCharsetMap = new ConcurrentHashMap();
        checkFreeMarkerVersionClash();
        NullArgumentException.check(INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE, incompatibleImprovements);
        checkCurrentVersionNotRecycled(incompatibleImprovements);
        this.incompatibleImprovements = incompatibleImprovements;
        createTemplateCache();
        loadBuiltInSharedVariables();
    }

    private static void checkFreeMarkerVersionClash() {
        if (FM_24_DETECTED) {
            throw new RuntimeException("Clashing FreeMarker versions (" + VERSION + " and some post-2.3.x) detected: found post-2.3.x class " + FM_24_DETECTION_CLASS_NAME + ". You probably have two different freemarker.jar-s in the classpath.");
        }
    }

    private void createTemplateCache() {
        this.cache = new TemplateCache(getDefaultTemplateLoader(), getDefaultCacheStorage(), getDefaultTemplateLookupStrategy(), getDefaultTemplateNameFormat(), null, this);
        this.cache.clear();
        this.cache.setDelay(5000L);
    }

    private void recreateTemplateCacheWith(TemplateLoader loader, CacheStorage storage, TemplateLookupStrategy templateLookupStrategy, TemplateNameFormat templateNameFormat, TemplateConfigurationFactory templateConfigurations) {
        TemplateCache oldCache = this.cache;
        this.cache = new TemplateCache(loader, storage, templateLookupStrategy, templateNameFormat, templateConfigurations, this);
        this.cache.clear();
        this.cache.setDelay(oldCache.getDelay());
        this.cache.setLocalizedLookup(this.localizedLookup);
    }

    private void recreateTemplateCache() {
        recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), getTemplateConfigurations());
    }

    private TemplateLoader getDefaultTemplateLoader() {
        return createDefaultTemplateLoader(getIncompatibleImprovements(), getTemplateLoader());
    }

    static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements) {
        return createDefaultTemplateLoader(incompatibleImprovements, null);
    }

    private static TemplateLoader createDefaultTemplateLoader(Version incompatibleImprovements, TemplateLoader existingTemplateLoader) {
        if (incompatibleImprovements.intValue() < _VersionInts.V_2_3_21) {
            if (existingTemplateLoader instanceof LegacyDefaultFileTemplateLoader) {
                return existingTemplateLoader;
            }
            try {
                return new LegacyDefaultFileTemplateLoader();
            } catch (Exception e) {
                CACHE_LOG.warn("Couldn't create legacy default TemplateLoader which accesses the current directory. (Use new Configuration(Configuration.VERSION_2_3_21) or higher to avoid this.)", e);
                return null;
            }
        }
        return null;
    }

    private TemplateLookupStrategy getDefaultTemplateLookupStrategy() {
        return getDefaultTemplateLookupStrategy(getIncompatibleImprovements());
    }

    static TemplateLookupStrategy getDefaultTemplateLookupStrategy(Version incompatibleImprovements) {
        return TemplateLookupStrategy.DEFAULT_2_3_0;
    }

    private TemplateNameFormat getDefaultTemplateNameFormat() {
        return getDefaultTemplateNameFormat(getIncompatibleImprovements());
    }

    static TemplateNameFormat getDefaultTemplateNameFormat(Version incompatibleImprovements) {
        return TemplateNameFormat.DEFAULT_2_3_0;
    }

    private CacheStorage getDefaultCacheStorage() {
        return createDefaultCacheStorage(getIncompatibleImprovements(), getCacheStorage());
    }

    static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements, CacheStorage existingCacheStorage) {
        if (existingCacheStorage instanceof DefaultSoftCacheStorage) {
            return existingCacheStorage;
        }
        return new DefaultSoftCacheStorage();
    }

    static CacheStorage createDefaultCacheStorage(Version incompatibleImprovements) {
        return createDefaultCacheStorage(incompatibleImprovements, null);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/Configuration$DefaultSoftCacheStorage.class */
    private static class DefaultSoftCacheStorage extends SoftCacheStorage {
        private DefaultSoftCacheStorage() {
        }
    }

    private TemplateExceptionHandler getDefaultTemplateExceptionHandler() {
        return getDefaultTemplateExceptionHandler(getIncompatibleImprovements());
    }

    private AttemptExceptionReporter getDefaultAttemptExceptionReporter() {
        return getDefaultAttemptExceptionReporter(getIncompatibleImprovements());
    }

    private boolean getDefaultLogTemplateExceptions() {
        return getDefaultLogTemplateExceptions(getIncompatibleImprovements());
    }

    private boolean getDefaultWrapUncheckedExceptions() {
        return getDefaultWrapUncheckedExceptions(getIncompatibleImprovements());
    }

    private ObjectWrapper getDefaultObjectWrapper() {
        return getDefaultObjectWrapper(getIncompatibleImprovements());
    }

    static TemplateExceptionHandler getDefaultTemplateExceptionHandler(Version incompatibleImprovements) {
        return TemplateExceptionHandler.DEBUG_HANDLER;
    }

    static AttemptExceptionReporter getDefaultAttemptExceptionReporter(Version incompatibleImprovements) {
        return AttemptExceptionReporter.LOG_ERROR_REPORTER;
    }

    static boolean getDefaultLogTemplateExceptions(Version incompatibleImprovements) {
        return true;
    }

    static boolean getDefaultWrapUncheckedExceptions(Version incompatibleImprovements) {
        return false;
    }

    @Override // freemarker.core.Configurable
    public Object clone() {
        try {
            Configuration copy = (Configuration) super.clone();
            copy.sharedVariables = new HashMap(this.sharedVariables);
            copy.localeToCharsetMap = new ConcurrentHashMap(this.localeToCharsetMap);
            copy.recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new BugException("Cloning failed", e);
        }
    }

    private void loadBuiltInSharedVariables() {
        this.sharedVariables.put("capture_output", new CaptureOutput());
        this.sharedVariables.put("compress", StandardCompress.INSTANCE);
        this.sharedVariables.put("html_escape", new HtmlEscape());
        this.sharedVariables.put("normalize_newlines", new NormalizeNewlines());
        this.sharedVariables.put("xml_escape", new XmlEscape());
    }

    public void loadBuiltInEncodingMap() {
        this.localeToCharsetMap.clear();
        this.localeToCharsetMap.put("ar", "ISO-8859-6");
        this.localeToCharsetMap.put("be", "ISO-8859-5");
        this.localeToCharsetMap.put("bg", "ISO-8859-5");
        this.localeToCharsetMap.put("ca", "ISO-8859-1");
        this.localeToCharsetMap.put("cs", "ISO-8859-2");
        this.localeToCharsetMap.put("da", "ISO-8859-1");
        this.localeToCharsetMap.put("de", "ISO-8859-1");
        this.localeToCharsetMap.put("el", "ISO-8859-7");
        this.localeToCharsetMap.put("en", "ISO-8859-1");
        this.localeToCharsetMap.put("es", "ISO-8859-1");
        this.localeToCharsetMap.put("et", "ISO-8859-1");
        this.localeToCharsetMap.put("fi", "ISO-8859-1");
        this.localeToCharsetMap.put("fr", "ISO-8859-1");
        this.localeToCharsetMap.put("hr", "ISO-8859-2");
        this.localeToCharsetMap.put("hu", "ISO-8859-2");
        this.localeToCharsetMap.put(BeanUtil.PREFIX_GETTER_IS, "ISO-8859-1");
        this.localeToCharsetMap.put("it", "ISO-8859-1");
        this.localeToCharsetMap.put("iw", "ISO-8859-8");
        this.localeToCharsetMap.put("ja", "Shift_JIS");
        this.localeToCharsetMap.put("ko", "EUC-KR");
        this.localeToCharsetMap.put("lt", "ISO-8859-2");
        this.localeToCharsetMap.put("lv", "ISO-8859-2");
        this.localeToCharsetMap.put("mk", "ISO-8859-5");
        this.localeToCharsetMap.put("nl", "ISO-8859-1");
        this.localeToCharsetMap.put("no", "ISO-8859-1");
        this.localeToCharsetMap.put("pl", "ISO-8859-2");
        this.localeToCharsetMap.put("pt", "ISO-8859-1");
        this.localeToCharsetMap.put("ro", "ISO-8859-2");
        this.localeToCharsetMap.put("ru", "ISO-8859-5");
        this.localeToCharsetMap.put("sh", "ISO-8859-5");
        this.localeToCharsetMap.put("sk", "ISO-8859-2");
        this.localeToCharsetMap.put("sl", "ISO-8859-2");
        this.localeToCharsetMap.put("sq", "ISO-8859-2");
        this.localeToCharsetMap.put("sr", "ISO-8859-5");
        this.localeToCharsetMap.put("sv", "ISO-8859-1");
        this.localeToCharsetMap.put("tr", "ISO-8859-9");
        this.localeToCharsetMap.put("uk", "ISO-8859-5");
        this.localeToCharsetMap.put("zh", "GB2312");
        this.localeToCharsetMap.put("zh_TW", "Big5");
    }

    public void clearEncodingMap() {
        this.localeToCharsetMap.clear();
    }

    @Deprecated
    public static Configuration getDefaultConfiguration() {
        Configuration defaultConfig2 = defaultConfig;
        if (defaultConfig2 == null) {
            synchronized (defaultConfigLock) {
                defaultConfig2 = defaultConfig;
                if (defaultConfig2 == null) {
                    defaultConfig2 = new Configuration();
                    defaultConfig = defaultConfig2;
                }
            }
        }
        return defaultConfig2;
    }

    @Deprecated
    public static void setDefaultConfiguration(Configuration config) {
        synchronized (defaultConfigLock) {
            defaultConfig = config;
        }
    }

    public void setTemplateLoader(TemplateLoader templateLoader) {
        synchronized (this) {
            if (this.cache.getTemplateLoader() != templateLoader) {
                recreateTemplateCacheWith(templateLoader, this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            }
            this.templateLoaderExplicitlySet = true;
        }
    }

    public void unsetTemplateLoader() {
        if (this.templateLoaderExplicitlySet) {
            setTemplateLoader(getDefaultTemplateLoader());
            this.templateLoaderExplicitlySet = false;
        }
    }

    public boolean isTemplateLoaderExplicitlySet() {
        return this.templateLoaderExplicitlySet;
    }

    public TemplateLoader getTemplateLoader() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateLoader();
    }

    public void setTemplateLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
        if (this.cache.getTemplateLookupStrategy() != templateLookupStrategy) {
            recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), templateLookupStrategy, this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
        }
        this.templateLookupStrategyExplicitlySet = true;
    }

    public void unsetTemplateLookupStrategy() {
        if (this.templateLookupStrategyExplicitlySet) {
            setTemplateLookupStrategy(getDefaultTemplateLookupStrategy());
            this.templateLookupStrategyExplicitlySet = false;
        }
    }

    public boolean isTemplateLookupStrategyExplicitlySet() {
        return this.templateLookupStrategyExplicitlySet;
    }

    public TemplateLookupStrategy getTemplateLookupStrategy() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateLookupStrategy();
    }

    public void setTemplateNameFormat(TemplateNameFormat templateNameFormat) {
        if (this.cache.getTemplateNameFormat() != templateNameFormat) {
            recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), templateNameFormat, this.cache.getTemplateConfigurations());
        }
        this.templateNameFormatExplicitlySet = true;
    }

    public void unsetTemplateNameFormat() {
        if (this.templateNameFormatExplicitlySet) {
            setTemplateNameFormat(getDefaultTemplateNameFormat());
            this.templateNameFormatExplicitlySet = false;
        }
    }

    public boolean isTemplateNameFormatExplicitlySet() {
        return this.templateNameFormatExplicitlySet;
    }

    public TemplateNameFormat getTemplateNameFormat() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateNameFormat();
    }

    public void setTemplateConfigurations(TemplateConfigurationFactory templateConfigurations) {
        if (this.cache.getTemplateConfigurations() != templateConfigurations) {
            if (templateConfigurations != null) {
                templateConfigurations.setConfiguration(this);
            }
            recreateTemplateCacheWith(this.cache.getTemplateLoader(), this.cache.getCacheStorage(), this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), templateConfigurations);
        }
    }

    public TemplateConfigurationFactory getTemplateConfigurations() {
        if (this.cache == null) {
            return null;
        }
        return this.cache.getTemplateConfigurations();
    }

    public void setCacheStorage(CacheStorage cacheStorage) {
        synchronized (this) {
            if (getCacheStorage() != cacheStorage) {
                recreateTemplateCacheWith(this.cache.getTemplateLoader(), cacheStorage, this.cache.getTemplateLookupStrategy(), this.cache.getTemplateNameFormat(), this.cache.getTemplateConfigurations());
            }
            this.cacheStorageExplicitlySet = true;
        }
    }

    public void unsetCacheStorage() {
        if (this.cacheStorageExplicitlySet) {
            setCacheStorage(getDefaultCacheStorage());
            this.cacheStorageExplicitlySet = false;
        }
    }

    public boolean isCacheStorageExplicitlySet() {
        return this.cacheStorageExplicitlySet;
    }

    public CacheStorage getCacheStorage() {
        synchronized (this) {
            if (this.cache == null) {
                return null;
            }
            return this.cache.getCacheStorage();
        }
    }

    public void setDirectoryForTemplateLoading(File dir) throws IOException {
        TemplateLoader tl = getTemplateLoader();
        if (tl instanceof FileTemplateLoader) {
            String path = ((FileTemplateLoader) tl).baseDir.getCanonicalPath();
            if (path.equals(dir.getCanonicalPath())) {
                return;
            }
        }
        setTemplateLoader(new FileTemplateLoader(dir));
    }

    public void setServletContextForTemplateLoading(Object servletContext, String path) {
        Class<?>[] constructorParamTypes;
        Object[] constructorParams;
        NullArgumentException.check(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
        Class<?> servletContextClass = null;
        Boolean jakartaMode = null;
        Exception jakartaServletClassLoadingException = null;
        try {
            servletContextClass = ClassUtil.forName("jakarta.servlet.ServletContext");
            if (servletContextClass.isInstance(servletContext)) {
                jakartaMode = true;
            }
        } catch (Exception e) {
            jakartaServletClassLoadingException = e;
        }
        Exception javaxServletClassLoadingException = null;
        if (jakartaMode == null) {
            try {
                servletContextClass = ClassUtil.forName("javax.servlet.ServletContext");
                if (servletContextClass.isInstance(servletContext)) {
                    jakartaMode = false;
                }
            } catch (Exception e2) {
                javaxServletClassLoadingException = e2;
            }
        }
        if (servletContextClass == null) {
            throw new UnsupportedOperationException("Failed to get ServletContext class; probably Servlet API-s are not supported in this environment, but check the exceptions:\n- When attempted use Jakarta Servlet support: " + jakartaServletClassLoadingException + "\n- When attempted use javax Servlet support: " + javaxServletClassLoadingException);
        }
        if (jakartaMode == null) {
            throw new IllegalArgumentException("servletContext must implement ServletContext, but " + servletContext.getClass().getName() + " does not.");
        }
        try {
            Class<?> webappTemplateLoaderClass = ClassUtil.forName(jakartaMode.booleanValue() ? "freemarker.ext.jakarta.servlet.WebappTemplateLoader" : "freemarker.cache.WebappTemplateLoader");
            if (path == null) {
                constructorParamTypes = new Class[]{servletContextClass};
                constructorParams = new Object[]{servletContext};
            } else {
                constructorParamTypes = new Class[]{servletContextClass, String.class};
                constructorParams = new Object[]{servletContext, path};
            }
            try {
                TemplateLoader templateLoader = (TemplateLoader) webappTemplateLoaderClass.getConstructor(constructorParamTypes).newInstance(constructorParams);
                setTemplateLoader(templateLoader);
            } catch (Exception e3) {
                throw new RuntimeException("Failed to instantiate " + webappTemplateLoaderClass.getName(), e3);
            }
        } catch (ClassNotFoundException e4) {
            throw new RuntimeException("Failed to get WebappTemplateLoader class", e4);
        }
    }

    public void setClassForTemplateLoading(Class resourceLoaderClass, String basePackagePath) {
        setTemplateLoader(new ClassTemplateLoader((Class<?>) resourceLoaderClass, basePackagePath));
    }

    public void setClassLoaderForTemplateLoading(ClassLoader classLoader, String basePackagePath) {
        setTemplateLoader(new ClassTemplateLoader(classLoader, basePackagePath));
    }

    @Deprecated
    public void setTemplateUpdateDelay(int seconds) {
        this.cache.setDelay(1000 * seconds);
    }

    public void setTemplateUpdateDelayMilliseconds(long millis) {
        this.cache.setDelay(millis);
    }

    public long getTemplateUpdateDelayMilliseconds() {
        return this.cache.getDelay();
    }

    @Deprecated
    public void setStrictSyntaxMode(boolean b) {
        this.strictSyntax = b;
    }

    @Override // freemarker.core.Configurable
    public void setObjectWrapper(ObjectWrapper objectWrapper) {
        ObjectWrapper prevObjectWrapper = getObjectWrapper();
        super.setObjectWrapper(objectWrapper);
        this.objectWrapperExplicitlySet = true;
        if (objectWrapper != prevObjectWrapper) {
            try {
                setSharedVariablesFromRewrappableSharedVariables();
            } catch (TemplateModelException e) {
                throw new RuntimeException("Failed to re-wrap earliearly set shared variables with the newly set object wrapper", e);
            }
        }
    }

    public void unsetObjectWrapper() {
        if (this.objectWrapperExplicitlySet) {
            setObjectWrapper(getDefaultObjectWrapper());
            this.objectWrapperExplicitlySet = false;
        }
    }

    public boolean isObjectWrapperExplicitlySet() {
        return this.objectWrapperExplicitlySet;
    }

    @Override // freemarker.core.Configurable
    public void setLocale(Locale locale) {
        super.setLocale(locale);
        this.localeExplicitlySet = true;
    }

    public void unsetLocale() {
        if (this.localeExplicitlySet) {
            setLocale(getDefaultLocale());
            this.localeExplicitlySet = false;
        }
    }

    public boolean isLocaleExplicitlySet() {
        return this.localeExplicitlySet;
    }

    static Locale getDefaultLocale() {
        return Locale.getDefault();
    }

    @Override // freemarker.core.Configurable
    public void setTimeZone(TimeZone timeZone) {
        super.setTimeZone(timeZone);
        this.timeZoneExplicitlySet = true;
    }

    public void unsetTimeZone() {
        if (this.timeZoneExplicitlySet) {
            setTimeZone(getDefaultTimeZone());
            this.timeZoneExplicitlySet = false;
        }
    }

    public boolean isTimeZoneExplicitlySet() {
        return this.timeZoneExplicitlySet;
    }

    static TimeZone getDefaultTimeZone() {
        return TimeZone.getDefault();
    }

    @Override // freemarker.core.Configurable
    public void setTemplateExceptionHandler(TemplateExceptionHandler templateExceptionHandler) {
        super.setTemplateExceptionHandler(templateExceptionHandler);
        this.templateExceptionHandlerExplicitlySet = true;
    }

    public void unsetTemplateExceptionHandler() {
        if (this.templateExceptionHandlerExplicitlySet) {
            setTemplateExceptionHandler(getDefaultTemplateExceptionHandler());
            this.templateExceptionHandlerExplicitlySet = false;
        }
    }

    public boolean isTemplateExceptionHandlerExplicitlySet() {
        return this.templateExceptionHandlerExplicitlySet;
    }

    @Override // freemarker.core.Configurable
    public void setAttemptExceptionReporter(AttemptExceptionReporter attemptExceptionReporter) {
        super.setAttemptExceptionReporter(attemptExceptionReporter);
        this.attemptExceptionReporterExplicitlySet = true;
    }

    public void unsetAttemptExceptionReporter() {
        if (this.attemptExceptionReporterExplicitlySet) {
            setAttemptExceptionReporter(getDefaultAttemptExceptionReporter());
            this.attemptExceptionReporterExplicitlySet = false;
        }
    }

    public boolean isAttemptExceptionReporterExplicitlySet() {
        return this.attemptExceptionReporterExplicitlySet;
    }

    @Override // freemarker.core.Configurable
    public void setLogTemplateExceptions(boolean value) {
        super.setLogTemplateExceptions(value);
        this.logTemplateExceptionsExplicitlySet = true;
    }

    public void unsetLogTemplateExceptions() {
        if (this.logTemplateExceptionsExplicitlySet) {
            setLogTemplateExceptions(getDefaultLogTemplateExceptions());
            this.logTemplateExceptionsExplicitlySet = false;
        }
    }

    public boolean isLogTemplateExceptionsExplicitlySet() {
        return this.logTemplateExceptionsExplicitlySet;
    }

    @Override // freemarker.core.Configurable
    public void setWrapUncheckedExceptions(boolean value) {
        super.setWrapUncheckedExceptions(value);
        this.wrapUncheckedExceptionsExplicitlySet = true;
    }

    public void unsetWrapUncheckedExceptions() {
        if (this.wrapUncheckedExceptionsExplicitlySet) {
            setWrapUncheckedExceptions(getDefaultWrapUncheckedExceptions());
            this.wrapUncheckedExceptionsExplicitlySet = false;
        }
    }

    public boolean isWrapUncheckedExceptionsExplicitlySet() {
        return this.wrapUncheckedExceptionsExplicitlySet;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getStrictSyntaxMode() {
        return this.strictSyntax;
    }

    public void setIncompatibleImprovements(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        if (!this.incompatibleImprovements.equals(incompatibleImprovements)) {
            checkCurrentVersionNotRecycled(incompatibleImprovements);
            this.incompatibleImprovements = incompatibleImprovements;
            if (!this.templateLoaderExplicitlySet) {
                this.templateLoaderExplicitlySet = true;
                unsetTemplateLoader();
            }
            if (!this.templateLookupStrategyExplicitlySet) {
                this.templateLookupStrategyExplicitlySet = true;
                unsetTemplateLookupStrategy();
            }
            if (!this.templateNameFormatExplicitlySet) {
                this.templateNameFormatExplicitlySet = true;
                unsetTemplateNameFormat();
            }
            if (!this.cacheStorageExplicitlySet) {
                this.cacheStorageExplicitlySet = true;
                unsetCacheStorage();
            }
            if (!this.templateExceptionHandlerExplicitlySet) {
                this.templateExceptionHandlerExplicitlySet = true;
                unsetTemplateExceptionHandler();
            }
            if (!this.attemptExceptionReporterExplicitlySet) {
                this.attemptExceptionReporterExplicitlySet = true;
                unsetAttemptExceptionReporter();
            }
            if (!this.logTemplateExceptionsExplicitlySet) {
                this.logTemplateExceptionsExplicitlySet = true;
                unsetLogTemplateExceptions();
            }
            if (!this.cFormatExplicitlySet) {
                this.cFormatExplicitlySet = true;
                unsetCFormat();
            }
            if (!this.wrapUncheckedExceptionsExplicitlySet) {
                this.wrapUncheckedExceptionsExplicitlySet = true;
                unsetWrapUncheckedExceptions();
            }
            if (!this.objectWrapperExplicitlySet) {
                this.objectWrapperExplicitlySet = true;
                unsetObjectWrapper();
            }
            recreateTemplateCache();
        }
    }

    private static void checkCurrentVersionNotRecycled(Version incompatibleImprovements) {
        _TemplateAPI.checkCurrentVersionNotRecycled(incompatibleImprovements, "freemarker.configuration", "Configuration");
    }

    @Override // freemarker.core.ParserConfiguration
    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    @Deprecated
    public void setIncompatibleEnhancements(String version) {
        setIncompatibleImprovements(new Version(version));
    }

    @Deprecated
    public String getIncompatibleEnhancements() {
        return this.incompatibleImprovements.toString();
    }

    @Deprecated
    public int getParsedIncompatibleEnhancements() {
        return getIncompatibleImprovements().intValue();
    }

    public void setWhitespaceStripping(boolean b) {
        this.whitespaceStripping = b;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getWhitespaceStripping() {
        return this.whitespaceStripping;
    }

    public void setAutoEscapingPolicy(int autoEscapingPolicy) {
        _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
        int prevAutoEscaping = getAutoEscapingPolicy();
        this.autoEscapingPolicy = autoEscapingPolicy;
        if (prevAutoEscaping != autoEscapingPolicy) {
            clearTemplateCache();
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        if (outputFormat == null) {
            throw new NullArgumentException(OUTPUT_FORMAT_KEY_CAMEL_CASE, "You may meant: " + UndefinedOutputFormat.class.getSimpleName() + ".INSTANCE");
        }
        OutputFormat prevOutputFormat = getOutputFormat();
        this.outputFormat = outputFormat;
        this.outputFormatExplicitlySet = true;
        if (prevOutputFormat != outputFormat) {
            clearTemplateCache();
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public OutputFormat getOutputFormat() {
        return this.outputFormat;
    }

    public boolean isOutputFormatExplicitlySet() {
        return this.outputFormatExplicitlySet;
    }

    public void unsetOutputFormat() {
        this.outputFormat = UndefinedOutputFormat.INSTANCE;
        this.outputFormatExplicitlySet = false;
    }

    public OutputFormat getOutputFormat(String name) throws UnregisteredOutputFormatException {
        if (name.length() == 0) {
            throw new IllegalArgumentException("0-length format name");
        }
        if (name.charAt(name.length() - 1) == '}') {
            int openBrcIdx = name.indexOf(123);
            if (openBrcIdx == -1) {
                throw new IllegalArgumentException("Missing opening '{' in: " + name);
            }
            MarkupOutputFormat outerOF = getMarkupOutputFormatForCombined(name.substring(0, openBrcIdx));
            MarkupOutputFormat innerOF = getMarkupOutputFormatForCombined(name.substring(openBrcIdx + 1, name.length() - 1));
            return new CombinedMarkupOutputFormat(name, outerOF, innerOF);
        }
        OutputFormat custOF = this.registeredCustomOutputFormats.get(name);
        if (custOF != null) {
            return custOF;
        }
        OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(name);
        if (stdOF == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("Unregistered output format name, ");
            sb.append(StringUtil.jQuote(name));
            sb.append(". The output formats registered in the Configuration are: ");
            Set<String> registeredNames = new TreeSet<>();
            registeredNames.addAll(STANDARD_OUTPUT_FORMATS.keySet());
            registeredNames.addAll(this.registeredCustomOutputFormats.keySet());
            boolean first = true;
            for (String registeredName : registeredNames) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(StringUtil.jQuote(registeredName));
            }
            throw new UnregisteredOutputFormatException(sb.toString());
        }
        return stdOF;
    }

    private MarkupOutputFormat getMarkupOutputFormatForCombined(String outerName) throws UnregisteredOutputFormatException {
        OutputFormat of = getOutputFormat(outerName);
        if (!(of instanceof MarkupOutputFormat)) {
            throw new IllegalArgumentException("The \"" + outerName + "\" output format can't be used in ...{...} expression, because it's not a markup format.");
        }
        MarkupOutputFormat outerOF = (MarkupOutputFormat) of;
        return outerOF;
    }

    public void setRegisteredCustomOutputFormats(Collection<? extends OutputFormat> registeredCustomOutputFormats) {
        NullArgumentException.check(registeredCustomOutputFormats);
        Map<String, OutputFormat> m = new LinkedHashMap<>((registeredCustomOutputFormats.size() * 4) / 3, 1.0f);
        for (OutputFormat outputFormat : registeredCustomOutputFormats) {
            String name = outputFormat.getName();
            if (name.equals(UndefinedOutputFormat.INSTANCE.getName())) {
                throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
            }
            if (name.equals(PlainTextOutputFormat.INSTANCE.getName())) {
                throw new IllegalArgumentException("The \"" + name + "\" output format can't be redefined");
            }
            if (name.length() == 0) {
                throw new IllegalArgumentException("The output format name can't be 0 long");
            }
            if (!Character.isLetterOrDigit(name.charAt(0))) {
                throw new IllegalArgumentException("The output format name must start with letter or digit: " + name);
            }
            if (name.indexOf(43) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"+\" character: " + name);
            }
            if (name.indexOf(123) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"{\" character: " + name);
            }
            if (name.indexOf(125) != -1) {
                throw new IllegalArgumentException("The output format name can't contain \"}\" character: " + name);
            }
            OutputFormat replaced = m.put(outputFormat.getName(), outputFormat);
            if (replaced != null) {
                if (replaced == outputFormat) {
                    throw new IllegalArgumentException("Duplicate output format in the collection: " + outputFormat);
                }
                throw new IllegalArgumentException("Clashing output format names between " + replaced + " and " + outputFormat + ".");
            }
        }
        this.registeredCustomOutputFormats = Collections.unmodifiableMap(m);
        clearTemplateCache();
    }

    public Collection<? extends OutputFormat> getRegisteredCustomOutputFormats() {
        return this.registeredCustomOutputFormats.values();
    }

    public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
        boolean prevEffectiveValue = getRecognizeStandardFileExtensions();
        this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
        if (prevEffectiveValue != recognizeStandardFileExtensions) {
            clearTemplateCache();
        }
    }

    public void unsetRecognizeStandardFileExtensions() {
        if (this.recognizeStandardFileExtensions != null) {
            this.recognizeStandardFileExtensions = null;
        }
    }

    public boolean isRecognizeStandardFileExtensionsExplicitlySet() {
        return this.recognizeStandardFileExtensions != null;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getRecognizeStandardFileExtensions() {
        if (this.recognizeStandardFileExtensions == null) {
            return this.incompatibleImprovements.intValue() >= _VersionInts.V_2_3_24;
        }
        return this.recognizeStandardFileExtensions.booleanValue();
    }

    @Override // freemarker.core.Configurable
    public void setCFormat(CFormat cFormat) {
        super.setCFormat(cFormat);
        this.cFormatExplicitlySet = true;
    }

    public void unsetCFormat() {
        if (this.cFormatExplicitlySet) {
            setCFormat(getDefaultCFormat(this.incompatibleImprovements));
            this.cFormatExplicitlySet = false;
        }
    }

    static CFormat getDefaultCFormat(Version incompatibleImprovements) {
        return incompatibleImprovements.intValue() >= _VersionInts.V_2_3_32 ? JavaScriptOrJSONCFormat.INSTANCE : LegacyCFormat.INSTANCE;
    }

    public boolean isCFormatExplicitlySet() {
        return this.cFormatExplicitlySet;
    }

    public void setTagSyntax(int tagSyntax) {
        _TemplateAPI.valideTagSyntaxValue(tagSyntax);
        this.tagSyntax = tagSyntax;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTagSyntax() {
        return this.tagSyntax;
    }

    public void setInterpolationSyntax(int interpolationSyntax) {
        _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
        this.interpolationSyntax = interpolationSyntax;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getInterpolationSyntax() {
        return this.interpolationSyntax;
    }

    public void setNamingConvention(int namingConvention) {
        _TemplateAPI.validateNamingConventionValue(namingConvention);
        this.namingConvention = namingConvention;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getNamingConvention() {
        return this.namingConvention;
    }

    public void setTabSize(int tabSize) {
        if (tabSize < 1) {
            throw new IllegalArgumentException("\"tabSize\" must be at least 1, but was " + tabSize);
        }
        if (tabSize > 256) {
            throw new IllegalArgumentException("\"tabSize\" can't be more than 256, but was " + tabSize);
        }
        this.tabSize = tabSize;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTabSize() {
        return this.tabSize;
    }

    public boolean getFallbackOnNullLoopVariable() {
        return this.fallbackOnNullLoopVariable;
    }

    public void setFallbackOnNullLoopVariable(boolean fallbackOnNullLoopVariable) {
        this.fallbackOnNullLoopVariable = fallbackOnNullLoopVariable;
    }

    boolean getPreventStrippings() {
        return this.preventStrippings;
    }

    void setPreventStrippings(boolean preventStrippings) {
        this.preventStrippings = preventStrippings;
    }

    public Template getTemplate(String name) throws IOException {
        return getTemplate(name, null, null, null, true, false);
    }

    public Template getTemplate(String name, Locale locale) throws IOException {
        return getTemplate(name, locale, null, null, true, false);
    }

    public Template getTemplate(String name, String encoding) throws IOException {
        return getTemplate(name, null, null, encoding, true, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding) throws IOException {
        return getTemplate(name, locale, null, encoding, true, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL) throws IOException {
        return getTemplate(name, locale, null, encoding, parseAsFTL, false);
    }

    public Template getTemplate(String name, Locale locale, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
        return getTemplate(name, locale, null, encoding, parseAsFTL, ignoreMissing);
    }

    public Template getTemplate(String name, Locale locale, Object customLookupCondition, String encoding, boolean parseAsFTL, boolean ignoreMissing) throws IOException {
        String msg;
        if (locale == null) {
            locale = getLocale();
        }
        if (encoding == null) {
            encoding = getEncoding(locale);
        }
        TemplateCache.MaybeMissingTemplate maybeTemp = this.cache.getTemplate(name, locale, customLookupCondition, encoding, parseAsFTL);
        Template temp = maybeTemp.getTemplate();
        if (temp == null) {
            if (ignoreMissing) {
                return null;
            }
            TemplateLoader tl = getTemplateLoader();
            if (tl == null) {
                msg = "Don't know where to load template " + StringUtil.jQuote(name) + " from because the \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), so it's null.";
            } else {
                String missingTempNormName = maybeTemp.getMissingTemplateNormalizedName();
                String missingTempReason = maybeTemp.getMissingTemplateReason();
                TemplateLookupStrategy templateLookupStrategy = getTemplateLookupStrategy();
                msg = "Template not found for name " + StringUtil.jQuote(name) + ((missingTempNormName == null || name == null || removeInitialSlash(name).equals(missingTempNormName)) ? "" : " (normalized: " + StringUtil.jQuote(missingTempNormName) + ")") + (customLookupCondition != null ? " and custom lookup condition " + StringUtil.jQuote(customLookupCondition) : "") + "." + (missingTempReason != null ? "\nReason given: " + ensureSentenceIsClosed(missingTempReason) : "") + "\nThe name was interpreted by this TemplateLoader: " + StringUtil.tryToString(tl) + "." + (!isKnownNonConfusingLookupStrategy(templateLookupStrategy) ? "\n(Before that, the name was possibly changed by this lookup strategy: " + StringUtil.tryToString(templateLookupStrategy) + ".)" : "") + (!this.templateLoaderExplicitlySet ? "\nWarning: The \"template_loader\" FreeMarker setting wasn't set (Configuration.setTemplateLoader), and using the default value is most certainly not intended and dangerous, and can be the cause of this error." : "") + ((missingTempReason != null || name.indexOf(92) == -1) ? "" : "\nWarning: The name contains backslash (\"\\\") instead of slash (\"/\"); template names should use slash only.");
            }
            String normName = maybeTemp.getMissingTemplateNormalizedName();
            throw new TemplateNotFoundException(normName != null ? normName : name, customLookupCondition, msg);
        }
        return temp;
    }

    private boolean isKnownNonConfusingLookupStrategy(TemplateLookupStrategy templateLookupStrategy) {
        return templateLookupStrategy == TemplateLookupStrategy.DEFAULT_2_3_0;
    }

    private String removeInitialSlash(String name) {
        return name.startsWith("/") ? name.substring(1) : name;
    }

    private String ensureSentenceIsClosed(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        char lastChar = s.charAt(s.length() - 1);
        return (lastChar == '.' || lastChar == '!' || lastChar == '?') ? s : s + ".";
    }

    public void setDefaultEncoding(String encoding) {
        this.defaultEncoding = encoding;
        this.defaultEncodingExplicitlySet = true;
    }

    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    public void unsetDefaultEncoding() {
        if (this.defaultEncodingExplicitlySet) {
            setDefaultEncoding(getDefaultDefaultEncoding());
            this.defaultEncodingExplicitlySet = false;
        }
    }

    public boolean isDefaultEncodingExplicitlySet() {
        return this.defaultEncodingExplicitlySet;
    }

    private static String getDefaultDefaultEncoding() {
        return getJVMDefaultEncoding();
    }

    private static String getJVMDefaultEncoding() {
        return SecurityUtilities.getSystemProperty("file.encoding", "utf-8");
    }

    public String getEncoding(Locale locale) {
        if (this.localeToCharsetMap.isEmpty()) {
            return this.defaultEncoding;
        }
        NullArgumentException.check("locale", locale);
        String charset = (String) this.localeToCharsetMap.get(locale.toString());
        if (charset == null) {
            if (locale.getVariant().length() > 0) {
                Locale l = new Locale(locale.getLanguage(), locale.getCountry());
                String charset2 = (String) this.localeToCharsetMap.get(l.toString());
                if (charset2 != null) {
                    this.localeToCharsetMap.put(locale.toString(), charset2);
                }
            }
            charset = (String) this.localeToCharsetMap.get(locale.getLanguage());
            if (charset != null) {
                this.localeToCharsetMap.put(locale.toString(), charset);
            }
        }
        return charset != null ? charset : this.defaultEncoding;
    }

    public void setEncoding(Locale locale, String encoding) {
        this.localeToCharsetMap.put(locale.toString(), encoding);
    }

    public void setSharedVariable(String name, TemplateModel tm) {
        Object replaced = this.sharedVariables.put(name, tm);
        if (replaced != null && this.rewrappableSharedVariables != null) {
            this.rewrappableSharedVariables.remove(name);
        }
    }

    public Set getSharedVariableNames() {
        return new HashSet(this.sharedVariables.keySet());
    }

    public void setSharedVariable(String name, Object value) throws TemplateModelException {
        setSharedVariable(name, getObjectWrapper().wrap(value));
    }

    public void setSharedVariables(Map<String, ?> map) throws TemplateModelException {
        this.rewrappableSharedVariables = new HashMap(map);
        this.sharedVariables.clear();
        setSharedVariablesFromRewrappableSharedVariables();
    }

    public void setSharedVaribles(Map map) throws TemplateModelException {
        setSharedVariables(map);
    }

    private void setSharedVariablesFromRewrappableSharedVariables() throws TemplateModelException {
        TemplateModel templateModelWrap;
        if (this.rewrappableSharedVariables == null) {
            return;
        }
        for (Map.Entry ent : this.rewrappableSharedVariables.entrySet()) {
            String name = (String) ent.getKey();
            Object value = ent.getValue();
            if (value instanceof TemplateModel) {
                templateModelWrap = (TemplateModel) value;
            } else {
                templateModelWrap = getObjectWrapper().wrap(value);
            }
            TemplateModel valueAsTM = templateModelWrap;
            this.sharedVariables.put(name, valueAsTM);
        }
    }

    public void setAllSharedVariables(TemplateHashModelEx hash) throws TemplateModelException {
        TemplateModelIterator keys = hash.keys().iterator();
        TemplateModelIterator values = hash.values().iterator();
        while (keys.hasNext()) {
            setSharedVariable(((TemplateScalarModel) keys.next()).getAsString(), values.next());
        }
    }

    public TemplateModel getSharedVariable(String name) {
        return (TemplateModel) this.sharedVariables.get(name);
    }

    public void clearSharedVariables() {
        this.sharedVariables.clear();
        loadBuiltInSharedVariables();
    }

    public void clearTemplateCache() {
        this.cache.clear();
    }

    public void removeTemplateFromCache(String name) throws IOException {
        Locale loc = getLocale();
        removeTemplateFromCache(name, loc, null, getEncoding(loc), true);
    }

    public void removeTemplateFromCache(String name, Locale locale) throws IOException {
        removeTemplateFromCache(name, locale, null, getEncoding(locale), true);
    }

    public void removeTemplateFromCache(String name, String encoding) throws IOException {
        removeTemplateFromCache(name, getLocale(), null, encoding, true);
    }

    public void removeTemplateFromCache(String name, Locale locale, String encoding) throws IOException {
        removeTemplateFromCache(name, locale, null, encoding, true);
    }

    public void removeTemplateFromCache(String name, Locale locale, String encoding, boolean parse) throws IOException {
        removeTemplateFromCache(name, locale, null, encoding, parse);
    }

    public void removeTemplateFromCache(String name, Locale locale, Object customLookupCondition, String encoding, boolean parse) throws IOException {
        this.cache.removeTemplate(name, locale, customLookupCondition, encoding, parse);
    }

    public boolean getLocalizedLookup() {
        return this.cache.getLocalizedLookup();
    }

    public void setLocalizedLookup(boolean localizedLookup) {
        this.localizedLookup = localizedLookup;
        this.cache.setLocalizedLookup(localizedLookup);
    }

    @Override // freemarker.core.Configurable
    public void setSetting(String name, String value) throws ParseException, TemplateException, ClassNotFoundException, NumberFormatException {
        long multiplier;
        String valueWithoutUnit;
        boolean unknown = false;
        try {
            if ("TemplateUpdateInterval".equalsIgnoreCase(name)) {
                name = "template_update_delay";
            } else if ("DefaultEncoding".equalsIgnoreCase(name)) {
                name = "default_encoding";
            }
            if ("default_encoding".equals(name) || DEFAULT_ENCODING_KEY_CAMEL_CASE.equals(name)) {
                if (JVM_DEFAULT.equalsIgnoreCase(value)) {
                    setDefaultEncoding(getJVMDefaultEncoding());
                } else {
                    setDefaultEncoding(value);
                }
            } else if ("localized_lookup".equals(name) || LOCALIZED_LOOKUP_KEY_CAMEL_CASE.equals(name)) {
                setLocalizedLookup(StringUtil.getYesNo(value));
            } else if ("strict_syntax".equals(name) || STRICT_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                setStrictSyntaxMode(StringUtil.getYesNo(value));
            } else if ("whitespace_stripping".equals(name) || WHITESPACE_STRIPPING_KEY_CAMEL_CASE.equals(name)) {
                setWhitespaceStripping(StringUtil.getYesNo(value));
            } else if ("auto_escaping_policy".equals(name) || AUTO_ESCAPING_POLICY_KEY_CAMEL_CASE.equals(name)) {
                if ("enable_if_default".equals(value) || "enableIfDefault".equals(value)) {
                    setAutoEscapingPolicy(21);
                } else if ("enable_if_supported".equals(value) || "enableIfSupported".equals(value)) {
                    setAutoEscapingPolicy(22);
                } else if ("force".equals(value)) {
                    setAutoEscapingPolicy(23);
                } else if ("disable".equals(value)) {
                    setAutoEscapingPolicy(20);
                } else {
                    throw invalidSettingValueException(name, value);
                }
            } else if ("output_format".equals(name) || OUTPUT_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetOutputFormat();
                } else {
                    OutputFormat stdOF = STANDARD_OUTPUT_FORMATS.get(value);
                    setOutputFormat(stdOF != null ? stdOF : (OutputFormat) _ObjectBuilderSettingEvaluator.eval(value, OutputFormat.class, true, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("registered_custom_output_formats".equals(name) || REGISTERED_CUSTOM_OUTPUT_FORMATS_KEY_CAMEL_CASE.equals(name)) {
                List list = (List) _ObjectBuilderSettingEvaluator.eval(value, List.class, true, _SettingEvaluationEnvironment.getCurrent());
                for (Object item : list) {
                    if (!(item instanceof OutputFormat)) {
                        throw new _MiscTemplateException(getEnvironment(), "Invalid value for setting ", new _DelayedJQuote(name), ": List items must be " + OutputFormat.class.getName() + " instances, in: ", value);
                    }
                }
                setRegisteredCustomOutputFormats(list);
            } else if ("recognize_standard_file_extensions".equals(name) || RECOGNIZE_STANDARD_FILE_EXTENSIONS_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetRecognizeStandardFileExtensions();
                } else {
                    setRecognizeStandardFileExtensions(StringUtil.getYesNo(value));
                }
            } else if ("cache_storage".equals(name) || CACHE_STORAGE_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetCacheStorage();
                }
                if (value.indexOf(46) == -1) {
                    int strongSize = 0;
                    int softSize = 0;
                    Map map = StringUtil.parseNameValuePairList(value, String.valueOf(Integer.MAX_VALUE));
                    for (Map.Entry ent : map.entrySet()) {
                        String pname = (String) ent.getKey();
                        try {
                            int pvalue = Integer.parseInt((String) ent.getValue());
                            if ("soft".equalsIgnoreCase(pname)) {
                                softSize = pvalue;
                            } else if ("strong".equalsIgnoreCase(pname)) {
                                strongSize = pvalue;
                            } else {
                                throw invalidSettingValueException(name, value);
                            }
                        } catch (NumberFormatException e) {
                            throw invalidSettingValueException(name, value);
                        }
                    }
                    if (softSize == 0 && strongSize == 0) {
                        throw invalidSettingValueException(name, value);
                    }
                    setCacheStorage(new MruCacheStorage(strongSize, softSize));
                } else {
                    setCacheStorage((CacheStorage) _ObjectBuilderSettingEvaluator.eval(value, CacheStorage.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("template_update_delay".equals(name) || TEMPLATE_UPDATE_DELAY_KEY_CAMEL_CASE.equals(name)) {
                if (value.endsWith("ms")) {
                    multiplier = 1;
                    valueWithoutUnit = rightTrim(value.substring(0, value.length() - 2));
                } else if (value.endsWith("s")) {
                    multiplier = 1000;
                    valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
                } else if (value.endsWith(ANSIConstants.ESC_END)) {
                    multiplier = 60000;
                    valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
                } else if (value.endsWith("h")) {
                    multiplier = 3600000;
                    valueWithoutUnit = rightTrim(value.substring(0, value.length() - 1));
                } else {
                    multiplier = 1000;
                    valueWithoutUnit = value;
                }
                setTemplateUpdateDelayMilliseconds(Integer.parseInt(valueWithoutUnit) * multiplier);
            } else if ("tag_syntax".equals(name) || TAG_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
                    setTagSyntax(0);
                } else if ("angle_bracket".equals(value) || "angleBracket".equals(value)) {
                    setTagSyntax(1);
                } else if ("square_bracket".equals(value) || "squareBracket".equals(value)) {
                    setTagSyntax(2);
                } else {
                    throw invalidSettingValueException(name, value);
                }
            } else if ("interpolation_syntax".equals(name) || INTERPOLATION_SYNTAX_KEY_CAMEL_CASE.equals(name)) {
                if ("legacy".equals(value)) {
                    setInterpolationSyntax(20);
                } else if ("dollar".equals(value)) {
                    setInterpolationSyntax(21);
                } else if ("square_bracket".equals(value) || "squareBracket".equals(value)) {
                    setInterpolationSyntax(22);
                } else {
                    throw invalidSettingValueException(name, value);
                }
            } else if ("naming_convention".equals(name) || NAMING_CONVENTION_KEY_CAMEL_CASE.equals(name)) {
                if ("auto_detect".equals(value) || "autoDetect".equals(value)) {
                    setNamingConvention(10);
                } else if ("legacy".equals(value)) {
                    setNamingConvention(11);
                } else if ("camel_case".equals(value) || "camelCase".equals(value)) {
                    setNamingConvention(12);
                } else {
                    throw invalidSettingValueException(name, value);
                }
            } else if ("tab_size".equals(name) || TAB_SIZE_KEY_CAMEL_CASE.equals(name)) {
                setTabSize(Integer.parseInt(value));
            } else if ("incompatible_improvements".equals(name) || INCOMPATIBLE_IMPROVEMENTS_KEY_CAMEL_CASE.equals(name)) {
                setIncompatibleImprovements(new Version(value));
            } else if (INCOMPATIBLE_ENHANCEMENTS.equals(name)) {
                setIncompatibleEnhancements(value);
            } else if ("template_loader".equals(name) || TEMPLATE_LOADER_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetTemplateLoader();
                } else {
                    setTemplateLoader((TemplateLoader) _ObjectBuilderSettingEvaluator.eval(value, TemplateLoader.class, true, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("template_lookup_strategy".equals(name) || TEMPLATE_LOOKUP_STRATEGY_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetTemplateLookupStrategy();
                } else {
                    setTemplateLookupStrategy((TemplateLookupStrategy) _ObjectBuilderSettingEvaluator.eval(value, TemplateLookupStrategy.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("template_name_format".equals(name) || TEMPLATE_NAME_FORMAT_KEY_CAMEL_CASE.equals(name)) {
                if (value.equalsIgnoreCase("default")) {
                    unsetTemplateNameFormat();
                } else if (value.equalsIgnoreCase("default_2_3_0")) {
                    setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_3_0);
                } else if (value.equalsIgnoreCase("default_2_4_0")) {
                    setTemplateNameFormat(TemplateNameFormat.DEFAULT_2_4_0);
                } else {
                    throw invalidSettingValueException(name, value);
                }
            } else if ("template_configurations".equals(name) || TEMPLATE_CONFIGURATIONS_KEY_CAMEL_CASE.equals(name)) {
                if (value.equals("null")) {
                    setTemplateConfigurations(null);
                } else {
                    setTemplateConfigurations((TemplateConfigurationFactory) _ObjectBuilderSettingEvaluator.eval(value, TemplateConfigurationFactory.class, false, _SettingEvaluationEnvironment.getCurrent()));
                }
            } else if ("fallback_on_null_loop_variable".equals(name) || FALLBACK_ON_NULL_LOOP_VARIABLE_KEY_CAMEL_CASE.equals(name)) {
                setFallbackOnNullLoopVariable(StringUtil.getYesNo(value));
            } else {
                unknown = true;
            }
            if (unknown) {
                super.setSetting(name, value);
            }
        } catch (Exception e2) {
            throw settingValueAssignmentException(name, value, e2);
        }
    }

    private String rightTrim(String s) {
        int ln = s.length();
        while (ln > 0 && Character.isWhitespace(s.charAt(ln - 1))) {
            ln--;
        }
        return s.substring(0, ln);
    }

    @Override // freemarker.core.Configurable
    public Set<String> getSettingNames(boolean camelCase) {
        return new _UnmodifiableCompositeSet(super.getSettingNames(camelCase), new _SortedArraySet(camelCase ? SETTING_NAMES_CAMEL_CASE : SETTING_NAMES_SNAKE_CASE));
    }

    @Override // freemarker.core.Configurable
    protected String getCorrectedNameForUnknownSetting(String name) {
        if ("encoding".equals(name) || BasicAuthenticator.charsetparam.equals(name) || "default_charset".equals(name)) {
            return "default_encoding";
        }
        if ("defaultCharset".equals(name)) {
            return DEFAULT_ENCODING_KEY_CAMEL_CASE;
        }
        return super.getCorrectedNameForUnknownSetting(name);
    }

    @Override // freemarker.core.Configurable
    protected void doAutoImportsAndIncludes(Environment env) throws TemplateException, IOException {
        Template t = env.getMainTemplate();
        doAutoImports(env, t);
        doAutoIncludes(env, t);
    }

    private void doAutoImports(Environment env, Template t) throws TemplateException, IOException {
        Map<String, String> envAutoImports = env.getAutoImportsWithoutFallback();
        Map<String, String> tAutoImports = t.getAutoImportsWithoutFallback();
        boolean lazyAutoImports = env.getLazyAutoImports() != null ? env.getLazyAutoImports().booleanValue() : env.getLazyImports();
        for (Map.Entry<String, String> autoImport : getAutoImportsWithoutFallback().entrySet()) {
            String nsVarName = autoImport.getKey();
            if (tAutoImports == null || !tAutoImports.containsKey(nsVarName)) {
                if (envAutoImports == null || !envAutoImports.containsKey(nsVarName)) {
                    env.importLib(autoImport.getValue(), nsVarName, lazyAutoImports);
                }
            }
        }
        if (tAutoImports != null) {
            for (Map.Entry<String, String> autoImport2 : tAutoImports.entrySet()) {
                String nsVarName2 = autoImport2.getKey();
                if (envAutoImports == null || !envAutoImports.containsKey(nsVarName2)) {
                    env.importLib(autoImport2.getValue(), nsVarName2, lazyAutoImports);
                }
            }
        }
        if (envAutoImports != null) {
            for (Map.Entry<String, String> autoImport3 : envAutoImports.entrySet()) {
                env.importLib(autoImport3.getValue(), autoImport3.getKey(), lazyAutoImports);
            }
        }
    }

    private void doAutoIncludes(Environment env, Template t) throws TemplateException, IOException {
        List<String> tAutoIncludes = t.getAutoIncludesWithoutFallback();
        List<String> envAutoIncludes = env.getAutoIncludesWithoutFallback();
        for (String templateName : getAutoIncludesWithoutFallback()) {
            if (tAutoIncludes == null || !tAutoIncludes.contains(templateName)) {
                if (envAutoIncludes == null || !envAutoIncludes.contains(templateName)) {
                    env.include(getTemplate(templateName, env.getLocale()));
                }
            }
        }
        if (tAutoIncludes != null) {
            for (String templateName2 : tAutoIncludes) {
                if (envAutoIncludes == null || !envAutoIncludes.contains(templateName2)) {
                    env.include(getTemplate(templateName2, env.getLocale()));
                }
            }
        }
        if (envAutoIncludes != null) {
            Iterator<String> it = envAutoIncludes.iterator();
            while (it.hasNext()) {
                env.include(getTemplate(it.next(), env.getLocale()));
            }
        }
    }

    @Deprecated
    public static String getVersionNumber() {
        return VERSION.toString();
    }

    public static Version getVersion() {
        return VERSION;
    }

    public static ObjectWrapper getDefaultObjectWrapper(Version incompatibleImprovements) {
        if (incompatibleImprovements.intValue() < _VersionInts.V_2_3_21) {
            return ObjectWrapper.DEFAULT_WRAPPER;
        }
        return new DefaultObjectWrapperBuilder(incompatibleImprovements).build();
    }

    public Set getSupportedBuiltInNames() {
        return getSupportedBuiltInNames(getNamingConvention());
    }

    public Set<String> getSupportedBuiltInNames(int namingConvention) {
        return _CoreAPI.getSupportedBuiltInNames(namingConvention);
    }

    public Set getSupportedBuiltInDirectiveNames() {
        return getSupportedBuiltInDirectiveNames(getNamingConvention());
    }

    public Set<String> getSupportedBuiltInDirectiveNames(int namingConvention) {
        if (namingConvention == 10) {
            return _CoreAPI.ALL_BUILT_IN_DIRECTIVE_NAMES;
        }
        if (namingConvention == 11) {
            return _CoreAPI.LEGACY_BUILT_IN_DIRECTIVE_NAMES;
        }
        if (namingConvention == 12) {
            return _CoreAPI.CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;
        }
        throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
    }

    private static String getRequiredVersionProperty(Properties vp, String properyName) {
        String s = vp.getProperty(properyName);
        if (s == null) {
            throw new RuntimeException("Version file is corrupt: \"" + properyName + "\" property is missing.");
        }
        return s;
    }
}
