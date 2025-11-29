package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: RouterFunctionDsl.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 3)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0.class */
final class RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0 implements HandlerFunction {
    private final /* synthetic */ Function1 function;

    RouterFunctionDslKt$sam$org_springframework_web_servlet_function_HandlerFunction$0(Function1 function1) {
        this.function = function1;
    }

    @Override // org.springframework.web.servlet.function.HandlerFunction
    public final /* synthetic */ ServerResponse handle(ServerRequest request) {
        Intrinsics.checkParameterIsNotNull(request, "request");
        return (ServerResponse) this.function.invoke(request);
    }
}
