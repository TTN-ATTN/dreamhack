package javax.annotation.sql;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jakarta.annotation-api-1.3.5.jar:javax/annotation/sql/DataSourceDefinitions.class */
public @interface DataSourceDefinitions {
    DataSourceDefinition[] value();
}
