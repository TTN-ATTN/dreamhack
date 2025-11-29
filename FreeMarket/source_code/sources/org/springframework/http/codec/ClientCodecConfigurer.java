package org.springframework.http.codec;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.CodecConfigurer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/ClientCodecConfigurer.class */
public interface ClientCodecConfigurer extends CodecConfigurer {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/ClientCodecConfigurer$ClientDefaultCodecs.class */
    public interface ClientDefaultCodecs extends CodecConfigurer.DefaultCodecs {
        MultipartCodecs multipartCodecs();

        void serverSentEventDecoder(Decoder<?> decoder);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/ClientCodecConfigurer$MultipartCodecs.class */
    public interface MultipartCodecs {
        MultipartCodecs encoder(Encoder<?> encoder);

        MultipartCodecs writer(HttpMessageWriter<?> writer);
    }

    @Override // org.springframework.http.codec.CodecConfigurer
    ClientDefaultCodecs defaultCodecs();

    @Override // org.springframework.http.codec.CodecConfigurer
    ClientCodecConfigurer clone();

    static ClientCodecConfigurer create() {
        return (ClientCodecConfigurer) CodecConfigurerFactory.create(ClientCodecConfigurer.class);
    }
}
