package org.springframework.boot.context.config;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.boot.context.properties.bind.AbstractBindHandler;
import org.springframework.boot.context.properties.bind.BindContext;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.origin.Origin;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataLocationBindHandler.class */
class ConfigDataLocationBindHandler extends AbstractBindHandler {
    ConfigDataLocationBindHandler() {
    }

    @Override // org.springframework.boot.context.properties.bind.AbstractBindHandler, org.springframework.boot.context.properties.bind.BindHandler
    public Object onSuccess(ConfigurationPropertyName name, Bindable<?> target, BindContext context, Object result) {
        if (result instanceof ConfigDataLocation) {
            return withOrigin(context, (ConfigDataLocation) result);
        }
        if (result instanceof List) {
            List<Object> list = (List) ((List) result).stream().filter(Objects::nonNull).collect(Collectors.toList());
            for (int i = 0; i < list.size(); i++) {
                Object element = list.get(i);
                if (element instanceof ConfigDataLocation) {
                    list.set(i, withOrigin(context, (ConfigDataLocation) element));
                }
            }
            return list;
        }
        if (result instanceof ConfigDataLocation[]) {
            ConfigDataLocation[] locations = (ConfigDataLocation[]) Arrays.stream((ConfigDataLocation[]) result).filter((v0) -> {
                return Objects.nonNull(v0);
            }).toArray(x$0 -> {
                return new ConfigDataLocation[x$0];
            });
            for (int i2 = 0; i2 < locations.length; i2++) {
                locations[i2] = withOrigin(context, locations[i2]);
            }
            return locations;
        }
        return result;
    }

    private ConfigDataLocation withOrigin(BindContext context, ConfigDataLocation result) {
        if (result.getOrigin() != null) {
            return result;
        }
        Origin origin = Origin.from(context.getConfigurationProperty());
        return result.withOrigin(origin);
    }
}
