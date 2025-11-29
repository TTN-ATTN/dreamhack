package freemarker.core;

import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateSequenceModel;
import java.util.Arrays;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/UnexpectedTypeException.class */
public class UnexpectedTypeException extends TemplateException {
    public UnexpectedTypeException(Environment env, String description) {
        super(description, env);
    }

    UnexpectedTypeException(Environment env, _ErrorDescriptionBuilder description) {
        super(null, env, null, description);
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env));
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, String tip, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env).tip(tip));
    }

    UnexpectedTypeException(Expression blamed, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
        super(null, env, blamed, newDescriptionBuilder(blamed, null, model, expectedTypesDesc, expectedTypes, env).tips(tips));
    }

    UnexpectedTypeException(String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Object[] tips, Environment env) throws InvalidReferenceException {
        super(null, env, null, newDescriptionBuilder(null, blamedAssignmentTargetVarName, model, expectedTypesDesc, expectedTypes, env).tips(tips));
    }

    private static _ErrorDescriptionBuilder newDescriptionBuilder(Expression blamed, String blamedAssignmentTargetVarName, TemplateModel model, String expectedTypesDesc, Class[] expectedTypes, Environment env) throws InvalidReferenceException {
        Object[] tip;
        if (model == null) {
            throw InvalidReferenceException.getInstance(blamed, env);
        }
        _ErrorDescriptionBuilder errorDescBuilder = new _ErrorDescriptionBuilder(unexpectedTypeErrorDescription(expectedTypesDesc, blamed, blamedAssignmentTargetVarName, model)).blame(blamed).showBlamer(true);
        if ((model instanceof _UnexpectedTypeErrorExplainerTemplateModel) && (tip = ((_UnexpectedTypeErrorExplainerTemplateModel) model).explainTypeError(expectedTypes)) != null) {
            errorDescBuilder.tip(tip);
        }
        if ((model instanceof TemplateCollectionModel) && (Arrays.asList(expectedTypes).contains(TemplateSequenceModel.class) || Arrays.asList(expectedTypes).contains(TemplateCollectionModelEx.class))) {
            errorDescBuilder.tip("As the problematic value contains a collection of items, you could convert it to a sequence like someValue?sequence. Be sure though that you won't have a large number of items, as all will be held in memory the same time.");
        }
        return errorDescBuilder;
    }

    private static Object[] unexpectedTypeErrorDescription(String str, Expression expression, String str2, TemplateModel templateModel) {
        Object[] objArr = new Object[7];
        objArr[0] = "Expected ";
        objArr[1] = new _DelayedAOrAn(str);
        objArr[2] = ", but ";
        objArr[3] = str2 == null ? expression != null ? "this" : "the expression" : new Object[]{"assignment target variable ", new _DelayedJQuote(str2)};
        objArr[4] = " has evaluated to ";
        objArr[5] = new _DelayedAOrAn(new _DelayedFTLTypeDescription(templateModel));
        objArr[6] = expression != null ? ":" : ".";
        return objArr;
    }
}
