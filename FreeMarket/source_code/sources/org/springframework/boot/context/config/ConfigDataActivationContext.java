package org.springframework.boot.context.config;

import org.springframework.boot.cloud.CloudPlatform;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.Environment;
import org.springframework.core.style.ToStringCreator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigDataActivationContext.class */
class ConfigDataActivationContext {
    private final CloudPlatform cloudPlatform;
    private final Profiles profiles;

    ConfigDataActivationContext(Environment environment, Binder binder) {
        this.cloudPlatform = deduceCloudPlatform(environment, binder);
        this.profiles = null;
    }

    ConfigDataActivationContext(CloudPlatform cloudPlatform, Profiles profiles) {
        this.cloudPlatform = cloudPlatform;
        this.profiles = profiles;
    }

    private CloudPlatform deduceCloudPlatform(Environment environment, Binder binder) {
        for (CloudPlatform candidate : CloudPlatform.values()) {
            if (candidate.isEnforced(binder)) {
                return candidate;
            }
        }
        return CloudPlatform.getActive(environment);
    }

    ConfigDataActivationContext withProfiles(Profiles profiles) {
        return new ConfigDataActivationContext(this.cloudPlatform, profiles);
    }

    CloudPlatform getCloudPlatform() {
        return this.cloudPlatform;
    }

    Profiles getProfiles() {
        return this.profiles;
    }

    public String toString() {
        ToStringCreator creator = new ToStringCreator(this);
        creator.append("cloudPlatform", this.cloudPlatform);
        creator.append("profiles", this.profiles);
        return creator.toString();
    }
}
