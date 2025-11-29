package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/AbstractHandlerMethodExceptionResolver.class */
public abstract class AbstractHandlerMethodExceptionResolver extends AbstractHandlerExceptionResolver {
    @Nullable
    protected abstract ModelAndView doResolveHandlerMethodException(HttpServletRequest request, HttpServletResponse response, @Nullable HandlerMethod handlerMethod, Exception ex);

    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    protected boolean shouldApplyTo(HttpServletRequest request, @Nullable Object handler) {
        if (handler == null) {
            return super.shouldApplyTo(request, null);
        }
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return super.shouldApplyTo(request, handlerMethod.getBean());
        }
        if (hasGlobalExceptionHandlers() && hasHandlerMappings()) {
            return super.shouldApplyTo(request, handler);
        }
        return false;
    }

    protected boolean hasGlobalExceptionHandlers() {
        return false;
    }

    @Override // org.springframework.web.servlet.handler.AbstractHandlerExceptionResolver
    @Nullable
    protected final ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        HandlerMethod handlerMethod = handler instanceof HandlerMethod ? (HandlerMethod) handler : null;
        return doResolveHandlerMethodException(request, response, handlerMethod, ex);
    }
}
