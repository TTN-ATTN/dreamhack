package org.springframework.web.servlet.function;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Principal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.RequestPath;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.servlet.function.ChangePathPatternParserVisitor;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates.class */
public abstract class RequestPredicates {
    private static final Log logger = LogFactory.getLog((Class<?>) RequestPredicates.class);

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$Visitor.class */
    public interface Visitor {
        void method(Set<HttpMethod> methods);

        void path(String pattern);

        void pathExtension(String extension);

        void header(String name, String value);

        void param(String name, String value);

        void startAnd();

        void and();

        void endAnd();

        void startOr();

        void or();

        void endOr();

        void startNegate();

        void endNegate();

        void unknown(RequestPredicate predicate);
    }

    public static RequestPredicate all() {
        return request -> {
            return true;
        };
    }

    public static RequestPredicate method(HttpMethod httpMethod) {
        return new HttpMethodPredicate(httpMethod);
    }

    public static RequestPredicate methods(HttpMethod... httpMethods) {
        return new HttpMethodPredicate(httpMethods);
    }

    public static RequestPredicate path(String pattern) {
        Assert.notNull(pattern, "'pattern' must not be null");
        if (!pattern.isEmpty() && !pattern.startsWith("/")) {
            pattern = "/" + pattern;
        }
        return pathPredicates(PathPatternParser.defaultInstance).apply(pattern);
    }

    public static Function<String, RequestPredicate> pathPredicates(PathPatternParser patternParser) {
        Assert.notNull(patternParser, "PathPatternParser must not be null");
        return pattern -> {
            return new PathPatternPredicate(patternParser.parse(pattern));
        };
    }

    public static RequestPredicate headers(Predicate<ServerRequest.Headers> headersPredicate) {
        return new HeadersPredicate(headersPredicate);
    }

    public static RequestPredicate contentType(MediaType... mediaTypes) {
        Assert.notEmpty(mediaTypes, "'mediaTypes' must not be empty");
        return new ContentTypePredicate(mediaTypes);
    }

    public static RequestPredicate accept(MediaType... mediaTypes) {
        Assert.notEmpty(mediaTypes, "'mediaTypes' must not be empty");
        return new AcceptPredicate(mediaTypes);
    }

    public static RequestPredicate GET(String pattern) {
        return method(HttpMethod.GET).and(path(pattern));
    }

    public static RequestPredicate HEAD(String pattern) {
        return method(HttpMethod.HEAD).and(path(pattern));
    }

    public static RequestPredicate POST(String pattern) {
        return method(HttpMethod.POST).and(path(pattern));
    }

    public static RequestPredicate PUT(String pattern) {
        return method(HttpMethod.PUT).and(path(pattern));
    }

    public static RequestPredicate PATCH(String pattern) {
        return method(HttpMethod.PATCH).and(path(pattern));
    }

    public static RequestPredicate DELETE(String pattern) {
        return method(HttpMethod.DELETE).and(path(pattern));
    }

    public static RequestPredicate OPTIONS(String pattern) {
        return method(HttpMethod.OPTIONS).and(path(pattern));
    }

    public static RequestPredicate pathExtension(String extension) {
        Assert.notNull(extension, "'extension' must not be null");
        return new PathExtensionPredicate(extension);
    }

    public static RequestPredicate pathExtension(Predicate<String> extensionPredicate) {
        return new PathExtensionPredicate(extensionPredicate);
    }

    public static RequestPredicate param(String name, String value) {
        return new ParamPredicate(name, value);
    }

