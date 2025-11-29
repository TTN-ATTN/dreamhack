package freemarker.ext.dom;

import freemarker.core.CustomAttribute;
import freemarker.core.Environment;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.jaxen.BaseXPath;
import org.jaxen.Function;
import org.jaxen.FunctionCallException;
import org.jaxen.FunctionContext;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.Navigator;
import org.jaxen.UnresolvableException;
import org.jaxen.VariableContext;
import org.jaxen.XPathFunctionContext;
import org.jaxen.dom.DocumentNavigator;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/JaxenXPathSupport.class */
class JaxenXPathSupport implements XPathSupport {
    private static final CustomAttribute XPATH_CACHE_ATTR = new CustomAttribute(1) { // from class: freemarker.ext.dom.JaxenXPathSupport.1
        @Override // freemarker.core.CustomAttribute
        protected Object create() {
            return new HashMap();
        }
    };
    private static final ArrayList EMPTY_ARRAYLIST = new ArrayList();
    private static final NamespaceContext customNamespaceContext = new NamespaceContext() { // from class: freemarker.ext.dom.JaxenXPathSupport.2
        public String translateNamespacePrefixToUri(String prefix) {
            if (prefix.equals(Template.DEFAULT_NAMESPACE_PREFIX)) {
                return Environment.getCurrentEnvironment().getDefaultNS();
            }
            return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
        }
    };
    private static final VariableContext FM_VARIABLE_CONTEXT = new VariableContext() { // from class: freemarker.ext.dom.JaxenXPathSupport.3
        /* JADX INFO: Thrown type has an unknown type hierarchy: org.jaxen.UnresolvableException */
        public Object getVariableValue(String namespaceURI, String prefix, String localName) throws UnresolvableException {
            try {
                TemplateModel model = Environment.getCurrentEnvironment().getVariable(localName);
                if (model == null) {
                    throw new UnresolvableException("Variable \"" + localName + "\" not found.");
                }
                if (model instanceof TemplateScalarModel) {
                    return ((TemplateScalarModel) model).getAsString();
                }
                if (model instanceof TemplateNumberModel) {
                    return ((TemplateNumberModel) model).getAsNumber();
                }
                if (model instanceof TemplateDateModel) {
                    return ((TemplateDateModel) model).getAsDate();
                }
                if (model instanceof TemplateBooleanModel) {
                    return Boolean.valueOf(((TemplateBooleanModel) model).getAsBoolean());
                }
                throw new UnresolvableException("Variable \"" + localName + "\" exists, but it's not a string, number, date, or boolean");
            } catch (TemplateModelException e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    };
    private static final FunctionContext FM_FUNCTION_CONTEXT = new XPathFunctionContext() { // from class: freemarker.ext.dom.JaxenXPathSupport.4
        public Function getFunction(String namespaceURI, String prefix, String localName) throws UnresolvableException {
            try {
                return super.getFunction(namespaceURI, prefix, localName);
            } catch (UnresolvableException e) {
                return super.getFunction((String) null, (String) null, localName);
            }
        }
    };
    private static final CustomAttribute FM_DOM_NAVIAGOTOR_CACHED_DOM = new CustomAttribute(1);
    private static final Navigator FM_DOM_NAVIGATOR = new DocumentNavigator() { // from class: freemarker.ext.dom.JaxenXPathSupport.5
        /* JADX INFO: Thrown type has an unknown type hierarchy: org.jaxen.FunctionCallException */
        public Object getDocument(String uri) throws ParserConfigurationException, SAXException, IOException, FunctionCallException {
            try {
                Template raw = JaxenXPathSupport.getTemplate(uri);
                Document doc = (Document) JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.get(raw);
                if (doc == null) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    factory.setNamespaceAware(true);
                    DocumentBuilder builder = factory.newDocumentBuilder();
                    FmEntityResolver er = new FmEntityResolver();
                    builder.setEntityResolver(er);
                    doc = builder.parse(JaxenXPathSupport.createInputSource(null, raw));
                    if (er.getCallCount() == 0) {
                        JaxenXPathSupport.FM_DOM_NAVIAGOTOR_CACHED_DOM.set(doc, raw);
                    }
                }
                return doc;
            } catch (Exception e) {
                throw new FunctionCallException("Failed to parse document for URI: " + uri, e);
            }
        }
    };

    JaxenXPathSupport() {
    }

    @Override // freemarker.ext.dom.XPathSupport
    public TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
        BaseXPath xpath;
        try {
            Map<String, BaseXPath> xpathCache = (Map) XPATH_CACHE_ATTR.get();
            synchronized (xpathCache) {
                xpath = xpathCache.get(xpathQuery);
                if (xpath == null) {
                    xpath = new BaseXPath(xpathQuery, FM_DOM_NAVIGATOR);
                    xpath.setNamespaceContext(customNamespaceContext);
                    xpath.setFunctionContext(FM_FUNCTION_CONTEXT);
                    xpath.setVariableContext(FM_VARIABLE_CONTEXT);
                    xpathCache.put(xpathQuery, xpath);
                }
            }
            List result = xpath.selectNodes(context != null ? context : EMPTY_ARRAYLIST);
            if (result.size() == 1) {
                return ObjectWrapper.DEFAULT_WRAPPER.wrap(result.get(0));
            }
            NodeListModel nlm = new NodeListModel(result, (NodeModel) null);
            nlm.xpathSupport = this;
            return nlm;
        } catch (UndeclaredThrowableException e) {
            Throwable t = e.getUndeclaredThrowable();
            if (t instanceof TemplateModelException) {
                throw ((TemplateModelException) t);
            }
            throw e;
        } catch (JaxenException je) {
            throw new TemplateModelException((Exception) je);
        }
    }

    static Template getTemplate(String systemId) throws IOException {
        Environment env = Environment.getCurrentEnvironment();
        String encoding = env.getTemplate().getEncoding();
        if (encoding == null) {
            encoding = env.getConfiguration().getEncoding(env.getLocale());
        }
        String templatePath = env.getTemplate().getName();
        int lastSlash = templatePath.lastIndexOf(47);
        Template raw = env.getConfiguration().getTemplate(env.toFullTemplateName(lastSlash == -1 ? "" : templatePath.substring(0, lastSlash + 1), systemId), env.getLocale(), encoding, false);
        return raw;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static InputSource createInputSource(String publicId, Template raw) throws SAXException, IOException {
        StringWriter sw = new StringWriter();
        try {
            raw.process(Collections.EMPTY_MAP, sw);
            InputSource is = new InputSource();
            is.setPublicId(publicId);
            is.setSystemId(raw.getName());
            is.setCharacterStream(new StringReader(sw.toString()));
            return is;
        } catch (TemplateException e) {
            throw new SAXException(e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/JaxenXPathSupport$FmEntityResolver.class */
    private static class FmEntityResolver implements EntityResolver {
        private int callCount;

        private FmEntityResolver() {
            this.callCount = 0;
        }

        @Override // org.xml.sax.EntityResolver
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            this.callCount++;
            return JaxenXPathSupport.createInputSource(publicId, JaxenXPathSupport.getTemplate(systemId));
        }

        int getCallCount() {
            return this.callCount;
        }
    }
}
