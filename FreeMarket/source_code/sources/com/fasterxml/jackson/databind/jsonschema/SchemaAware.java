package com.fasterxml.jackson.databind.jsonschema;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.lang.reflect.Type;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-databind-2.13.5.jar:com/fasterxml/jackson/databind/jsonschema/SchemaAware.class */
public interface SchemaAware {
    JsonNode getSchema(SerializerProvider serializerProvider, Type type) throws JsonMappingException;

    JsonNode getSchema(SerializerProvider serializerProvider, Type type, boolean z) throws JsonMappingException;
}
