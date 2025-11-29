package org.springframework.http.codec.json;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.json.Json;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractEncoder;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/json/KotlinSerializationJsonEncoder.class */
public class KotlinSerializationJsonEncoder extends AbstractEncoder<Object> {
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final Json json;
    private final CharSequenceEncoder charSequenceEncoder;

    public KotlinSerializationJsonEncoder() {
        this(Json.Default);
    }

    public KotlinSerializationJsonEncoder(Json json) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.charSequenceEncoder = CharSequenceEncoder.allMimeTypes();
        this.json = json;
    }

    @Override // org.springframework.core.codec.AbstractEncoder, org.springframework.core.codec.Encoder
    public boolean canEncode(ResolvableType elementType, @Nullable MimeType mimeType) {
        try {
            serializer(elementType.getType());
            if (super.canEncode(elementType, mimeType) && !String.class.isAssignableFrom(elementType.toClass())) {
                if (!ServerSentEvent.class.isAssignableFrom(elementType.toClass())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.springframework.core.codec.Encoder
    public Flux<DataBuffer> encode(Publisher<?> inputStream, DataBufferFactory bufferFactory, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        if (inputStream instanceof Mono) {
            return Mono.from(inputStream).map(value -> {
                return encodeValue(value, bufferFactory, elementType, mimeType, hints);
            }).flux();
        }
        ResolvableType listType = ResolvableType.forClassWithGenerics((Class<?>) List.class, elementType);
        return Flux.from(inputStream).collectList().map(list -> {
            return encodeValue(list, bufferFactory, listType, mimeType, hints);
        }).flux();
    }

    @Override // org.springframework.core.codec.Encoder
    public DataBuffer encodeValue(Object value, DataBufferFactory bufferFactory, ResolvableType valueType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        String json = this.json.encodeToString(serializer(valueType.getType()), value);
        return this.charSequenceEncoder.encodeValue((CharSequence) json, bufferFactory, valueType, mimeType, (Map<String, Object>) null);
    }

    private KSerializer<Object> serializer(Type type) {
        KSerializer<Object> serializer = serializerCache.get(type);
        if (serializer == null) {
            serializer = SerializersKt.serializer(type);
            if (hasPolymorphism(serializer.getDescriptor(), new HashSet())) {
                throw new UnsupportedOperationException("Open polymorphic serialization is not supported yet");
            }
            serializerCache.put(type, serializer);
        }
        return serializer;
    }

    private boolean hasPolymorphism(SerialDescriptor descriptor, Set<String> alreadyProcessed) {
        alreadyProcessed.add(descriptor.getSerialName());
        if (descriptor.getKind().equals(PolymorphicKind.OPEN.INSTANCE)) {
            return true;
        }
        for (int i = 0; i < descriptor.getElementsCount(); i++) {
            SerialDescriptor elementDescriptor = descriptor.getElementDescriptor(i);
            if (!alreadyProcessed.contains(elementDescriptor.getSerialName()) && hasPolymorphism(elementDescriptor, alreadyProcessed)) {
                return true;
            }
        }
        return false;
    }
}
