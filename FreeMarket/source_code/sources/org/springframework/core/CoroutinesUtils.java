package org.springframework.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import kotlin.Unit;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.reflect.KClassifier;
import kotlin.reflect.KFunction;
import kotlin.reflect.full.KCallables;
import kotlin.reflect.jvm.KCallablesJvm;
import kotlin.reflect.jvm.ReflectJvmMapping;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.GlobalScope;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.reactor.MonoKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/CoroutinesUtils.class */
public abstract class CoroutinesUtils {
    public static <T> Mono<T> deferredToMono(Deferred<T> source) {
        return MonoKt.mono(Dispatchers.getUnconfined(), (scope, continuation) -> {
            return source.await(continuation);
        });
    }

    public static <T> Deferred<T> monoToDeferred(Mono<T> source) {
        return BuildersKt.async(GlobalScope.INSTANCE, Dispatchers.getUnconfined(), CoroutineStart.DEFAULT, (scope, continuation) -> {
            return MonoKt.awaitSingleOrNull(source, continuation);
        });
    }

    public static Publisher<?> invokeSuspendingFunction(Method method, Object target, Object... args) {
        KFunction<?> function = (KFunction) Objects.requireNonNull(ReflectJvmMapping.getKotlinFunction(method));
        if (method.isAccessible() && !KCallablesJvm.isAccessible(function)) {
            KCallablesJvm.setAccessible(function, true);
        }
        KClassifier classifier = function.getReturnType().getClassifier();
        Mono<Object> mono = MonoKt.mono(Dispatchers.getUnconfined(), (scope, continuation) -> {
            return KCallables.callSuspend(function, getSuspendedFunctionArgs(target, args), continuation);
        }).filter(result -> {
            return !Objects.equals(result, Unit.INSTANCE);
        }).onErrorMap(InvocationTargetException.class, (v0) -> {
            return v0.getTargetException();
        });
        if (classifier != null && classifier.equals(JvmClassMappingKt.getKotlinClass(Flow.class))) {
            return mono.flatMapMany(CoroutinesUtils::asFlux);
        }
        return mono;
    }

    private static Object[] getSuspendedFunctionArgs(Object target, Object... args) {
        Object[] functionArgs = new Object[args.length];
        functionArgs[0] = target;
        System.arraycopy(args, 0, functionArgs, 1, args.length - 1);
        return functionArgs;
    }

    private static Flux<?> asFlux(Object flow) {
        return ReactorFlowKt.asFlux((Flow) flow);
    }
}
