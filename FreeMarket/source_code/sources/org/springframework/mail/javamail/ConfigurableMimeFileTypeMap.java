package org.springframework.mail.javamail;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/mail/javamail/ConfigurableMimeFileTypeMap.class */
public class ConfigurableMimeFileTypeMap extends FileTypeMap implements InitializingBean {
    private Resource mappingLocation = new ClassPathResource("mime.types", getClass());

    @Nullable
    private String[] mappings;

    @Nullable
    private FileTypeMap fileTypeMap;

    public void setMappingLocation(Resource mappingLocation) {
        this.mappingLocation = mappingLocation;
    }

    public void setMappings(String... mappings) {
        this.mappings = mappings;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() {
        getFileTypeMap();
    }

    protected final FileTypeMap getFileTypeMap() {
        if (this.fileTypeMap == null) {
            try {
                this.fileTypeMap = createFileTypeMap(this.mappingLocation, this.mappings);
            } catch (IOException ex) {
                throw new IllegalStateException("Could not load specified MIME type mapping file: " + this.mappingLocation, ex);
            }
        }
        return this.fileTypeMap;
    }

    protected FileTypeMap createFileTypeMap(@Nullable Resource mappingLocation, @Nullable String[] mappings) throws IOException {
        MimetypesFileTypeMap fileTypeMap;
        if (mappingLocation != null) {
            InputStream is = mappingLocation.getInputStream();
            Throwable th = null;
            try {
                try {
                    fileTypeMap = new MimetypesFileTypeMap(is);
                    if (is != null) {
                        if (0 != 0) {
                            try {
                                is.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            is.close();
                        }
                    }
                } finally {
                }
            } catch (Throwable th3) {
                if (is != null) {
                    if (th != null) {
                        try {
                            is.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        is.close();
                    }
                }
                throw th3;
            }
        } else {
            fileTypeMap = new MimetypesFileTypeMap();
        }
        if (mappings != null) {
            for (String mapping : mappings) {
                fileTypeMap.addMimeTypes(mapping);
            }
        }
        return fileTypeMap;
    }

    public String getContentType(File file) {
        return getFileTypeMap().getContentType(file);
    }

    public String getContentType(String fileName) {
        return getFileTypeMap().getContentType(fileName);
    }
}
