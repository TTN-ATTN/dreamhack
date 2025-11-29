package org.springframework.web.multipart;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/multipart/MultipartFileResource.class */
class MultipartFileResource extends AbstractResource {
    private final MultipartFile multipartFile;

    public MultipartFileResource(MultipartFile multipartFile) {
        Assert.notNull(multipartFile, "MultipartFile must not be null");
        this.multipartFile = multipartFile;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean exists() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public boolean isOpen() {
        return true;
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public long contentLength() {
        return this.multipartFile.getSize();
    }

    @Override // org.springframework.core.io.AbstractResource, org.springframework.core.io.Resource
    public String getFilename() {
        return this.multipartFile.getOriginalFilename();
    }

    @Override // org.springframework.core.io.InputStreamSource
    public InputStream getInputStream() throws IllegalStateException, IOException {
        return this.multipartFile.getInputStream();
    }

    @Override // org.springframework.core.io.Resource
    public String getDescription() {
        return "MultipartFile resource [" + this.multipartFile.getName() + "]";
    }

    @Override // org.springframework.core.io.AbstractResource
    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof MultipartFileResource) && ((MultipartFileResource) other).multipartFile.equals(this.multipartFile));
    }

    @Override // org.springframework.core.io.AbstractResource
    public int hashCode() {
        return this.multipartFile.hashCode();
    }
}
