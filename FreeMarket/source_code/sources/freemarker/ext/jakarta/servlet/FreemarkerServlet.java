package freemarker.ext.jakarta.servlet;

import freemarker.cache.TemplateLoader;
import freemarker.core.Environment;
import freemarker.ext.jakarta.jsp.TaglibFactory;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNotFoundException;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import org.springframework.http.HttpHeaders;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet.class */
public class FreemarkerServlet extends HttpServlet {
    public static final long serialVersionUID = -2440216393145762479L;
    public static final String INIT_PARAM_TEMPLATE_PATH = "TemplatePath";
    public static final String INIT_PARAM_NO_CACHE = "NoCache";
    public static final String INIT_PARAM_CONTENT_TYPE = "ContentType";
    public static final String INIT_PARAM_OVERRIDE_RESPONSE_CONTENT_TYPE = "OverrideResponseContentType";
    public static final String INIT_PARAM_RESPONSE_CHARACTER_ENCODING = "ResponseCharacterEncoding";
    public static final String INIT_PARAM_OVERRIDE_RESPONSE_LOCALE = "OverrideResponseLocale";
    public static final String INIT_PARAM_BUFFER_SIZE = "BufferSize";
    public static final String INIT_PARAM_META_INF_TLD_LOCATIONS = "MetaInfTldSources";
    public static final String INIT_PARAM_EXCEPTION_ON_MISSING_TEMPLATE = "ExceptionOnMissingTemplate";
    public static final String INIT_PARAM_CLASSPATH_TLDS = "ClasspathTlds";
    private static final String INIT_PARAM_DEBUG = "Debug";
    private static final String DEPR_INITPARAM_TEMPLATE_DELAY = "TemplateDelay";
    private static final String DEPR_INITPARAM_ENCODING = "DefaultEncoding";
    private static final String DEPR_INITPARAM_OBJECT_WRAPPER = "ObjectWrapper";
    private static final String DEPR_INITPARAM_WRAPPER_SIMPLE = "simple";
    private static final String DEPR_INITPARAM_WRAPPER_BEANS = "beans";
    private static final String DEPR_INITPARAM_WRAPPER_JYTHON = "jython";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER = "TemplateExceptionHandler";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW = "rethrow";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_DEBUG = "debug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG = "htmlDebug";
    private static final String DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE = "ignore";
    private static final String DEPR_INITPARAM_DEBUG = "debug";
    public static final String INIT_PARAM_VALUE_NEVER = "never";
    public static final String INIT_PARAM_VALUE_ALWAYS = "always";
    public static final String INIT_PARAM_VALUE_WHEN_TEMPLATE_HAS_MIME_TYPE = "whenTemplateHasMimeType";
    public static final String INIT_PARAM_VALUE_FROM_TEMPLATE = "fromTemplate";
    public static final String INIT_PARAM_VALUE_LEGACY = "legacy";
    public static final String INIT_PARAM_VALUE_DO_NOT_SET = "doNotSet";
    public static final String INIT_PARAM_VALUE_FORCE_PREFIX = "force ";
    public static final String SYSTEM_PROPERTY_META_INF_TLD_SOURCES = "org.freemarker.jsp.metaInfTldSources";
    public static final String SYSTEM_PROPERTY_CLASSPATH_TLDS = "org.freemarker.jsp.classpathTlds";
    public static final String META_INF_TLD_LOCATION_WEB_INF_PER_LIB_JARS = "webInfPerLibJars";
    public static final String META_INF_TLD_LOCATION_CLASSPATH = "classpath";
    public static final String META_INF_TLD_LOCATION_CLEAR = "clear";
    public static final String KEY_REQUEST = "Request";
    public static final String KEY_INCLUDE = "include_page";
    public static final String KEY_REQUEST_PRIVATE = "__FreeMarkerServlet.Request__";
    public static final String KEY_REQUEST_PARAMETERS = "RequestParameters";
    public static final String KEY_SESSION = "Session";
    public static final String KEY_APPLICATION = "Application";
    public static final String KEY_APPLICATION_PRIVATE = "__FreeMarkerServlet.Application__";
    public static final String KEY_JSP_TAGLIBS = "JspTaglibs";
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";
    private static final String ATTR_REQUEST_PARAMETERS_MODEL = ".freemarker.RequestParameters";
    private static final String ATTR_SESSION_MODEL = ".freemarker.Session";

    @Deprecated
    private static final String ATTR_APPLICATION_MODEL = ".freemarker.Application";

    @Deprecated
    private static final String ATTR_JSP_TAGLIBS_MODEL = ".freemarker.JspTaglibs";
    private static final String ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS = "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern";
    private static final String EXPIRATION_DATE;
    private String templatePath;
    private boolean noCache;
    private Integer bufferSize;
    private boolean exceptionOnMissingTemplate;

