package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/FalseTemplateBooleanModel.class */
final class FalseTemplateBooleanModel implements SerializableTemplateBooleanModel {
    FalseTemplateBooleanModel() {
    }

    @Override // freemarker.template.TemplateBooleanModel
    public boolean getAsBoolean() {
        return false;
    }

    private Object readResolve() {
        return FALSE;
    }
}
