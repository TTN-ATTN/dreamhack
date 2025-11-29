package org.springframework.boot.json;

import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/JsonParser.class */
public interface JsonParser {
    Map<String, Object> parseMap(String json) throws JsonParseException;

    List<Object> parseList(String json) throws JsonParseException;
}
