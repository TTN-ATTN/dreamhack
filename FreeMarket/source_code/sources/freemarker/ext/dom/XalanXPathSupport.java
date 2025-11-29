package freemarker.ext.dom;

import freemarker.core.Environment;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.List;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xpath.objects.XNull;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.objects.XString;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeIterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/dom/XalanXPathSupport.class */
class XalanXPathSupport implements XPathSupport {
    private XPathContext xpathContext = new XPathContext();
    private static final PrefixResolver CUSTOM_PREFIX_RESOLVER = new PrefixResolver() { // from class: freemarker.ext.dom.XalanXPathSupport.1
        public String getNamespaceForPrefix(String prefix, Node node) {
            return getNamespaceForPrefix(prefix);
        }

        public String getNamespaceForPrefix(String prefix) {
            if (prefix.equals(Template.DEFAULT_NAMESPACE_PREFIX)) {
                return Environment.getCurrentEnvironment().getDefaultNS();
            }
            return Environment.getCurrentEnvironment().getNamespaceForPrefix(prefix);
        }

        public String getBaseIdentifier() {
            return null;
        }

        public boolean handlesNullPrefixes() {
            return false;
        }
    };

    XalanXPathSupport() {
    }

    @Override // freemarker.ext.dom.XPathSupport
    public synchronized TemplateModel executeQuery(Object context, String xpathQuery) throws TemplateModelException {
        Node n;
        if (!(context instanceof Node)) {
            if (context == null || isNodeList(context)) {
                int cnt = context != null ? ((List) context).size() : 0;
                throw new TemplateModelException((cnt != 0 ? "Xalan can't perform an XPath query against a Node Set (contains " + cnt + " node(s)). Expecting a single Node." : "Xalan can't perform an XPath query against an empty Node Set.") + " (There's no such restriction if you configure FreeMarker to use Jaxen for XPath.)");
            }
            throw new TemplateModelException("Can't perform an XPath query against a " + context.getClass().getName() + ". Expecting a single org.w3c.dom.Node.");
        }
        Node node = (Node) context;
        try {
            XPath xpath = new XPath(xpathQuery, (SourceLocator) null, CUSTOM_PREFIX_RESOLVER, 0, (ErrorListener) null);
            int ctxtNode = this.xpathContext.getDTMHandleFromNode(node);
            XBoolean xBooleanExecute = xpath.execute(this.xpathContext, ctxtNode, CUSTOM_PREFIX_RESOLVER);
            if (xBooleanExecute instanceof XNodeSet) {
                NodeListModel result = new NodeListModel(node);
                result.xpathSupport = this;
                NodeIterator nodeIterator = xBooleanExecute.nodeset();
                do {
                    n = nodeIterator.nextNode();
                    if (n != null) {
                        result.add(n);
                    }
                } while (n != null);
                return result.size() == 1 ? result.get(0) : result;
            }
            if (xBooleanExecute instanceof XBoolean) {
                return xBooleanExecute.bool() ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
            }
            if (xBooleanExecute instanceof XNull) {
                return null;
            }
            if (xBooleanExecute instanceof XString) {
                return new SimpleScalar(xBooleanExecute.toString());
            }
            if (xBooleanExecute instanceof XNumber) {
                return new SimpleNumber(Double.valueOf(((XNumber) xBooleanExecute).num()));
            }
            throw new TemplateModelException("Cannot deal with type: " + xBooleanExecute.getClass().getName());
        } catch (TransformerException te) {
            throw new TemplateModelException((Exception) te);
        }
    }

    private static boolean isNodeList(Object context) {
        if (!(context instanceof List)) {
            return false;
        }
        List ls = (List) context;
        int ln = ls.size();
        for (int i = 0; i < ln; i++) {
            if (!(ls.get(i) instanceof Node)) {
                return false;
            }
        }
        return true;
    }
}
