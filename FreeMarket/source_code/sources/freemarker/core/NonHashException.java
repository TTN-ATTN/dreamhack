package freemarker.core;

import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonHashException.class */
public class NonHashException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateHashModel.class};

    public NonHashException(Environment env) {
        super(env, "Expecting hash value here");
    }

    public NonHashException(String description, Environment env) {
        super(env, description);
    }

    NonHashException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonHashException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "hash", EXPECTED_TYPES, env);
    }

    NonHashException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "hash", EXPECTED_TYPES, tip, env);
    }

    NonHashException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "hash", EXPECTED_TYPES, tips, env);
    }
}
