package org.springframework.http.codec.json;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.json.Json;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.codec.AbstractDecoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/json/KotlinSerializationJsonDecoder.class */
public class KotlinSerializationJsonDecoder extends AbstractDecoder<Object> {
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final Json json;
    private final StringDecoder stringDecoder;

    public KotlinSerializationJsonDecoder() {
        this(Json.Default);
    }

    public KotlinSerializationJsonDecoder(Json json) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.stringDecoder = StringDecoder.allMimeTypes(StringDecoder.DEFAULT_DELIMITERS, false);
        this.json = json;
    }

    public void setMaxInMemorySize(int byteCount) {
        this.stringDecoder.setMaxInMemorySize(byteCount);
    }

    public int getMaxInMemorySize() {
        return this.stringDecoder.getMaxInMemorySize();
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType) {
        try {
            serializer(elementType.getType());
            if (super.canDecode(elementType, mimeType)) {
                if (!CharSequence.class.isAssignableFrom(elementType.toClass())) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.springframework.core.codec.Decoder
    public Flux<Object> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return Flux.error(new UnsupportedOperationException());
    }

    @Override // org.springframework.core.codec.AbstractDecoder, org.springframework.core.codec.Decoder
    public Mono<Object> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints) {
        return this.stringDecoder.decodeToMono(inputStream, elementType, mimeType, hints).map(jsonText -> {
            return this.json.decodeFromString(serializer(elementType.getType()), jsonText);
        });
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
