package com.fasterxml.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/JsonSerializable.class */
public interface JsonSerializable {
    void serialize(JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException;

    void serializeWithType(JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSerializer) throws IOException;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/JsonSerializable$Base.class */
    public static abstract class Base implements JsonSerializable {
        public boolean isEmpty(SerializerProvider serializers) {
            return false;
        }
    }
}
