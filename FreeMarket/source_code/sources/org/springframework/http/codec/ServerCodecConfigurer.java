package org.springframework.http.codec;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/ServerCodecConfigurer.class */
public interface ServerCodecConfigurer extends CodecConfigurer {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/ServerCodecConfigurer$ServerDefaultCodecs.class */
    public interface ServerDefaultCodecs extends CodecConfigurer.DefaultCodecs {
        void multipartReader(HttpMessageReader<?> reader);

        void serverSentEventEncoder(Encoder<?> encoder);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    ServerDefaultCodecs defaultCodecs();

    @Override // org.springframework.http.codec.CodecConfigurer
    ServerCodecConfigurer clone();

    static ServerCodecConfigurer create() {
        return (ServerCodecConfigurer) CodecConfigurerFactory.create(ServerCodecConfigurer.class);
    }
}
