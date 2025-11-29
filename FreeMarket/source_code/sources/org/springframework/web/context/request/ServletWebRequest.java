package org.springframework.web.context.request;

import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.WebContentGenerator;
import org.springframework.web.util.WebUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/context/request/ServletWebRequest.class */
public class ServletWebRequest extends ServletRequestAttributes implements NativeWebRequest {
    private static final List<String> SAFE_METHODS = Arrays.asList("GET", WebContentGenerator.METHOD_HEAD);
    private static final Pattern ETAG_HEADER_VALUE_PATTERN = Pattern.compile("\\*|\\s*((W\\/)?(\"[^\"]*\"))\\s*,?");
    private static final String[] DATE_FORMATS = {"EEE, dd MMM yyyy HH:mm:ss zzz", "EEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM dd HH:mm:ss yyyy"};
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private boolean notModified;

    public ServletWebRequest(HttpServletRequest request) {
        super(request);
        this.notModified = false;
    }

    public ServletWebRequest(HttpServletRequest request, @Nullable HttpServletResponse response) {
        super(request, response);
        this.notModified = false;
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeRequest() {
        return getRequest();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public Object getNativeResponse() {
        return getResponse();
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeRequest(@Nullable Class<T> cls) {
        return (T) WebUtils.getNativeRequest(getRequest(), cls);
    }

    @Override // org.springframework.web.context.request.NativeWebRequest
    public <T> T getNativeResponse(@Nullable Class<T> cls) {
        HttpServletResponse response = getResponse();
        if (response != null) {
            return (T) WebUtils.getNativeResponse(response, cls);
        }
        return null;
    }

    @Nullable
    public HttpMethod getHttpMethod() {
        return HttpMethod.resolve(getRequest().getMethod());
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getHeader(String headerName) {
        return getRequest().getHeader(headerName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getHeaderValues(String headerName) {
        String[] headerValues = StringUtils.toStringArray(getRequest().getHeaders(headerName));
        if (ObjectUtils.isEmpty((Object[]) headerValues)) {
            return null;
        }
        return headerValues;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getHeaderNames() {
        return CollectionUtils.toIterator(getRequest().getHeaderNames());
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getParameter(String paramName) {
        return getRequest().getParameter(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String[] getParameterValues(String paramName) {
        return getRequest().getParameterValues(paramName);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Iterator<String> getParameterNames() {
        return CollectionUtils.toIterator(getRequest().getParameterNames());
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Map<String, String[]> getParameterMap() {
        return getRequest().getParameterMap();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public Locale getLocale() {
        return getRequest().getLocale();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getContextPath() {
        return getRequest().getContextPath();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public String getRemoteUser() {
        return getRequest().getRemoteUser();
    }

    @Override // org.springframework.web.context.request.WebRequest
    @Nullable
    public Principal getUserPrincipal() {
        return getRequest().getUserPrincipal();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isUserInRole(String role) {
        return getRequest().isUserInRole(role);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean isSecure() {
        return getRequest().isSecure();
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(long lastModifiedTimestamp) {
        return checkNotModified(null, lastModifiedTimestamp);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(String etag) {
        return checkNotModified(etag, -1L);
    }

    @Override // org.springframework.web.context.request.WebRequest
    public boolean checkNotModified(@Nullable String etag, long lastModifiedTimestamp) {
        HttpServletResponse response = getResponse();
        if (this.notModified || (response != null && HttpStatus.OK.value() != response.getStatus())) {
            return this.notModified;
        }
        if (validateIfUnmodifiedSince(lastModifiedTimestamp)) {
            if (this.notModified && response != null) {
                response.setStatus(HttpStatus.PRECONDITION_FAILED.value());
            }
            if (SAFE_METHODS.contains(getRequest().getMethod())) {
                if (StringUtils.hasLength(etag) && response.getHeader(HttpHeaders.ETAG) == null) {
                    response.setHeader(HttpHeaders.ETAG, padEtagIfNecessary(etag));
                }
                response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedTimestamp);
            }
            return this.notModified;
        }
        boolean validated = validateIfNoneMatch(etag);
        if (!validated) {
            validateIfModifiedSince(lastModifiedTimestamp);
        }
        if (response != null) {
            boolean isHttpGetOrHead = SAFE_METHODS.contains(getRequest().getMethod());
            if (this.notModified) {
                response.setStatus(isHttpGetOrHead ? HttpStatus.NOT_MODIFIED.value() : HttpStatus.PRECONDITION_FAILED.value());
            }
            if (isHttpGetOrHead) {
                if (lastModifiedTimestamp > 0 && parseDateValue(response.getHeader(HttpHeaders.LAST_MODIFIED)) == -1) {
                    response.setDateHeader(HttpHeaders.LAST_MODIFIED, lastModifiedTimestamp);
                }
                if (StringUtils.hasLength(etag) && response.getHeader(HttpHeaders.ETAG) == null) {
                    response.setHeader(HttpHeaders.ETAG, padEtagIfNecessary(etag));
                }
            }
        }
        return this.notModified;
    }

    private boolean validateIfUnmodifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return false;
        }
        long ifUnmodifiedSince = parseDateHeader(HttpHeaders.IF_UNMODIFIED_SINCE);
        if (ifUnmodifiedSince == -1) {
            return false;
        }
        this.notModified = ifUnmodifiedSince < (lastModifiedTimestamp / 1000) * 1000;
        return true;
    }

    private boolean validateIfNoneMatch(@Nullable String etag) {
        if (!StringUtils.hasLength(etag)) {
            return false;
        }
        try {
            Enumeration<String> ifNoneMatch = getRequest().getHeaders(HttpHeaders.IF_NONE_MATCH);
            if (!ifNoneMatch.hasMoreElements()) {
                return false;
            }
            String etag2 = padEtagIfNecessary(etag);
            if (etag2.startsWith("W/")) {
                etag2 = etag2.substring(2);
            }
            while (ifNoneMatch.hasMoreElements()) {
                String clientETags = ifNoneMatch.nextElement();
                Matcher etagMatcher = ETAG_HEADER_VALUE_PATTERN.matcher(clientETags);
                while (true) {
                    if (!etagMatcher.find()) {
                        break;
                    }
                    if (StringUtils.hasLength(etagMatcher.group()) && etag2.equals(etagMatcher.group(3))) {
                        this.notModified = true;
                        break;
                    }
                }
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String padEtagIfNecessary(String etag) {
        if (!StringUtils.hasLength(etag)) {
            return etag;
        }
        if ((etag.startsWith("\"") || etag.startsWith("W/\"")) && etag.endsWith("\"")) {
            return etag;
        }
        return "\"" + etag + "\"";
    }

    private boolean validateIfModifiedSince(long lastModifiedTimestamp) {
        if (lastModifiedTimestamp < 0) {
            return false;
        }
        long ifModifiedSince = parseDateHeader(HttpHeaders.IF_MODIFIED_SINCE);
        if (ifModifiedSince == -1) {
            return false;
        }
        this.notModified = ifModifiedSince >= (lastModifiedTimestamp / 1000) * 1000;
        return true;
    }

    public boolean isNotModified() {
        return this.notModified;
    }

    private long parseDateHeader(String headerName) {
        int separatorIndex;
        long dateValue = -1;
        try {
            dateValue = getRequest().getDateHeader(headerName);
        } catch (IllegalArgumentException e) {
            String headerValue = getHeader(headerName);
            if (headerValue != null && (separatorIndex = headerValue.indexOf(59)) != -1) {
                String datePart = headerValue.substring(0, separatorIndex);
                dateValue = parseDateValue(datePart);
            }
        }
        return dateValue;
    }

    private long parseDateValue(@Nullable String headerValue) {
        if (headerValue != null && headerValue.length() >= 3) {
            for (String dateFormat : DATE_FORMATS) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, Locale.US);
                simpleDateFormat.setTimeZone(GMT);
                try {
                    return simpleDateFormat.parse(headerValue).getTime();
                } catch (ParseException e) {
                }
            }
            return -1L;
        }
        return -1L;
    }

    @Override // org.springframework.web.context.request.WebRequest
    public String getDescription(boolean includeClientInfo) {
        HttpServletRequest request = getRequest();
        StringBuilder sb = new StringBuilder();
        sb.append("uri=").append(request.getRequestURI());
        if (includeClientInfo) {
            String client = request.getRemoteAddr();
            if (StringUtils.hasLength(client)) {
                sb.append(";client=").append(client);
            }
            HttpSession session = request.getSession(false);
            if (session != null) {
                sb.append(";session=").append(session.getId());
            }
            String user = request.getRemoteUser();
            if (StringUtils.hasLength(user)) {
                sb.append(";user=").append(user);
            }
        }
        return sb.toString();
    }

    @Override // org.springframework.web.context.request.ServletRequestAttributes
    public String toString() {
        return "ServletWebRequest: " + getDescription(true);
    }
}
