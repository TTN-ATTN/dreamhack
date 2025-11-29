package freemarker.cache;

import freemarker.template.utility.NullArgumentException;
import java.io.IOException;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MultiTemplateLoader.class */
public class MultiTemplateLoader implements StatefulTemplateLoader {
    private final TemplateLoader[] templateLoaders;
    private final Map<String, TemplateLoader> lastTemplateLoaderForName = new ConcurrentHashMap();
    private boolean sticky = true;

    public MultiTemplateLoader(TemplateLoader[] templateLoaders) {
        NullArgumentException.check("templateLoaders", templateLoaders);
        this.templateLoaders = (TemplateLoader[]) templateLoaders.clone();
    }

    @Override // freemarker.cache.TemplateLoader
    public Object findTemplateSource(String name) throws IOException {
        Object source;
        Object source2;
        TemplateLoader lastTemplateLoader = null;
        if (this.sticky) {
            lastTemplateLoader = this.lastTemplateLoaderForName.get(name);
            if (lastTemplateLoader != null && (source2 = lastTemplateLoader.findTemplateSource(name)) != null) {
                return new MultiSource(source2, lastTemplateLoader);
            }
        }
        for (TemplateLoader templateLoader : this.templateLoaders) {
            if (lastTemplateLoader != templateLoader && (source = templateLoader.findTemplateSource(name)) != null) {
                if (this.sticky) {
                    this.lastTemplateLoaderForName.put(name, templateLoader);
                }
                return new MultiSource(source, templateLoader);
            }
        }
        if (this.sticky) {
            this.lastTemplateLoaderForName.remove(name);
            return null;
        }
        return null;
    }

    @Override // freemarker.cache.TemplateLoader
    public long getLastModified(Object templateSource) {
        return ((MultiSource) templateSource).getLastModified();
    }

    @Override // freemarker.cache.TemplateLoader
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return ((MultiSource) templateSource).getReader(encoding);
    }

    @Override // freemarker.cache.TemplateLoader
    public void closeTemplateSource(Object templateSource) throws IOException {
        ((MultiSource) templateSource).close();
    }

    @Override // freemarker.cache.StatefulTemplateLoader
    public void resetState() {
        this.lastTemplateLoaderForName.clear();
        for (TemplateLoader loader : this.templateLoaders) {
            if (loader instanceof StatefulTemplateLoader) {
                ((StatefulTemplateLoader) loader).resetState();
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/MultiTemplateLoader$MultiSource.class */
    static final class MultiSource {
        private final Object source;
        private final TemplateLoader loader;

        MultiSource(Object source, TemplateLoader loader) {
            this.source = source;
            this.loader = loader;
        }

        long getLastModified() {
            return this.loader.getLastModified(this.source);
        }

        Reader getReader(String encoding) throws IOException {
            return this.loader.getReader(this.source, encoding);
        }

        void close() throws IOException {
            this.loader.closeTemplateSource(this.source);
        }

        Object getWrappedSource() {
            return this.source;
        }

        public boolean equals(Object o) {
            if (o instanceof MultiSource) {
                MultiSource m = (MultiSource) o;
                return m.loader.equals(this.loader) && m.source.equals(this.source);
            }
            return false;
        }

        public int hashCode() {
            return this.loader.hashCode() + (31 * this.source.hashCode());
        }

        public String toString() {
            return this.source.toString();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MultiTemplateLoader(");
        for (int i = 0; i < this.templateLoaders.length; i++) {
            if (i != 0) {
                sb.append(", ");
            }
            sb.append("loader").append(i + 1).append(" = ").append(this.templateLoaders[i]);
        }
        sb.append(")");
        return sb.toString();
    }

    public int getTemplateLoaderCount() {
        return this.templateLoaders.length;
    }

    public TemplateLoader getTemplateLoader(int index) {
        return this.templateLoaders[index];
    }

    public boolean isSticky() {
        return this.sticky;
    }

    public void setSticky(boolean sticky) {
        this.sticky = sticky;
    }
}
