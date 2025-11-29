package freemarker.ext.servlet;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.CLASS)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/servlet/SuppressFBWarnings.class */
@interface SuppressFBWarnings {
    String[] value() default {};

    String justification() default "";
}
