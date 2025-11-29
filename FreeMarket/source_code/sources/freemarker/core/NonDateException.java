package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonDateException.class */
public class NonDateException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateDateModel.class};

    public NonDateException(Environment env) {
        super(env, "Expecting date/time value here");
    }

    public NonDateException(String description, Environment env) {
        super(env, description);
    }

    NonDateException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, env);
    }

    NonDateException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, tip, env);
    }

    NonDateException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "date/time", EXPECTED_TYPES, tips, env);
    }
}
