package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/support/ClientDefaultCodecsImpl.class */
class ClientDefaultCodecsImpl extends BaseDefaultCodecs implements ClientCodecConfigurer.ClientDefaultCodecs {

    @Nullable
    private DefaultMultipartCodecs multipartCodecs;

    @Nullable
    private Decoder<?> sseDecoder;

    @Nullable
    private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;

    ClientDefaultCodecsImpl() {
    }

    ClientDefaultCodecsImpl(ClientDefaultCodecsImpl other) {
        super(other);
        this.multipartCodecs = other.multipartCodecs != null ? new DefaultMultipartCodecs(other.multipartCodecs) : null;
        this.sseDecoder = other.sseDecoder;
    }

    void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
        this.partWritersSupplier = supplier;
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.ClientCodecConfigurer.ClientDefaultCodecs
    public ClientCodecConfigurer.MultipartCodecs multipartCodecs() {
        if (this.multipartCodecs == null) {
            this.multipartCodecs = new DefaultMultipartCodecs();
        }
        return this.multipartCodecs;
    }

    @Override // org.springframework.http.codec.ClientCodecConfigurer.ClientDefaultCodecs
    public void serverSentEventDecoder(Decoder<?> decoder) {
        this.sseDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
        Decoder<?> kotlinSerializationJsonDecoder;
        if (this.sseDecoder != null) {
            kotlinSerializationJsonDecoder = this.sseDecoder;
        } else if (jackson2Present) {
            kotlinSerializationJsonDecoder = getJackson2JsonDecoder();
        } else {
            kotlinSerializationJsonDecoder = kotlinSerializationJsonPresent ? getKotlinSerializationJsonDecoder() : null;
        }
        Decoder<?> decoder = kotlinSerializationJsonDecoder;
        addCodec(objectReaders, new ServerSentEventHttpMessageReader(decoder));
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        addCodec(typedWriters, new MultipartHttpMessageWriter(getPartWriters(), new FormHttpMessageWriter()));
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        if (this.multipartCodecs != null) {
            return this.multipartCodecs.getWriters();
        }
        if (this.partWritersSupplier != null) {
            return this.partWritersSupplier.get();
        }
        return Collections.emptyList();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/support/ClientDefaultCodecsImpl$DefaultMultipartCodecs.class */
    private class DefaultMultipartCodecs implements ClientCodecConfigurer.MultipartCodecs {
        private final List<HttpMessageWriter<?>> writers = new ArrayList();

        DefaultMultipartCodecs() {
        }

        DefaultMultipartCodecs(DefaultMultipartCodecs other) {
            this.writers.addAll(other.writers);
        }

        @Override // org.springframework.http.codec.ClientCodecConfigurer.MultipartCodecs
        public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
            writer(new EncoderHttpMessageWriter(encoder));
            ClientDefaultCodecsImpl.this.initTypedWriters();
            return this;
        }

        @Override // org.springframework.http.codec.ClientCodecConfigurer.MultipartCodecs
        public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
            this.writers.add(writer);
            ClientDefaultCodecsImpl.this.initTypedWriters();
            return this;
        }

        List<HttpMessageWriter<?>> getWriters() {
            return this.writers;
        }
    }
}
