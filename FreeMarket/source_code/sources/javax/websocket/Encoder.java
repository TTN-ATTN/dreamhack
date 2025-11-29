package javax.websocket;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.ByteBuffer;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Encoder.class */
public interface Encoder {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Encoder$Binary.class */
    public interface Binary<T> extends Encoder {
        ByteBuffer encode(T t) throws EncodeException;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Encoder$BinaryStream.class */
    public interface BinaryStream<T> extends Encoder {
        void encode(T t, OutputStream outputStream) throws IOException, EncodeException;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Encoder$Text.class */
    public interface Text<T> extends Encoder {
        String encode(T t) throws EncodeException;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-websocket-9.0.75.jar:javax/websocket/Encoder$TextStream.class */
    public interface TextStream<T> extends Encoder {
        void encode(T t, Writer writer) throws IOException, EncodeException;
    }

    void init(EndpointConfig endpointConfig);

    void destroy();
}
