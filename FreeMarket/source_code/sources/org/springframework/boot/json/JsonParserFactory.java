package org.springframework.boot.json;

import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/JsonParserFactory.class */
public abstract class JsonParserFactory {
    public static JsonParser getJsonParser() {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new JacksonJsonParser();
        }
        if (ClassUtils.isPresent("com.google.gson.Gson", null)) {
            return new GsonJsonParser();
        }
        if (ClassUtils.isPresent("org.yaml.snakeyaml.Yaml", null)) {
            return new YamlJsonParser();
        }
        return new BasicJsonParser();
    }
}
