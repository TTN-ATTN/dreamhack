package freemarker.template;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/TemplateScalarModel.class */
public interface TemplateScalarModel extends TemplateModel {
    public static final TemplateModel EMPTY_STRING = new SimpleScalar("");

    String getAsString() throws TemplateModelException;
}
