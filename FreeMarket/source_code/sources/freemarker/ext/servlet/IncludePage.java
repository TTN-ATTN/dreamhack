package freemarker.ext.servlet;

import freemarker.core.Environment;
import freemarker.core._DelayedFTLTypeDescription;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.utility.DeepUnwrap;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/servlet/IncludePage.class */
public class IncludePage implements TemplateDirectiveModel {
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public IncludePage(HttpServletRequest request, HttpServletResponse response) {
        this.request = request;
        this.response = response;
    }

    @Override // freemarker.template.TemplateDirectiveModel
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
        HttpServletResponse wrappedResponse;
        boolean inheritParams;
        Map paramsMap;
        HttpServletRequest wrappedRequest;
        TemplateModel path = (TemplateModel) params.get("path");
        if (path == null) {
            throw new _MiscTemplateException(env, "Missing required parameter \"path\"");
        }
        if (!(path instanceof TemplateScalarModel)) {
            throw new _MiscTemplateException(env, "Expected a scalar model. \"path\" is instead ", new _DelayedFTLTypeDescription(path));
        }
        String strPath = ((TemplateScalarModel) path).getAsString();
        if (strPath == null) {
            throw new _MiscTemplateException(env, "String value of \"path\" parameter is null");
        }
        Writer envOut = env.getOut();
        if (envOut == this.response.getWriter()) {
            wrappedResponse = this.response;
        } else {
            final PrintWriter printWriter = envOut instanceof PrintWriter ? (PrintWriter) envOut : new PrintWriter(envOut);
            wrappedResponse = new HttpServletResponseWrapper(this.response) { // from class: freemarker.ext.servlet.IncludePage.1
                @Override // javax.servlet.ServletResponseWrapper, javax.servlet.ServletResponse
                public PrintWriter getWriter() {
                    return printWriter;
                }
            };
        }
        TemplateModel inheritParamsModel = (TemplateModel) params.get("inherit_params");
        if (inheritParamsModel == null) {
            inheritParams = true;
        } else {
            if (!(inheritParamsModel instanceof TemplateBooleanModel)) {
                throw new _MiscTemplateException(env, "\"inherit_params\" should be a boolean but it's a(n) ", inheritParamsModel.getClass().getName(), " instead");
            }
            inheritParams = ((TemplateBooleanModel) inheritParamsModel).getAsBoolean();
        }
        TemplateModel paramsModel = (TemplateModel) params.get("params");
        if (paramsModel == null && inheritParams) {
            wrappedRequest = this.request;
        } else {
            if (paramsModel != null) {
                Object unwrapped = DeepUnwrap.unwrap(paramsModel);
                if (!(unwrapped instanceof Map)) {
                    throw new _MiscTemplateException(env, "Expected \"params\" to unwrap into a java.util.Map. It unwrapped into ", unwrapped.getClass().getName(), " instead.");
                }
                paramsMap = (Map) unwrapped;
            } else {
                paramsMap = Collections.EMPTY_MAP;
            }
            wrappedRequest = new CustomParamsRequest(this.request, paramsMap, inheritParams);
        }
        try {
            this.request.getRequestDispatcher(strPath).include(wrappedRequest, wrappedResponse);
        } catch (ServletException e) {
            throw new _MiscTemplateException(e, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/servlet/IncludePage$CustomParamsRequest.class */
    private static final class CustomParamsRequest extends HttpServletRequestWrapper {
        private final HashMap paramsMap;

        private CustomParamsRequest(HttpServletRequest request, Map paramMap, boolean inheritParams) {
            String[] valueArray;
            super(request);
            this.paramsMap = inheritParams ? new HashMap(request.getParameterMap()) : new HashMap();
            for (Map.Entry entry : paramMap.entrySet()) {
                String name = String.valueOf(entry.getKey());
                Object value = entry.getValue();
                if (value == null) {
                    valueArray = new String[]{null};
                } else if (value instanceof String[]) {
                    valueArray = (String[]) value;
                } else if (value instanceof Collection) {
                    Collection col = (Collection) value;
                    valueArray = new String[col.size()];
                    int i = 0;
                    Iterator it2 = col.iterator();
                    while (it2.hasNext()) {
                        int i2 = i;
                        i++;
                        valueArray[i2] = String.valueOf(it2.next());
                    }
                } else if (value.getClass().isArray()) {
                    int len = Array.getLength(value);
                    valueArray = new String[len];
                    for (int i3 = 0; i3 < len; i3++) {
                        valueArray[i3] = String.valueOf(Array.get(value, i3));
                    }
                } else {
                    valueArray = new String[]{String.valueOf(value)};
                }
                String[] existingParams = (String[]) this.paramsMap.get(name);
                int el = existingParams == null ? 0 : existingParams.length;
                if (el == 0) {
                    this.paramsMap.put(name, valueArray);
                } else {
                    int vl = valueArray.length;
                    if (vl > 0) {
                        String[] newValueArray = new String[el + vl];
                        System.arraycopy(valueArray, 0, newValueArray, 0, vl);
                        System.arraycopy(existingParams, 0, newValueArray, vl, el);
                        this.paramsMap.put(name, newValueArray);
                    }
                }
            }
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public String[] getParameterValues(String name) {
            String[] value = (String[]) this.paramsMap.get(name);
            if (value != null) {
                return (String[]) value.clone();
            }
            return null;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public String getParameter(String name) {
            String[] values = (String[]) this.paramsMap.get(name);
            if (values == null || values.length <= 0) {
                return null;
            }
            return values[0];
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Enumeration getParameterNames() {
            return Collections.enumeration(this.paramsMap.keySet());
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Map getParameterMap() {
            HashMap clone = (HashMap) this.paramsMap.clone();
            for (Map.Entry entry : clone.entrySet()) {
                entry.setValue(((String[]) entry.getValue()).clone());
            }
            return Collections.unmodifiableMap(clone);
        }
    }
}
