package org.springframework.web.servlet.mvc.condition;

import javax.servlet.http.HttpServletRequest;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/condition/RequestCondition.class */
public interface RequestCondition<T> {
    T combine(T other);

    @Nullable
    T getMatchingCondition(HttpServletRequest request);

    int compareTo(T other, HttpServletRequest request);
}
