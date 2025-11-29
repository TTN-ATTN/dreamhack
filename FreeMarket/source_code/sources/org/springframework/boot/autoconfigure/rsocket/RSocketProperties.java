package org.springframework.boot.autoconfigure.rsocket;

import java.net.InetAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.rsocket.server.RSocketServer;
import org.springframework.boot.web.server.Ssl;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties("spring.rsocket")
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketProperties.class */
public class RSocketProperties {

    @NestedConfigurationProperty
    private final Server server = new Server();

    public Server getServer() {
        return this.server;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/rsocket/RSocketProperties$Server.class */
    public static class Server {
        private Integer port;
        private InetAddress address;
        private RSocketServer.Transport transport = RSocketServer.Transport.TCP;
        private String mappingPath;
        private DataSize fragmentSize;

        @NestedConfigurationProperty
        private Ssl ssl;

        public Integer getPort() {
            return this.port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public InetAddress getAddress() {
            return this.address;
        }

        public void setAddress(InetAddress address) {
            this.address = address;
        }

        public RSocketServer.Transport getTransport() {
            return this.transport;
        }

        public void setTransport(RSocketServer.Transport transport) {
            this.transport = transport;
        }

        public String getMappingPath() {
            return this.mappingPath;
        }

        public void setMappingPath(String mappingPath) {
            this.mappingPath = mappingPath;
        }

        public DataSize getFragmentSize() {
            return this.fragmentSize;
        }

        public void setFragmentSize(DataSize fragmentSize) {
            this.fragmentSize = fragmentSize;
        }

        public Ssl getSsl() {
            return this.ssl;
        }

        public void setSsl(Ssl ssl) {
            this.ssl = ssl;
        }
    }
}
