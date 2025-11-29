package javax.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Decoder.class */
public interface Decoder {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Decoder$Binary.class */
    public interface Binary<T> extends Decoder {
        T decode(ByteBuffer byteBuffer) throws DecodeException;

        boolean willDecode(ByteBuffer byteBuffer);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Decoder$BinaryStream.class */
    public interface BinaryStream<T> extends Decoder {
        T decode(InputStream inputStream) throws IOException, DecodeException;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Decoder$Text.class */
    public interface Text<T> extends Decoder {
        T decode(String str) throws DecodeException;

        boolean willDecode(String str);
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Decoder$TextStream.class */
    public interface TextStream<T> extends Decoder {
        T decode(Reader reader) throws IOException, DecodeException;
    }

    void init(EndpointConfig endpointConfig);

    void destroy();
}
