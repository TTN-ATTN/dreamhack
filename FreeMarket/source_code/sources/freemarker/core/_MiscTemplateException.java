package freemarker.core;

import freemarker.template.TemplateException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_MiscTemplateException.class */
public class _MiscTemplateException extends TemplateException {
    public _MiscTemplateException(String description) {
        super(description, (Environment) null);
    }

    public _MiscTemplateException(Environment env, String description) {
        super(description, env);
    }

    public _MiscTemplateException(Throwable cause, String description) {
        this(cause, (Environment) null, description);
    }

    public _MiscTemplateException(Throwable cause, Environment env) {
        this(cause, env, (String) null);
    }

    public _MiscTemplateException(Throwable cause) {
        this(cause, (Environment) null, (String) null);
    }

    public _MiscTemplateException(Throwable cause, Environment env, String description) {
        super(description, cause, env);
    }

    public _MiscTemplateException(_ErrorDescriptionBuilder description) {
        this((Environment) null, description);
    }

    public _MiscTemplateException(Environment env, _ErrorDescriptionBuilder description) {
        this((Throwable) null, env, description);
    }

    public _MiscTemplateException(Throwable cause, Environment env, _ErrorDescriptionBuilder description) {
        super(cause, env, null, description);
    }

    public _MiscTemplateException(Object... descriptionParts) {
        this((Environment) null, descriptionParts);
    }

    public _MiscTemplateException(Environment env, Object... descriptionParts) {
        this((Throwable) null, env, descriptionParts);
    }

    public _MiscTemplateException(Throwable cause, Object... descriptionParts) {
        this(cause, (Environment) null, descriptionParts);
    }

    public _MiscTemplateException(Throwable cause, Environment env, Object... descriptionParts) {
        super(cause, env, null, new _ErrorDescriptionBuilder(descriptionParts));
    }

    public _MiscTemplateException(Expression blamed, Object... descriptionParts) {
        this(blamed, (Environment) null, descriptionParts);
    }

    public _MiscTemplateException(Expression blamed, Environment env, Object... descriptionParts) {
        this(blamed, (Throwable) null, env, descriptionParts);
    }

    public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, Object... descriptionParts) {
        super(cause, env, blamed, new _ErrorDescriptionBuilder(descriptionParts).blame(blamed));
    }

    public _MiscTemplateException(Expression blamed, String description) {
        this(blamed, (Environment) null, description);
    }

    public _MiscTemplateException(Expression blamed, Environment env, String description) {
        this(blamed, (Throwable) null, env, description);
    }

    public _MiscTemplateException(Expression blamed, Throwable cause, Environment env, String description) {
        super(cause, env, blamed, new _ErrorDescriptionBuilder(description).blame(blamed));
    }
}
