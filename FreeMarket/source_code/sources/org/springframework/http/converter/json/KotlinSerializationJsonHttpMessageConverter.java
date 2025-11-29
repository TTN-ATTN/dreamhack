package org.springframework.http.converter.json;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.SerializationException;
import kotlinx.serialization.SerializersKt;
import kotlinx.serialization.descriptors.PolymorphicKind;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.json.Json;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractGenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.Nullable;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.StreamUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/converter/json/KotlinSerializationJsonHttpMessageConverter.class */
public class KotlinSerializationJsonHttpMessageConverter extends AbstractGenericHttpMessageConverter<Object> {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Map<Type, KSerializer<Object>> serializerCache = new ConcurrentReferenceHashMap();
    private final Json json;

    public KotlinSerializationJsonHttpMessageConverter() {
        this(Json.Default);
    }

    public KotlinSerializationJsonHttpMessageConverter(Json json) {
        super(MediaType.APPLICATION_JSON, new MediaType("application", "*+json"));
        this.json = json;
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.AbstractHttpMessageConverter
    protected boolean supports(Class<?> clazz) {
        try {
            serializer(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canRead(Type type, @Nullable Class<?> contextClass, @Nullable MediaType mediaType) {
        try {
            serializer(GenericTypeResolver.resolveType(type, contextClass));
            return canRead(mediaType);
        } catch (Exception e) {
            return false;
        }
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter, org.springframework.http.converter.GenericHttpMessageConverter
    public boolean canWrite(@Nullable Type type, Class<?> clazz, @Nullable MediaType mediaType) {
        Type typeResolveType;
        if (type != null) {
            try {
                typeResolveType = GenericTypeResolver.resolveType(type, clazz);
            } catch (Exception e) {
                return false;
            }
        } else {
            typeResolveType = clazz;
        }
        serializer(typeResolveType);
        return canWrite(mediaType);
    }

    @Override // org.springframework.http.converter.GenericHttpMessageConverter
    public final Object read(Type type, @Nullable Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return decode(serializer(GenericTypeResolver.resolveType(type, contextClass)), inputMessage);
    }

    @Override // org.springframework.http.converter.AbstractHttpMessageConverter
    protected final Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return decode(serializer(clazz), inputMessage);
    }

    private Object decode(KSerializer<Object> serializer, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        MediaType contentType = inputMessage.getHeaders().getContentType();
        String jsonText = StreamUtils.copyToString(inputMessage.getBody(), getCharsetToUse(contentType));
        try {
            return this.json.decodeFromString(serializer, jsonText);
        } catch (SerializationException ex) {
            throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex, inputMessage);
        }
    }

    @Override // org.springframework.http.converter.AbstractGenericHttpMessageConverter
    protected final void writeInternal(Object object, @Nullable Type type, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        encode(object, serializer(type != null ? type : object.getClass()), outputMessage);
    }

    private void encode(Object object, KSerializer<Object> serializer, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        try {
            String json = this.json.encodeToString(serializer, object);
            MediaType contentType = outputMessage.getHeaders().getContentType();
            outputMessage.getBody().write(json.getBytes(getCharsetToUse(contentType)));
            outputMessage.getBody().flush();
        } catch (IOException ex) {
            throw ex;
        } catch (Exception ex2) {
            throw new HttpMessageNotWritableException("Could not write JSON: " + ex2.getMessage(), ex2);
        }
    }

    private Charset getCharsetToUse(@Nullable MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        }
        return DEFAULT_CHARSET;
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
