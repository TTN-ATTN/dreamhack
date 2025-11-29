package org.springframework.http.server.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/AbstractServerHttpResponse.class */
public abstract class AbstractServerHttpResponse implements ServerHttpResponse {
    private final DataBufferFactory dataBufferFactory;

    @Nullable
    private Integer statusCode;
    private final HttpHeaders headers;
    private final MultiValueMap<String, ResponseCookie> cookies;
    private final AtomicReference<State> state;
    private final List<Supplier<? extends Mono<Void>>> commitActions;

    @Nullable
    private HttpHeaders readOnlyHeaders;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/server/reactive/AbstractServerHttpResponse$State.class */
    private enum State {
        NEW,
        COMMITTING,
        COMMIT_ACTION_FAILED,
        COMMITTED
    }

    public abstract <T> T getNativeResponse();

    protected abstract Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> body);

    protected abstract Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> body);

    protected abstract void applyStatusCode();

    protected abstract void applyHeaders();

    protected abstract void applyCookies();

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory) {
        this(dataBufferFactory, new HttpHeaders());
    }

    public AbstractServerHttpResponse(DataBufferFactory dataBufferFactory, HttpHeaders headers) {
        this.state = new AtomicReference<>(State.NEW);
        this.commitActions = new ArrayList(4);
        Assert.notNull(dataBufferFactory, "DataBufferFactory must not be null");
        Assert.notNull(headers, "HttpHeaders must not be null");
        this.dataBufferFactory = dataBufferFactory;
        this.headers = headers;
        this.cookies = new LinkedMultiValueMap();
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public boolean setStatusCode(@Nullable HttpStatus status) {
        if (this.state.get() == State.COMMITTED) {
            return false;
        }
        this.statusCode = status != null ? Integer.valueOf(status.value()) : null;
        return true;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    @Nullable
    public HttpStatus getStatusCode() {
        if (this.statusCode != null) {
            return HttpStatus.resolve(this.statusCode.intValue());
        }
        return null;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public boolean setRawStatusCode(@Nullable Integer statusCode) {
        if (this.state.get() == State.COMMITTED) {
            return false;
        }
        this.statusCode = statusCode;
        return true;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    @Nullable
    public Integer getRawStatusCode() {
        return this.statusCode;
    }

    @Deprecated
    public void setStatusCodeValue(@Nullable Integer statusCode) {
        if (this.state.get() != State.COMMITTED) {
            this.statusCode = statusCode;
        }
    }

    @Nullable
    @Deprecated
    public Integer getStatusCodeValue() {
        return this.statusCode;
    }

    @Override // org.springframework.http.HttpMessage
    public HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (this.state.get() == State.COMMITTED) {
            this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
            return this.readOnlyHeaders;
        }
        return this.headers;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.state.get() == State.COMMITTED ? CollectionUtils.unmodifiableMultiValueMap(this.cookies) : this.cookies;
    }

    @Override // org.springframework.http.server.reactive.ServerHttpResponse
    public void addCookie(ResponseCookie cookie) {
        Assert.notNull(cookie, "ResponseCookie must not be null");
        if (this.state.get() == State.COMMITTED) {
            throw new IllegalStateException("Can't add the cookie " + cookie + "because the HTTP response has already been committed");
        }
        getCookies().add(cookie.getName(), cookie);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public void beforeCommit(Supplier<? extends Mono<Void>> action) {
        this.commitActions.add(action);
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public boolean isCommitted() {
        State state = this.state.get();
        return (state == State.NEW || state == State.COMMIT_ACTION_FAILED) ? false : true;
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        if (body instanceof Mono) {
            return ((Mono) body).flatMap(buffer -> {
                touchDataBuffer(buffer);
                AtomicBoolean subscribed = new AtomicBoolean();
                return doCommit(() -> {
                    try {
                        return writeWithInternal(Mono.fromCallable(() -> {
                            return buffer;
                        }).doOnSubscribe(s -> {
                            subscribed.set(true);
                        }).doOnDiscard(PooledDataBuffer.class, (v0) -> {
                            DataBufferUtils.release(v0);
                        }));
                    } catch (Throwable ex) {
                        return Mono.error(ex);
                    }
                }).doOnError(ex -> {
                    DataBufferUtils.release(buffer);
                }).doOnCancel(() -> {
                    if (!subscribed.get()) {
                        DataBufferUtils.release(buffer);
                    }
                });
            }).doOnError(t -> {
                getHeaders().clearContentHeaders();
            }).doOnDiscard(PooledDataBuffer.class, (v0) -> {
                DataBufferUtils.release(v0);
            });
        }
        return new ChannelSendOperator(body, inner -> {
            return doCommit(() -> {
                return writeWithInternal(inner);
            });
        }).doOnError(t2 -> {
            getHeaders().clearContentHeaders();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return new ChannelSendOperator(body, inner -> {
            return doCommit(() -> {
                return writeAndFlushWithInternal(inner);
            });
        }).doOnError(t -> {
            getHeaders().clearContentHeaders();
        });
    }

    @Override // org.springframework.http.ReactiveHttpOutputMessage
    public Mono<Void> setComplete() {
        return !isCommitted() ? doCommit(null) : Mono.empty();
    }

    protected Mono<Void> doCommit() {
        return doCommit(null);
    }

    protected Mono<Void> doCommit(@Nullable Supplier<? extends Mono<Void>> writeAction) {
        Flux<Void> allActions = Flux.empty();
        if (this.state.compareAndSet(State.NEW, State.COMMITTING)) {
            if (!this.commitActions.isEmpty()) {
                allActions = Flux.concat(Flux.fromIterable(this.commitActions).map((v0) -> {
                    return v0.get();
                })).doOnError(ex -> {
                    if (this.state.compareAndSet(State.COMMITTING, State.COMMIT_ACTION_FAILED)) {
                        getHeaders().clearContentHeaders();
                    }
                });
            }
        } else if (!this.state.compareAndSet(State.COMMIT_ACTION_FAILED, State.COMMITTING)) {
            return Mono.empty();
        }
        Flux<Void> allActions2 = allActions.concatWith(Mono.fromRunnable(() -> {
            applyStatusCode();
            applyHeaders();
            applyCookies();
            this.state.set(State.COMMITTED);
        }));
        if (writeAction != null) {
            allActions2 = allActions2.concatWith(writeAction.get());
        }
        return allActions2.then();
    }

    protected void touchDataBuffer(DataBuffer buffer) {
    }
}
