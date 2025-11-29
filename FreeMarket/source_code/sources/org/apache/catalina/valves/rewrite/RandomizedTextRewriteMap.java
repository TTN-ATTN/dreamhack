package org.apache.catalina.valves.rewrite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/valves/rewrite/RandomizedTextRewriteMap.class */
public class RandomizedTextRewriteMap implements RewriteMap {
    protected static final StringManager sm = StringManager.getManager((Class<?>) RandomizedTextRewriteMap.class);
    private static final Random random = new Random();
    private final Map<String, String[]> map = new HashMap();

    public RandomizedTextRewriteMap(String txtFilePath, boolean useRandom) {
        String[] possibleValues;
        try {
            ConfigurationSource.Resource txtResource = ConfigFileLoader.getSource().getResource(txtFilePath);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(txtResource.getInputStream()));
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line != null) {
                            if (!line.startsWith("#") && !line.isEmpty()) {
                                String[] keyValuePair = line.split(" ", 2);
                                if (keyValuePair.length > 1) {
                                    String key = keyValuePair[0];
                                    String value = keyValuePair[1];
                                    if (useRandom && value.contains("|")) {
                                        possibleValues = value.split("\\|");
                                    } else {
                                        possibleValues = new String[]{value};
                                    }
                                    this.map.put(key, possibleValues);
                                } else {
                                    throw new IllegalArgumentException(sm.getString("rewriteMap.txtInvalidLine", line, txtFilePath));
                                }
                            }
                        } else {
                            reader.close();
                            if (txtResource != null) {
                                txtResource.close();
                            }
                            return;
                        }
                    } catch (Throwable th) {
                        try {
                            reader.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                        throw th;
                    }
                }
            } finally {
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(sm.getString("rewriteMap.txtReadError", txtFilePath), e);
        }
    }

    @Override // org.apache.catalina.valves.rewrite.RewriteMap
    public String setParameters(String params) {
        throw new IllegalArgumentException(StringManager.getManager((Class<?>) RewriteMap.class).getString("rewriteMap.tooManyParameters"));
    }

    @Override // org.apache.catalina.valves.rewrite.RewriteMap
    public String lookup(String key) {
        String[] possibleValues = this.map.get(key);
        if (possibleValues != null) {
            if (possibleValues.length > 1) {
                return possibleValues[random.nextInt(possibleValues.length)];
            }
            return possibleValues[0];
        }
        return null;
    }
}
