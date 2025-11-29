package freemarker.core;

import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateTransformModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonUserDefinedDirectiveLikeException.class */
class NonUserDefinedDirectiveLikeException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateDirectiveModel.class, TemplateTransformModel.class, Macro.class};

    public NonUserDefinedDirectiveLikeException(Environment env) {
        super(env, "Expecting user-defined directive, transform or macro value here");
    }

    public NonUserDefinedDirectiveLikeException(String description, Environment env) {
        super(env, description);
    }

    NonUserDefinedDirectiveLikeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, env);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, tip, env);
    }

    NonUserDefinedDirectiveLikeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "user-defined directive, transform or macro", EXPECTED_TYPES, tips, env);
    }
}
