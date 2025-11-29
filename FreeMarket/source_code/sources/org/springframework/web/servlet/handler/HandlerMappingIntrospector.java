package org.springframework.web.servlet.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.ServletRequestPathUtils;
import org.springframework.web.util.UrlPathHelper;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector.class */
public class HandlerMappingIntrospector implements CorsConfigurationSource, ApplicationContextAware, InitializingBean {

    @Nullable
    private ApplicationContext applicationContext;

    @Nullable
    private List<HandlerMapping> handlerMappings;
    private Map<HandlerMapping, PathPatternMatchableHandlerMapping> pathPatternMappings = Collections.emptyMap();

    public HandlerMappingIntrospector() {
    }

    @Deprecated
    public HandlerMappingIntrospector(ApplicationContext context) {
        this.handlerMappings = initHandlerMappings(context);
    }

    @Override // org.springframework.context.ApplicationContextAware
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        if (this.handlerMappings == null) {
            Assert.notNull(this.applicationContext, "No ApplicationContext");
            this.handlerMappings = initHandlerMappings(this.applicationContext);
            this.pathPatternMappings = (Map) this.handlerMappings.stream().filter(m -> {
                return (m instanceof MatchableHandlerMapping) && ((MatchableHandlerMapping) m).getPatternParser() != null;
            }).map(mapping -> {
                return (MatchableHandlerMapping) mapping;
            }).collect(Collectors.toMap(mapping2 -> {
                return mapping2;
            }, PathPatternMatchableHandlerMapping::new));
        }
    }

    private static List<HandlerMapping> initHandlerMappings(ApplicationContext context) throws BeansException {
        Map<String, HandlerMapping> beans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
        if (!beans.isEmpty()) {
            List<HandlerMapping> mappings = new ArrayList<>(beans.values());
            AnnotationAwareOrderComparator.sort(mappings);
            return Collections.unmodifiableList(mappings);
        }
        return Collections.unmodifiableList(initFallback(context));
    }

    private static List<HandlerMapping> initFallback(ApplicationContext applicationContext) throws LinkageError, BeansException {
        try {
            Resource resource = new ClassPathResource("DispatcherServlet.properties", (Class<?>) DispatcherServlet.class);
            Properties properties = PropertiesLoaderUtils.loadProperties(resource);
            String value = properties.getProperty(HandlerMapping.class.getName());
            String[] names = StringUtils.commaDelimitedListToStringArray(value);
            List<HandlerMapping> result = new ArrayList<>(names.length);
            for (String name : names) {
                try {
                    Class<?> clazz = ClassUtils.forName(name, DispatcherServlet.class.getClassLoader());
                    Object mapping = applicationContext.getAutowireCapableBeanFactory().createBean(clazz);
                    result.add((HandlerMapping) mapping);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Could not find default HandlerMapping [" + name + "]");
                }
            }
            return result;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load DispatcherServlet.properties: " + ex.getMessage());
        }
    }

    public List<HandlerMapping> getHandlerMappings() {
        return this.handlerMappings != null ? this.handlerMappings : Collections.emptyList();
    }

    @Nullable
    public MatchableHandlerMapping getMatchableHandlerMapping(HttpServletRequest request) throws Exception {
        HttpServletRequest wrappedRequest = new AttributesPreservingRequest(request);
        return (MatchableHandlerMapping) doWithHandlerMapping(wrappedRequest, false, (mapping, executionChain) -> {
            if (mapping instanceof MatchableHandlerMapping) {
                PathPatternMatchableHandlerMapping pathPatternMapping = this.pathPatternMappings.get(mapping);
                if (pathPatternMapping != null) {
                    RequestPath requestPath = ServletRequestPathUtils.getParsedRequestPath(wrappedRequest);
                    return new LookupPathMatchableHandlerMapping(pathPatternMapping, requestPath);
                }
                String lookupPath = (String) wrappedRequest.getAttribute(UrlPathHelper.PATH_ATTRIBUTE);
                return new LookupPathMatchableHandlerMapping((MatchableHandlerMapping) mapping, lookupPath);
            }
            throw new IllegalStateException("HandlerMapping is not a MatchableHandlerMapping");
        });
    }

    @Override // org.springframework.web.cors.CorsConfigurationSource
    @Nullable
    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
        AttributesPreservingRequest wrappedRequest = new AttributesPreservingRequest(request);
        return (CorsConfiguration) doWithHandlerMappingIgnoringException(wrappedRequest, (handlerMapping, executionChain) -> {
            for (HandlerInterceptor interceptor : executionChain.getInterceptorList()) {
                if (interceptor instanceof CorsConfigurationSource) {
                    return ((CorsConfigurationSource) interceptor).getCorsConfiguration(wrappedRequest);
                }
            }
            if (executionChain.getHandler() instanceof CorsConfigurationSource) {
                return ((CorsConfigurationSource) executionChain.getHandler()).getCorsConfiguration(wrappedRequest);
            }
            return null;
        });
    }

    @Nullable
    private <T> T doWithHandlerMapping(HttpServletRequest request, boolean ignoreException, BiFunction<HandlerMapping, HandlerExecutionChain, T> extractor) throws Exception {
        Assert.state(this.handlerMappings != null, "HandlerMapping's not initialized");
        boolean parsePath = !this.pathPatternMappings.isEmpty();
        RequestPath previousPath = null;
        if (parsePath) {
            previousPath = (RequestPath) request.getAttribute(ServletRequestPathUtils.PATH_ATTRIBUTE);
            ServletRequestPathUtils.parseAndCache(request);
        }
        try {
            for (HandlerMapping handlerMapping : this.handlerMappings) {
                HandlerExecutionChain chain = null;
                try {
                    chain = handlerMapping.getHandler(request);
                } catch (Exception ex) {
                    if (!ignoreException) {
                        throw ex;
                    }
                }
                if (chain != null) {
                    T tApply = extractor.apply(handlerMapping, chain);
                    if (parsePath) {
                        ServletRequestPathUtils.setParsedRequestPath(previousPath, request);
                    }
                    return tApply;
                }
            }
        } finally {
            if (parsePath) {
                ServletRequestPathUtils.setParsedRequestPath(previousPath, request);
            }
        }
    }

    @Nullable
    private <T> T doWithHandlerMappingIgnoringException(HttpServletRequest httpServletRequest, BiFunction<HandlerMapping, HandlerExecutionChain, T> biFunction) {
        try {
            return (T) doWithHandlerMapping(httpServletRequest, true, biFunction);
        } catch (Exception e) {
            throw new IllegalStateException("HandlerMapping exception not suppressed", e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$AttributesPreservingRequest.class */
    private static class AttributesPreservingRequest extends HttpServletRequestWrapper {
        private final Map<String, Object> attributes;

        AttributesPreservingRequest(HttpServletRequest request) {
            super(request);
            this.attributes = initAttributes(request);
        }

        private Map<String, Object> initAttributes(HttpServletRequest request) {
            Map<String, Object> map = new HashMap<>();
            Enumeration<String> names = request.getAttributeNames();
            while (names.hasMoreElements()) {
                String name = names.nextElement();
                map.put(name, request.getAttribute(name));
            }
            return map;
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public void setAttribute(String name, Object value) {
            this.attributes.put(name, value);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Object getAttribute(String name) {
            return this.attributes.get(name);
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public Enumeration<String> getAttributeNames() {
            return Collections.enumeration(this.attributes.keySet());
        }

        @Override // javax.servlet.ServletRequestWrapper, javax.servlet.ServletRequest
        public void removeAttribute(String name) {
            this.attributes.remove(name);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/handler/HandlerMappingIntrospector$LookupPathMatchableHandlerMapping.class */
    private static class LookupPathMatchableHandlerMapping implements MatchableHandlerMapping {
        private final MatchableHandlerMapping delegate;
        private final Object lookupPath;
        private final String pathAttributeName;

        LookupPathMatchableHandlerMapping(MatchableHandlerMapping delegate, Object lookupPath) {
            this.delegate = delegate;
            this.lookupPath = lookupPath;
            this.pathAttributeName = lookupPath instanceof RequestPath ? ServletRequestPathUtils.PATH_ATTRIBUTE : UrlPathHelper.PATH_ATTRIBUTE;
        }

        @Override // org.springframework.web.servlet.handler.MatchableHandlerMapping
        @Nullable
        public RequestMatchResult match(HttpServletRequest request, String pattern) {
            String pattern2 = (!StringUtils.hasLength(pattern) || pattern.startsWith("/")) ? pattern : "/" + pattern;
            Object previousPath = request.getAttribute(this.pathAttributeName);
            request.setAttribute(this.pathAttributeName, this.lookupPath);
            try {
                RequestMatchResult requestMatchResultMatch = this.delegate.match(request, pattern2);
                request.setAttribute(this.pathAttributeName, previousPath);
                return requestMatchResultMatch;
            } catch (Throwable th) {
                request.setAttribute(this.pathAttributeName, previousPath);
                throw th;
            }
        }

        @Override // org.springframework.web.servlet.HandlerMapping
        @Nullable
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            return this.delegate.getHandler(request);
        }
    }
}
