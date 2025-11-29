package org.springframework.http.codec.multipart;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Subscription;
import org.springframework.core.codec.DecodingException;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.MultipartParser;
import org.springframework.util.FastByteArrayOutputStream;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.util.context.Context;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator.class */
final class PartGenerator extends BaseSubscriber<MultipartParser.Token> {
    private static final Log logger = LogFactory.getLog((Class<?>) PartGenerator.class);
    private final AtomicReference<State> state = new AtomicReference<>(new InitialState());
    private final AtomicInteger partCount = new AtomicInteger();
    private final AtomicBoolean requestOutstanding = new AtomicBoolean();
    private final FluxSink<Part> sink;
    private final int maxParts;
    private final boolean streaming;
    private final int maxInMemorySize;
    private final long maxDiskUsagePerPart;
    private final Mono<Path> fileStorageDirectory;
    private final Scheduler blockingOperationScheduler;

    private PartGenerator(FluxSink<Part> sink, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
        this.sink = sink;
        this.maxParts = maxParts;
        this.maxInMemorySize = maxInMemorySize;
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
        this.streaming = streaming;
        this.fileStorageDirectory = fileStorageDirectory;
        this.blockingOperationScheduler = blockingOperationScheduler;
    }

    public static Flux<Part> createParts(Flux<MultipartParser.Token> tokens, int maxParts, int maxInMemorySize, long maxDiskUsagePerPart, boolean streaming, Mono<Path> fileStorageDirectory, Scheduler blockingOperationScheduler) {
        return Flux.create(sink -> {
            PartGenerator generator = new PartGenerator(sink, maxParts, maxInMemorySize, maxDiskUsagePerPart, streaming, fileStorageDirectory, blockingOperationScheduler);
            generator.getClass();
            sink.onCancel(generator::onSinkCancel);
            sink.onRequest(l -> {
                generator.requestToken();
            });
            tokens.subscribe(generator);
        });
    }

    public Context currentContext() {
        return Context.of(this.sink.contextView());
    }

