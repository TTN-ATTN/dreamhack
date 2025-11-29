package org.springframework.core.codec;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.reactivestreams.Publisher;
import org.springframework.core.ResolvableType;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/codec/Decoder.class */
public interface Decoder<T> {
    boolean canDecode(ResolvableType elementType, @Nullable MimeType mimeType);

    Flux<T> decode(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

    Mono<T> decodeToMono(Publisher<DataBuffer> inputStream, ResolvableType elementType, @Nullable MimeType mimeType, @Nullable Map<String, Object> hints);

    List<MimeType> getDecodableMimeTypes();

    /* JADX WARN: Removed duplicated region for block: B:11:0x003d  */
    /* JADX WARN: Removed duplicated region for block: B:12:0x0045  */
    @org.springframework.lang.Nullable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    default T decode(org.springframework.core.io.buffer.DataBuffer r7, org.springframework.core.ResolvableType r8, @org.springframework.lang.Nullable org.springframework.util.MimeType r9, @org.springframework.lang.Nullable java.util.Map<java.lang.String, java.lang.Object> r10) throws org.springframework.core.codec.DecodingException {
        /*
            r6 = this;
            r0 = r6
            r1 = r7
            reactor.core.publisher.Mono r1 = reactor.core.publisher.Mono.just(r1)
            r2 = r8
            r3 = r9
            r4 = r10
            reactor.core.publisher.Mono r0 = r0.decodeToMono(r1, r2, r3, r4)
            java.util.concurrent.CompletableFuture r0 = r0.toFuture()
            r11 = r0
            r0 = r11
            boolean r0 = r0.isDone()
            java.lang.String r1 = "DataBuffer decoding should have completed."
            org.springframework.util.Assert.state(r0, r1)
            r0 = r11
            java.lang.Object r0 = r0.get()     // Catch: java.util.concurrent.ExecutionException -> L23 java.lang.InterruptedException -> L2f
            return r0
        L23:
            r13 = move-exception
            r0 = r13
            java.lang.Throwable r0 = r0.getCause()
            r12 = r0
            goto L35
        L2f:
            r13 = move-exception
            r0 = r13
            r12 = r0
        L35:
            r0 = r12
            boolean r0 = r0 instanceof org.springframework.core.codec.CodecException
            if (r0 == 0) goto L45
            r0 = r12
            org.springframework.core.codec.CodecException r0 = (org.springframework.core.codec.CodecException) r0
            goto L65
        L45:
            org.springframework.core.codec.DecodingException r0 = new org.springframework.core.codec.DecodingException
            r1 = r0
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = r2
            r3.<init>()
            java.lang.String r3 = "Failed to decode: "
            java.lang.StringBuilder r2 = r2.append(r3)
            r3 = r12
            java.lang.String r3 = r3.getMessage()
            java.lang.StringBuilder r2 = r2.append(r3)
            java.lang.String r2 = r2.toString()
            r3 = r12
            r1.<init>(r2, r3)
        L65:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.core.codec.Decoder.decode(org.springframework.core.io.buffer.DataBuffer, org.springframework.core.ResolvableType, org.springframework.util.MimeType, java.util.Map):java.lang.Object");
    }

    default List<MimeType> getDecodableMimeTypes(ResolvableType targetType) {
        return canDecode(targetType, null) ? getDecodableMimeTypes() : Collections.emptyList();
    }
}
