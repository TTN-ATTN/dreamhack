package org.springframework.http.codec.protobuf;

import com.google.protobuf.Message;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageEncoder;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/protobuf/ProtobufEncoder.class */
public class ProtobufEncoder extends ProtobufCodecSupport implements HttpMessageEncoder<Message> {
    private static final List<MediaType> streamingMediaTypes = (List) MIME_TYPES.stream().map(mimeType -> {
        return new MediaType(mimeType.getType(), mimeType.getSubtype(), (Map<String, String>) Collections.singletonMap("delimited", "true"));
    }).collect(Collectors.toList());

    @Override // org.springframework.core.codec.Encoder
    public /* bridge */ /* synthetic */ DataBuffer encodeValue(Object message, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map hints) {
        return encodeValue((Message) message, bufferFactory, valueType, mimeType, (Map<String, Object>) hints);
    }

    @Override // org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        return Message.class.isAssignableFrom(elementType.toClass()) && supportsMimeType(mimeType);
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<? extends Message> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.from(inputStream).map(message -> {
            return encodeValue(message, bufferFactory, !(inputStream instanceof Mono));
        });
    }

    public DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return encodeValue(message, bufferFactory, false);
    }

    private DataBuffer encodeValue(Message message, DataBufferFactory bufferFactory, boolean delimited) {
        DataBuffer buffer = bufferFactory.allocateBuffer();
        boolean release = true;
        try {
            try {
                if (delimited) {
                    message.writeDelimitedTo(buffer.asOutputStream());
                } else {
                    message.writeTo(buffer.asOutputStream());
                }
                release = false;
                if (0 != 0) {
                    DataBufferUtils.release(buffer);
                }
                return buffer;
            } catch (IOException ex) {
                throw new IllegalStateException("Unexpected I/O error while writing to data buffer", ex);
            }
        } catch (Throwable th) {
            if (release) {
                DataBufferUtils.release(buffer);
            }
            throw th;
        }
    }

    @Override // org.springframework.http.codec.HttpMessageEncoder
    public List<MediaType> getStreamingMediaTypes() {
        return streamingMediaTypes;
    }

    @Override // org.springframework.core.codec.Encoder
    public List<MimeType> getEncodableMimeTypes() {
        return getMimeTypes();
    }
}
