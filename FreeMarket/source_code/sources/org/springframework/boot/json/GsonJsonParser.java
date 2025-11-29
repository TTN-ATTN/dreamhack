package org.springframework.boot.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/GsonJsonParser.class */
public class GsonJsonParser extends AbstractJsonParser {
    private static final TypeToken<?> MAP_TYPE = new MapTypeToken();
    private static final TypeToken<?> LIST_TYPE = new ListTypeToken();
    private Gson gson = new GsonBuilder().create();

    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return (Map) tryParse(() -> {
            return parseMap(json, trimmed -> {
                return (Map) this.gson.fromJson(trimmed, MAP_TYPE.getType());
            });
        }, Exception.class);
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return (List) tryParse(() -> {
            return parseList(json, trimmed -> {
                return (List) this.gson.fromJson(trimmed, LIST_TYPE.getType());
            });
        }, Exception.class);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/GsonJsonParser$MapTypeToken.class */
    private static final class MapTypeToken extends TypeToken<Map<String, Object>> {
        private MapTypeToken() {
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/GsonJsonParser$ListTypeToken.class */
    private static final class ListTypeToken extends TypeToken<List<Object>> {
        private ListTypeToken() {
        }
    }
}
