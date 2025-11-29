package org.springframework.context.annotation;

import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

/* compiled from: AnnotationConfigApplicationContextExtensions.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 2, d1 = {"��\u0016\n��\n\u0002\u0018\u0002\n��\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n��\u001a!\u0010��\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0002\b\u0005H\u0007¨\u0006\u0006"}, d2 = {"AnnotationConfigApplicationContext", "Lorg/springframework/context/annotation/AnnotationConfigApplicationContext;", "configure", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "spring-context"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/annotation/AnnotationConfigApplicationContextExtensionsKt.class */
public final class AnnotationConfigApplicationContextExtensionsKt {
    @Deprecated(message = "Use regular apply method instead.", replaceWith = @ReplaceWith(imports = {}, expression = "AnnotationConfigApplicationContext().apply(configure)"))
    @NotNull
    public static final AnnotationConfigApplicationContext AnnotationConfigApplicationContext(@NotNull Function1<? super AnnotationConfigApplicationContext, Unit> function1) {
        Intrinsics.checkParameterIsNotNull(function1, "configure");
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        function1.invoke(annotationConfigApplicationContext);
        return annotationConfigApplicationContext;
    }
}
