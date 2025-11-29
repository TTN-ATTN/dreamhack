package javax.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jakarta.annotation-api-1.3.5.jar:javax/annotation/Priority.class */
public @interface Priority {
    int value();
}
