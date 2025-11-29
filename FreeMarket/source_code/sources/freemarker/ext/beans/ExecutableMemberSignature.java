package freemarker.ext.beans;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import org.springframework.cglib.core.Constants;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ExecutableMemberSignature.class */
final class ExecutableMemberSignature {
    private final String name;
    private final Class<?>[] args;

    ExecutableMemberSignature(String name, Class<?>[] args) {
        this.name = name;
        this.args = args;
    }

    ExecutableMemberSignature(Method method) {
        this(method.getName(), method.getParameterTypes());
    }

    ExecutableMemberSignature(Constructor<?> constructor) {
        this(Constants.CONSTRUCTOR_NAME, constructor.getParameterTypes());
    }

    public boolean equals(Object o) {
        if (o instanceof ExecutableMemberSignature) {
            ExecutableMemberSignature ms = (ExecutableMemberSignature) o;
            return ms.name.equals(this.name) && Arrays.equals(this.args, ms.args);
        }
        return false;
    }

    public int hashCode() {
        return this.name.hashCode() + (this.args.length * 31);
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + this.name + ", " + Arrays.toString(this.args) + ")";
    }
}
