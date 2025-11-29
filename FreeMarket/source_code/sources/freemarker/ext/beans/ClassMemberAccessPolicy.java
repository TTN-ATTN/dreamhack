package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ClassMemberAccessPolicy.class */
public interface ClassMemberAccessPolicy {
    boolean isMethodExposed(Method method);

    boolean isConstructorExposed(Constructor<?> constructor);

    boolean isFieldExposed(Field field);
}
