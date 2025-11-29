package freemarker.cache;

import freemarker.template.utility.NullArgumentException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupResult.class */
public abstract class TemplateLookupResult {
    public abstract String getTemplateSourceName();

    public abstract boolean isPositive();

    abstract Object getTemplateSource();

    static TemplateLookupResult createNegativeResult() {
        return NegativeTemplateLookupResult.INSTANCE;
    }

    static TemplateLookupResult from(String templateSourceName, Object templateSource) {
        return templateSource != null ? new PositiveTemplateLookupResult(templateSourceName, templateSource) : createNegativeResult();
    }

    private TemplateLookupResult() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupResult$PositiveTemplateLookupResult.class */
    private static final class PositiveTemplateLookupResult extends TemplateLookupResult {
        private final String templateSourceName;
        private final Object templateSource;

        private PositiveTemplateLookupResult(String templateSourceName, Object templateSource) {
            super();
            NullArgumentException.check("templateName", templateSourceName);
            NullArgumentException.check("templateSource", templateSource);
            if (templateSource instanceof TemplateLookupResult) {
                throw new IllegalArgumentException();
            }
            this.templateSourceName = templateSourceName;
            this.templateSource = templateSource;
        }

        @Override // freemarker.cache.TemplateLookupResult
        public String getTemplateSourceName() {
            return this.templateSourceName;
        }

        @Override // freemarker.cache.TemplateLookupResult
        Object getTemplateSource() {
            return this.templateSource;
        }

        @Override // freemarker.cache.TemplateLookupResult
        public boolean isPositive() {
            return true;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupResult$NegativeTemplateLookupResult.class */
    private static final class NegativeTemplateLookupResult extends TemplateLookupResult {
        private static final NegativeTemplateLookupResult INSTANCE = new NegativeTemplateLookupResult();

        private NegativeTemplateLookupResult() {
            super();
        }

        @Override // freemarker.cache.TemplateLookupResult
        public String getTemplateSourceName() {
            return null;
        }

        @Override // freemarker.cache.TemplateLookupResult
        Object getTemplateSource() {
            return null;
        }

        @Override // freemarker.cache.TemplateLookupResult
        public boolean isPositive() {
            return false;
        }
    }
}
