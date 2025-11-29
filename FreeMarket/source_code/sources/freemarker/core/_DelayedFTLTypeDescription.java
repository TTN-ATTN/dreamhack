package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.utility.ClassUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedFTLTypeDescription.class */
public class _DelayedFTLTypeDescription extends _DelayedConversionToString {
    public _DelayedFTLTypeDescription(TemplateModel tm) {
        super(tm);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        return ClassUtil.getFTLTypeDescription((TemplateModel) obj);
    }
}
