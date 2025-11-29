package freemarker.template.utility;

import freemarker.template.EmptyMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/Collections12.class */
public class Collections12 {
    public static final Map EMPTY_MAP = new EmptyMap();

    private Collections12() {
    }

    public static Map singletonMap(Object key, Object value) {
        return Collections.singletonMap(key, value);
    }

    public static List singletonList(Object o) {
        return Collections.singletonList(o);
    }
}
