package org.springframework.http.codec.support;

import java.util.List;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.PartHttpMessageWriter;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/support/ServerDefaultCodecsImpl.class */
class ServerDefaultCodecsImpl extends BaseDefaultCodecs implements ServerCodecConfigurer.ServerDefaultCodecs {

    @Nullable
    private HttpMessageReader<?> multipartReader;

    @Nullable
    private Encoder<?> sseEncoder;

    ServerDefaultCodecsImpl() {
    }

    ServerDefaultCodecsImpl(ServerDefaultCodecsImpl other) {
        super(other);
        this.multipartReader = other.multipartReader;
        this.sseEncoder = other.sseEncoder;
    }

    @Override // org.springframework.http.codec.ServerCodecConfigurer.ServerDefaultCodecs
    public void multipartReader(HttpMessageReader<?> reader) {
        this.multipartReader = reader;
        initTypedReaders();
    }

    @Override // org.springframework.http.codec.ServerCodecConfigurer.ServerDefaultCodecs
    public void serverSentEventEncoder(Encoder<?> encoder) {
        this.sseEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
        if (this.multipartReader != null) {
            addCodec(typedReaders, this.multipartReader);
            return;
        }
        DefaultPartHttpMessageReader partReader = new DefaultPartHttpMessageReader();
        addCodec(typedReaders, partReader);
        addCodec(typedReaders, new MultipartHttpMessageReader(partReader));
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
        addCodec(typedWriters, new PartHttpMessageWriter());
    }

    @Override // org.springframework.http.codec.support.BaseDefaultCodecs
    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
        objectWriters.add(new ServerSentEventHttpMessageWriter(getSseEncoder()));
    }

    @Nullable
    private Encoder<?> getSseEncoder() {
        if (this.sseEncoder != null) {
            return this.sseEncoder;
        }
        if (jackson2Present) {
            return getJackson2JsonEncoder();
        }
        if (kotlinSerializationJsonPresent) {
            return getKotlinSerializationJsonEncoder();
        }
        return null;
    }
}
