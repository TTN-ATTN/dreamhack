package freemarker.cache;

import java.io.IOException;
import java.io.Reader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLoader.class */
public interface TemplateLoader {
    Object findTemplateSource(String str) throws IOException;

    long getLastModified(Object obj);

    Reader getReader(Object obj, String str) throws IOException;

    void closeTemplateSource(Object obj) throws IOException;
}
