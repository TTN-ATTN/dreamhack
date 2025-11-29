package freemarker.core;

import freemarker.core.Environment;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonNamespaceException.class */
class NonNamespaceException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {Environment.Namespace.class};

    public NonNamespaceException(Environment env) {
        super(env, "Expecting namespace value here");
    }

    public NonNamespaceException(String description, Environment env) {
        super(env, description);
    }

    NonNamespaceException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonNamespaceException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "namespace", EXPECTED_TYPES, env);
    }

    NonNamespaceException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "namespace", EXPECTED_TYPES, tip, env);
    }

    NonNamespaceException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "namespace", EXPECTED_TYPES, tips, env);
    }
}
