package org.apache.catalina.core;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.ValveBase;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.http.HttpHeaders;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/core/StandardWrapperValve.class */
final class StandardWrapperValve extends ValveBase {
    private static final StringManager sm = StringManager.getManager((Class<?>) StandardWrapperValve.class);
    private volatile long processingTime;
    private volatile long maxTime;
    private volatile long minTime;
    private final AtomicInteger requestCount;
    private final AtomicInteger errorCount;

    StandardWrapperValve() {
        super(true);
        this.minTime = Long.MAX_VALUE;
        this.requestCount = new AtomicInteger(0);
        this.errorCount = new AtomicInteger(0);
    }

    /* JADX WARN: Removed duplicated region for block: B:103:0x0393  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x03ed  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x03fd  */
    /* JADX WARN: Removed duplicated region for block: B:132:0x04a9  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x0503  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x0513  */
    /* JADX WARN: Removed duplicated region for block: B:161:0x05c1  */
    /* JADX WARN: Removed duplicated region for block: B:168:0x061b  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x062b  */
    /* JADX WARN: Removed duplicated region for block: B:193:0x06ee  */
    /* JADX WARN: Removed duplicated region for block: B:200:0x0748  */
    /* JADX WARN: Removed duplicated region for block: B:203:0x0758  */
    /* JADX WARN: Removed duplicated region for block: B:222:0x0809  */
    /* JADX WARN: Removed duplicated region for block: B:229:0x0863  */
    /* JADX WARN: Removed duplicated region for block: B:232:0x0873  */
    /* JADX WARN: Removed duplicated region for block: B:251:0x08e7  */
    /* JADX WARN: Removed duplicated region for block: B:258:0x0941  */
    /* JADX WARN: Removed duplicated region for block: B:261:0x0951  */
    /* JADX WARN: Removed duplicated region for block: B:271:0x05b0 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:273:0x08d6 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:275:0x07f8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:277:0x0498 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:279:0x06dd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:288:0x0382 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:301:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:303:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:305:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:307:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:309:? A[RETURN, SYNTHETIC] */
    @Override // org.apache.catalina.Valve
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void invoke(org.apache.catalina.connector.Request r9, org.apache.catalina.connector.Response r10) throws javax.servlet.ServletException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 2395
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.core.StandardWrapperValve.invoke(org.apache.catalina.connector.Request, org.apache.catalina.connector.Response):void");
    }

    private void checkWrapperAvailable(Response response, StandardWrapper wrapper) throws IOException {
        long available = wrapper.getAvailable();
        if (available > 0 && available < Long.MAX_VALUE) {
            response.setDateHeader(HttpHeaders.RETRY_AFTER, available);
            response.sendError(503, sm.getString("standardWrapper.isUnavailable", wrapper.getName()));
        } else if (available == Long.MAX_VALUE) {
            response.sendError(404, sm.getString("standardWrapper.notFound", wrapper.getName()));
        }
    }

    private void exception(Request request, Response response, Throwable exception) throws IOException {
        request.setAttribute("javax.servlet.error.exception", exception);
        response.setStatus(500);
        response.setError();
    }

    public long getProcessingTime() {
        return this.processingTime;
    }

    public long getMaxTime() {
        return this.maxTime;
    }

    public long getMinTime() {
        return this.minTime;
    }

    public int getRequestCount() {
        return this.requestCount.get();
    }

    public int getErrorCount() {
        return this.errorCount.get();
    }

    public void incrementErrorCount() {
        this.errorCount.incrementAndGet();
    }

    @Override // org.apache.catalina.valves.ValveBase, org.apache.catalina.util.LifecycleMBeanBase, org.apache.catalina.util.LifecycleBase
    protected void initInternal() throws LifecycleException {
    }
}