    protected void hookOnSubscribe(Subscription subscription) {
        requestToken();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hookOnNext(MultipartParser.Token token) {
        this.requestOutstanding.set(false);
        State state = this.state.get();
        if (token instanceof MultipartParser.HeadersToken) {
            state.partComplete(false);
            if (tooManyParts()) {
                return;
            }
            newPart(state, token.headers());
            return;
        }
        state.body(token.buffer());
    }

    private void newPart(State currentState, HttpHeaders headers) {
        if (isFormField(headers)) {
            changeStateInternal(new FormFieldState(headers));
            requestToken();
        } else if (!this.streaming) {
            changeStateInternal(new InMemoryState(headers));
            requestToken();
        } else {
            Flux<DataBuffer> streamingContent = Flux.create(contentSink -> {
                State newState = new StreamingState(contentSink);
                if (changeState(currentState, newState)) {
                    contentSink.onRequest(l -> {
                        requestToken();
                    });
                    requestToken();
                }
            });
            emitPart(DefaultParts.part(headers, streamingContent));
        }
    }

    protected void hookOnComplete() {
        this.state.get().partComplete(true);
    }

    protected void hookOnError(Throwable throwable) {
        this.state.get().error(throwable);
        changeStateInternal(DisposedState.INSTANCE);
        this.sink.error(throwable);
    }

    private void onSinkCancel() {
        changeStateInternal(DisposedState.INSTANCE);
        cancel();
    }

    boolean changeState(State oldState, State newState) {
        if (this.state.compareAndSet(oldState, newState)) {
            if (logger.isTraceEnabled()) {
                logger.trace("Changed state: " + oldState + " -> " + newState);
            }
            oldState.dispose();
            return true;
        }
        logger.warn("Could not switch from " + oldState + " to " + newState + "; current state:" + this.state.get());
        return false;
    }

    private void changeStateInternal(State newState) {
        if (this.state.get() == DisposedState.INSTANCE) {
            return;
        }
        State oldState = this.state.getAndSet(newState);
        if (logger.isTraceEnabled()) {
            logger.trace("Changed state: " + oldState + " -> " + newState);
        }
        oldState.dispose();
    }

    void emitPart(Part part) {
        if (logger.isTraceEnabled()) {
            logger.trace("Emitting: " + part);
        }
        this.sink.next(part);
    }

    void emitComplete() {
        this.sink.complete();
    }

    void emitError(Throwable t) {
        cancel();
        this.sink.error(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestToken() {
        if (upstream() != null && !this.sink.isCancelled() && this.sink.requestedFromDownstream() > 0 && this.state.get().canRequest() && this.requestOutstanding.compareAndSet(false, true)) {
            request(1L);
        }
    }

    private boolean tooManyParts() {
        int count = this.partCount.incrementAndGet();
        if (this.maxParts > 0 && count > this.maxParts) {
            emitError(new DecodingException("Too many parts (" + count + "/" + this.maxParts + " allowed)"));
            return true;
        }
        return false;
    }

    private static boolean isFormField(HttpHeaders headers) {
        MediaType contentType = headers.getContentType();
        return (contentType == null || MediaType.TEXT_PLAIN.equalsTypeAndSubtype(contentType)) && headers.getContentDisposition().getFilename() == null;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$State.class */
    private interface State {
        void body(DataBuffer dataBuffer);

        void partComplete(boolean finalPart);

        default void error(Throwable throwable) {
        }

        default boolean canRequest() {
            return true;
        }

        default void dispose() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$InitialState.class */
    private final class InitialState implements State {
        private InitialState() {
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        public String toString() {
            return "INITIAL";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$FormFieldState.class */
    private final class FormFieldState implements State {
        private final FastByteArrayOutputStream value = new FastByteArrayOutputStream();
        private final HttpHeaders headers;

        public FormFieldState(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            int size = this.value.size() + dataBuffer.readableByteCount();
            if (PartGenerator.this.maxInMemorySize == -1 || size < PartGenerator.this.maxInMemorySize) {
                store(dataBuffer);
                PartGenerator.this.requestToken();
            } else {
                DataBufferUtils.release(dataBuffer);
                PartGenerator.this.emitError(new DataBufferLimitException("Form field value exceeded the memory usage limit of " + PartGenerator.this.maxInMemorySize + " bytes"));
            }
        }

        private void store(DataBuffer dataBuffer) {
            try {
                byte[] bytes = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(bytes);
                this.value.write(bytes);
            } catch (IOException ex) {
                PartGenerator.this.emitError(ex);
            } finally {
                DataBufferUtils.release(dataBuffer);
            }
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            byte[] bytes = this.value.toByteArrayUnsafe();
            String value = new String(bytes, MultipartUtils.charset(this.headers));
            PartGenerator.this.emitPart(DefaultParts.formFieldPart(this.headers, value));
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        public String toString() {
            return "FORM-FIELD";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$StreamingState.class */
    private final class StreamingState implements State {
        private final FluxSink<DataBuffer> bodySink;

        public StreamingState(FluxSink<DataBuffer> bodySink) {
            this.bodySink = bodySink;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.next(dataBuffer);
                if (this.bodySink.requestedFromDownstream() > 0) {
                    PartGenerator.this.requestToken();
                    return;
                }
                return;
            }
            DataBufferUtils.release(dataBuffer);
            PartGenerator.this.requestToken();
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.complete();
            }
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void error(Throwable throwable) {
            if (!this.bodySink.isCancelled()) {
                this.bodySink.error(throwable);
            }
        }

        public String toString() {
            return "STREAMING";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$InMemoryState.class */
    private final class InMemoryState implements State {
        private final HttpHeaders headers;
        private final AtomicLong byteCount = new AtomicLong();
        private final Queue<DataBuffer> content = new ConcurrentLinkedQueue();
        private volatile boolean releaseOnDispose = true;

        public InMemoryState(HttpHeaders headers) {
            this.headers = headers;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            long prevCount = this.byteCount.get();
            long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
            if (PartGenerator.this.maxInMemorySize == -1 || count <= PartGenerator.this.maxInMemorySize) {
                storeBuffer(dataBuffer);
            } else if (prevCount <= PartGenerator.this.maxInMemorySize) {
                switchToFile(dataBuffer, count);
            } else {
                DataBufferUtils.release(dataBuffer);
                PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
            }
        }

        private void storeBuffer(DataBuffer dataBuffer) {
            this.content.add(dataBuffer);
            PartGenerator.this.requestToken();
        }

        private void switchToFile(DataBuffer current, long byteCount) {
            List<DataBuffer> content = new ArrayList<>(this.content);
            content.add(current);
            this.releaseOnDispose = false;
            CreateFileState newState = PartGenerator.this.new CreateFileState(this.headers, content, byteCount);
            if (PartGenerator.this.changeState(this, newState)) {
                newState.createFile();
            } else {
                content.forEach(DataBufferUtils::release);
            }
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            emitMemoryPart();
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        private void emitMemoryPart() {
            byte[] bytes = new byte[(int) this.byteCount.get()];
            int idx = 0;
            for (DataBuffer buffer : this.content) {
                int len = buffer.readableByteCount();
                buffer.read(bytes, idx, len);
                idx += len;
                DataBufferUtils.release(buffer);
            }
            this.content.clear();
            Flux<DataBuffer> content = Flux.just(DefaultDataBufferFactory.sharedInstance.wrap(bytes));
            PartGenerator.this.emitPart(DefaultParts.part(this.headers, content));
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void dispose() {
            if (this.releaseOnDispose) {
                this.content.forEach(DataBufferUtils::release);
            }
        }

        public String toString() {
            return "IN-MEMORY";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$CreateFileState.class */
    private final class CreateFileState implements State {
        private final HttpHeaders headers;
        private final Collection<DataBuffer> content;
        private final long byteCount;
        private volatile boolean completed;
        private volatile boolean finalPart;
        private volatile boolean releaseOnDispose = true;

        public CreateFileState(HttpHeaders headers, Collection<DataBuffer> content, long byteCount) {
            this.headers = headers;
            this.content = content;
            this.byteCount = byteCount;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            this.completed = true;
            this.finalPart = finalPart;
        }

        public void createFile() {
            Mono monoSubscribeOn = PartGenerator.this.fileStorageDirectory.map(this::createFileState).subscribeOn(PartGenerator.this.blockingOperationScheduler);
            Consumer consumer = this::fileCreated;
            PartGenerator partGenerator = PartGenerator.this;
            monoSubscribeOn.subscribe(consumer, partGenerator::emitError);
        }

        private WritingFileState createFileState(Path directory) throws IOException {
            try {
                Path tempFile = Files.createTempFile(directory, null, ".multipart", new FileAttribute[0]);
                if (PartGenerator.logger.isTraceEnabled()) {
                    PartGenerator.logger.trace("Storing multipart data in file " + tempFile);
                }
                WritableByteChannel channel = Files.newByteChannel(tempFile, StandardOpenOption.WRITE);
                return PartGenerator.this.new WritingFileState(this, tempFile, channel);
            } catch (IOException ex) {
                throw new UncheckedIOException("Could not create temp file in " + directory, ex);
            }
        }

        private void fileCreated(WritingFileState newState) throws IOException {
            this.releaseOnDispose = false;
            if (PartGenerator.this.changeState(this, newState)) {
                newState.writeBuffers(this.content);
                if (this.completed) {
                    newState.partComplete(this.finalPart);
                    return;
                }
                return;
            }
            MultipartUtils.closeChannel(newState.channel);
            MultipartUtils.deleteFile(newState.file);
            this.content.forEach(DataBufferUtils::release);
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void dispose() {
            if (this.releaseOnDispose) {
                this.content.forEach(DataBufferUtils::release);
            }
        }

        public String toString() {
            return "CREATE-FILE";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$IdleFileState.class */
    private final class IdleFileState implements State {
        private final HttpHeaders headers;
        private final Path file;
        private final WritableByteChannel channel;
        private final AtomicLong byteCount;
        private volatile boolean closeOnDispose = true;
        private volatile boolean deleteOnDispose = true;

        public IdleFileState(WritingFileState state) {
            this.headers = state.headers;
            this.file = state.file;
            this.channel = state.channel;
            this.byteCount = state.byteCount;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) throws IOException {
            long count = this.byteCount.addAndGet(dataBuffer.readableByteCount());
            if (PartGenerator.this.maxDiskUsagePerPart == -1 || count <= PartGenerator.this.maxDiskUsagePerPart) {
                this.closeOnDispose = false;
                this.deleteOnDispose = false;
                WritingFileState newState = PartGenerator.this.new WritingFileState(this);
                if (PartGenerator.this.changeState(this, newState)) {
                    newState.writeBuffer(dataBuffer);
                    return;
                }
                MultipartUtils.closeChannel(this.channel);
                MultipartUtils.deleteFile(this.file);
                DataBufferUtils.release(dataBuffer);
                return;
            }
            MultipartUtils.closeChannel(this.channel);
            MultipartUtils.deleteFile(this.file);
            DataBufferUtils.release(dataBuffer);
            PartGenerator.this.emitError(new DataBufferLimitException("Part exceeded the disk usage limit of " + PartGenerator.this.maxDiskUsagePerPart + " bytes"));
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) throws IOException {
            MultipartUtils.closeChannel(this.channel);
            this.deleteOnDispose = false;
            PartGenerator.this.emitPart(DefaultParts.part(this.headers, this.file, PartGenerator.this.blockingOperationScheduler));
            if (finalPart) {
                PartGenerator.this.emitComplete();
            }
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void dispose() throws IOException {
            if (this.closeOnDispose) {
                MultipartUtils.closeChannel(this.channel);
            }
            if (this.deleteOnDispose) {
                MultipartUtils.deleteFile(this.file);
            }
        }

        public String toString() {
            return "IDLE-FILE";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$WritingFileState.class */
    private final class WritingFileState implements State {
        private final HttpHeaders headers;
        private final Path file;
        private final WritableByteChannel channel;
        private final AtomicLong byteCount;
        private volatile boolean completed;
        private volatile boolean finalPart;
        private volatile boolean disposed;

        public WritingFileState(CreateFileState state, Path file, WritableByteChannel channel) {
            this.headers = state.headers;
            this.file = file;
            this.channel = channel;
            this.byteCount = new AtomicLong(state.byteCount);
        }

        public WritingFileState(IdleFileState state) {
            this.headers = state.headers;
            this.file = state.file;
            this.channel = state.channel;
            this.byteCount = state.byteCount;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
            PartGenerator.this.emitError(new IllegalStateException("Body token not expected"));
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
            State state = (State) PartGenerator.this.state.get();
            if (state != this) {
                state.partComplete(finalPart);
            } else {
                this.completed = true;
                this.finalPart = finalPart;
            }
        }

        public void writeBuffer(DataBuffer dataBuffer) {
            Mono monoSubscribeOn = Mono.just(dataBuffer).flatMap(this::writeInternal).subscribeOn(PartGenerator.this.blockingOperationScheduler);
            PartGenerator partGenerator = PartGenerator.this;
            monoSubscribeOn.subscribe((Consumer) null, partGenerator::emitError, this::writeComplete);
        }

        public void writeBuffers(Iterable<DataBuffer> dataBuffers) {
            Mono monoSubscribeOn = Flux.fromIterable(dataBuffers).concatMap(this::writeInternal).then().subscribeOn(PartGenerator.this.blockingOperationScheduler);
            PartGenerator partGenerator = PartGenerator.this;
            monoSubscribeOn.subscribe((Consumer) null, partGenerator::emitError, this::writeComplete);
        }

        private void writeComplete() throws IOException {
            IdleFileState newState = PartGenerator.this.new IdleFileState(this);
            if (this.disposed) {
                newState.dispose();
                return;
            }
            if (PartGenerator.this.changeState(this, newState)) {
                if (this.completed) {
                    newState.partComplete(this.finalPart);
                    return;
                } else {
                    PartGenerator.this.requestToken();
                    return;
                }
            }
            MultipartUtils.closeChannel(this.channel);
            MultipartUtils.deleteFile(this.file);
        }

        private Mono<Void> writeInternal(DataBuffer dataBuffer) {
            try {
                try {
                    ByteBuffer byteBuffer = dataBuffer.asByteBuffer();
                    while (byteBuffer.hasRemaining()) {
                        this.channel.write(byteBuffer);
                    }
                    Mono<Void> monoEmpty = Mono.empty();
                    DataBufferUtils.release(dataBuffer);
                    return monoEmpty;
                } catch (IOException ex) {
                    MultipartUtils.closeChannel(this.channel);
                    MultipartUtils.deleteFile(this.file);
                    Mono<Void> monoError = Mono.error(ex);
                    DataBufferUtils.release(dataBuffer);
                    return monoError;
                }
            } catch (Throwable th) {
                DataBufferUtils.release(dataBuffer);
                throw th;
            }
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public boolean canRequest() {
            return false;
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void dispose() {
            this.disposed = true;
        }

        public String toString() {
            return "WRITE-FILE";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/PartGenerator$DisposedState.class */
    private static final class DisposedState implements State {
        public static final DisposedState INSTANCE = new DisposedState();

        private DisposedState() {
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void body(DataBuffer dataBuffer) {
            DataBufferUtils.release(dataBuffer);
        }

        @Override // org.springframework.http.codec.multipart.PartGenerator.State
        public void partComplete(boolean finalPart) {
        }

        public String toString() {
            return "DISPOSED";
        }
    }
}