    @Deprecated
    protected boolean debug;

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Not investing into making this Servlet serializable")
    private Configuration config;

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Not investing into making this Servlet serializable")
    private ObjectWrapper wrapper;
    private ContentType contentType;

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Not investing into making this Servlet serializable")
    private Charset forcedResponseCharacterEncoding;
    private List metaInfTldSources;
    private List classpathTlds;

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Not investing into making this Servlet serializable")
    private ServletContextHashModel servletContextModel;

    @SuppressFBWarnings(value = {"SE_BAD_FIELD"}, justification = "Not investing into making this Servlet serializable")
    private TaglibFactory taglibFactory;
    private boolean objectWrapperMismatchWarnLogged;
    private static final Logger LOG = Logger.getLogger("freemarker.servlet");
    private static final Logger LOG_RT = Logger.getLogger("freemarker.runtime");
    private static final ContentType DEFAULT_CONTENT_TYPE = new ContentType("text/html");
    private OverrideResponseContentType overrideResponseContentType = (OverrideResponseContentType) initParamValueToEnum(getDefaultOverrideResponseContentType(), OverrideResponseContentType.values());
    private ResponseCharacterEncoding responseCharacterEncoding = ResponseCharacterEncoding.LEGACY;
    private OverrideResponseLocale overrideResponseLocale = OverrideResponseLocale.ALWAYS;
    private Object lazyInitFieldsLock = new Object();

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$InitParamValueEnum.class */
    private interface InitParamValueEnum {
        String getInitParamValue();
    }

