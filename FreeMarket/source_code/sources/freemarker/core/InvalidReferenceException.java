package freemarker.core;

import freemarker.template.TemplateException;
import org.springframework.beans.factory.support.PropertiesBeanDefinitionReader;
import org.springframework.web.servlet.tags.form.InputTag;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/InvalidReferenceException.class */
public class InvalidReferenceException extends TemplateException {
    static final InvalidReferenceException FAST_INSTANCE;
    private static final Object[] TIP;
    private static final Object[] TIP_MISSING_ASSIGNMENT_TARGET;
    private static final String TIP_NO_DOLLAR = "Variable references must not start with \"$\", unless the \"$\" is really part of the variable name.";
    private static final String TIP_LAST_STEP_DOT = "It's the step after the last dot that caused this error, not those before it.";
    private static final String TIP_LAST_STEP_SQUARE_BRACKET = "It's the final [] step that caused this error, not those before it.";
    private static final String TIP_JSP_TAGLIBS = "The \"JspTaglibs\" variable isn't a core FreeMarker feature; it's only available when templates are invoked through freemarker.ext.servlet.FreemarkerServlet (or other custom FreeMarker-JSP integration solution).";

    static {
        Environment prevEnv = Environment.getCurrentEnvironment();
        try {
            Environment.setCurrentEnvironment(null);
            FAST_INSTANCE = new InvalidReferenceException("Invalid reference. Details are unavailable, as this should have been handled by an FTL construct. If it wasn't, that's probably a bug in FreeMarker.", null);
            TIP = new Object[]{"If the failing expression is known to legally refer to something that's sometimes null or missing, either specify a default value like myOptionalVar!myDefault, or use ", "<#if myOptionalVar??>", "when-present", "<#else>", "when-missing", "</#if>", ". (These only cover the last step of the expression; to cover the whole expression, use parenthesis: (myOptionalVar.foo)!myDefault, (myOptionalVar.foo)??"};
            TIP_MISSING_ASSIGNMENT_TARGET = new Object[]{"If the target variable is known to be legally null or missing sometimes, instead of something like ", "<#assign x += 1>", ", you could write ", "<#if x??>", "<#assign x += 1>", "</#if>", " or ", "<#assign x = (x!0) + 1>"};
        } finally {
            Environment.setCurrentEnvironment(prevEnv);
        }
    }

    public InvalidReferenceException(Environment env) {
        super("Invalid reference: The expression has evaluated to null or refers to something that doesn't exist.", env);
    }

    public InvalidReferenceException(String description, Environment env) {
        super(description, env);
    }

    InvalidReferenceException(_ErrorDescriptionBuilder description, Environment env, Expression expression) {
        super(null, env, expression, description);
    }

    static InvalidReferenceException getInstance(Expression blamed, Environment env) {
        if (env != null && env.getFastInvalidReferenceExceptions()) {
            return FAST_INSTANCE;
        }
        if (blamed != null) {
            _ErrorDescriptionBuilder errDescBuilder = new _ErrorDescriptionBuilder("The following has evaluated to null or missing:").blame(blamed);
            if (endsWithDollarVariable(blamed)) {
                errDescBuilder.tips(TIP_NO_DOLLAR, TIP);
            } else if (blamed instanceof Dot) {
                String rho = ((Dot) blamed).getRHO();
                String nameFixTip = null;
                if (InputTag.SIZE_ATTRIBUTE.equals(rho)) {
                    nameFixTip = "To query the size of a collection or map use ?size, like myList?size";
                } else if ("length".equals(rho)) {
                    nameFixTip = "To query the length of a string use ?length, like myString?size";
                }
                errDescBuilder.tips(nameFixTip == null ? new Object[]{TIP_LAST_STEP_DOT, TIP} : new Object[]{TIP_LAST_STEP_DOT, nameFixTip, TIP});
            } else if (blamed instanceof DynamicKeyName) {
                errDescBuilder.tips(TIP_LAST_STEP_SQUARE_BRACKET, TIP);
            } else if ((blamed instanceof Identifier) && ((Identifier) blamed).getName().equals("JspTaglibs")) {
                errDescBuilder.tips(TIP_JSP_TAGLIBS, TIP);
            } else {
                errDescBuilder.tip(TIP);
            }
            return new InvalidReferenceException(errDescBuilder, env, blamed);
        }
        return new InvalidReferenceException(env);
    }

    static InvalidReferenceException getInstance(int scope, String missingAssignedVarName, String assignmentOperator, Environment env) {
        if (env != null && env.getFastInvalidReferenceExceptions()) {
            return FAST_INSTANCE;
        }
        _ErrorDescriptionBuilder errDescBuilder = new _ErrorDescriptionBuilder("The target variable of the assignment, ", new _DelayedJQuote(missingAssignedVarName), ", was null or missing in the " + Assignment.scopeAsString(scope) + ", and the \"", assignmentOperator, "\" operator must get its value from there before assigning to it.");
        if (missingAssignedVarName.startsWith(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) {
            errDescBuilder.tips(TIP_NO_DOLLAR, TIP_MISSING_ASSIGNMENT_TARGET);
        } else {
            errDescBuilder.tip(TIP_MISSING_ASSIGNMENT_TARGET);
        }
        return new InvalidReferenceException(errDescBuilder, env, null);
    }

    private static boolean endsWithDollarVariable(Expression blame) {
        return ((blame instanceof Identifier) && ((Identifier) blame).getName().startsWith(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX)) || ((blame instanceof Dot) && ((Dot) blame).getRHO().startsWith(PropertiesBeanDefinitionReader.CONSTRUCTOR_ARG_PREFIX));
    }
}
