package freemarker.cache;

import freemarker.template.utility.StringUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/ByteArrayTemplateLoader.class */
public class ByteArrayTemplateLoader implements TemplateLoader {
    private final Map<String, ByteArrayTemplateSource> templates = new HashMap();

    public void putTemplate(String name, byte[] templateContent) {
        putTemplate(name, templateContent, System.currentTimeMillis());
    }

    public void putTemplate(String name, byte[] templateContent, long lastModified) {
        this.templates.put(name, new ByteArrayTemplateSource(name, templateContent, lastModified));
    }

    public boolean removeTemplate(String name) {
        return this.templates.remove(name) != null;
    }

    @Override // freemarker.cache.TemplateLoader
    public void closeTemplateSource(Object templateSource) {
    }

    @Override // freemarker.cache.TemplateLoader
    public Object findTemplateSource(String name) {
        return this.templates.get(name);
    }

    @Override // freemarker.cache.TemplateLoader
    public long getLastModified(Object templateSource) {
        return ((ByteArrayTemplateSource) templateSource).lastModified;
    }

    @Override // freemarker.cache.TemplateLoader
    public Reader getReader(Object templateSource, String encoding) throws UnsupportedEncodingException {
        return new InputStreamReader(new ByteArrayInputStream(((ByteArrayTemplateSource) templateSource).templateContent), encoding);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/ByteArrayTemplateLoader$ByteArrayTemplateSource.class */
    private static class ByteArrayTemplateSource {
        private final String name;
        private final byte[] templateContent;
        private final long lastModified;

        ByteArrayTemplateSource(String name, byte[] templateContent, long lastModified) {
            if (name == null) {
                throw new IllegalArgumentException("name == null");
            }
            if (templateContent == null) {
                throw new IllegalArgumentException("templateContent == null");
            }
            if (lastModified < -1) {
                throw new IllegalArgumentException("lastModified < -1L");
            }
            this.name = name;
            this.templateContent = templateContent;
            this.lastModified = lastModified;
        }

        public int hashCode() {
            int result = (31 * 1) + (this.name == null ? 0 : this.name.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ByteArrayTemplateSource other = (ByteArrayTemplateSource) obj;
            if (this.name == null) {
                if (other.name != null) {
                    return false;
                }
                return true;
            }
            if (!this.name.equals(other.name)) {
                return false;
            }
            return true;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(TemplateLoaderUtils.getClassNameForToString(this));
        sb.append("(Map { ");
        int cnt = 0;
        Iterator<String> it = this.templates.keySet().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            String name = it.next();
            cnt++;
            if (cnt != 1) {
                sb.append(", ");
            }
            if (cnt > 10) {
                sb.append("...");
                break;
            }
            sb.append(StringUtil.jQuote(name));
            sb.append("=...");
        }
        if (cnt != 0) {
            sb.append(' ');
        }
        sb.append("})");
        return sb.toString();
    }
}
