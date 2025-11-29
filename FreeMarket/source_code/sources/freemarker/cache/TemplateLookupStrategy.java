package freemarker.cache;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupStrategy.class */
public abstract class TemplateLookupStrategy {
    public static final TemplateLookupStrategy DEFAULT_2_3_0 = new Default020300();

    public abstract TemplateLookupResult lookup(TemplateLookupContext templateLookupContext) throws IOException;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateLookupStrategy$Default020300.class */
    private static class Default020300 extends TemplateLookupStrategy {
        private Default020300() {
        }

        @Override // freemarker.cache.TemplateLookupStrategy
        public TemplateLookupResult lookup(TemplateLookupContext ctx) throws IOException {
            return ctx.lookupWithLocalizedThenAcquisitionStrategy(ctx.getTemplateName(), ctx.getTemplateLocale());
        }

        public String toString() {
            return "TemplateLookupStrategy.DEFAULT_2_3_0";
        }
    }
}
