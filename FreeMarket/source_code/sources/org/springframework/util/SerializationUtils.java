package org.springframework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/util/SerializationUtils.class */
public abstract class SerializationUtils {
    @Nullable
    public static byte[] serialize(@Nullable Object object) throws IOException {
        if (object == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            Throwable th = null;
            try {
                try {
                    oos.writeObject(object);
                    oos.flush();
                    if (oos != null) {
                        if (0 != 0) {
                            try {
                                oos.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            oos.close();
                        }
                    }
                    return baos.toByteArray();
                } finally {
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), ex);
        }
    }

    @Nullable
    public static Object deserialize(@Nullable byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
            Throwable th = null;
            try {
                Object object = ois.readObject();
                if (ois != null) {
                    if (0 != 0) {
                        try {
                            ois.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        ois.close();
                    }
                }
                return object;
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to deserialize object", ex);
        } catch (ClassNotFoundException ex2) {
            throw new IllegalStateException("Failed to deserialize object type", ex2);
        }
    }
}
