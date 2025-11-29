package org.springframework.util.function;

import java.util.function.Supplier;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/function/SupplierUtils.class */
public abstract class SupplierUtils {
    @Nullable
    public static <T> T resolve(@Nullable Supplier<T> supplier) {
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }
}
