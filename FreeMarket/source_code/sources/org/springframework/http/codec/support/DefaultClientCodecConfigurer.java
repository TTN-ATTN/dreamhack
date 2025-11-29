package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.CodecConfigurer;
import org.springframework.http.codec.HttpMessageWriter;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/http/codec/support/DefaultClientCodecConfigurer.class */
public class DefaultClientCodecConfigurer extends BaseCodecConfigurer implements ClientCodecConfigurer {
    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getWriters() {
        return super.getWriters();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ List getReaders() {
        return super.getReaders();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ CodecConfigurer.CustomCodecs customCodecs() {
        return super.customCodecs();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public /* bridge */ /* synthetic */ void registerDefaults(boolean shouldRegister) {
        super.registerDefaults(shouldRegister);
    }

    public DefaultClientCodecConfigurer() {
        super(new ClientDefaultCodecsImpl());
        ((ClientDefaultCodecsImpl) defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
    }

    private DefaultClientCodecConfigurer(DefaultClientCodecConfigurer other) {
        super(other);
        ((ClientDefaultCodecsImpl) defaultCodecs()).setPartWritersSupplier(this::getPartWriters);
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    public ClientCodecConfigurer.ClientDefaultCodecs defaultCodecs() {
        return (ClientCodecConfigurer.ClientDefaultCodecs) super.defaultCodecs();
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer, org.springframework.http.codec.CodecConfigurer
    /* renamed from: clone */
    public DefaultClientCodecConfigurer mo1924clone() {
        return new DefaultClientCodecConfigurer(this);
    }

    @Override // org.springframework.http.codec.support.BaseCodecConfigurer
    protected BaseDefaultCodecs cloneDefaultCodecs() {
        return new ClientDefaultCodecsImpl((ClientDefaultCodecsImpl) defaultCodecs());
    }

    private List<HttpMessageWriter<?>> getPartWriters() {
        List<HttpMessageWriter<?>> result = new ArrayList<>();
        result.addAll(this.customCodecs.getTypedWriters().keySet());
        result.addAll(this.defaultCodecs.getBaseTypedWriters());
        result.addAll(this.customCodecs.getObjectWriters().keySet());
        result.addAll(this.defaultCodecs.getBaseObjectWriters());
        result.addAll(this.defaultCodecs.getCatchAllWriters());
        return result;
    }
}
