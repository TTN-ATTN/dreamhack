package org.springframework.http.codec.multipart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.CodecException;
import org.springframework.core.codec.Hints;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/MultipartHttpMessageWriter.class */
public class MultipartHttpMessageWriter extends MultipartWriterSupport implements HttpMessageWriter<MultiValueMap<String, ?>> {
    private static final Map<String, Object> DEFAULT_HINTS = Hints.from(Hints.SUPPRESS_LOGGING_HINT, true);
    private final List<HttpMessageWriter<?>> partWriters;

    @Nullable
    private final HttpMessageWriter<MultiValueMap<String, String>> formWriter;

    public MultipartHttpMessageWriter() {
        this(Arrays.asList(new EncoderHttpMessageWriter(CharSequenceEncoder.textPlainOnly()), new ResourceHttpMessageWriter()));
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters) {
        this(partWriters, new FormHttpMessageWriter());
    }

    public MultipartHttpMessageWriter(List<HttpMessageWriter<?>> partWriters, @Nullable HttpMessageWriter<MultiValueMap<String, String>> formWriter) {
        super(initMediaTypes(formWriter));
        this.partWriters = partWriters;
        this.formWriter = formWriter;
    }

    private static List<MediaType> initMediaTypes(@Nullable HttpMessageWriter<?> formWriter) {
        List<MediaType> result = new ArrayList<>(MultipartHttpMessageReader.MIME_TYPES);
        if (formWriter != null) {
            result.addAll(formWriter.getWritableMediaTypes());
        }
        return Collections.unmodifiableList(result);
    }

    public List<HttpMessageWriter<?>> getPartWriters() {
        return Collections.unmodifiableList(this.partWriters);
    }

    @Nullable
    public HttpMessageWriter<MultiValueMap<String, String>> getFormWriter() {
        return this.formWriter;
    }

    @Override // org.springframework.http.codec.HttpMessageWriter
    public Mono<Void> write(Publisher<? extends MultiValueMap<String, ?>> inputStream, ResolvableType elementType, @Nullable MediaType mediaType, ReactiveHttpOutputMessage outputMessage, Map<String, Object> hints) {
        return Mono.from(inputStream).flatMap(map -> {
            if (this.formWriter == null || isMultipart(map, mediaType)) {
                return writeMultipart(map, outputMessage, mediaType, hints);
            }
            return this.formWriter.write(Mono.just(map), elementType, mediaType, outputMessage, hints);
        });
    }

    private boolean isMultipart(MultiValueMap<String, ?> map, @Nullable MediaType contentType) {
        if (contentType != null) {
            return contentType.getType().equalsIgnoreCase("multipart");
        }
        Iterator<?> it = map.values().iterator();
        while (it.hasNext()) {
            List<?> values = (List) it.next();
            for (Object value : values) {
                if (value != null && !(value instanceof String)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Mono<Void> writeMultipart(MultiValueMap<String, ?> map, ReactiveHttpOutputMessage outputMessage, @Nullable MediaType mediaType, Map<String, Object> hints) {
        byte[] boundary = generateMultipartBoundary();
        outputMessage.getHeaders().setContentType(getMultipartMediaType(mediaType, boundary));
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String value;
            StringBuilder sbAppend = new StringBuilder().append(Hints.getLogPrefix(hints)).append("Encoding ");
            if (isEnableLoggingRequestDetails()) {
                value = LogFormatUtils.formatValue(map, !traceOn.booleanValue());
            } else {
                value = "parts " + map.keySet() + " (content masked)";
            }
            return sbAppend.append(value).toString();
        });
        DataBufferFactory bufferFactory = outputMessage.bufferFactory();
        Flux<DataBuffer> body = Flux.fromIterable(map.entrySet()).concatMap(entry -> {
            return encodePartValues(boundary, (String) entry.getKey(), (List) entry.getValue(), bufferFactory);
        }).concatWith(generateLastLine(boundary, bufferFactory)).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            DataBufferUtils.release(v0);
        });
        if (this.logger.isDebugEnabled()) {
            body = body.doOnNext(buffer -> {
                Hints.touchDataBuffer(buffer, hints, this.logger);
            });
        }
        return outputMessage.writeWith(body);
    }

