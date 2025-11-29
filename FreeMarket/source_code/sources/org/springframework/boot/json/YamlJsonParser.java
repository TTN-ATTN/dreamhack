package org.springframework.boot.json;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.util.Assert;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/YamlJsonParser.class */
public class YamlJsonParser extends AbstractJsonParser {
    private final Yaml yaml = new Yaml(new TypeLimitedConstructor());

    @Override // org.springframework.boot.json.JsonParser
    public Map<String, Object> parseMap(String json) {
        return parseMap(json, trimmed -> {
            return (Map) this.yaml.loadAs(trimmed, Map.class);
        });
    }

    @Override // org.springframework.boot.json.JsonParser
    public List<Object> parseList(String json) {
        return parseList(json, trimmed -> {
            return (List) this.yaml.loadAs(trimmed, List.class);
        });
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/json/YamlJsonParser$TypeLimitedConstructor.class */
    private static class TypeLimitedConstructor extends Constructor {
        private static final Set<String> SUPPORTED_TYPES;

        private TypeLimitedConstructor() {
        }

        static {
            Set<Class<?>> supportedTypes = new LinkedHashSet<>();
            supportedTypes.add(List.class);
            supportedTypes.add(Map.class);
            SUPPORTED_TYPES = (Set) supportedTypes.stream().map((v0) -> {
                return v0.getName();
            }).collect(Collectors.collectingAndThen(Collectors.toSet(), Collections::unmodifiableSet));
        }

        @Override // org.yaml.snakeyaml.constructor.Constructor
        protected Class<?> getClassForName(String name) throws ClassNotFoundException {
            Assert.state(SUPPORTED_TYPES.contains(name), (Supplier<String>) () -> {
                return "Unsupported '" + name + "' type encountered in YAML document";
            });
            return super.getClassForName(name);
        }
    }
}