    static {
        GregorianCalendar expiration = new GregorianCalendar();
        expiration.roll(1, -1);
        SimpleDateFormat httpDate = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        EXPIRATION_DATE = httpDate.format(expiration.getTime());
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: jakarta.servlet.ServletException */
    public void init() throws ServletException {
        try {
            initialize();
        } catch (Exception e) {
            throw new ServletException("Error while initializing " + getClass().getName() + " servlet; see cause exception.", e);
        }
    }

    private void initialize() throws ConflictingInitParamsException, MalformedWebXmlException, InitParamValueException {
        this.config = createConfiguration();
        String iciInitParamValue = getInitParameter("incompatible_improvements");
        if (iciInitParamValue != null) {
            try {
                this.config.setSetting("incompatible_improvements", iciInitParamValue);
            } catch (Exception e) {
                throw new InitParamValueException("incompatible_improvements", iciInitParamValue, e);
            }
        }
        if (!this.config.isTemplateExceptionHandlerExplicitlySet()) {
            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        }
        if (!this.config.isLogTemplateExceptionsExplicitlySet()) {
            this.config.setLogTemplateExceptions(false);
        }
        this.contentType = DEFAULT_CONTENT_TYPE;
        this.wrapper = createObjectWrapper();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Using object wrapper: " + this.wrapper);
        }
        this.config.setObjectWrapper(this.wrapper);
        this.templatePath = getInitParameter("TemplatePath");
        if (this.templatePath == null && !this.config.isTemplateLoaderExplicitlySet()) {
            this.templatePath = "class://";
        }
        if (this.templatePath != null) {
            try {
                this.config.setTemplateLoader(createTemplateLoader(this.templatePath));
            } catch (Exception e2) {
                throw new InitParamValueException("TemplatePath", this.templatePath, e2);
            }
        }
        this.metaInfTldSources = createDefaultMetaInfTldSources();
        this.classpathTlds = createDefaultClassPathTlds();
        Enumeration initpnames = getServletConfig().getInitParameterNames();
        while (initpnames.hasMoreElements()) {
            String name = (String) initpnames.nextElement();
            String value = getInitParameter(name);
            if (name == null) {
                throw new MalformedWebXmlException("init-param without param-name. Maybe the web.xml is not well-formed?");
            }
            if (value == null) {
                throw new MalformedWebXmlException("init-param " + StringUtil.jQuote(name) + " without param-value. Maybe the web.xml is not well-formed?");
            }
            try {
                if (!name.equals(DEPR_INITPARAM_OBJECT_WRAPPER) && !name.equals("object_wrapper") && !name.equals("TemplatePath") && !name.equals("incompatible_improvements")) {
                    if (name.equals(DEPR_INITPARAM_ENCODING)) {
                        if (getInitParameter("default_encoding") != null) {
                            throw new ConflictingInitParamsException("default_encoding", DEPR_INITPARAM_ENCODING);
                        }
                        this.config.setDefaultEncoding(value);
                    } else if (name.equals(DEPR_INITPARAM_TEMPLATE_DELAY)) {
                        if (getInitParameter("template_update_delay") != null) {
                            throw new ConflictingInitParamsException("template_update_delay", DEPR_INITPARAM_TEMPLATE_DELAY);
                        }
                        try {
                            this.config.setTemplateUpdateDelay(Integer.parseInt(value));
                        } catch (NumberFormatException e3) {
                        }
                    } else if (name.equals(DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER)) {
                        if (getInitParameter("template_exception_handler") != null) {
                            throw new ConflictingInitParamsException("template_exception_handler", DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER);
                        }
                        if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_RETHROW.equals(value)) {
                            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
                        } else if ("debug".equals(value)) {
                            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
                        } else if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_HTML_DEBUG.equals(value)) {
                            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
                        } else if (DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER_IGNORE.equals(value)) {
                            this.config.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);
                        } else {
                            throw new InitParamValueException(DEPR_INITPARAM_TEMPLATE_EXCEPTION_HANDLER, value, "Not one of the supported values.");
                        }
                    } else if (name.equals("NoCache")) {
                        this.noCache = StringUtil.getYesNo(value);
                    } else if (name.equals("BufferSize")) {
                        this.bufferSize = Integer.valueOf(parseSize(value));
                    } else if (name.equals("debug")) {
                        if (getInitParameter(INIT_PARAM_DEBUG) != null) {
                            throw new ConflictingInitParamsException(INIT_PARAM_DEBUG, "debug");
                        }
                        this.debug = StringUtil.getYesNo(value);
                    } else if (name.equals(INIT_PARAM_DEBUG)) {
                        this.debug = StringUtil.getYesNo(value);
                    } else if (name.equals("ContentType")) {
                        this.contentType = new ContentType(value);
                    } else if (name.equals("OverrideResponseContentType")) {
                        this.overrideResponseContentType = (OverrideResponseContentType) initParamValueToEnum(value, OverrideResponseContentType.values());
                    } else if (name.equals("ResponseCharacterEncoding")) {
                        this.responseCharacterEncoding = (ResponseCharacterEncoding) initParamValueToEnum(value, ResponseCharacterEncoding.values());
                        if (this.responseCharacterEncoding == ResponseCharacterEncoding.FORCE_CHARSET) {
                            String charsetName = value.substring("force ".length()).trim();
                            this.forcedResponseCharacterEncoding = Charset.forName(charsetName);
                        }
                    } else if (name.equals("OverrideResponseLocale")) {
                        this.overrideResponseLocale = (OverrideResponseLocale) initParamValueToEnum(value, OverrideResponseLocale.values());
                    } else if (name.equals("ExceptionOnMissingTemplate")) {
                        this.exceptionOnMissingTemplate = StringUtil.getYesNo(value);
                    } else if (name.equals("MetaInfTldSources")) {
                        this.metaInfTldSources = parseAsMetaInfTldLocations(value);
                    } else if (name.equals("ClasspathTlds")) {
                        List newClasspathTlds = new ArrayList();
                        if (this.classpathTlds != null) {
                            newClasspathTlds.addAll(this.classpathTlds);
                        }
                        newClasspathTlds.addAll(InitParamParser.parseCommaSeparatedList(value));
                        this.classpathTlds = newClasspathTlds;
                    } else {
                        this.config.setSetting(name, value);
                    }
                }
            } catch (ConflictingInitParamsException e4) {
                throw e4;
            } catch (Exception e5) {
                throw new InitParamValueException(name, value, e5);
            }
        }
        if (this.contentType.containsCharset && this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY) {
            throw new InitParamValueException("ContentType", this.contentType.httpHeaderValue, new IllegalStateException("You can't specify the charset in the content type, because the \"ResponseCharacterEncoding\" init-param isn't set to \"legacy\"."));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v33, types: [freemarker.ext.jakarta.jsp.TaglibFactory$ClasspathMetaInfTldSource] */
    /* JADX WARN: Type inference failed for: r0v34, types: [freemarker.ext.jakarta.jsp.TaglibFactory$ClasspathMetaInfTldSource] */
    /* JADX WARN: Type inference failed for: r0v39, types: [freemarker.ext.jakarta.jsp.TaglibFactory$WebInfPerLibJarMetaInfTldSource] */
    private List parseAsMetaInfTldLocations(String value) throws ParseException {
        TaglibFactory.ClearMetaInfTldSource classpathMetaInfTldSource;
        List metaInfTldSources = null;
        List<String> values = InitParamParser.parseCommaSeparatedList(value);
        for (String itemStr : values) {
            if (itemStr.equals("webInfPerLibJars")) {
                classpathMetaInfTldSource = TaglibFactory.WebInfPerLibJarMetaInfTldSource.INSTANCE;
            } else if (itemStr.startsWith("classpath")) {
                String itemRightSide = itemStr.substring("classpath".length()).trim();
                if (itemRightSide.length() == 0) {
                    classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(".*", 32));
                } else if (itemRightSide.startsWith(":")) {
                    String regexpStr = itemRightSide.substring(1).trim();
                    if (regexpStr.length() == 0) {
                        throw new ParseException("Empty regular expression after \"classpath:\"", -1);
                    }
                    classpathMetaInfTldSource = new TaglibFactory.ClasspathMetaInfTldSource(Pattern.compile(regexpStr));
                } else {
                    throw new ParseException("Invalid \"classpath\" value syntax: " + value, -1);
                }
            } else if (itemStr.startsWith("clear")) {
                classpathMetaInfTldSource = TaglibFactory.ClearMetaInfTldSource.INSTANCE;
            } else {
                throw new ParseException("Item has no recognized source type prefix: " + itemStr, -1);
            }
            if (metaInfTldSources == null) {
                metaInfTldSources = new ArrayList();
            }
            metaInfTldSources.add(classpathMetaInfTldSource);
        }
        return metaInfTldSources;
    }

    protected TemplateLoader createTemplateLoader(String templatePath) throws IOException {
        return InitParamParser.createTemplateLoader(templatePath, getConfiguration(), getClass(), getServletContext());
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        process(request, response);
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: jakarta.servlet.ServletException */
    private void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        boolean suppressServletException;
        String actualOutputCharset;
        if (preprocessRequest(request, response)) {
            return;
        }
        if (this.bufferSize != null && !response.isCommitted()) {
            try {
                response.setBufferSize(this.bufferSize.intValue());
            } catch (IllegalStateException e) {
                LOG.debug("Can't set buffer size any more,", e);
            }
        }
        String templatePath = requestUrlToTemplatePath(request);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Requested template " + StringUtil.jQuoteNoXSS(templatePath) + ".");
        }
        Locale locale = request.getLocale();
        if (locale == null || this.overrideResponseLocale != OverrideResponseLocale.NEVER) {
            locale = deduceLocale(templatePath, request, response);
        }
        try {
            Template template = this.config.getTemplate(templatePath, locale);
            boolean tempSpecContentTypeContainsCharset = false;
            if (response.getContentType() == null || this.overrideResponseContentType != OverrideResponseContentType.NEVER) {
                ContentType templateSpecificContentType = getTemplateSpecificContentType(template);
                if (templateSpecificContentType != null) {
                    response.setContentType(this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET ? templateSpecificContentType.httpHeaderValue : templateSpecificContentType.getMimeType());
                    tempSpecContentTypeContainsCharset = templateSpecificContentType.containsCharset;
                } else if (response.getContentType() == null || this.overrideResponseContentType == OverrideResponseContentType.ALWAYS) {
                    if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY && !this.contentType.containsCharset) {
                        response.setContentType(this.contentType.httpHeaderValue + "; charset=" + getTemplateSpecificOutputEncoding(template));
                    } else {
                        response.setContentType(this.contentType.httpHeaderValue);
                    }
                }
            }
            if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY && this.responseCharacterEncoding != ResponseCharacterEncoding.DO_NOT_SET) {
                if (this.responseCharacterEncoding != ResponseCharacterEncoding.FORCE_CHARSET) {
                    if (!tempSpecContentTypeContainsCharset) {
                        response.setCharacterEncoding(getTemplateSpecificOutputEncoding(template));
                    }
                } else {
                    response.setCharacterEncoding(this.forcedResponseCharacterEncoding.name());
                }
            }
            setBrowserCachingPolicy(response);
            ServletContext servletContext = getServletContext();
            try {
                logWarnOnObjectWrapperMismatch();
                TemplateModel model = createModel(this.wrapper, servletContext, request, response);
                if (preTemplateProcess(request, response, template, model)) {
                    try {
                        Environment env = template.createProcessingEnvironment(model, response.getWriter());
                        if (this.responseCharacterEncoding != ResponseCharacterEncoding.LEGACY && (actualOutputCharset = response.getCharacterEncoding()) != null) {
                            env.setOutputEncoding(actualOutputCharset);
                        }
                        processEnvironment(env, request, response);
                        postTemplateProcess(request, response, template, model);
                    } catch (Throwable th) {
                        postTemplateProcess(request, response, template, model);
                        throw th;
                    }
                }
            } catch (TemplateException e2) {
                TemplateExceptionHandler teh = this.config.getTemplateExceptionHandler();
                if (teh == TemplateExceptionHandler.HTML_DEBUG_HANDLER || teh == TemplateExceptionHandler.DEBUG_HANDLER || teh.getClass().getName().contains(INIT_PARAM_DEBUG)) {
                    response.flushBuffer();
                    suppressServletException = true;
                } else {
                    suppressServletException = false;
                }
                if (suppressServletException) {
                    logServletExceptionWithFreemarkerLog("Error executing FreeMarker template", e2);
                    log("Error executing FreeMarker template. Servlet-level exception was suppressed to show debug page with HTTP 200. See earlier FreeMarker log message for details!");
                    return;
                }
                throw newServletExceptionWithFreeMarkerLogging("Error executing FreeMarker template", e2);
            }
        } catch (freemarker.core.ParseException e3) {
            throw newServletExceptionWithFreeMarkerLogging("Parsing error with template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e3);
        } catch (TemplateNotFoundException e4) {
            if (this.exceptionOnMissingTemplate) {
                throw newServletExceptionWithFreeMarkerLogging("Template not found for name " + StringUtil.jQuoteNoXSS(templatePath) + ".", e4);
            }
            if (LOG.isDebugEnabled()) {
                LOG.debug("Responding HTTP 404 \"Not found\" for missing template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e4);
            }
            response.sendError(404, "Page template not found");
        } catch (Exception e5) {
            throw newServletExceptionWithFreeMarkerLogging("Unexpected error when loading template " + StringUtil.jQuoteNoXSS(templatePath) + ".", e5);
        }
    }

    protected void processEnvironment(Environment env, HttpServletRequest request, HttpServletResponse response) throws TemplateException, IOException {
        env.process();
    }

    private String getTemplateSpecificOutputEncoding(Template template) {
        String outputEncoding = this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY ? null : template.getOutputEncoding();
        return outputEncoding != null ? outputEncoding : template.getEncoding();
    }

    private ContentType getTemplateSpecificContentType(Template template) {
        Object contentTypeAttr = template.getCustomAttribute("content_type");
        if (contentTypeAttr != null) {
            return new ContentType(contentTypeAttr.toString());
        }
        String outputFormatMimeType = template.getOutputFormat().getMimeType();
        if (outputFormatMimeType != null) {
            if (this.responseCharacterEncoding == ResponseCharacterEncoding.LEGACY) {
                return new ContentType(outputFormatMimeType + "; charset=" + getTemplateSpecificOutputEncoding(template), true);
            }
            return new ContentType(outputFormatMimeType, false);
        }
        return null;
    }

    private ServletException newServletExceptionWithFreeMarkerLogging(String message, Throwable cause) throws ServletException {
        logServletExceptionWithFreemarkerLog(message, cause);
        ServletException e = new ServletException(message, cause);
        try {
            e.initCause(cause);
        } catch (Exception e2) {
        }
        return e;
    }

    private static void logServletExceptionWithFreemarkerLog(String message, Throwable cause) {
        if (cause instanceof TemplateException) {
            LOG_RT.error(message, cause);
        } else {
            LOG.error(message, cause);
        }
    }

    @SuppressFBWarnings(value = {"MSF_MUTABLE_SERVLET_FIELD", "DC_DOUBLECHECK"}, justification = "Performance trick")
    private void logWarnOnObjectWrapperMismatch() {
        boolean logWarn;
        if (this.wrapper != this.config.getObjectWrapper() && !this.objectWrapperMismatchWarnLogged && LOG.isWarnEnabled()) {
            synchronized (this) {
                logWarn = !this.objectWrapperMismatchWarnLogged;
                if (logWarn) {
                    this.objectWrapperMismatchWarnLogged = true;
                }
            }
            if (logWarn) {
                LOG.warn(getClass().getName() + ".wrapper != config.getObjectWrapper(); possibly the result of incorrect extension of " + FreemarkerServlet.class.getName() + ".");
            }
        }
    }

    protected Locale deduceLocale(String templatePath, HttpServletRequest request, HttpServletResponse response) throws ServletException {
        return this.config.getLocale();
    }

    protected TemplateModel createModel(ObjectWrapper objectWrapper, ServletContext servletContext, HttpServletRequest request, HttpServletResponse response) throws TemplateModelException, ServletException {
        ServletContextHashModel servletContextModel;
        TaglibFactory taglibFactory;
        HttpSessionHashModel sessionModel;
        try {
            AllHttpScopesHashModel params = new AllHttpScopesHashModel(objectWrapper, servletContext, request);
            synchronized (this.lazyInitFieldsLock) {
                if (this.servletContextModel == null) {
                    servletContextModel = new ServletContextHashModel((GenericServlet) this, objectWrapper);
                    taglibFactory = createTaglibFactory(objectWrapper, servletContext);
                    servletContext.setAttribute(ATTR_APPLICATION_MODEL, servletContextModel);
                    servletContext.setAttribute(ATTR_JSP_TAGLIBS_MODEL, taglibFactory);
                    initializeServletContext(request, response);
                    this.taglibFactory = taglibFactory;
                    this.servletContextModel = servletContextModel;
                } else {
                    servletContextModel = this.servletContextModel;
                    taglibFactory = this.taglibFactory;
                }
            }
            params.putUnlistedModel("Application", servletContextModel);
            params.putUnlistedModel("__FreeMarkerServlet.Application__", servletContextModel);
            params.putUnlistedModel("JspTaglibs", taglibFactory);
            HttpSession session = request.getSession(false);
            if (session != null) {
                sessionModel = (HttpSessionHashModel) session.getAttribute(ATTR_SESSION_MODEL);
                if (sessionModel == null || sessionModel.isOrphaned(session)) {
                    sessionModel = new HttpSessionHashModel(session, objectWrapper);
                    initializeSessionAndInstallModel(request, response, sessionModel, session);
                }
            } else {
                sessionModel = new HttpSessionHashModel(this, request, response, objectWrapper);
            }
            params.putUnlistedModel("Session", sessionModel);
            HttpRequestHashModel requestModel = (HttpRequestHashModel) request.getAttribute(ATTR_REQUEST_MODEL);
            if (requestModel == null || requestModel.getRequest() != request) {
                requestModel = new HttpRequestHashModel(request, response, objectWrapper);
                request.setAttribute(ATTR_REQUEST_MODEL, requestModel);
                request.setAttribute(ATTR_REQUEST_PARAMETERS_MODEL, createRequestParametersHashModel(request));
            }
            params.putUnlistedModel("Request", requestModel);
            params.putUnlistedModel("include_page", new IncludePage(request, response));
            params.putUnlistedModel("__FreeMarkerServlet.Request__", requestModel);
            HttpRequestParametersHashModel requestParametersModel = (HttpRequestParametersHashModel) request.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL);
            params.putUnlistedModel("RequestParameters", requestParametersModel);
            return params;
        } catch (ServletException | IOException e) {
            throw new TemplateModelException((Exception) e);
        }
    }

    protected TaglibFactory createTaglibFactory(ObjectWrapper objectWrapper, ServletContext servletContext) throws TemplateModelException {
        TaglibFactory taglibFactory = new TaglibFactory(servletContext);
        taglibFactory.setObjectWrapper(objectWrapper);
        List mergedMetaInfTldSources = new ArrayList();
        if (this.metaInfTldSources != null) {
            mergedMetaInfTldSources.addAll(this.metaInfTldSources);
        }
        String sysPropVal = SecurityUtilities.getSystemProperty("org.freemarker.jsp.metaInfTldSources", (String) null);
        if (sysPropVal != null) {
            try {
                List metaInfTldSourcesSysProp = parseAsMetaInfTldLocations(sysPropVal);
                if (metaInfTldSourcesSysProp != null) {
                    mergedMetaInfTldSources.addAll(metaInfTldSourcesSysProp);
                }
            } catch (ParseException e) {
                throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.metaInfTldSources\"", (Exception) e);
            }
        }
        List<Pattern> jettyTaglibJarPatterns = null;
        try {
            String attrVal = (String) servletContext.getAttribute(ATTR_JETTY_CP_TAGLIB_JAR_PATTERNS);
            jettyTaglibJarPatterns = attrVal != null ? InitParamParser.parseCommaSeparatedPatterns(attrVal) : null;
        } catch (Exception e2) {
            LOG.error("Failed to parse application context attribute \"org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern\" - it will be ignored", e2);
        }
        if (jettyTaglibJarPatterns != null) {
            for (Pattern pattern : jettyTaglibJarPatterns) {
                mergedMetaInfTldSources.add(new TaglibFactory.ClasspathMetaInfTldSource(pattern));
            }
        }
        taglibFactory.setMetaInfTldSources(mergedMetaInfTldSources);
        List mergedClassPathTlds = new ArrayList();
        if (this.classpathTlds != null) {
            mergedClassPathTlds.addAll(this.classpathTlds);
        }
        String sysPropVal2 = SecurityUtilities.getSystemProperty("org.freemarker.jsp.classpathTlds", (String) null);
        if (sysPropVal2 != null) {
            try {
                List classpathTldsSysProp = InitParamParser.parseCommaSeparatedList(sysPropVal2);
                if (classpathTldsSysProp != null) {
                    mergedClassPathTlds.addAll(classpathTldsSysProp);
                }
            } catch (ParseException e3) {
                throw new TemplateModelException("Failed to parse system property \"org.freemarker.jsp.classpathTlds\"", (Exception) e3);
            }
        }
        taglibFactory.setClasspathTlds(mergedClassPathTlds);
        return taglibFactory;
    }

    protected List createDefaultClassPathTlds() {
        return TaglibFactory.DEFAULT_CLASSPATH_TLDS;
    }

    protected List createDefaultMetaInfTldSources() {
        return TaglibFactory.DEFAULT_META_INF_TLD_SOURCES;
    }

    void initializeSessionAndInstallModel(HttpServletRequest request, HttpServletResponse response, HttpSessionHashModel sessionModel, HttpSession session) throws IOException, ServletException {
        session.setAttribute(ATTR_SESSION_MODEL, sessionModel);
        initializeSession(request, response);
    }

    protected String requestUrlToTemplatePath(HttpServletRequest request) throws ServletException {
        String includeServletPath = (String) request.getAttribute("jakarta.servlet.include.servlet_path");
        if (includeServletPath != null) {
            String includePathInfo = (String) request.getAttribute("jakarta.servlet.include.path_info");
            return includePathInfo == null ? includeServletPath : includePathInfo;
        }
        String path = request.getPathInfo();
        if (path != null) {
            return path;
        }
        String path2 = request.getServletPath();
        return path2 != null ? path2 : "";
    }

    protected boolean preprocessRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    protected Configuration createConfiguration() {
        return new Configuration();
    }

    protected void setConfigurationDefaults() {
    }

    protected ObjectWrapper createObjectWrapper() throws freemarker.core.ParseException, ClassNotFoundException, NumberFormatException {
        String wrapper = getServletConfig().getInitParameter(DEPR_INITPARAM_OBJECT_WRAPPER);
        if (wrapper != null) {
            if (getInitParameter("object_wrapper") != null) {
                throw new RuntimeException("Conflicting init-params: object_wrapper and ObjectWrapper");
            }
            if ("beans".equals(wrapper)) {
                return ObjectWrapper.BEANS_WRAPPER;
            }
            if (DEPR_INITPARAM_WRAPPER_SIMPLE.equals(wrapper)) {
                return ObjectWrapper.SIMPLE_WRAPPER;
            }
            if (DEPR_INITPARAM_WRAPPER_JYTHON.equals(wrapper)) {
                try {
                    return (ObjectWrapper) Class.forName("freemarker.ext.jython.JythonWrapper").newInstance();
                } catch (ClassNotFoundException e) {
                    throw new NoClassDefFoundError(e.getMessage());
                } catch (IllegalAccessException e2) {
                    throw new IllegalAccessError(e2.getMessage());
                } catch (InstantiationException e3) {
                    throw new InstantiationError(e3.getMessage());
                }
            }
            return createDefaultObjectWrapper();
        }
        String wrapper2 = getInitParameter("object_wrapper");
        if (wrapper2 == null) {
            if (!this.config.isObjectWrapperExplicitlySet()) {
                return createDefaultObjectWrapper();
            }
            return this.config.getObjectWrapper();
        }
        try {
            this.config.setSetting("object_wrapper", wrapper2);
            return this.config.getObjectWrapper();
        } catch (TemplateException e4) {
            throw new RuntimeException("Failed to set object_wrapper", e4);
        }
    }

    protected ObjectWrapper createDefaultObjectWrapper() {
        return Configuration.getDefaultObjectWrapper(this.config.getIncompatibleImprovements());
    }

    protected ObjectWrapper getObjectWrapper() {
        return this.wrapper;
    }

    @Deprecated
    protected final String getTemplatePath() {
        return this.templatePath;
    }

    protected HttpRequestParametersHashModel createRequestParametersHashModel(HttpServletRequest request) {
        return new HttpRequestParametersHashModel(request);
    }

    protected void initializeServletContext(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    protected void initializeSession(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
    }

    protected boolean preTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel model) throws IOException, ServletException {
        return true;
    }

    protected void postTemplateProcess(HttpServletRequest request, HttpServletResponse response, Template template, TemplateModel data) throws IOException, ServletException {
    }

    protected Configuration getConfiguration() {
        return this.config;
    }

    protected String getDefaultOverrideResponseContentType() {
        return "always";
    }

    private void setBrowserCachingPolicy(HttpServletResponse res) {
        if (this.noCache) {
            res.setHeader(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, post-check=0, pre-check=0");
            res.setHeader(HttpHeaders.PRAGMA, "no-cache");
            res.setHeader(HttpHeaders.EXPIRES, EXPIRATION_DATE);
        }
    }

    private int parseSize(String value) throws NumberFormatException, ParseException {
        int unit;
        char c;
        int lastDigitIdx = value.length() - 1;
        while (lastDigitIdx >= 0 && ((c = value.charAt(lastDigitIdx)) < '0' || c > '9')) {
            lastDigitIdx--;
        }
        int n = Integer.parseInt(value.substring(0, lastDigitIdx + 1).trim());
        String unitStr = value.substring(lastDigitIdx + 1).trim().toUpperCase();
        if (unitStr.length() == 0 || unitStr.equals("B")) {
            unit = 1;
        } else if (unitStr.equals("K") || unitStr.equals("KB") || unitStr.equals("KIB")) {
            unit = 1024;
        } else if (unitStr.equals("M") || unitStr.equals("MB") || unitStr.equals("MIB")) {
            unit = 1048576;
        } else {
            throw new ParseException("Unknown unit: " + unitStr, lastDigitIdx + 1);
        }
        long size = n * unit;
        if (size < 0) {
            throw new IllegalArgumentException("Buffer size can't be negative");
        }
        if (size > 2147483647L) {
            throw new IllegalArgumentException("Buffer size can't bigger than 2147483647");
        }
        return (int) size;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$InitParamValueException.class */
    private static class InitParamValueException extends Exception {
        InitParamValueException(String initParamName, String initParamValue, Throwable casue) {
            super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + "; see cause exception.", casue);
        }

        public InitParamValueException(String initParamName, String initParamValue, String cause) {
            super("Failed to set the " + StringUtil.jQuote(initParamName) + " servlet init-param to " + StringUtil.jQuote(initParamValue) + ": " + cause);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$ConflictingInitParamsException.class */
    private static class ConflictingInitParamsException extends Exception {
        ConflictingInitParamsException(String recommendedName, String otherName) {
            super("Conflicting servlet init-params: " + StringUtil.jQuote(recommendedName) + " and " + StringUtil.jQuote(otherName) + ". Only use " + StringUtil.jQuote(recommendedName) + ".");
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$MalformedWebXmlException.class */
    private static class MalformedWebXmlException extends Exception {
        MalformedWebXmlException(String message) {
            super(message);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$ContentType.class */
    private static class ContentType {
        private final String httpHeaderValue;
        private final boolean containsCharset;

        public ContentType(String httpHeaderValue) {
            this(httpHeaderValue, contentTypeContainsCharset(httpHeaderValue));
        }

        public ContentType(String httpHeaderValue, boolean containsCharset) {
            this.httpHeaderValue = httpHeaderValue;
            this.containsCharset = containsCharset;
        }

        private static boolean contentTypeContainsCharset(String contentType) {
            int charsetIdx = contentType.toLowerCase().indexOf("charset=");
            if (charsetIdx != -1) {
                char c = 0;
                do {
                    charsetIdx--;
                    if (charsetIdx < 0) {
                        break;
                    }
                    c = contentType.charAt(charsetIdx);
                } while (Character.isWhitespace(c));
                if (charsetIdx == -1 || c == ';') {
                    return true;
                }
                return false;
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getMimeType() {
            int scIdx = this.httpHeaderValue.indexOf(59);
            return (scIdx == -1 ? this.httpHeaderValue : this.httpHeaderValue.substring(0, scIdx)).trim();
        }
    }

    private <T extends InitParamValueEnum> T initParamValueToEnum(String initParamValue, T[] enumValues) {
        for (T enumValue : enumValues) {
            String enumInitParamValue = enumValue.getInitParamValue();
            if (initParamValue.equals(enumInitParamValue) || (enumInitParamValue.endsWith("}") && initParamValue.startsWith(enumInitParamValue.substring(0, enumInitParamValue.indexOf("${"))))) {
                return enumValue;
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtil.jQuote(initParamValue));
        sb.append(" is not a one of the enumeration values: ");
        boolean first = true;
        for (T value : enumValues) {
            if (!first) {
                sb.append(", ");
            } else {
                first = false;
            }
            sb.append(StringUtil.jQuote(value.getInitParamValue()));
        }
        throw new IllegalArgumentException(sb.toString());
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$OverrideResponseContentType.class */
    private enum OverrideResponseContentType implements InitParamValueEnum {
        ALWAYS("always"),
        NEVER("never"),
        WHEN_TEMPLATE_HAS_MIME_TYPE("whenTemplateHasMimeType");

        private final String initParamValue;

        OverrideResponseContentType(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override // freemarker.ext.jakarta.servlet.FreemarkerServlet.InitParamValueEnum
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$ResponseCharacterEncoding.class */
    private enum ResponseCharacterEncoding implements InitParamValueEnum {
        LEGACY("legacy"),
        FROM_TEMPLATE("fromTemplate"),
        DO_NOT_SET("doNotSet"),
        FORCE_CHARSET("force ${charsetName}");

        private final String initParamValue;

        ResponseCharacterEncoding(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override // freemarker.ext.jakarta.servlet.FreemarkerServlet.InitParamValueEnum
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/servlet/FreemarkerServlet$OverrideResponseLocale.class */
    private enum OverrideResponseLocale implements InitParamValueEnum {
        ALWAYS("always"),
        NEVER("never");

        private final String initParamValue;

        OverrideResponseLocale(String initParamValue) {
            this.initParamValue = initParamValue;
        }

        @Override // freemarker.ext.jakarta.servlet.FreemarkerServlet.InitParamValueEnum
        public String getInitParamValue() {
            return this.initParamValue;
        }
    }
}
