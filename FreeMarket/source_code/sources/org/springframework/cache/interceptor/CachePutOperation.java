package org.springframework.cache.interceptor;

import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CachePutOperation.class */
public class CachePutOperation extends CacheOperation {

    @Nullable
    private final String unless;

    public CachePutOperation(Builder b) {
        super(b);
        this.unless = b.unless;
    }

    @Nullable
    public String getUnless() {
        return this.unless;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CachePutOperation$Builder.class */
    public static class Builder extends CacheOperation.Builder {

        @Nullable
        private String unless;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append('\'');
            return sb;
        }

        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        public CachePutOperation build() {
            return new CachePutOperation(this);
        }
    }
}
