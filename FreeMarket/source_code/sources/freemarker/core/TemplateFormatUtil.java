package freemarker.core;

import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateFormatUtil.class */
public final class TemplateFormatUtil {
    private TemplateFormatUtil() {
    }

    public static void checkHasNoParameters(String params) throws InvalidFormatParametersException {
        if (params.length() != 0) {
            throw new InvalidFormatParametersException("This number format doesn't support any parameters.");
        }
    }

    public static Number getNonNullNumber(TemplateNumberModel numberModel) throws TemplateModelException, UnformattableValueException {
        Number number = numberModel.getAsNumber();
        if (number == null) {
            throw EvalUtil.newModelHasStoredNullException(Number.class, numberModel, null);
        }
        return number;
    }

    public static Date getNonNullDate(TemplateDateModel dateModel) throws TemplateModelException {
        Date date = dateModel.getAsDate();
        if (date == null) {
            throw EvalUtil.newModelHasStoredNullException(Date.class, dateModel, null);
        }
        return date;
    }
}
