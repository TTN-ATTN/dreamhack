package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.springframework.core.SpringProperties;
import org.springframework.core.codec.AbstractDataBufferDecoder;
import org.springframework.core.codec.ByteArrayDecoder;
import org.springframework.core.codec.ByteArrayEncoder;
import org.springframework.core.codec.ByteBufferDecoder;
import org.springframework.core.codec.ByteBufferEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.DataBufferDecoder;
import org.springframework.core.codec.DataBufferEncoder;
import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.core.codec.NettyByteBufDecoder;
import org.springframework.core.codec.NettyByteBufEncoder;
import org.springframework.core.codec.ResourceDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.DecoderHttpMessageReader;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageReader;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ResourceHttpMessageReader;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.json.AbstractJackson2Decoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.codec.json.Jackson2SmileDecoder;
import org.springframework.http.codec.json.Jackson2SmileEncoder;
import org.springframework.http.codec.json.KotlinSerializationJsonDecoder;
import org.springframework.http.codec.json.KotlinSerializationJsonEncoder;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.http.codec.protobuf.ProtobufDecoder;
import org.springframework.http.codec.protobuf.ProtobufEncoder;
import org.springframework.http.codec.protobuf.ProtobufHttpMessageWriter;
import org.springframework.http.codec.support.BaseCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/support/BaseDefaultCodecs.class */
class BaseDefaultCodecs implements CodecConfigurer.DefaultCodecs, CodecConfigurer.DefaultCodecConfig {
    private static final boolean shouldIgnoreXml = SpringProperties.getFlag("spring.xml.ignore");
    static final boolean jackson2Present;
    private static final boolean jackson2SmilePresent;
    private static final boolean jaxb2Present;
    private static final boolean protobufPresent;
    static final boolean synchronossMultipartPresent;
    static final boolean nettyByteBufPresent;
    static final boolean kotlinSerializationJsonPresent;

    @Nullable
    private Decoder<?> jackson2JsonDecoder;

    @Nullable
    private Encoder<?> jackson2JsonEncoder;

    @Nullable
    private Encoder<?> jackson2SmileEncoder;

    @Nullable
    private Decoder<?> jackson2SmileDecoder;

    @Nullable
    private Decoder<?> protobufDecoder;

    @Nullable
    private Encoder<?> protobufEncoder;

    @Nullable
    private Decoder<?> jaxb2Decoder;

    @Nullable
    private Encoder<?> jaxb2Encoder;

    @Nullable
    private Decoder<?> kotlinSerializationJsonDecoder;

    @Nullable
    private Encoder<?> kotlinSerializationJsonEncoder;

    @Nullable
    private Consumer<Object> codecConsumer;

    @Nullable
    private Integer maxInMemorySize;

    @Nullable
    private Boolean enableLoggingRequestDetails;
    private boolean registerDefaults;
    private final List<HttpMessageReader<?>> typedReaders;
    private final List<HttpMessageReader<?>> objectReaders;
    private final List<HttpMessageWriter<?>> typedWriters;
    private final List<HttpMessageWriter<?>> objectWriters;

    static {
        ClassLoader classLoader = BaseCodecConfigurer.class.getClassLoader();
        jackson2Present = ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", classLoader) && ClassUtils.isPresent("com.fasterxml.jackson.core.JsonGenerator", classLoader);
        jackson2SmilePresent = ClassUtils.isPresent("com.fasterxml.jackson.dataformat.smile.SmileFactory", classLoader);
        jaxb2Present = ClassUtils.isPresent("javax.xml.bind.Binder", classLoader);
        protobufPresent = ClassUtils.isPresent("com.google.protobuf.Message", classLoader);
        synchronossMultipartPresent = ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser", classLoader);
        nettyByteBufPresent = ClassUtils.isPresent("io.netty.buffer.ByteBuf", classLoader);
        kotlinSerializationJsonPresent = ClassUtils.isPresent("kotlinx.serialization.json.Json", classLoader);
    }

    BaseDefaultCodecs() {
        this.registerDefaults = true;
        this.typedReaders = new ArrayList();
        this.objectReaders = new ArrayList();
        this.typedWriters = new ArrayList();
        this.objectWriters = new ArrayList();
        initReaders();
        initWriters();
    }

    protected void initReaders() {
        initTypedReaders();
        initObjectReaders();
    }

    protected void initWriters() {
        initTypedWriters();
        initObjectWriters();
    }

