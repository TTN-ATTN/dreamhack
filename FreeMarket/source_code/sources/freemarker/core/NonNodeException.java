package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonNodeException.class */
public class NonNodeException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateNodeModel.class};

    public NonNodeException(Environment env) {
        super(env, "Expecting node value here");
    }

    public NonNodeException(String description, Environment env) {
        super(env, description);
    }

    NonNodeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, env);
    }

    NonNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, tip, env);
    }

    NonNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "node", EXPECTED_TYPES, tips, env);
    }
}
