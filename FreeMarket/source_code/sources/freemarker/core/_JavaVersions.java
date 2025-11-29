package freemarker.core;

import freemarker.log.Logger;
import freemarker.template.Version;
import freemarker.template.utility.SecurityUtilities;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_JavaVersions.class */
public final class _JavaVersions {
    public static final _Java9 JAVA_9;
    public static final _Java16 JAVA_16;

    private _JavaVersions() {
    }

    static {
        JAVA_9 = isAtLeast(9, "java.lang.Module") ? (_Java9) tryLoadJavaSupportSingleton(9, _Java9.class) : null;
        JAVA_16 = isAtLeast(16, "java.net.UnixDomainSocketAddress") ? (_Java16) tryLoadJavaSupportSingleton(16, _Java16.class) : null;
    }

    private static <T> T tryLoadJavaSupportSingleton(int i, Class<T> cls) {
        String str = "freemarker.core._Java" + i + "Impl";
        try {
            return (T) Class.forName(str).getField("INSTANCE").get(null);
        } catch (Exception e) {
            try {
                if (e instanceof ClassNotFoundException) {
                    Logger.getLogger("freemarker.runtime").warn("Seems that the Java " + i + " support class (" + str + ") wasn't included in the build");
                } else {
                    Logger.getLogger("freemarker.runtime").error("Failed to load Java " + i + " support class", e);
                }
                return null;
            } catch (Exception e2) {
                return null;
            }
        }
    }

    private static boolean isAtLeast(int minimumMajorVersion, String proofClassPresence) throws ClassNotFoundException {
        boolean result = false;
        String vStr = SecurityUtilities.getSystemProperty("java.version", (String) null);
        if (vStr != null) {
            try {
                Version v = new Version(vStr);
                result = v.getMajor() >= minimumMajorVersion;
            } catch (Exception e) {
            }
        } else {
            try {
                Class.forName(proofClassPresence);
                result = true;
            } catch (Exception e2) {
            }
        }
        return result;
    }
}
