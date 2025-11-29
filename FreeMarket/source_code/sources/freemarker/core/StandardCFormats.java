package freemarker.core;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/StandardCFormats.class */
final class StandardCFormats {
    static final Map<String, CFormat> STANDARD_C_FORMATS;

    private StandardCFormats() {
    }

    static {
        Map<String, CFormat> map = new LinkedHashMap<>();
        addStandardCFormat(map, JavaScriptOrJSONCFormat.INSTANCE);
        addStandardCFormat(map, JSONCFormat.INSTANCE);
        addStandardCFormat(map, JavaScriptCFormat.INSTANCE);
        addStandardCFormat(map, JavaCFormat.INSTANCE);
        addStandardCFormat(map, XSCFormat.INSTANCE);
        addStandardCFormat(map, LegacyCFormat.INSTANCE);
        STANDARD_C_FORMATS = Collections.unmodifiableMap(map);
    }

    private static void addStandardCFormat(Map<String, CFormat> map, CFormat cFormat) {
        map.put(cFormat.getName(), cFormat);
    }
}
