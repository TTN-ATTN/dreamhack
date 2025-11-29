package org.springframework.web.servlet.function.support;

import java.util.List;
import java.util.function.Supplier;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.async.AsyncWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/support/HandlerFunctionAdapter.class */
public class HandlerFunctionAdapter implements HandlerAdapter, Ordered {
    private static final Log logger = LogFactory.getLog((Class<?>) HandlerFunctionAdapter.class);
    private int order = Integer.MAX_VALUE;

    @Nullable
    private Long asyncRequestTimeout;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setAsyncRequestTimeout(long timeout) {
        this.asyncRequestTimeout = Long.valueOf(timeout);
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public boolean supports(Object handler) {
        return handler instanceof HandlerFunction;
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    @Nullable
    public ModelAndView handle(HttpServletRequest servletRequest, HttpServletResponse servletResponse, Object handler) throws Exception {
        ServerResponse serverResponse;
        WebAsyncManager asyncManager = getWebAsyncManager(servletRequest, servletResponse);
        ServerRequest serverRequest = getServerRequest(servletRequest);
        if (asyncManager.hasConcurrentResult()) {
            serverResponse = handleAsync(asyncManager);
        } else {
            HandlerFunction<?> handlerFunction = (HandlerFunction) handler;
            serverResponse = handlerFunction.handle(serverRequest);
        }
        if (serverResponse != null) {
            return serverResponse.writeTo(servletRequest, servletResponse, new ServerRequestContext(serverRequest));
        }
        return null;
    }

    private WebAsyncManager getWebAsyncManager(HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
        AsyncWebRequest asyncWebRequest = WebAsyncUtils.createAsyncWebRequest(servletRequest, servletResponse);
        asyncWebRequest.setTimeout(this.asyncRequestTimeout);
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(servletRequest);
        asyncManager.setAsyncWebRequest(asyncWebRequest);
        return asyncManager;
    }

    private ServerRequest getServerRequest(HttpServletRequest servletRequest) {
        ServerRequest serverRequest = (ServerRequest) servletRequest.getAttribute(RouterFunctions.REQUEST_ATTRIBUTE);
        Assert.state(serverRequest != null, (Supplier<String>) () -> {
            return "Required attribute '" + RouterFunctions.REQUEST_ATTRIBUTE + "' is missing";
        });
        return serverRequest;
    }

    @Nullable
    private ServerResponse handleAsync(WebAsyncManager asyncManager) throws Exception {
        Object result = asyncManager.getConcurrentResult();
        asyncManager.clearConcurrentResult();
        LogFormatUtils.traceDebug(logger, traceOn -> {
            String formatted = LogFormatUtils.formatValue(result, !traceOn.booleanValue());
            return "Resume with async result [" + formatted + "]";
        });
        if (result instanceof ServerResponse) {
            return (ServerResponse) result;
        }
        if (result instanceof Exception) {
            throw ((Exception) result);
        }
        if (result instanceof Throwable) {
            throw new ServletException("Async processing failed", (Throwable) result);
        }
        if (result == null) {
            return null;
        }
        throw new IllegalArgumentException("Unknown result from WebAsyncManager: [" + result + "]");
    }

    @Override // org.springframework.web.servlet.HandlerAdapter
    public long getLastModified(HttpServletRequest request, Object handler) {
        return -1L;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/support/HandlerFunctionAdapter$ServerRequestContext.class */
    private static class ServerRequestContext implements ServerResponse.Context {
        private final ServerRequest serverRequest;

        public ServerRequestContext(ServerRequest serverRequest) {
            this.serverRequest = serverRequest;
        }

        @Override // org.springframework.web.servlet.function.ServerResponse.Context
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.serverRequest.messageConverters();
        }
    }
}
