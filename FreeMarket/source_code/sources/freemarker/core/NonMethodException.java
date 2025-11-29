package freemarker.core;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonMethodException.class */
public class NonMethodException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateMethodModel.class};
    private static final Class[] EXPECTED_TYPES_WITH_FUNCTION = {TemplateMethodModel.class, Macro.class};

    public NonMethodException(Environment env) {
        super(env, "Expecting method value here");
    }

    public NonMethodException(String description, Environment env) {
        super(env, description);
    }

    NonMethodException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMethodException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method", EXPECTED_TYPES, tip, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        this(blamed, model, false, false, tips, env);
    }

    NonMethodException(Expression blamed, TemplateModel model, boolean allowFTLFunction, boolean allowLambdaExp, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "method" + (allowFTLFunction ? " or function" : "") + (allowLambdaExp ? " or lambda expression" : ""), allowFTLFunction ? EXPECTED_TYPES_WITH_FUNCTION : EXPECTED_TYPES, tips, env);
    }
}
