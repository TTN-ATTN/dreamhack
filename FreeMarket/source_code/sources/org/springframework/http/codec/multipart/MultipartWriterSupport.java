package org.springframework.http.codec.multipart;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tomcat.websocket.BasicAuthenticator;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.codec.LoggingCodecSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/multipart/MultipartWriterSupport.class */
public class MultipartWriterSupport extends LoggingCodecSupport {
    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final List<MediaType> supportedMediaTypes;
    private Charset charset = DEFAULT_CHARSET;

    protected MultipartWriterSupport(List<MediaType> supportedMediaTypes) {
        this.supportedMediaTypes = supportedMediaTypes;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public void setCharset(Charset charset) {
        Assert.notNull(charset, "Charset must not be null");
        this.charset = charset;
    }

    public List<MediaType> getWritableMediaTypes() {
        return this.supportedMediaTypes;
    }

    public boolean canWrite(ResolvableType elementType, @Nullable MediaType mediaType) {
        if (MultiValueMap.class.isAssignableFrom(elementType.toClass())) {
            if (mediaType == null) {
                return true;
            }
            for (MediaType supportedMediaType : this.supportedMediaTypes) {
                if (supportedMediaType.isCompatibleWith(mediaType)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    protected byte[] generateMultipartBoundary() {
        return MimeTypeUtils.generateMultipartBoundary();
    }

    protected MediaType getMultipartMediaType(@Nullable MediaType mediaType, byte[] boundary) {
        Map<String, String> params = new HashMap<>();
        if (mediaType != null) {
            params.putAll(mediaType.getParameters());
        }
        params.put("boundary", new String(boundary, StandardCharsets.US_ASCII));
        Charset charset = getCharset();
        if (!charset.equals(StandardCharsets.UTF_8) && !charset.equals(StandardCharsets.US_ASCII)) {
            params.put(BasicAuthenticator.charsetparam, charset.name());
        }
        return new MediaType(mediaType != null ? mediaType : MediaType.MULTIPART_FORM_DATA, params);
    }

    protected Mono<DataBuffer> generateBoundaryLine(byte[] boundary, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 4);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write(boundary);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    protected Mono<DataBuffer> generateNewLine(DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(2);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    protected Mono<DataBuffer> generateLastLine(byte[] boundary, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer(boundary.length + 6);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write(boundary);
            buffer.write((byte) 45);
            buffer.write((byte) 45);
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }

    protected Mono<DataBuffer> generatePartHeaders(HttpHeaders headers, DataBufferFactory bufferFactory) {
        return Mono.fromCallable(() -> {
            DataBuffer buffer = bufferFactory.allocateBuffer();
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                byte[] headerName = entry.getKey().getBytes(getCharset());
                for (String headerValueString : entry.getValue()) {
                    byte[] headerValue = headerValueString.getBytes(getCharset());
                    buffer.write(headerName);
                    buffer.write((byte) 58);
                    buffer.write((byte) 32);
                    buffer.write(headerValue);
                    buffer.write((byte) 13);
                    buffer.write((byte) 10);
                }
            }
            buffer.write((byte) 13);
            buffer.write((byte) 10);
            return buffer;
        });
    }
}
