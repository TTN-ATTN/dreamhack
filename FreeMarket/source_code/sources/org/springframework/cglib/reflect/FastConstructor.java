package org.springframework.cglib.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/reflect/FastConstructor.class */
public class FastConstructor extends FastMember {
    FastConstructor(FastClass fc, Constructor constructor) {
        super(fc, constructor, fc.getIndex(constructor.getParameterTypes()));
    }

    @Override // org.springframework.cglib.reflect.FastMember
    public Class[] getParameterTypes() {
        return ((Constructor) this.member).getParameterTypes();
    }

    @Override // org.springframework.cglib.reflect.FastMember
    public Class[] getExceptionTypes() {
        return ((Constructor) this.member).getExceptionTypes();
    }

    public Object newInstance() throws InvocationTargetException {
        return this.fc.newInstance(this.index, (Object[]) null);
    }

    public Object newInstance(Object[] args) throws InvocationTargetException {
        return this.fc.newInstance(this.index, args);
    }

    public Constructor getJavaConstructor() {
        return (Constructor) this.member;
    }
}
