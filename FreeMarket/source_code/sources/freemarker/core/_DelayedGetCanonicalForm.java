package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedGetCanonicalForm.class */
public class _DelayedGetCanonicalForm extends _DelayedConversionToString {
    public _DelayedGetCanonicalForm(TemplateObject obj) {
        super(obj);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        try {
            return ((TemplateObject) obj).getCanonicalForm();
        } catch (Exception e) {
            return "{Error getting canonical form}";
        }
    }
}
