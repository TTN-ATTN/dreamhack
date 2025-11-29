package org.springframework.boot.context.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/context/config/ConfigTreeConfigDataResource.class */
public class ConfigTreeConfigDataResource extends ConfigDataResource {
    private final Path path;

    ConfigTreeConfigDataResource(String path) {
        Assert.notNull(path, "Path must not be null");
        this.path = Paths.get(path, new String[0]).toAbsolutePath();
    }

    ConfigTreeConfigDataResource(Path path) {
        Assert.notNull(path, "Path must not be null");
        this.path = path.toAbsolutePath();
    }

    Path getPath() {
        return this.path;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        ConfigTreeConfigDataResource other = (ConfigTreeConfigDataResource) obj;
        return Objects.equals(this.path, other.path);
    }

    public int hashCode() {
        return this.path.hashCode();
    }

    public String toString() {
        return "config tree [" + this.path + "]";
    }
}
