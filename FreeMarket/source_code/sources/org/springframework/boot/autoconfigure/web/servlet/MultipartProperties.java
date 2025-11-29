package org.springframework.boot.autoconfigure.web.servlet;

import javax.servlet.MultipartConfigElement;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.servlet.multipart", ignoreUnknownFields = false)
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/servlet/MultipartProperties.class */
public class MultipartProperties {
    private String location;
    private boolean enabled = true;
    private DataSize maxFileSize = DataSize.ofMegabytes(1);
    private DataSize maxRequestSize = DataSize.ofMegabytes(10);
    private DataSize fileSizeThreshold = DataSize.ofBytes(0);
    private boolean resolveLazily = false;

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DataSize getMaxFileSize() {
        return this.maxFileSize;
    }

    public void setMaxFileSize(DataSize maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    public DataSize getMaxRequestSize() {
        return this.maxRequestSize;
    }

    public void setMaxRequestSize(DataSize maxRequestSize) {
        this.maxRequestSize = maxRequestSize;
    }

    public DataSize getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }

    public void setFileSizeThreshold(DataSize fileSizeThreshold) {
        this.fileSizeThreshold = fileSizeThreshold;
    }

    public boolean isResolveLazily() {
        return this.resolveLazily;
    }

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    public MultipartConfigElement createMultipartConfig() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        PropertyMapper.Source sourceFrom = map.from((PropertyMapper) this.fileSizeThreshold);
        factory.getClass();
        sourceFrom.to(factory::setFileSizeThreshold);
        PropertyMapper.Source sourceWhenHasText = map.from((PropertyMapper) this.location).whenHasText();
        factory.getClass();
        sourceWhenHasText.to(factory::setLocation);
        PropertyMapper.Source sourceFrom2 = map.from((PropertyMapper) this.maxRequestSize);
        factory.getClass();
        sourceFrom2.to(factory::setMaxRequestSize);
        PropertyMapper.Source sourceFrom3 = map.from((PropertyMapper) this.maxFileSize);
        factory.getClass();
        sourceFrom3.to(factory::setMaxFileSize);
        return factory.createMultipartConfig();
    }
}
