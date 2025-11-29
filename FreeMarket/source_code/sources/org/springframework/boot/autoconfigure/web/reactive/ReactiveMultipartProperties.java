package org.springframework.boot.autoconfigure.web.reactive;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "spring.webflux.multipart")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/web/reactive/ReactiveMultipartProperties.class */
public class ReactiveMultipartProperties {
    private String fileStorageDirectory;
    private DataSize maxInMemorySize = DataSize.ofKilobytes(256);
    private DataSize maxHeadersSize = DataSize.ofKilobytes(10);
    private DataSize maxDiskUsagePerPart = DataSize.ofBytes(-1);
    private Integer maxParts = -1;
    private Boolean streaming = Boolean.FALSE;
    private Charset headersCharset = StandardCharsets.UTF_8;

    public DataSize getMaxInMemorySize() {
        return this.maxInMemorySize;
    }

    public void setMaxInMemorySize(DataSize maxInMemorySize) {
        this.maxInMemorySize = maxInMemorySize;
    }

    public DataSize getMaxHeadersSize() {
        return this.maxHeadersSize;
    }

    public void setMaxHeadersSize(DataSize maxHeadersSize) {
        this.maxHeadersSize = maxHeadersSize;
    }

    public DataSize getMaxDiskUsagePerPart() {
        return this.maxDiskUsagePerPart;
    }

    public void setMaxDiskUsagePerPart(DataSize maxDiskUsagePerPart) {
        this.maxDiskUsagePerPart = maxDiskUsagePerPart;
    }

    public Integer getMaxParts() {
        return this.maxParts;
    }

    public void setMaxParts(Integer maxParts) {
        this.maxParts = maxParts;
    }

    public Boolean getStreaming() {
        return this.streaming;
    }

    public void setStreaming(Boolean streaming) {
        this.streaming = streaming;
    }

    public String getFileStorageDirectory() {
        return this.fileStorageDirectory;
    }

    public void setFileStorageDirectory(String fileStorageDirectory) {
        this.fileStorageDirectory = fileStorageDirectory;
    }

    public Charset getHeadersCharset() {
        return this.headersCharset;
    }

    public void setHeadersCharset(Charset headersCharset) {
        this.headersCharset = headersCharset;
    }
}
