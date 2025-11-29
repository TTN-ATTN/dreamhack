package org.springframework.web.servlet.function;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.EnumSet;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/ResourceHandlerFunction.class */
class ResourceHandlerFunction implements HandlerFunction<ServerResponse> {
    private static final Set<HttpMethod> SUPPORTED_METHODS = EnumSet.of(HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS);
    private final Resource resource;

    public ResourceHandlerFunction(Resource resource) {
        this.resource = resource;
    }

    @Override // org.springframework.web.servlet.function.HandlerFunction
    public ServerResponse handle(ServerRequest request) {
        HttpMethod method = request.method();
        if (method != null) {
            switch (method) {
                case GET:
                    return EntityResponse.fromObject(this.resource).build();
                case HEAD:
                    Resource headResource = new HeadMethodResource(this.resource);
                    return EntityResponse.fromObject(headResource).build();
                case OPTIONS:
                    return ServerResponse.ok().allow(SUPPORTED_METHODS).build();
            }
        }
        return ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).allow(SUPPORTED_METHODS).build();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/function/ResourceHandlerFunction$HeadMethodResource.class */
    private static class HeadMethodResource implements Resource {
        private static final byte[] EMPTY = new byte[0];
        private final Resource delegate;

        public HeadMethodResource(Resource delegate) {
            this.delegate = delegate;
        }

        @Override // org.springframework.core.io.InputStreamSource
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(EMPTY);
        }

        @Override // org.springframework.core.io.Resource
        public boolean exists() {
            return this.delegate.exists();
        }

        @Override // org.springframework.core.io.Resource
        public URL getURL() throws IOException {
            return this.delegate.getURL();
        }

        @Override // org.springframework.core.io.Resource
        public URI getURI() throws IOException {
            return this.delegate.getURI();
        }

        @Override // org.springframework.core.io.Resource
        public File getFile() throws IOException {
            return this.delegate.getFile();
        }

        @Override // org.springframework.core.io.Resource
        public long contentLength() throws IOException {
            return this.delegate.contentLength();
        }

        @Override // org.springframework.core.io.Resource
        public long lastModified() throws IOException {
            return this.delegate.lastModified();
        }

        @Override // org.springframework.core.io.Resource
        public Resource createRelative(String relativePath) throws IOException {
            return this.delegate.createRelative(relativePath);
        }

        @Override // org.springframework.core.io.Resource
        @Nullable
        public String getFilename() {
            return this.delegate.getFilename();
        }

        @Override // org.springframework.core.io.Resource
        public String getDescription() {
            return this.delegate.getDescription();
        }
    }
}
