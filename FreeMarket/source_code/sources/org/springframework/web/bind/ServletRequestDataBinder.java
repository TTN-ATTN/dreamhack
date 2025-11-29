package org.springframework.web.bind;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.multipart.support.StandardServletPartUtils;
import org.springframework.web.util.WebUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/bind/ServletRequestDataBinder.class */
public class ServletRequestDataBinder extends WebDataBinder {
    public ServletRequestDataBinder(@Nullable Object target) {
        super(target);
    }

    public ServletRequestDataBinder(@Nullable Object target, String objectName) {
        super(target, objectName);
    }

    public void bind(ServletRequest request) throws MultipartException {
        HttpServletRequest httpServletRequest;
        MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
        MultipartRequest multipartRequest = (MultipartRequest) WebUtils.getNativeRequest(request, MultipartRequest.class);
        if (multipartRequest != null) {
            bindMultipart(multipartRequest.getMultiFileMap(), mpvs);
        } else if (StringUtils.startsWithIgnoreCase(request.getContentType(), "multipart/form-data") && (httpServletRequest = (HttpServletRequest) WebUtils.getNativeRequest(request, HttpServletRequest.class)) != null && HttpMethod.POST.matches(httpServletRequest.getMethod())) {
            StandardServletPartUtils.bindParts(httpServletRequest, mpvs, isBindEmptyMultipartFiles());
        }
        addBindValues(mpvs, request);
        doBind(mpvs);
    }

    protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
    }

    public void closeNoCatch() throws ServletRequestBindingException {
        if (getBindingResult().hasErrors()) {
            throw new ServletRequestBindingException("Errors binding onto object '" + getBindingResult().getObjectName() + "'", new BindException(getBindingResult()));
        }
    }
}
