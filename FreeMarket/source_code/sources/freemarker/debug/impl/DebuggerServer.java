package freemarker.debug.impl;

import freemarker.debug.Debugger;
import freemarker.log.Logger;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.UndeclaredThrowableException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/DebuggerServer.class */
class DebuggerServer {
    private static final Logger LOG = Logger.getLogger("freemarker.debug.server");
    private static final Random R = new SecureRandom();
    private final byte[] password;
    private final Serializable debuggerStub;
    private ServerSocket serverSocket;
    private boolean stop = false;
    private final int port = SecurityUtilities.getSystemProperty("freemarker.debug.port", Debugger.DEFAULT_PORT).intValue();

    public DebuggerServer(Serializable debuggerStub) {
        try {
            this.password = SecurityUtilities.getSystemProperty("freemarker.debug.password", "").getBytes("UTF-8");
            this.debuggerStub = debuggerStub;
        } catch (UnsupportedEncodingException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    public void start() {
        new Thread(new Runnable() { // from class: freemarker.debug.impl.DebuggerServer.1
            @Override // java.lang.Runnable
            public void run() throws IOException {
                DebuggerServer.this.startInternal();
            }
        }, "FreeMarker Debugger Server Acceptor").start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startInternal() throws IOException {
        try {
            this.serverSocket = new ServerSocket(this.port);
            while (!this.stop) {
                Socket s = this.serverSocket.accept();
                new Thread(new DebuggerAuthProtocol(s)).start();
            }
        } catch (IOException e) {
            LOG.error("Debugger server shut down.", e);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/impl/DebuggerServer$DebuggerAuthProtocol.class */
    private class DebuggerAuthProtocol implements Runnable {
        private final Socket s;

        DebuggerAuthProtocol(Socket s) {
            this.s = s;
        }

        @Override // java.lang.Runnable
        public void run() throws NoSuchAlgorithmException, IOException {
            try {
                ObjectOutputStream out = new ObjectOutputStream(this.s.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(this.s.getInputStream());
                byte[] challenge = new byte[512];
                DebuggerServer.R.nextBytes(challenge);
                out.writeInt(220);
                out.writeObject(challenge);
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(DebuggerServer.this.password);
                md.update(challenge);
                byte[] response = (byte[]) in.readObject();
                if (Arrays.equals(response, md.digest())) {
                    out.writeObject(DebuggerServer.this.debuggerStub);
                } else {
                    out.writeObject(null);
                }
            } catch (Exception e) {
                DebuggerServer.LOG.warn("Connection to " + this.s.getInetAddress().getHostAddress() + " abruply broke", e);
            }
        }
    }

    public void stop() throws IOException {
        this.stop = true;
        if (this.serverSocket != null) {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                LOG.error("Unable to close server socket.", e);
            }
        }
    }
}
