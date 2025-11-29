package javax.servlet.http;

import java.text.MessageFormat;

/* compiled from: Cookie.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:javax/servlet/http/RFC2109Validator.class */
class RFC2109Validator extends RFC6265Validator {
    RFC2109Validator(boolean allowSlash) {
        if (allowSlash) {
            this.allowed.set(47);
        }
    }

    @Override // javax.servlet.http.CookieNameValidator
    void validate(String name) {
        super.validate(name);
        if (name.charAt(0) == '$') {
            String errMsg = lStrings.getString("err.cookie_name_is_token");
            throw new IllegalArgumentException(MessageFormat.format(errMsg, name));
        }
    }
}
