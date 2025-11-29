package org.springframework.http.codec;

import org.apache.commons.logging.Log;
import org.springframework.http.HttpLogging;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/LoggingCodecSupport.class */
public class LoggingCodecSupport {
    protected final Log logger = HttpLogging.forLogName(getClass());
    private boolean enableLoggingRequestDetails = false;

    public void setEnableLoggingRequestDetails(boolean enable) {
        this.enableLoggingRequestDetails = enable;
    }

    public boolean isEnableLoggingRequestDetails() {
        return this.enableLoggingRequestDetails;
    }
}
