package org.springframework.web.method.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.context.MessageSource;
import org.springframework.core.CoroutinesUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.KotlinDetector;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/method/support/InvocableHandlerMethod.class */
public class InvocableHandlerMethod extends HandlerMethod {
    private static final Object[] EMPTY_ARGS = new Object[0];
    private HandlerMethodArgumentResolverComposite resolvers;
    private ParameterNameDiscoverer parameterNameDiscoverer;

    @Nullable
    private WebDataBinderFactory dataBinderFactory;

    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    protected InvocableHandlerMethod(Object bean, Method method, @Nullable MessageSource messageSource) {
        super(bean, method, messageSource);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        super(bean, methodName, parameterTypes);
        this.resolvers = new HandlerMethodArgumentResolverComposite();
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    public void setHandlerMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.resolvers = argumentResolvers;
    }

    public void setParameterNameDiscoverer(ParameterNameDiscoverer parameterNameDiscoverer) {
        this.parameterNameDiscoverer = parameterNameDiscoverer;
    }

    public void setDataBinderFactory(WebDataBinderFactory dataBinderFactory) {
        this.dataBinderFactory = dataBinderFactory;
    }

    @Nullable
    public Object invokeForRequest(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        Object[] args = getMethodArgumentValues(request, mavContainer, providedArgs);
        if (logger.isTraceEnabled()) {
            logger.trace("Arguments: " + Arrays.toString(args));
        }
        return doInvoke(args);
    }

    protected Object[] getMethodArgumentValues(NativeWebRequest request, @Nullable ModelAndViewContainer mavContainer, Object... providedArgs) throws Exception {
        String exMsg;
        MethodParameter[] parameters = getMethodParameters();
        if (ObjectUtils.isEmpty((Object[]) parameters)) {
            return EMPTY_ARGS;
        }
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);
            args[i] = findProvidedArgument(parameter, providedArgs);
            if (args[i] == null) {
                if (!this.resolvers.supportsParameter(parameter)) {
                    throw new IllegalStateException(formatArgumentError(parameter, "No suitable resolver"));
                }
                try {
                    args[i] = this.resolvers.resolveArgument(parameter, mavContainer, request, this.dataBinderFactory);
                } catch (Exception ex) {
                    if (logger.isDebugEnabled() && (exMsg = ex.getMessage()) != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                        logger.debug(formatArgumentError(parameter, exMsg));
                    }
                    throw ex;
                }
            }
        }
        return args;
    }

    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        Method method = getBridgedMethod();
        try {
            if (KotlinDetector.isSuspendingFunction(method)) {
                return CoroutinesUtils.invokeSuspendingFunction(method, getBean(), args);
            }
            return method.invoke(getBean(), args);
        } catch (IllegalArgumentException ex) {
            assertTargetBean(method, getBean(), args);
            String text = ex.getMessage() != null ? ex.getMessage() : "Illegal argument";
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        } catch (InvocationTargetException ex2) {
            Throwable targetException = ex2.getTargetException();
            if (targetException instanceof RuntimeException) {
                throw ((RuntimeException) targetException);
            }
            if (targetException instanceof Error) {
                throw ((Error) targetException);
            }
            if (targetException instanceof Exception) {
                throw ((Exception) targetException);
            }
            throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
        }
    }
}