    public static RequestPredicate param(String name, Predicate<String> predicate) {
        return new ParamPredicate(name, predicate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void traceMatch(String prefix, Object desired, @Nullable Object actual, boolean match) {
        if (logger.isTraceEnabled()) {
            Log log = logger;
            Object[] objArr = new Object[4];
            objArr[0] = prefix;
            objArr[1] = desired;
            objArr[2] = match ? "matches" : "does not match";
            objArr[3] = actual;
            log.trace(String.format("%s \"%s\" %s against value \"%s\"", objArr));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void restoreAttributes(ServerRequest request, Map<String, Object> attributes) {
        request.attributes().clear();
        request.attributes().putAll(attributes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Map<String, String> mergePathVariables(Map<String, String> oldVariables, Map<String, String> newVariables) {
        if (!newVariables.isEmpty()) {
            Map<String, String> mergedVariables = new LinkedHashMap<>(oldVariables);
            mergedVariables.putAll(newVariables);
            return mergedVariables;
        }
        return oldVariables;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PathPattern mergePatterns(@Nullable PathPattern oldPattern, PathPattern newPattern) {
        if (oldPattern != null) {
            return oldPattern.combine(newPattern);
        }
        return newPattern;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$HttpMethodPredicate.class */
    private static class HttpMethodPredicate implements RequestPredicate {
        private final Set<HttpMethod> httpMethods;

        public HttpMethodPredicate(HttpMethod httpMethod) {
            Assert.notNull(httpMethod, "HttpMethod must not be null");
            this.httpMethods = EnumSet.of(httpMethod);
        }

        public HttpMethodPredicate(HttpMethod... httpMethods) {
            Assert.notEmpty(httpMethods, "HttpMethods must not be empty");
            this.httpMethods = EnumSet.copyOf((Collection) Arrays.asList(httpMethods));
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            HttpMethod method = method(request);
            boolean match = this.httpMethods.contains(method);
            RequestPredicates.traceMatch("Method", this.httpMethods, method, match);
            return match;
        }

        @Nullable
        private static HttpMethod method(ServerRequest request) {
            if (CorsUtils.isPreFlightRequest(request.servletRequest())) {
                String accessControlRequestMethod = request.headers().firstHeader("Access-Control-Request-Method");
                return HttpMethod.resolve(accessControlRequestMethod);
            }
            return request.method();
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.method(Collections.unmodifiableSet(this.httpMethods));
        }

        public String toString() {
            if (this.httpMethods.size() == 1) {
                return this.httpMethods.iterator().next().toString();
            }
            return this.httpMethods.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$PathPatternPredicate.class */
    private static class PathPatternPredicate implements RequestPredicate, ChangePathPatternParserVisitor.Target {
        private PathPattern pattern;

        public PathPatternPredicate(PathPattern pattern) {
            Assert.notNull(pattern, "'pattern' must not be null");
            this.pattern = pattern;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            PathContainer pathContainer = request.requestPath().pathWithinApplication();
            PathPattern.PathMatchInfo info = this.pattern.matchAndExtract(pathContainer);
            RequestPredicates.traceMatch("Pattern", this.pattern.getPatternString(), request.path(), info != null);
            if (info != null) {
                mergeAttributes(request, info.getUriVariables(), this.pattern);
                return true;
            }
            return false;
        }

        private static void mergeAttributes(ServerRequest request, Map<String, String> variables, PathPattern pattern) {
            Map<String, String> pathVariables = RequestPredicates.mergePathVariables(request.pathVariables(), variables);
            request.attributes().put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.unmodifiableMap(pathVariables));
            request.attributes().put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, RequestPredicates.mergePatterns((PathPattern) request.attributes().get(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE), pattern));
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            return Optional.ofNullable(this.pattern.matchStartOfPath(request.requestPath().pathWithinApplication())).map(info -> {
                return new SubPathServerRequestWrapper(request, info, this.pattern);
            });
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.path(this.pattern.getPatternString());
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            String patternString = this.pattern.getPatternString();
            this.pattern = parser.parse(patternString);
        }

        public String toString() {
            return this.pattern.getPatternString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$HeadersPredicate.class */
    private static class HeadersPredicate implements RequestPredicate {
        private final Predicate<ServerRequest.Headers> headersPredicate;

        public HeadersPredicate(Predicate<ServerRequest.Headers> headersPredicate) {
            Assert.notNull(headersPredicate, "Predicate must not be null");
            this.headersPredicate = headersPredicate;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            if (CorsUtils.isPreFlightRequest(request.servletRequest())) {
                return true;
            }
            return this.headersPredicate.test(request.headers());
        }

        public String toString() {
            return this.headersPredicate.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$ContentTypePredicate.class */
    private static class ContentTypePredicate extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public ContentTypePredicate(MediaType... mediaTypes) {
            this(new HashSet(Arrays.asList(mediaTypes)));
        }

        private ContentTypePredicate(Set<MediaType> mediaTypes) {
            super(headers -> {
                MediaType contentType = headers.contentType().orElse(MediaType.APPLICATION_OCTET_STREAM);
                boolean match = mediaTypes.stream().anyMatch(mediaType -> {
                    return mediaType.includes(contentType);
                });
                RequestPredicates.traceMatch(HttpHeaders.CONTENT_TYPE, mediaTypes, contentType, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String string;
            if (this.mediaTypes.size() == 1) {
                string = this.mediaTypes.iterator().next().toString();
            } else {
                string = this.mediaTypes.toString();
            }
            visitor.header(HttpHeaders.CONTENT_TYPE, string);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.HeadersPredicate
        public String toString() {
            String string;
            Object[] objArr = new Object[1];
            if (this.mediaTypes.size() == 1) {
                string = this.mediaTypes.iterator().next().toString();
            } else {
                string = this.mediaTypes.toString();
            }
            objArr[0] = string;
            return String.format("Content-Type: %s", objArr);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$AcceptPredicate.class */
    private static class AcceptPredicate extends HeadersPredicate {
        private final Set<MediaType> mediaTypes;

        public AcceptPredicate(MediaType... mediaTypes) {
            this(new HashSet(Arrays.asList(mediaTypes)));
        }

        private AcceptPredicate(Set<MediaType> mediaTypes) {
            super(headers -> {
                List<MediaType> acceptedMediaTypes = acceptedMediaTypes(headers);
                boolean match = acceptedMediaTypes.stream().anyMatch(acceptedMediaType -> {
                    Stream stream = mediaTypes.stream();
                    acceptedMediaType.getClass();
                    return stream.anyMatch(acceptedMediaType::isCompatibleWith);
                });
                RequestPredicates.traceMatch(HttpHeaders.ACCEPT, mediaTypes, acceptedMediaTypes, match);
                return match;
            });
            this.mediaTypes = mediaTypes;
        }

        @NonNull
        private static List<MediaType> acceptedMediaTypes(ServerRequest.Headers headers) {
            List<MediaType> acceptedMediaTypes = headers.accept();
            if (acceptedMediaTypes.isEmpty()) {
                acceptedMediaTypes = Collections.singletonList(MediaType.ALL);
            } else {
                MediaType.sortBySpecificityAndQuality(acceptedMediaTypes);
            }
            return acceptedMediaTypes;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            String string;
            if (this.mediaTypes.size() == 1) {
                string = this.mediaTypes.iterator().next().toString();
            } else {
                string = this.mediaTypes.toString();
            }
            visitor.header(HttpHeaders.ACCEPT, string);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicates.HeadersPredicate
        public String toString() {
            String string;
            Object[] objArr = new Object[1];
            if (this.mediaTypes.size() == 1) {
                string = this.mediaTypes.iterator().next().toString();
            } else {
                string = this.mediaTypes.toString();
            }
            objArr[0] = string;
            return String.format("Accept: %s", objArr);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$PathExtensionPredicate.class */
    private static class PathExtensionPredicate implements RequestPredicate {
        private final Predicate<String> extensionPredicate;

        @Nullable
        private final String extension;

        public PathExtensionPredicate(Predicate<String> extensionPredicate) {
            Assert.notNull(extensionPredicate, "Predicate must not be null");
            this.extensionPredicate = extensionPredicate;
            this.extension = null;
        }

        public PathExtensionPredicate(String extension) {
            Assert.notNull(extension, "Extension must not be null");
            this.extensionPredicate = s -> {
                boolean match = extension.equalsIgnoreCase(s);
                RequestPredicates.traceMatch("Extension", extension, s, match);
                return match;
            };
            this.extension = extension;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            String pathExtension = UriUtils.extractFileExtension(request.path());
            return this.extensionPredicate.test(pathExtension);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.pathExtension(this.extension != null ? this.extension : this.extensionPredicate.toString());
        }

        public String toString() {
            Object[] objArr = new Object[1];
            objArr[0] = this.extension != null ? this.extension : this.extensionPredicate;
            return String.format("*.%s", objArr);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$ParamPredicate.class */
    private static class ParamPredicate implements RequestPredicate {
        private final String name;
        private final Predicate<String> valuePredicate;

        @Nullable
        private final String value;

        public ParamPredicate(String name, Predicate<String> valuePredicate) {
            Assert.notNull(name, "Name must not be null");
            Assert.notNull(valuePredicate, "Predicate must not be null");
            this.name = name;
            this.valuePredicate = valuePredicate;
            this.value = null;
        }

        public ParamPredicate(String name, String value) {
            Assert.notNull(name, "Name must not be null");
            Assert.notNull(value, "Value must not be null");
            this.name = name;
            value.getClass();
            this.valuePredicate = (v1) -> {
                return r1.equals(v1);
            };
            this.value = value;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            Optional<String> s = request.param(this.name);
            return s.filter(this.valuePredicate).isPresent();
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.param(this.name, this.value != null ? this.value : this.valuePredicate.toString());
        }

        public String toString() {
            Object[] objArr = new Object[2];
            objArr[0] = this.name;
            objArr[1] = this.value != null ? this.value : this.valuePredicate;
            return String.format("?%s %s", objArr);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$AndRequestPredicate.class */
    static class AndRequestPredicate implements RequestPredicate, ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public AndRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull(left, "Left RequestPredicate must not be null");
            Assert.notNull(right, "Right RequestPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            Map<String, Object> oldAttributes = new HashMap<>(request.attributes());
            if (!this.left.test(request) || !this.right.test(request)) {
                RequestPredicates.restoreAttributes(request, oldAttributes);
                return false;
            }
            return true;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            Optional<ServerRequest> optionalNest = this.left.nest(request);
            RequestPredicate requestPredicate = this.right;
            requestPredicate.getClass();
            return optionalNest.flatMap(requestPredicate::nest);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startAnd();
            this.left.accept(visitor);
            visitor.and();
            this.right.accept(visitor);
            visitor.endAnd();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            if (this.left instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target) this.left).changeParser(parser);
            }
            if (this.right instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target) this.right).changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s && %s)", this.left, this.right);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$NegateRequestPredicate.class */
    static class NegateRequestPredicate implements RequestPredicate, ChangePathPatternParserVisitor.Target {
        private final RequestPredicate delegate;

        public NegateRequestPredicate(RequestPredicate delegate) {
            Assert.notNull(delegate, "Delegate must not be null");
            this.delegate = delegate;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            Map<String, Object> oldAttributes = new HashMap<>(request.attributes());
            boolean result = !this.delegate.test(request);
            if (!result) {
                RequestPredicates.restoreAttributes(request, oldAttributes);
            }
            return result;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startNegate();
            this.delegate.accept(visitor);
            visitor.endNegate();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            if (this.delegate instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target) this.delegate).changeParser(parser);
            }
        }

        public String toString() {
            return "!" + this.delegate.toString();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$OrRequestPredicate.class */
    static class OrRequestPredicate implements RequestPredicate, ChangePathPatternParserVisitor.Target {
        private final RequestPredicate left;
        private final RequestPredicate right;

        public OrRequestPredicate(RequestPredicate left, RequestPredicate right) {
            Assert.notNull(left, "Left RequestPredicate must not be null");
            Assert.notNull(right, "Right RequestPredicate must not be null");
            this.left = left;
            this.right = right;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public boolean test(ServerRequest request) {
            Map<String, Object> oldAttributes = new HashMap<>(request.attributes());
            if (!this.left.test(request)) {
                RequestPredicates.restoreAttributes(request, oldAttributes);
                if (!this.right.test(request)) {
                    RequestPredicates.restoreAttributes(request, oldAttributes);
                    return false;
                }
                return true;
            }
            return true;
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public Optional<ServerRequest> nest(ServerRequest request) {
            Optional<ServerRequest> leftResult = this.left.nest(request);
            if (leftResult.isPresent()) {
                return leftResult;
            }
            return this.right.nest(request);
        }

        @Override // org.springframework.web.servlet.function.RequestPredicate
        public void accept(Visitor visitor) {
            visitor.startOr();
            this.left.accept(visitor);
            visitor.or();
            this.right.accept(visitor);
            visitor.endOr();
        }

        @Override // org.springframework.web.servlet.function.ChangePathPatternParserVisitor.Target
        public void changeParser(PathPatternParser parser) {
            if (this.left instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target) this.left).changeParser(parser);
            }
            if (this.right instanceof ChangePathPatternParserVisitor.Target) {
                ((ChangePathPatternParserVisitor.Target) this.right).changeParser(parser);
            }
        }

        public String toString() {
            return String.format("(%s || %s)", this.left, this.right);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RequestPredicates$SubPathServerRequestWrapper.class */
    private static class SubPathServerRequestWrapper implements ServerRequest {
        private final ServerRequest request;
        private RequestPath requestPath;
        private final Map<String, Object> attributes;

        public SubPathServerRequestWrapper(ServerRequest request, PathPattern.PathRemainingMatchInfo info, PathPattern pattern) {
            this.request = request;
            this.requestPath = requestPath(request.requestPath(), info);
            this.attributes = mergeAttributes(request, info.getUriVariables(), pattern);
        }

        private static RequestPath requestPath(RequestPath original, PathPattern.PathRemainingMatchInfo info) {
            StringBuilder contextPath = new StringBuilder(original.contextPath().value());
            contextPath.append(info.getPathMatched().value());
            int length = contextPath.length();
            if (length > 0 && contextPath.charAt(length - 1) == '/') {
                contextPath.setLength(length - 1);
            }
            return original.modifyContextPath(contextPath.toString());
        }

        private static Map<String, Object> mergeAttributes(ServerRequest request, Map<String, String> pathVariables, PathPattern pattern) {
            Map<String, Object> result = new ConcurrentHashMap<>(request.attributes());
            result.put(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestPredicates.mergePathVariables(request.pathVariables(), pathVariables));
            result.put(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE, RequestPredicates.mergePatterns((PathPattern) request.attributes().get(RouterFunctions.MATCHING_PATTERN_ATTRIBUTE), pattern));
            return result;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpMethod method() {
            return this.request.method();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public String methodName() {
            return this.request.methodName();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public URI uri() {
            return this.request.uri();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public UriBuilder uriBuilder() {
            return this.request.uriBuilder();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public RequestPath requestPath() {
            return this.requestPath;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public ServerRequest.Headers headers() {
            return this.request.headers();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Cookie> cookies() {
            return this.request.cookies();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<InetSocketAddress> remoteAddress() {
            return this.request.remoteAddress();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public List<HttpMessageConverter<?>> messageConverters() {
            return this.request.messageConverters();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(Class<T> cls) throws ServletException, IOException {
            return (T) this.request.body(cls);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public <T> T body(ParameterizedTypeReference<T> parameterizedTypeReference) throws ServletException, IOException {
            return (T) this.request.body(parameterizedTypeReference);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<Object> attribute(String name) {
            return this.request.attribute(name);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, Object> attributes() {
            return this.attributes;
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<String> param(String name) {
            return this.request.param(name);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, String> params() {
            return this.request.params();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public MultiValueMap<String, Part> multipartData() throws ServletException, IOException {
            return this.request.multipartData();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Map<String, String> pathVariables() {
            return (Map) this.attributes.getOrDefault(RouterFunctions.URI_TEMPLATE_VARIABLES_ATTRIBUTE, Collections.emptyMap());
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpSession session() {
            return this.request.session();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<Principal> principal() {
            return this.request.principal();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public HttpServletRequest servletRequest() {
            return this.request.servletRequest();
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(Instant lastModified) {
            return this.request.checkNotModified(lastModified);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(String etag) {
            return this.request.checkNotModified(etag);
        }

        @Override // org.springframework.web.servlet.function.ServerRequest
        public Optional<ServerResponse> checkNotModified(Instant lastModified, String etag) {
            return this.request.checkNotModified(lastModified, etag);
        }

        public String toString() {
            return method() + " " + path();
        }
    }
}
