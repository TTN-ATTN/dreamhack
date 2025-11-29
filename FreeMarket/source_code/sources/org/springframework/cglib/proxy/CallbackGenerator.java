package org.springframework.cglib.proxy;

import java.util.List;
import org.springframework.cglib.core.ClassEmitter;
import org.springframework.cglib.core.CodeEmitter;
import org.springframework.cglib.core.MethodInfo;
import org.springframework.cglib.core.Signature;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/proxy/CallbackGenerator.class */
interface CallbackGenerator {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/cglib/proxy/CallbackGenerator$Context.class */
    public interface Context {
        ClassLoader getClassLoader();

        CodeEmitter beginMethod(ClassEmitter classEmitter, MethodInfo methodInfo);

        int getOriginalModifiers(MethodInfo methodInfo);

        int getIndex(MethodInfo methodInfo);

        void emitCallback(CodeEmitter codeEmitter, int i);

        Signature getImplSignature(MethodInfo methodInfo);

        void emitLoadArgsAndInvoke(CodeEmitter codeEmitter, MethodInfo methodInfo);
    }

    void generate(ClassEmitter classEmitter, Context context, List list) throws Exception;

    void generateStatic(CodeEmitter codeEmitter, Context context, List list) throws Exception;
}
