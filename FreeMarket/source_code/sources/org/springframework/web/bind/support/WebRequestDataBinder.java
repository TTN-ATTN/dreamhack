package org.springframework.web.bind.support;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/support/WebRequestDataBinder.class */
public class WebRequestDataBinder extends WebDataBinder {
    public WebRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public WebRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(WebRequest request) throws MultipartException {
        HttpServletRequest servletRequest;
        MutablePropertyValues mpvs = new MutablePropertyValues(request.getParameterMap());
        if (request instanceof NativeWebRequest) {
            NativeWebRequest nativeRequest = (NativeWebRequest) request;
            MultipartRequest multipartRequest = (MultipartRequest) nativeRequest.getNativeRequest(MultipartRequest.class);
            if (multipartRequest != null) {
                bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
            } else if (StringUtils.startsWithIgnoreCase(request.getHeader(HttpHeaders.CONTENT_TYPE), "multipart/form-data") && (servletRequest = (HttpServletRequest) nativeRequest.getNativeRequest(HttpServletRequest.class)) != null && HttpMethod.POST.matches(servletRequest.getMethod())) {
                StandardServletPartUtils.bindParts(servletRequest, mpvs, isBindEmptyMultipartFiles());
            }
        }
        doBind(mpvs);
    }

    public void closeNoCatch() throws BindException {
        if (getBindingResult().hasErrors()) {
            throw new BindException(getBindingResult());
        }
    }
}
