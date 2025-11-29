package org.springframework.boot.web.client;

import java.nio.charset.Charset;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/web/client/BasicAuthentication.class */
class BasicAuthentication {
    private final String username;
    private final String password;
    private final Charset charset;

    BasicAuthentication(String username, String password, Charset charset) {
        Assert.notNull(username, "Username must not be null");
        Assert.notNull(password, "Password must not be null");
        this.username = username;
        this.password = password;
        this.charset = charset;
    }

    void applyTo(HttpHeaders headers) {
        if (!headers.containsKey("Authorization")) {
            headers.setBasicAuth(this.username, this.password, this.charset);
        }
    }
}
