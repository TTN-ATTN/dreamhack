package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BackwardCompatibleTemplateNumberFormat.class */
abstract class BackwardCompatibleTemplateNumberFormat extends TemplateNumberFormat {
    abstract String format(Number number) throws UnformattableValueException;

    BackwardCompatibleTemplateNumberFormat() {
    }
}
