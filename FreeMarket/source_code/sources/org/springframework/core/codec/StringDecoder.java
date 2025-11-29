package org.springframework.core.codec;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.LimitedDataBufferList;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/codec/StringDecoder.class */
public final class StringDecoder extends AbstractDataBufferDecoder<String> {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final List<String> DEFAULT_DELIMITERS = Arrays.asList("\r\n", "\n");
    private final List<String> delimiters;
    private final boolean stripDelimiter;
    private Charset defaultCharset;
    private final ConcurrentMap<Charset, byte[][]> delimitersCache;

    @Override // org.springframework.core.codec.Decoder
    public /* bridge */ /* synthetic */ Object decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map hints) throws DecodingException {
        return decode(dataBuffer, elementType, mimeType, (Map<String, Object>) hints);
    }

    private StringDecoder(List<String> delimiters, boolean stripDelimiter, MimeType... mimeTypes) {
        super(mimeTypes);
        this.defaultCharset = DEFAULT_CHARSET;
        this.delimitersCache = new ConcurrentHashMap();
        Assert.notEmpty(delimiters, "'delimiters' must not be empty");
        this.delimiters = new ArrayList(delimiters);
        this.stripDelimiter = stripDelimiter;
    }

    public void setDefaultCharset(Charset defaultCharset) {
        this.defaultCharset = defaultCharset;
    }

    public Charset getDefaultCharset() {
        return this.defaultCharset;
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return elementType.resolve() == String.class && super.canDecode(elementType, mimeType);
    }

    @Override // org.springframework.core.codec.AbstractDataBufferDecoder, org.springframework.core.codec.Decoder
    public Flux<String> decode(Publisher<DataBuffer> input, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        byte[][] delimiterBytes = getDelimiterBytes(mimeType);
        LimitedDataBufferList chunks = new LimitedDataBufferList(getMaxInMemorySize());
        DataBufferUtils.Matcher matcher = DataBufferUtils.matcher(delimiterBytes);
        Flux fluxConcatWith = Flux.from(input).concatMapIterable(buffer -> {
            return processDataBuffer(buffer, matcher, chunks);
        }).concatWith(Mono.defer(() -> {
            if (chunks.isEmpty()) {
                return Mono.empty();
            }
            DataBuffer lastBuffer = chunks.get(0).factory().join(chunks);
            chunks.clear();
            return Mono.just(lastBuffer);
        }));
        chunks.getClass();
        return fluxConcatWith.doOnTerminate(chunks::releaseAndClear).doOnDiscard(PooledDataBuffer.class, (v0) -> {
            v0.release();
        }).map(buffer2 -> {
            return decode(buffer2, elementType, mimeType, (Map<String, Object>) hints);
        });
    }

    private byte[][] getDelimiterBytes(@Nullable MimeType mimeType) {
        return this.delimitersCache.computeIfAbsent(getCharset(mimeType), charset -> {
            ?? r0 = new byte[this.delimiters.size()];
            for (int i = 0; i < this.delimiters.size(); i++) {
                r0[i] = this.delimiters.get(i).getBytes(charset);
            }
            return r0;
        });
    }

    private Collection<DataBuffer> processDataBuffer(DataBuffer buffer, DataBufferUtils.Matcher matcher, LimitedDataBufferList chunks) {
        List<DataBuffer> result = null;
        while (true) {
            try {
                int endIndex = matcher.match(buffer);
                if (endIndex == -1) {
                    chunks.add(buffer);
                    DataBufferUtils.retain(buffer);
                    break;
                }
                int startIndex = buffer.readPosition();
                int length = (endIndex - startIndex) + 1;
                DataBuffer slice = buffer.retainedSlice(startIndex, length);
                result = result != null ? result : new ArrayList<>();
                if (chunks.isEmpty()) {
                    if (this.stripDelimiter) {
                        slice.writePosition(slice.writePosition() - matcher.delimiter().length);
                    }
                    result.add(slice);
                } else {
                    chunks.add(slice);
                    DataBuffer joined = buffer.factory().join(chunks);
                    if (this.stripDelimiter) {
                        joined.writePosition(joined.writePosition() - matcher.delimiter().length);
                    }
                    result.add(joined);
                    chunks.clear();
                }
                buffer.readPosition(endIndex + 1);
                if (buffer.readableByteCount() <= 0) {
                    break;
                }
            } finally {
                DataBufferUtils.release(buffer);
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    @Override // org.springframework.core.codec.Decoder
    public String decode(DataBuffer dataBuffer, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        Charset charset = getCharset(mimeType);
        CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
        DataBufferUtils.release(dataBuffer);
        String value = charBuffer.toString();
        LogFormatUtils.traceDebug(this.logger, traceOn -> {
            String formatted = LogFormatUtils.formatValue(value, !traceOn.booleanValue());
            return Hints.getLogPrefix(hints) + "Decoded " + formatted;
        });
        return value;
    }

    private Charset getCharset(@Nullable MimeType mimeType) {
        if (mimeType != null && mimeType.getCharset() != null) {
            return mimeType.getCharset();
        }
        return getDefaultCharset();
    }

    @Deprecated
    public static StringDecoder textPlainOnly(boolean stripDelimiter) {
        return textPlainOnly();
    }

    public static StringDecoder textPlainOnly() {
        return textPlainOnly(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder textPlainOnly(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET));
    }

    @Deprecated
    public static StringDecoder allMimeTypes(boolean stripDelimiter) {
        return allMimeTypes();
    }

    public static StringDecoder allMimeTypes() {
        return allMimeTypes(DEFAULT_DELIMITERS, true);
    }

    public static StringDecoder allMimeTypes(List<String> delimiters, boolean stripDelimiter) {
        return new StringDecoder(delimiters, stripDelimiter, new MimeType("text", "plain", DEFAULT_CHARSET), MimeTypeUtils.ALL);
    }
}
