package org.springframework.boot.context.properties.source;

import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/properties/source/PrefixedConfigurationPropertySource.class */
class PrefixedConfigurationPropertySource implements ConfigurationPropertySource {
    private final ConfigurationPropertySource source;
    private final ConfigurationPropertyName prefix;

    PrefixedConfigurationPropertySource(ConfigurationPropertySource source, String prefix) {
        Assert.notNull(source, "Source must not be null");
        Assert.hasText(prefix, "Prefix must not be empty");
        this.source = source;
        this.prefix = ConfigurationPropertyName.of(prefix);
    }

    protected final ConfigurationPropertyName getPrefix() {
        return this.prefix;
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationProperty getConfigurationProperty(ConfigurationPropertyName name) {
        ConfigurationProperty configurationProperty = this.source.getConfigurationProperty(getPrefixedName(name));
        if (configurationProperty == null) {
            return null;
        }
        return ConfigurationProperty.of(configurationProperty.getSource(), name, configurationProperty.getValue(), configurationProperty.getOrigin());
    }

    private ConfigurationPropertyName getPrefixedName(ConfigurationPropertyName name) {
        return this.prefix.append(name);
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public ConfigurationPropertyState containsDescendantOf(ConfigurationPropertyName name) {
        return this.source.containsDescendantOf(getPrefixedName(name));
    }

    @Override // org.springframework.boot.context.properties.source.ConfigurationPropertySource
    public Object getUnderlyingSource() {
        return this.source.getUnderlyingSource();
    }

    protected ConfigurationPropertySource getSource() {
        return this.source;
    }
}
