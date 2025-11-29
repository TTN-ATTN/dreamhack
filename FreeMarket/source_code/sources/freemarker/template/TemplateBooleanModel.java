package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateBooleanModel.class */
public interface TemplateBooleanModel extends TemplateModel {
    public static final TemplateBooleanModel FALSE = new FalseTemplateBooleanModel();
    public static final TemplateBooleanModel TRUE = new TrueTemplateBooleanModel();

    boolean getAsBoolean() throws TemplateModelException;
}
