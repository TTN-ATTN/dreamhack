package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TrueTemplateBooleanModel.class */
final class TrueTemplateBooleanModel implements SerializableTemplateBooleanModel {
    TrueTemplateBooleanModel() {
    }

    @Override // freemarker.template.TemplateBooleanModel
    public boolean getAsBoolean() {
        return true;
    }

    private Object readResolve() {
        return TRUE;
    }
}
