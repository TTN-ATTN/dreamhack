package org.springframework.web.servlet.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/AbstractController.class */
public abstract class AbstractController extends WebContentGenerator implements Controller {
    private boolean synchronizeOnSession;

    @Nullable
    protected abstract ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception;

    public AbstractController() {
        this(true);
    }

    public AbstractController(boolean restrictDefaultSupportedMethods) {
        super(restrictDefaultSupportedMethods);
        this.synchronizeOnSession = false;
    }

    public final void setSynchronizeOnSession(boolean synchronizeOnSession) {
        this.synchronizeOnSession = synchronizeOnSession;
    }

    public final boolean isSynchronizeOnSession() {
        return this.synchronizeOnSession;
    }

    @Override // org.springframework.web.servlet.mvc.Controller
    @Nullable
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpSession session;
        ModelAndView modelAndViewHandleRequestInternal;
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            response.setHeader(HttpHeaders.ALLOW, getAllowHeader());
            return null;
        }
        checkRequest(request);
        prepareResponse(response);
        if (this.synchronizeOnSession && (session = request.getSession(false)) != null) {
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                modelAndViewHandleRequestInternal = handleRequestInternal(request, response);
            }
            return modelAndViewHandleRequestInternal;
        }
        return handleRequestInternal(request, response);
    }
}