    private Flux<DataBuffer> encodePartValues(byte[] boundary, String name, List<?> values, DataBufferFactory bufferFactory) {
        return Flux.fromIterable(values).concatMap(value -> {
            return encodePart(boundary, name, value, bufferFactory);
        });
    }

    /* JADX WARN: Multi-variable type inference failed */
    private <T> Flux<DataBuffer> encodePart(byte[] boundary, String name, T value, DataBufferFactory factory) {
        Object objDoOnNext;
        MultipartHttpOutputMessage message = new MultipartHttpOutputMessage(factory);
        HttpHeaders headers = message.getHeaders();
        ResolvableType resolvableType = null;
        if (value instanceof HttpEntity) {
            HttpEntity httpEntity = (HttpEntity) value;
            headers.putAll(httpEntity.getHeaders());
            objDoOnNext = httpEntity.getBody();
            Assert.state(objDoOnNext != null, "MultipartHttpMessageWriter only supports HttpEntity with body");
            if (httpEntity instanceof ResolvableTypeProvider) {
                resolvableType = ((ResolvableTypeProvider) httpEntity).getResolvableType();
            }
        } else {
            objDoOnNext = value;
        }
        if (resolvableType == null) {
            resolvableType = ResolvableType.forClass(objDoOnNext.getClass());
        }
        if (!headers.containsKey(HttpHeaders.CONTENT_DISPOSITION)) {
            if (objDoOnNext instanceof Resource) {
                headers.setContentDispositionFormData(name, ((Resource) objDoOnNext).getFilename());
            } else if (resolvableType.resolve() == Resource.class) {
                objDoOnNext = Mono.from((Publisher) objDoOnNext).doOnNext(o -> {
                    headers.setContentDispositionFormData(name, ((Resource) o).getFilename());
                });
            } else {
                headers.setContentDispositionFormData(name, null);
            }
        }
        MediaType contentType = headers.getContentType();
        ResolvableType finalBodyType = resolvableType;
        Optional<HttpMessageWriter<?>> writer = this.partWriters.stream().filter(partWriter -> {
            return partWriter.canWrite(finalBodyType, contentType);
        }).findFirst();
        if (!writer.isPresent()) {
            return Flux.error(new CodecException("No suitable writer found for part: " + name));
        }
        Mono<Void> partContentReady = writer.get().write(objDoOnNext instanceof Publisher ? (Publisher) objDoOnNext : Mono.just(objDoOnNext), resolvableType, contentType, message, DEFAULT_HINTS);
        message.getClass();
        return Flux.concat(new Publisher[]{generateBoundaryLine(boundary, factory), partContentReady.thenMany(Flux.defer(message::getBody)), generateNewLine(factory)});
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/MultipartHttpMessageWriter$MultipartHttpOutputMessage.class */
    private class MultipartHttpOutputMessage implements ReactiveHttpOutputMessage {
        private final DataBufferFactory bufferFactory;
        private final HttpHeaders headers = new HttpHeaders();
        private final AtomicBoolean committed = new AtomicBoolean();

        @Nullable
        private Flux<DataBuffer> body;

        public MultipartHttpOutputMessage(DataBufferFactory bufferFactory) {
            this.bufferFactory = bufferFactory;
        }

        @Override // org.springframework.http.HttpMessage
        public HttpHeaders getHeaders() {
            return this.body != null ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public DataBufferFactory bufferFactory() {
            return this.bufferFactory;
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public void beforeCommit(Supplier<? extends Mono<Void>> action) {
            this.committed.set(true);
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public boolean isCommitted() {
            return this.committed.get();
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
            if (this.body != null) {
                return Mono.error(new IllegalStateException("Multiple calls to writeWith() not supported"));
            }
            this.body = MultipartHttpMessageWriter.this.generatePartHeaders(this.headers, this.bufferFactory).concatWith(body);
            return Mono.empty();
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
            return Mono.error(new UnsupportedOperationException());
        }

        public Flux<DataBuffer> getBody() {
            return this.body != null ? this.body : Flux.error(new IllegalStateException("Body has not been written yet"));
        }

        @Override // org.springframework.http.ReactiveHttpOutputMessage
        public Mono<Void> setComplete() {
            return Mono.error(new UnsupportedOperationException());
        }
    }
}
