package javax.annotation.security;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jakarta.annotation-api-1.3.5.jar:javax/annotation/security/PermitAll.class */
public @interface PermitAll {
}
