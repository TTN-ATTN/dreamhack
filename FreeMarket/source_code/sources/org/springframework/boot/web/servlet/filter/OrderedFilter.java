package org.springframework.boot.web.servlet.filter;

import javax.servlet.Filter;
import org.springframework.core.Ordered;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/servlet/filter/OrderedFilter.class */
public interface OrderedFilter extends Filter, Ordered {
    public static final int REQUEST_WRAPPER_FILTER_MAX_ORDER = 0;
}
