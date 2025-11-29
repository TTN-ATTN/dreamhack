package org.springframework.boot.origin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/origin/Origin.class */
public interface Origin {
    default Origin getParent() {
        return null;
    }

    static Origin from(Object source) {
        if (source instanceof Origin) {
            return (Origin) source;
        }
        Origin origin = null;
        if (source instanceof OriginProvider) {
            origin = ((OriginProvider) source).getOrigin();
        }
        if (origin == null && (source instanceof Throwable)) {
            return from(((Throwable) source).getCause());
        }
        return origin;
    }

    static List<Origin> parentsFrom(Object source) {
        Origin origin = from(source);
        if (origin == null) {
            return Collections.emptyList();
        }
        Set<Origin> parents = new LinkedHashSet<>();
        Origin parent = origin.getParent();
        while (true) {
            Origin origin2 = parent;
            if (origin2 == null || parents.contains(origin2)) {
                break;
            }
            parents.add(origin2);
            parent = origin2.getParent();
        }
        return Collections.unmodifiableList(new ArrayList(parents));
    }
}
