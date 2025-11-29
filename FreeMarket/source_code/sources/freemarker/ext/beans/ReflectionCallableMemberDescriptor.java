package freemarker.ext.beans;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ReflectionCallableMemberDescriptor.class */
final class ReflectionCallableMemberDescriptor extends CallableMemberDescriptor {
    private final Member member;
    final Class[] paramTypes;

    ReflectionCallableMemberDescriptor(Method member, Class[] paramTypes) {
        this.member = member;
        this.paramTypes = paramTypes;
    }

    ReflectionCallableMemberDescriptor(Constructor member, Class[] paramTypes) {
        this.member = member;
        this.paramTypes = paramTypes;
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    TemplateModel invokeMethod(BeansWrapper bw, Object obj, Object[] args) throws IllegalAccessException, TemplateModelException, InvocationTargetException {
        return bw.invokeMethod(obj, (Method) this.member, args);
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    Object invokeConstructor(BeansWrapper bw, Object[] args) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        return ((Constructor) this.member).newInstance(args);
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    String getDeclaration() {
        return _MethodUtil.toString(this.member);
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    boolean isConstructor() {
        return this.member instanceof Constructor;
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    boolean isStatic() {
        return (this.member.getModifiers() & 8) != 0;
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    boolean isVarargs() {
        return _MethodUtil.isVarargs(this.member);
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    Class[] getParamTypes() {
        return this.paramTypes;
    }

    @Override // freemarker.ext.beans.CallableMemberDescriptor
    String getName() {
        return this.member.getName();
    }
}
