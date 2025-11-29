package org.springframework.core;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import kotlinx.coroutines.CompletableDeferredKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import kotlinx.coroutines.reactor.ReactorFlowKt;
import org.reactivestreams.Publisher;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.RxReactiveStreams;
import rx.Single;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry.class */
public class ReactiveAdapterRegistry {

    @Nullable
    private static volatile ReactiveAdapterRegistry sharedInstance;
    private static final boolean reactorPresent;
    private static final boolean rxjava1Present;
    private static final boolean rxjava2Present;
    private static final boolean rxjava3Present;
    private static final boolean flowPublisherPresent;
    private static final boolean kotlinCoroutinesPresent;
    private static final boolean mutinyPresent;
    private final List<ReactiveAdapter> adapters = new ArrayList();

    static {
        ClassLoader classLoader = ReactiveAdapterRegistry.class.getClassLoader();
        reactorPresent = ClassUtils.isPresent("reactor.core.publisher.Flux", classLoader);
        flowPublisherPresent = ClassUtils.isPresent("java.util.concurrent.Flow.Publisher", classLoader);
        rxjava1Present = ClassUtils.isPresent("rx.Observable", classLoader) && ClassUtils.isPresent("rx.RxReactiveStreams", classLoader);
        rxjava2Present = ClassUtils.isPresent("io.reactivex.Flowable", classLoader);
        rxjava3Present = ClassUtils.isPresent("io.reactivex.rxjava3.core.Flowable", classLoader);
        kotlinCoroutinesPresent = ClassUtils.isPresent("kotlinx.coroutines.reactor.MonoKt", classLoader);
        mutinyPresent = ClassUtils.isPresent("io.smallrye.mutiny.Multi", classLoader);
    }

    public ReactiveAdapterRegistry() {
        if (reactorPresent) {
            new ReactorRegistrar().registerAdapters(this);
            if (flowPublisherPresent) {
                new ReactorJdkFlowAdapterRegistrar().registerAdapter(this);
            }
        }
        if (rxjava1Present) {
            new RxJava1Registrar().registerAdapters(this);
        }
        if (rxjava2Present) {
            new RxJava2Registrar().registerAdapters(this);
        }
        if (rxjava3Present) {
            new RxJava3Registrar().registerAdapters(this);
        }
        if (reactorPresent && kotlinCoroutinesPresent) {
            new CoroutinesRegistrar().registerAdapters(this);
        }
        if (mutinyPresent) {
            new MutinyRegistrar().registerAdapters(this);
        }
    }

    public boolean hasAdapters() {
        return !this.adapters.isEmpty();
    }

    public void registerReactiveType(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        if (reactorPresent) {
            this.adapters.add(new ReactorAdapter(descriptor, toAdapter, fromAdapter));
        } else {
            this.adapters.add(new ReactiveAdapter(descriptor, toAdapter, fromAdapter));
        }
    }

    @Nullable
    public ReactiveAdapter getAdapter(Class<?> reactiveType) {
        return getAdapter(reactiveType, null);
    }

    @Nullable
    public ReactiveAdapter getAdapter(@Nullable Class<?> reactiveType, @Nullable Object source) {
        if (this.adapters.isEmpty()) {
            return null;
        }
        Object sourceToUse = source instanceof Optional ? ((Optional) source).orElse(null) : source;
        Class<?> clazz = sourceToUse != null ? sourceToUse.getClass() : reactiveType;
        if (clazz == null) {
            return null;
        }
        for (ReactiveAdapter adapter : this.adapters) {
            if (adapter.getReactiveType() == clazz) {
                return adapter;
            }
        }
        for (ReactiveAdapter adapter2 : this.adapters) {
            if (adapter2.getReactiveType().isAssignableFrom(clazz)) {
                return adapter2;
            }
        }
        return null;
    }

