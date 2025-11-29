package org.springframework.core.env;

import ch.qos.logback.core.joran.util.beans.BeanUtil;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/* compiled from: PropertyResolverExtensions.kt */
@Metadata(mv = {1, 1, 18}, bv = {1, 0, 3}, k = 2, d1 = {"��\u0014\n��\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010��\n��\u001a\u0017\u0010��\u001a\u0004\u0018\u00010\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0086\u0002\u001a$\u0010\u0004\u001a\u0004\u0018\u0001H\u0005\"\u0006\b��\u0010\u0005\u0018\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0086\b¢\u0006\u0002\u0010\u0006\u001a&\u0010\u0007\u001a\u0002H\u0005\"\n\b��\u0010\u0005\u0018\u0001*\u00020\b*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0001H\u0086\b¢\u0006\u0002\u0010\u0006¨\u0006\t"}, d2 = {BeanUtil.PREFIX_GETTER_GET, "", "Lorg/springframework/core/env/PropertyResolver;", "key", "getProperty", "T", "(Lorg/springframework/core/env/PropertyResolver;Ljava/lang/String;)Ljava/lang/Object;", "getRequiredProperty", "", "spring-core"})
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/PropertyResolverExtensionsKt.class */
public final class PropertyResolverExtensionsKt {
    @Nullable
    public static final String get(@NotNull PropertyResolver $this$get, @NotNull String key) {
        Intrinsics.checkParameterIsNotNull($this$get, "$this$get");
        Intrinsics.checkParameterIsNotNull(key, "key");
        return $this$get.getProperty(key);
    }

    public static final /* synthetic */ <T> T getProperty(PropertyResolver propertyResolver, String str) {
        Intrinsics.checkParameterIsNotNull(propertyResolver, "$this$getProperty");
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.reifiedOperationMarker(4, "T");
        return (T) propertyResolver.getProperty(str, Object.class);
    }

    public static final /* synthetic */ <T> T getRequiredProperty(PropertyResolver propertyResolver, String str) {
        Intrinsics.checkParameterIsNotNull(propertyResolver, "$this$getRequiredProperty");
        Intrinsics.checkParameterIsNotNull(str, "key");
        Intrinsics.reifiedOperationMarker(4, "T");
        T t = (T) propertyResolver.getRequiredProperty(str, Object.class);
        Intrinsics.checkExpressionValueIsNotNull(t, "getRequiredProperty(key, T::class.java)");
        return t;
    }
}
