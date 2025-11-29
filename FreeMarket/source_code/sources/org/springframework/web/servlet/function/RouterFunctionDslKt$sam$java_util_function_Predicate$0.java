package org.springframework.web.servlet.function;

import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 3)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RouterFunctionDslKt$sam$java_util_function_Predicate$0.class */
final class RouterFunctionDslKt$sam$java_util_function_Predicate$0 implements Predicate {
    private final /* synthetic */ Function1 function;

    RouterFunctionDslKt$sam$java_util_function_Predicate$0(Function1 function1) {
        this.function = function1;
    }

    @Override // java.util.function.Predicate
    public final /* synthetic */ boolean test(Object p0) {
        Object objInvoke = this.function.invoke(p0);
        Intrinsics.checkExpressionValueIsNotNull(objInvoke, "invoke(...)");
        return ((Boolean) objInvoke).booleanValue();
    }
}
