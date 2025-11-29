package freemarker.core;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.ClassUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_TemplateModelException.class */
public class _TemplateModelException extends TemplateModelException {
    public _TemplateModelException(String description) {
        super(description);
    }

    public _TemplateModelException(Throwable cause, String description) {
        this(cause, (Environment) null, description);
    }

    public _TemplateModelException(Environment env, String description) {
        this((Throwable) null, env, description);
    }

    public _TemplateModelException(Throwable cause, Environment env) {
        this(cause, env, (String) null);
    }

    public _TemplateModelException(Throwable cause) {
        this(cause, (Environment) null, (String) null);
    }

    public _TemplateModelException(Throwable cause, Environment env, String description) {
        super(cause, env, description, true);
    }

    public _TemplateModelException(_ErrorDescriptionBuilder description) {
        this((Environment) null, description);
    }

    public _TemplateModelException(Environment env, _ErrorDescriptionBuilder description) {
        this((Throwable) null, env, description);
    }

    public _TemplateModelException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
        super(cause, env, description, true);
    }

    public _TemplateModelException(Object... descriptionParts) {
        this((Environment) null, descriptionParts);
    }

    public _TemplateModelException(Environment env, Object... descriptionParts) {
        this((Throwable) null, env, descriptionParts);
    }

    public _TemplateModelException(Throwable cause, Object... descriptionParts) {
        this(cause, (Environment) null, descriptionParts);
    }

    public _TemplateModelException(Throwable cause, Environment env, Object... descriptionParts) {
        super(cause, env, new _ErrorDescriptionBuilder(descriptionParts), true);
    }

    public _TemplateModelException(Expression blamed, Object... descriptionParts) {
        this(blamed, (Environment) null, descriptionParts);
    }

    public _TemplateModelException(Expression blamed, Environment env, Object... descriptionParts) {
        this(blamed, (Throwable) null, env, descriptionParts);
    }

    public _TemplateModelException(Expression blamed, Throwable cause, Environment env, Object... descriptionParts) {
        super(cause, env, new _ErrorDescriptionBuilder(descriptionParts).blame(blamed), true);
    }

    public _TemplateModelException(Expression blamed, String description) {
        this(blamed, (Environment) null, description);
    }

    public _TemplateModelException(Expression blamed, Environment env, String description) {
        this(blamed, (Throwable) null, env, description);
    }

    public _TemplateModelException(Expression blamed, Throwable cause, Environment env, String description) {
        super(cause, env, new _ErrorDescriptionBuilder(description).blame(blamed), true);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static Object[] modelHasStoredNullDescription(Class cls, TemplateModel templateModel) {
        Object[] objArr = new Object[5];
        objArr[0] = "The FreeMarker value exists, but has nothing inside it; the TemplateModel object (class: ";
        objArr[1] = templateModel.getClass().getName();
        objArr[2] = ") has returned a null";
        objArr[3] = cls != null ? new Object[]{" instead of a ", ClassUtil.getShortClassName(cls)} : "";
        objArr[4] = ". This is possibly a bug in the non-FreeMarker code that builds the data-model.";
        return objArr;
    }
}
