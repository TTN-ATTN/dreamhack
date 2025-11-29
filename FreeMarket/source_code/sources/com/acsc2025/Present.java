package com.acsc2025;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/classes/com/acsc2025/Present.class */
public class Present {
    public String getFlag() throws Exception {
        Process process = null;
        try {
            try {
                process = Runtime.getRuntime().exec("cat /flag");
                InputStream is = process.getInputStream();
                try {
                    Scanner s = new Scanner(is).useDelimiter("\\A");
                    try {
                        String next = s.hasNext() ? s.next() : "";
                        if (s != null) {
                            s.close();
                        }
                        if (is != null) {
                            is.close();
                        }
                        return next;
                    } catch (Throwable th) {
                        if (s != null) {
                            try {
                                s.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        }
                        throw th;
                    }
                } catch (Throwable th3) {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (Throwable th4) {
                            th3.addSuppressed(th4);
                        }
                    }
                    throw th3;
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to read flag", e);
            }
        } finally {
            if (process != null) {
                process.destroy();
            }
        }
    }
}
