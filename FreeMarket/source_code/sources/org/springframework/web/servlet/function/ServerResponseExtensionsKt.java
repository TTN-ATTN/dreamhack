package org.springframework.web.servlet.function;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.servlet.function.ServerResponse;

/* compiled from: ServerResponseExtensions.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 2, d1 = {"��\u0014\n��\n\u0002\u0018\u0002\n��\n\u0002\u0010��\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a&\u0010��\u001a\u00020\u0001\"\n\b��\u0010\u0002\u0018\u0001*\u00020\u0003*\u00020\u00042\u0006\u0010\u0005\u001a\u0002H\u0002H\u0086\b¢\u0006\u0002\u0010\u0006¨\u0006\u0007"}, d2 = {"bodyWithType", "Lorg/springframework/web/servlet/function/ServerResponse;", "T", "", "Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;", "body", "(Lorg/springframework/web/servlet/function/ServerResponse$BodyBuilder;Ljava/lang/Object;)Lorg/springframework/web/servlet/function/ServerResponse;", "spring-webmvc"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/ServerResponseExtensionsKt.class */
public final class ServerResponseExtensionsKt {
    public static final /* synthetic */ <T> ServerResponse bodyWithType(ServerResponse.BodyBuilder $this$bodyWithType, T t) {
        Intrinsics.checkParameterIsNotNull($this$bodyWithType, "$this$bodyWithType");
        Intrinsics.checkParameterIsNotNull(t, "body");
        Intrinsics.needClassReification();
        ServerResponse serverResponseBody = $this$bodyWithType.body(t, new ParameterizedTypeReference<T>() { // from class: org.springframework.web.servlet.function.ServerResponseExtensionsKt.bodyWithType.1
        });
        Intrinsics.checkExpressionValueIsNotNull(serverResponseBody, "body(body, object : Para…zedTypeReference<T>() {})");
        return serverResponseBody;
    }
}
