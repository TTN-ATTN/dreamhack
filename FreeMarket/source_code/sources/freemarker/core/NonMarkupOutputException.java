package freemarker.core;

import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonMarkupOutputException.class */
public class NonMarkupOutputException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateMarkupOutputModel.class};

    public NonMarkupOutputException(Environment env) {
        super(env, "Expecting markup output value here");
    }

    public NonMarkupOutputException(String description, Environment env) {
        super(env, description);
    }

    NonMarkupOutputException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, env);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, tip, env);
    }

    NonMarkupOutputException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "markup output", EXPECTED_TYPES, tips, env);
    }
}
