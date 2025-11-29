package org.springframework.boot.env;

import java.util.Map;
import org.springframework.boot.origin.Origin;
import org.springframework.boot.origin.OriginLookup;
import org.springframework.boot.origin.OriginTrackedValue;
import org.springframework.core.env.MapPropertySource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/env/OriginTrackedMapPropertySource.class */
public final class OriginTrackedMapPropertySource extends MapPropertySource implements OriginLookup<String> {
    private final boolean immutable;

    public OriginTrackedMapPropertySource(String name, Map source) {
        this(name, source, false);
    }

    public OriginTrackedMapPropertySource(String name, Map source, boolean immutable) {
        super(name, source);
        this.immutable = immutable;
    }

    @Override // org.springframework.core.env.MapPropertySource, org.springframework.core.env.PropertySource
    public Object getProperty(String name) {
        Object value = super.getProperty(name);
        if (value instanceof OriginTrackedValue) {
            return ((OriginTrackedValue) value).getValue();
        }
        return value;
    }

    @Override // org.springframework.boot.origin.OriginLookup
    public Origin getOrigin(String name) {
        Object value = super.getProperty(name);
        if (value instanceof OriginTrackedValue) {
            return ((OriginTrackedValue) value).getOrigin();
        }
        return null;
    }

    @Override // org.springframework.boot.origin.OriginLookup
    public boolean isImmutable() {
        return this.immutable;
    }
}
