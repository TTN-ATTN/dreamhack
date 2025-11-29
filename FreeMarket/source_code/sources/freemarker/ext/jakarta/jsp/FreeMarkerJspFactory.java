package freemarker.ext.jakarta.jsp;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.jsp.JspApplicationContext;
import jakarta.servlet.jsp.JspEngineInfo;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/jakarta/jsp/FreeMarkerJspFactory.class */
class FreeMarkerJspFactory extends JspFactory {
    private static final String SPECIFICATION_VERSION = "2.2";
    private static final String JSPCTX_KEY = "freemarker.ext.jakarta.jsp.FreeMarkerJspFactory21#jspAppContext";

    FreeMarkerJspFactory() {
    }

    String getSpecificationVersion() {
        return SPECIFICATION_VERSION;
    }

    public JspEngineInfo getEngineInfo() {
        return new JspEngineInfo() { // from class: freemarker.ext.jakarta.jsp.FreeMarkerJspFactory.1
            public String getSpecificationVersion() {
                return FreeMarkerJspFactory.this.getSpecificationVersion();
            }
        };
    }

    public PageContext getPageContext(Servlet servlet, ServletRequest request, ServletResponse response, String errorPageURL, boolean needsSession, int bufferSize, boolean autoFlush) {
        throw new UnsupportedOperationException();
    }

    public void releasePageContext(PageContext ctx) {
        throw new UnsupportedOperationException();
    }

    public JspApplicationContext getJspApplicationContext(ServletContext ctx) {
        JspApplicationContext jspctx = (JspApplicationContext) ctx.getAttribute(JSPCTX_KEY);
        if (jspctx == null) {
            synchronized (ctx) {
                jspctx = (JspApplicationContext) ctx.getAttribute(JSPCTX_KEY);
                if (jspctx == null) {
                    jspctx = new FreeMarkerJspApplicationContext();
                    ctx.setAttribute(JSPCTX_KEY, jspctx);
                }
            }
        }
        return jspctx;
    }
}
