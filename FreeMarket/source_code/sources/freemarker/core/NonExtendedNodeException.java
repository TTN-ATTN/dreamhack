package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModelEx;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonExtendedNodeException.class */
public class NonExtendedNodeException extends UnexpectedTypeException {
    private static final Class<?>[] EXPECTED_TYPES = {TemplateNodeModelEx.class};

    public NonExtendedNodeException(Environment env) {
        super(env, "Expecting extended node value here");
    }

    public NonExtendedNodeException(String description, Environment env) {
        super(env, description);
    }

    NonExtendedNodeException(Environment env, _ErrorDescriptionBuilder description) {
        super(env, description);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", EXPECTED_TYPES, env);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", EXPECTED_TYPES, tip, env);
    }

    NonExtendedNodeException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "extended node", EXPECTED_TYPES, tips, env);
    }
}
