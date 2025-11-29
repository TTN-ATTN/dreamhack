package org.springframework.cache.interceptor;

import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheableOperation.class */
public class CacheableOperation extends CacheOperation {

    @Nullable
    private final String unless;
    private final boolean sync;

    public CacheableOperation(Builder b) {
        super(b);
        this.unless = b.unless;
        this.sync = b.sync;
    }

    @Nullable
    public String getUnless() {
        return this.unless;
    }

    public boolean isSync() {
        return this.sync;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/cache/interceptor/CacheableOperation$Builder.class */
    public static class Builder extends CacheOperation.Builder {

        @Nullable
        private String unless;
        private boolean sync;

        public void setUnless(String unless) {
            this.unless = unless;
        }

        public void setSync(boolean sync) {
            this.sync = sync;
        }

        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        protected StringBuilder getOperationDescription() {
            StringBuilder sb = super.getOperationDescription();
            sb.append(" | unless='");
            sb.append(this.unless);
            sb.append('\'');
            sb.append(" | sync='");
            sb.append(this.sync);
            sb.append('\'');
            return sb;
        }

        @Override // org.springframework.cache.interceptor.CacheOperation.Builder
        public CacheableOperation build() {
            return new CacheableOperation(this);
        }
    }
}
