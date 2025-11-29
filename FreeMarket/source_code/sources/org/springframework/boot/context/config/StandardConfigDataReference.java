package org.springframework.boot.context.config;

import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/StandardConfigDataReference.class */
class StandardConfigDataReference {
    private final ConfigDataLocation configDataLocation;
    private final String resourceLocation;
    private final String directory;
    private final String profile;
    private final PropertySourceLoader propertySourceLoader;

    StandardConfigDataReference(ConfigDataLocation configDataLocation, String directory, String root, String profile, String extension, PropertySourceLoader propertySourceLoader) {
        this.configDataLocation = configDataLocation;
        String profileSuffix = StringUtils.hasText(profile) ? "-" + profile : "";
        this.resourceLocation = root + profileSuffix + (extension != null ? "." + extension : "");
        this.directory = directory;
        this.profile = profile;
        this.propertySourceLoader = propertySourceLoader;
    }

    ConfigDataLocation getConfigDataLocation() {
        return this.configDataLocation;
    }

    String getResourceLocation() {
        return this.resourceLocation;
    }

    boolean isMandatoryDirectory() {
        return (this.configDataLocation.isOptional() || this.directory == null) ? false : true;
    }

    String getDirectory() {
        return this.directory;
    }

    String getProfile() {
        return this.profile;
    }

    boolean isSkippable() {
        return (!this.configDataLocation.isOptional() && this.directory == null && this.profile == null) ? false : true;
    }

    PropertySourceLoader getPropertySourceLoader() {
        return this.propertySourceLoader;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        StandardConfigDataReference other = (StandardConfigDataReference) obj;
        return this.resourceLocation.equals(other.resourceLocation);
    }

    public int hashCode() {
        return this.resourceLocation.hashCode();
    }

    public String toString() {
        return this.resourceLocation;
    }
}