    public static ReactiveAdapterRegistry getSharedInstance() {
        ReactiveAdapterRegistry registry = sharedInstance;
        if (registry == null) {
            synchronized (ReactiveAdapterRegistry.class) {
                registry = sharedInstance;
                if (registry == null) {
                    registry = new ReactiveAdapterRegistry();
                    sharedInstance = registry;
                }
            }
        }
        return registry;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorAdapter.class */
    private static class ReactorAdapter extends ReactiveAdapter {
        ReactorAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toPublisherFunction, Function<Publisher<?>, Object> fromPublisherFunction) {
            super(descriptor, toPublisherFunction, fromPublisherFunction);
        }

        @Override // org.springframework.core.ReactiveAdapter
        public <T> Publisher<T> toPublisher(@Nullable Object source) {
            Publisher<T> publisher = super.toPublisher(source);
            return isMultiValue() ? Flux.from(publisher) : Mono.from(publisher);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorRegistrar.class */
    private static class ReactorRegistrar {
        private ReactorRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Mono.class, Mono::empty), source -> {
                return (Mono) source;
            }, Mono::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flux.class, Flux::empty), source2 -> {
                return (Flux) source2;
            }, Flux::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Publisher.class, Flux::empty), source3 -> {
                return (Publisher) source3;
            }, source4 -> {
                return source4;
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.nonDeferredAsyncValue(CompletionStage.class, EmptyCompletableFuture::new), source5 -> {
                return Mono.fromCompletionStage((CompletionStage) source5);
            }, source6 -> {
                return Mono.from(source6).toFuture();
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$EmptyCompletableFuture.class */
    private static class EmptyCompletableFuture<T> extends CompletableFuture<T> {
        EmptyCompletableFuture() {
            complete(null);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$ReactorJdkFlowAdapterRegistrar.class */
    private static class ReactorJdkFlowAdapterRegistrar {
        private ReactorJdkFlowAdapterRegistrar() {
        }

        void registerAdapter(ReactiveAdapterRegistry registry) {
            try {
                Class<?> publisherClass = ClassUtils.forName("java.util.concurrent.Flow.Publisher", getClass().getClassLoader());
                Class<?> flowAdapterClass = ClassUtils.forName("reactor.adapter.JdkFlowAdapter", getClass().getClassLoader());
                Method toFluxMethod = flowAdapterClass.getMethod("flowPublisherToFlux", publisherClass);
                Method toFlowMethod = flowAdapterClass.getMethod("publisherToFlowPublisher", Publisher.class);
                Object emptyFlow = ReflectionUtils.invokeMethod(toFlowMethod, null, Flux.empty());
                registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(publisherClass, () -> {
                    return emptyFlow;
                }), source -> {
                    return (Publisher) ReflectionUtils.invokeMethod(toFluxMethod, null, source);
                }, publisher -> {
                    return ReflectionUtils.invokeMethod(toFlowMethod, null, publisher);
                });
            } catch (Throwable th) {
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava1Registrar.class */
    private static class RxJava1Registrar {
        private RxJava1Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> {
                return RxReactiveStreams.toPublisher((Observable) source);
            }, RxReactiveStreams::toObservable);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(Single.class), source2 -> {
                return RxReactiveStreams.toPublisher((Single) source2);
            }, RxReactiveStreams::toSingle);
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source3 -> {
                return RxReactiveStreams.toPublisher((Completable) source3);
            }, RxReactiveStreams::toCompletable);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava2Registrar.class */
    private static class RxJava2Registrar {
        private RxJava2Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> {
                return (Flowable) source;
            }, Flowable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(io.reactivex.Observable.class, io.reactivex.Observable::empty), source2 -> {
                return ((io.reactivex.Observable) source2).toFlowable(BackpressureStrategy.BUFFER);
            }, io.reactivex.Observable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(io.reactivex.Single.class), source3 -> {
                return ((io.reactivex.Single) source3).toFlowable();
            }, io.reactivex.Single::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source4 -> {
                return ((Maybe) source4).toFlowable();
            }, source5 -> {
                return Flowable.fromPublisher(source5).toObservable().singleElement();
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(io.reactivex.Completable.class, io.reactivex.Completable::complete), source6 -> {
                return ((io.reactivex.Completable) source6).toFlowable();
            }, io.reactivex.Completable::fromPublisher);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$RxJava3Registrar.class */
    private static class RxJava3Registrar {
        private RxJava3Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(io.reactivex.rxjava3.core.Flowable.class, io.reactivex.rxjava3.core.Flowable::empty), source -> {
                return (io.reactivex.rxjava3.core.Flowable) source;
            }, io.reactivex.rxjava3.core.Flowable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(io.reactivex.rxjava3.core.Observable.class, io.reactivex.rxjava3.core.Observable::empty), source2 -> {
                return ((io.reactivex.rxjava3.core.Observable) source2).toFlowable(io.reactivex.rxjava3.core.BackpressureStrategy.BUFFER);
            }, io.reactivex.rxjava3.core.Observable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(io.reactivex.rxjava3.core.Single.class), source3 -> {
                return ((io.reactivex.rxjava3.core.Single) source3).toFlowable();
            }, io.reactivex.rxjava3.core.Single::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(io.reactivex.rxjava3.core.Maybe.class, io.reactivex.rxjava3.core.Maybe::empty), source4 -> {
                return ((io.reactivex.rxjava3.core.Maybe) source4).toFlowable();
            }, io.reactivex.rxjava3.core.Maybe::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(io.reactivex.rxjava3.core.Completable.class, io.reactivex.rxjava3.core.Completable::complete), source5 -> {
                return ((io.reactivex.rxjava3.core.Completable) source5).toFlowable();
            }, io.reactivex.rxjava3.core.Completable::fromPublisher);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$CoroutinesRegistrar.class */
    private static class CoroutinesRegistrar {
        private CoroutinesRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Deferred.class, () -> {
                return CompletableDeferredKt.CompletableDeferred((Job) null);
            }), source -> {
                return CoroutinesUtils.deferredToMono((Deferred) source);
            }, source2 -> {
                return CoroutinesUtils.monoToDeferred(Mono.from(source2));
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flow.class, FlowKt::emptyFlow), source3 -> {
                return ReactorFlowKt.asFlux((Flow) source3);
            }, ReactiveFlowKt::asFlow);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$MutinyRegistrar.class */
    private static class MutinyRegistrar {
        private MutinyRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Uni.class, () -> {
                return Uni.createFrom().nothing();
            }), uni -> {
                return ((Uni) uni).convert().toPublisher();
            }, publisher -> {
                return Uni.createFrom().publisher(publisher);
            });
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Multi.class, () -> {
                return Multi.createFrom().empty();
            }), multi -> {
                return (Multi) multi;
            }, publisher2 -> {
                return Multi.createFrom().publisher(publisher2);
            });
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/ReactiveAdapterRegistry$SpringCoreBlockHoundIntegration.class */
    public static class SpringCoreBlockHoundIntegration implements BlockHoundIntegration {
        public void applyTo(BlockHound.Builder builder) {
            builder.allowBlockingCallsInside("org.springframework.core.LocalVariableTableParameterNameDiscoverer", "inspectClass");
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "doTask");
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "clear");
            builder.allowBlockingCallsInside("org.springframework.util.ConcurrentReferenceHashMap$Segment", "restructure");
        }
    }
}
