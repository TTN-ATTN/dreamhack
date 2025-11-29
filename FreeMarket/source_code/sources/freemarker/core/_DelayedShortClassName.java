package freemarker.core;

import freemarker.template.utility.ClassUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_DelayedShortClassName.class */
public class _DelayedShortClassName extends _DelayedConversionToString {
    public _DelayedShortClassName(Class pClass) {
        super(pClass);
    }

    @Override // freemarker.core._DelayedConversionToString
    protected String doConversion(Object obj) {
        return ClassUtil.getShortClassName((Class) obj, true);
    }
}
