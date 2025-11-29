package org.springframework.web.servlet.function;

import java.util.function.BiFunction;
import kotlin.Metadata;
import kotlin.jvm.functions.Function2;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 3)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RouterFunctionDslKt$sam$java_util_function_BiFunction$0.class */
final class RouterFunctionDslKt$sam$java_util_function_BiFunction$0 implements BiFunction {
    private final /* synthetic */ Function2 function;

    RouterFunctionDslKt$sam$java_util_function_BiFunction$0(Function2 function2) {
        this.function = function2;
    }

    @Override // java.util.function.BiFunction
    public final /* synthetic */ Object apply(Object p0, Object p1) {
        return this.function.invoke(p0, p1);
    }
}
