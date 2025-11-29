package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateNumberModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/NonNumericalException.class */
public class NonNumericalException extends UnexpectedTypeException {
    private static final Class[] EXPECTED_TYPES = {TemplateNumberModel.class};

    public NonNumericalException(Environment env) {
        super(env, "Expecting numerical value here");
    }

    public NonNumericalException(String description, Environment env) {
        super(env, description);
    }

    NonNumericalException(_ErrorDescriptionBuilder description, Environment env) {
        super(env, description);
    }

    NonNumericalException(Expression blamed, TemplateModel model, Environment env) throws InvalidReferenceException {
        super(blamed, model, "number", EXPECTED_TYPES, env);
    }

    NonNumericalException(Expression blamed, TemplateModel model, String tip, Environment env) throws InvalidReferenceException {
        super(blamed, model, "number", EXPECTED_TYPES, tip, env);
    }

    NonNumericalException(Expression blamed, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(blamed, model, "number", EXPECTED_TYPES, tips, env);
    }

    NonNumericalException(String assignmentTargetVarName, TemplateModel model, String[] tips, Environment env) throws InvalidReferenceException {
        super(assignmentTargetVarName, model, "number", EXPECTED_TYPES, tips, env);
    }

    static NonNumericalException newMalformedNumberException(Expression blamed, String text, Environment env) {
        return new NonNumericalException(new _ErrorDescriptionBuilder("Can't convert this string to number: ", new _DelayedJQuote(text)).blame(blamed), env);
    }
}
