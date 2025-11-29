package org.springframework.boot.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/system/ApplicationPid.class */
public class ApplicationPid {
    private static final Log logger = LogFactory.getLog((Class<?>) ApplicationPid.class);
    private static final PosixFilePermission[] WRITE_PERMISSIONS = {PosixFilePermission.OWNER_WRITE, PosixFilePermission.GROUP_WRITE, PosixFilePermission.OTHERS_WRITE};
    private static final long JVM_NAME_RESOLVE_THRESHOLD = 200;
    private final String pid;

    public ApplicationPid() {
        this.pid = getPid();
    }

    protected ApplicationPid(String pid) {
        this.pid = pid;
    }

    private String getPid() {
        try {
            String jvmName = resolveJvmName();
            return jvmName.split("@")[0];
        } catch (Throwable th) {
            return null;
        }
    }

    private String resolveJvmName() {
        long startTime = System.currentTimeMillis();
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        long elapsed = System.currentTimeMillis() - startTime;
        if (elapsed > JVM_NAME_RESOLVE_THRESHOLD) {
            logger.warn(LogMessage.of(() -> {
                StringBuilder warning = new StringBuilder();
                warning.append("ManagementFactory.getRuntimeMXBean().getName() took ");
                warning.append(elapsed);
                warning.append(" milliseconds to respond.");
                warning.append(" This may be due to slow host name resolution.");
                warning.append(" Please verify your network configuration");
                if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                    warning.append(" (macOS machines may need to add entries to /etc/hosts)");
                }
                warning.append(".");
                return warning;
            }));
        }
        return jvmName;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ApplicationPid) {
            return ObjectUtils.nullSafeEquals(this.pid, ((ApplicationPid) obj).pid);
        }
        return false;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.pid);
    }

    public String toString() {
        return this.pid != null ? this.pid : "???";
    }

    public void write(File file) throws IOException {
        Assert.state(this.pid != null, "No PID available");
        createParentDirectory(file);
        if (file.exists()) {
            assertCanOverwrite(file);
        }
        FileWriter writer = new FileWriter(file);
        Throwable th = null;
        try {
            try {
                writer.append((CharSequence) this.pid);
                if (writer != null) {
                    if (0 != 0) {
                        try {
                            writer.close();
                            return;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            return;
                        }
                    }
                    writer.close();
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (writer != null) {
                if (th != null) {
                    try {
                        writer.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    writer.close();
                }
            }
            throw th4;
        }
    }

    private void createParentDirectory(File file) {
        File parent = file.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
    }

    private void assertCanOverwrite(File file) throws IOException {
        if (!file.canWrite() || !canWritePosixFile(file)) {
            throw new FileNotFoundException(file.toString() + " (permission denied)");
        }
    }

    private boolean canWritePosixFile(File file) throws IOException {
        try {
            Set<PosixFilePermission> permissions = Files.getPosixFilePermissions(file.toPath(), new LinkOption[0]);
            for (PosixFilePermission permission : WRITE_PERMISSIONS) {
                if (permissions.contains(permission)) {
                    return true;
                }
            }
            return false;
        } catch (UnsupportedOperationException e) {
            return true;
        }
    }
}
