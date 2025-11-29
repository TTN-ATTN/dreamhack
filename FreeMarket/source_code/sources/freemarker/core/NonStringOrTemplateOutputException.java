package freemarker.core;

import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonStringOrTemplateOutputException.class */
public class NonStringOrTemplateOutputException extends UnexpectedTypeException {
    static final String STRING_COERCABLE_TYPES_OR_TOM_DESC = "string or something automatically convertible to string (number, date or boolean), or \"template output\" ";
    static final Class[] STRING_COERCABLE_TYPES_AND_TOM = new Class[NonStringException.STRING_COERCABLE_TYPES.length + 1];
    private static final String DEFAULT_DESCRIPTION = "Expecting string or something automatically convertible to string (number, date or boolean), or \"template output\"  value here";

    static {
        int i = 0;
        while (i < NonStringException.STRING_COERCABLE_TYPES.length) {
            STRING_COERCABLE_TYPES_AND_TOM[i] = NonStringException.STRING_COERCABLE_TYPES[i];
            i++;
        }
        STRING_COERCABLE_TYPES_AND_TOM[i] = TemplateMarkupOutputModel.class;
    }

    public NonStringOrTemplateOutputException(Environment env) {
        super(env, DEFAULT_DESCRIPTION);
    }

    public NonStringOrTemplateOutputException(String description, Environment env) {
        super(env, description);
    }

    NonStringOrTemplateOutputException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, STRING_COERCABLE_TYPES_OR_TOM_DESC, STRING_COERCABLE_TYPES_AND_TOM, env);
    }

    NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, STRING_COERCABLE_TYPES_OR_TOM_DESC, STRING_COERCABLE_TYPES_AND_TOM, tip, env);
    }

    NonStringOrTemplateOutputException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, STRING_COERCABLE_TYPES_OR_TOM_DESC, STRING_COERCABLE_TYPES_AND_TOM, tips, env);
    }
}