    protected BaseDefaultCodecs(BaseDefaultCodecs other) {
        this.registerDefaults = true;
        this.typedReaders = new ArrayList();
        this.objectReaders = new ArrayList();
        this.typedWriters = new ArrayList();
        this.objectWriters = new ArrayList();
        this.jackson2JsonDecoder = other.jackson2JsonDecoder;
        this.jackson2JsonEncoder = other.jackson2JsonEncoder;
        this.jackson2SmileDecoder = other.jackson2SmileDecoder;
        this.jackson2SmileEncoder = other.jackson2SmileEncoder;
        this.protobufDecoder = other.protobufDecoder;
        this.protobufEncoder = other.protobufEncoder;
        this.jaxb2Decoder = other.jaxb2Decoder;
        this.jaxb2Encoder = other.jaxb2Encoder;
        this.kotlinSerializationJsonDecoder = other.kotlinSerializationJsonDecoder;
        this.kotlinSerializationJsonEncoder = other.kotlinSerializationJsonEncoder;
        this.codecConsumer = other.codecConsumer;
        this.maxInMemorySize = other.maxInMemorySize;
        this.enableLoggingRequestDetails = other.enableLoggingRequestDetails;
        this.registerDefaults = other.registerDefaults;
        this.typedReaders.addAll(other.typedReaders);
        this.objectReaders.addAll(other.objectReaders);
        this.typedWriters.addAll(other.typedWriters);
        this.objectWriters.addAll(other.objectWriters);
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonDecoder(Decoder<?> decoder) {
        this.jackson2JsonDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2JsonEncoder(Encoder<?> encoder) {
        this.jackson2JsonEncoder = encoder;
        initObjectWriters();
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2SmileDecoder(Decoder<?> decoder) {
        this.jackson2SmileDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jackson2SmileEncoder(Encoder<?> encoder) {
        this.jackson2SmileEncoder = encoder;
        initObjectWriters();
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufDecoder(Decoder<?> decoder) {
        this.protobufDecoder = decoder;
        initTypedReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void protobufEncoder(Encoder<?> encoder) {
        this.protobufEncoder = encoder;
        initTypedWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jaxb2Decoder(Decoder<?> decoder) {
        this.jaxb2Decoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void jaxb2Encoder(Encoder<?> encoder) {
        this.jaxb2Encoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationJsonDecoder(Decoder<?> decoder) {
        this.kotlinSerializationJsonDecoder = decoder;
        initObjectReaders();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void kotlinSerializationJsonEncoder(Encoder<?> encoder) {
        this.kotlinSerializationJsonEncoder = encoder;
        initObjectWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void configureDefaultCodec(Consumer<Object> codecConsumer) {
        this.codecConsumer = this.codecConsumer != null ? this.codecConsumer.andThen(codecConsumer) : codecConsumer;
        initReaders();
        initWriters();
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void maxInMemorySize(int byteCount) {
        if (!ObjectUtils.nullSafeEquals(this.maxInMemorySize, Integer.valueOf(byteCount))) {
            this.maxInMemorySize = Integer.valueOf(byteCount);
            initReaders();
        }
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecConfig
    @Nullable
    public Integer maxInMemorySize() {
        return this.maxInMemorySize;
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecs
    public void enableLoggingRequestDetails(boolean enable) {
        if (!ObjectUtils.nullSafeEquals(this.enableLoggingRequestDetails, Boolean.valueOf(enable))) {
            this.enableLoggingRequestDetails = Boolean.valueOf(enable);
            initReaders();
            initWriters();
        }
    }

    @Override // org.springframework.http.codec.CodecConfigurer.DefaultCodecConfig
    @Nullable
    public Boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }

    void registerDefaults(boolean registerDefaults) {
        if (this.registerDefaults != registerDefaults) {
            this.registerDefaults = registerDefaults;
            initReaders();
            initWriters();
        }
    }

    final List<HttpMessageReader<?>> getTypedReaders() {
        return this.typedReaders;
    }

    protected void initTypedReaders() {
        this.typedReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new ByteArrayDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new ByteBufferDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(new DataBufferDecoder()));
        if (nettyByteBufPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(new NettyByteBufDecoder()));
        }
        addCodec(this.typedReaders, new ResourceHttpMessageReader(new ResourceDecoder()));
        addCodec(this.typedReaders, new DecoderHttpMessageReader(StringDecoder.textPlainOnly()));
        if (protobufPresent) {
            addCodec(this.typedReaders, new DecoderHttpMessageReader(this.protobufDecoder != null ? (ProtobufDecoder) this.protobufDecoder : new ProtobufDecoder()));
        }
        addCodec(this.typedReaders, new FormHttpMessageReader());
        extendTypedReaders(this.typedReaders);
    }

    protected <T> void addCodec(List<T> codecs, T codec) {
        initCodec(codec);
        codecs.add(codec);
    }

    private void initCodec(@Nullable Object codec) {
        if (codec instanceof DecoderHttpMessageReader) {
            codec = ((DecoderHttpMessageReader) codec).getDecoder();
        } else if (codec instanceof EncoderHttpMessageWriter) {
            codec = ((EncoderHttpMessageWriter) codec).getEncoder();
        }
        if (codec == null) {
            return;
        }
        Integer size = this.maxInMemorySize;
        if (size != null) {
            if (codec instanceof AbstractDataBufferDecoder) {
                ((AbstractDataBufferDecoder) codec).setMaxInMemorySize(size.intValue());
            }
            if (protobufPresent && (codec instanceof ProtobufDecoder)) {
                ((ProtobufDecoder) codec).setMaxMessageSize(size.intValue());
            }
            if (kotlinSerializationJsonPresent && (codec instanceof KotlinSerializationJsonDecoder)) {
                ((KotlinSerializationJsonDecoder) codec).setMaxInMemorySize(size.intValue());
            }
            if (jackson2Present && (codec instanceof AbstractJackson2Decoder)) {
                ((AbstractJackson2Decoder) codec).setMaxInMemorySize(size.intValue());
            }
            if (jaxb2Present && !shouldIgnoreXml && (codec instanceof Jaxb2XmlDecoder)) {
                ((Jaxb2XmlDecoder) codec).setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof FormHttpMessageReader) {
                ((FormHttpMessageReader) codec).setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof ServerSentEventHttpMessageReader) {
                ((ServerSentEventHttpMessageReader) codec).setMaxInMemorySize(size.intValue());
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                ((DefaultPartHttpMessageReader) codec).setMaxInMemorySize(size.intValue());
            }
            if (synchronossMultipartPresent && (codec instanceof SynchronossPartHttpMessageReader)) {
                ((SynchronossPartHttpMessageReader) codec).setMaxInMemorySize(size.intValue());
            }
        }
        Boolean enable = this.enableLoggingRequestDetails;
        if (enable != null) {
            if (codec instanceof FormHttpMessageReader) {
                ((FormHttpMessageReader) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof MultipartHttpMessageReader) {
                ((MultipartHttpMessageReader) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof DefaultPartHttpMessageReader) {
                ((DefaultPartHttpMessageReader) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (synchronossMultipartPresent && (codec instanceof SynchronossPartHttpMessageReader)) {
                ((SynchronossPartHttpMessageReader) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof FormHttpMessageWriter) {
                ((FormHttpMessageWriter) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
            if (codec instanceof MultipartHttpMessageWriter) {
                ((MultipartHttpMessageWriter) codec).setEnableLoggingRequestDetails(enable.booleanValue());
            }
        }
        if (this.codecConsumer != null) {
            this.codecConsumer.accept(codec);
        }
        if (codec instanceof MultipartHttpMessageReader) {
            initCodec(((MultipartHttpMessageReader) codec).getPartReader());
            return;
        }
        if (codec instanceof MultipartHttpMessageWriter) {
            initCodec(((MultipartHttpMessageWriter) codec).getFormWriter());
        } else if (codec instanceof ServerSentEventHttpMessageReader) {
            initCodec(((ServerSentEventHttpMessageReader) codec).getDecoder());
        } else if (codec instanceof ServerSentEventHttpMessageWriter) {
            initCodec(((ServerSentEventHttpMessageWriter) codec).getEncoder());
        }
    }

    protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
    }

    final List<HttpMessageReader<?>> getObjectReaders() {
        return this.objectReaders;
    }

    protected void initObjectReaders() {
        this.objectReaders.clear();
        if (!this.registerDefaults) {
            return;
        }
        if (kotlinSerializationJsonPresent) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(getKotlinSerializationJsonDecoder()));
        }
        if (jackson2Present) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(getJackson2JsonDecoder()));
        }
        if (jackson2SmilePresent) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(this.jackson2SmileDecoder != null ? (Jackson2SmileDecoder) this.jackson2SmileDecoder : new Jackson2SmileDecoder()));
        }
        if (jaxb2Present && !shouldIgnoreXml) {
            addCodec(this.objectReaders, new DecoderHttpMessageReader(this.jaxb2Decoder != null ? (Jaxb2XmlDecoder) this.jaxb2Decoder : new Jaxb2XmlDecoder()));
        }
        extendObjectReaders(this.objectReaders);
    }

    protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
    }

    final List<HttpMessageReader<?>> getCatchAllReaders() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        addCodec(arrayList, new DecoderHttpMessageReader(StringDecoder.allMimeTypes()));
        return arrayList;
    }

    final List<HttpMessageWriter<?>> getTypedWriters() {
        return this.typedWriters;
    }

    protected void initTypedWriters() {
        this.typedWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.typedWriters.addAll(getBaseTypedWriters());
        extendTypedWriters(this.typedWriters);
    }

    final List<HttpMessageWriter<?>> getBaseTypedWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        addCodec(arrayList, new EncoderHttpMessageWriter(new ByteArrayEncoder()));
        addCodec(arrayList, new EncoderHttpMessageWriter(new ByteBufferEncoder()));
        addCodec(arrayList, new EncoderHttpMessageWriter(new DataBufferEncoder()));
        if (nettyByteBufPresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(new NettyByteBufEncoder()));
        }
        addCodec(arrayList, new ResourceHttpMessageWriter());
        addCodec(arrayList, new EncoderHttpMessageWriter(CharSequenceEncoder.textPlainOnly()));
        if (protobufPresent) {
            addCodec(arrayList, new ProtobufHttpMessageWriter(this.protobufEncoder != null ? (ProtobufEncoder) this.protobufEncoder : new ProtobufEncoder()));
        }
        return arrayList;
    }

    protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {
    }

    final List<HttpMessageWriter<?>> getObjectWriters() {
        return this.objectWriters;
    }

    protected void initObjectWriters() {
        this.objectWriters.clear();
        if (!this.registerDefaults) {
            return;
        }
        this.objectWriters.addAll(getBaseObjectWriters());
        extendObjectWriters(this.objectWriters);
    }

    final List<HttpMessageWriter<?>> getBaseObjectWriters() {
        ArrayList arrayList = new ArrayList();
        if (kotlinSerializationJsonPresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(getKotlinSerializationJsonEncoder()));
        }
        if (jackson2Present) {
            addCodec(arrayList, new EncoderHttpMessageWriter(getJackson2JsonEncoder()));
        }
        if (jackson2SmilePresent) {
            addCodec(arrayList, new EncoderHttpMessageWriter(this.jackson2SmileEncoder != null ? (Jackson2SmileEncoder) this.jackson2SmileEncoder : new Jackson2SmileEncoder()));
        }
        if (jaxb2Present && !shouldIgnoreXml) {
            addCodec(arrayList, new EncoderHttpMessageWriter(this.jaxb2Encoder != null ? (Jaxb2XmlEncoder) this.jaxb2Encoder : new Jaxb2XmlEncoder()));
        }
        return arrayList;
    }

    protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
    }

    List<HttpMessageWriter<?>> getCatchAllWriters() {
        if (!this.registerDefaults) {
            return Collections.emptyList();
        }
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.add(new EncoderHttpMessageWriter<>(CharSequenceEncoder.allMimeTypes()));
        return result;
    }

    void applyDefaultConfig(BaseCodecConfigurer.DefaultCustomCodecs customCodecs) {
        applyDefaultConfig(customCodecs.getTypedReaders());
        applyDefaultConfig(customCodecs.getObjectReaders());
        applyDefaultConfig(customCodecs.getTypedWriters());
        applyDefaultConfig(customCodecs.getObjectWriters());
        customCodecs.getDefaultConfigConsumers().forEach(consumer -> {
            consumer.accept(this);
        });
    }

    private void applyDefaultConfig(Map<?, Boolean> readers) {
        readers.entrySet().stream().filter((v0) -> {
            return v0.getValue();
        }).map((v0) -> {
            return v0.getKey();
        }).forEach(this::initCodec);
    }

    protected Decoder<?> getJackson2JsonDecoder() {
        if (this.jackson2JsonDecoder == null) {
            this.jackson2JsonDecoder = new Jackson2JsonDecoder();
        }
        return this.jackson2JsonDecoder;
    }

    protected Encoder<?> getJackson2JsonEncoder() {
        if (this.jackson2JsonEncoder == null) {
            this.jackson2JsonEncoder = new Jackson2JsonEncoder();
        }
        return this.jackson2JsonEncoder;
    }

    protected Decoder<?> getKotlinSerializationJsonDecoder() {
        if (this.kotlinSerializationJsonDecoder == null) {
            this.kotlinSerializationJsonDecoder = new KotlinSerializationJsonDecoder();
        }
        return this.kotlinSerializationJsonDecoder;
    }

    protected Encoder<?> getKotlinSerializationJsonEncoder() {
        if (this.kotlinSerializationJsonEncoder == null) {
            this.kotlinSerializationJsonEncoder = new KotlinSerializationJsonEncoder();
        }
        return this.kotlinSerializationJsonEncoder;
    }
}
