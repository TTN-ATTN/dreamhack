package org.springframework.boot.web.reactive.filter;

import org.springframework.web.filter.reactive.HiddenHttpMethodFilter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/reactive/filter/OrderedHiddenHttpMethodFilter.class */
public class OrderedHiddenHttpMethodFilter extends HiddenHttpMethodFilter implements OrderedWebFilter {
    public static final int DEFAULT_ORDER = -10000;
    private int order = -10000;

    @Override // org.springframework.core.Ordered
    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
