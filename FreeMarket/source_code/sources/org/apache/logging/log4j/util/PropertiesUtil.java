package org.apache.logging.log4j.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.util.PropertySource;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/PropertiesUtil.class */
public final class PropertiesUtil {
    private static final String LOG4J_SYSTEM_PROPERTIES_FILE_NAME = "log4j2.system.properties";
    private final Environment environment;
    private static final String LOG4J_PROPERTIES_FILE_NAME = "log4j2.component.properties";
    private static final PropertiesUtil LOG4J_PROPERTIES = new PropertiesUtil(LOG4J_PROPERTIES_FILE_NAME);

    public PropertiesUtil(final Properties props) {
        this.environment = new Environment(new PropertiesPropertySource(props));
    }

    public PropertiesUtil(final String propertiesFileName) {
        this.environment = new Environment(new PropertyFilePropertySource(propertiesFileName));
    }

    static Properties loadClose(final InputStream in, final Object source) throws IOException {
        Properties props = new Properties();
        if (null != in) {
            try {
                try {
                    props.load(in);
                    try {
                        in.close();
                    } catch (IOException e) {
                        LowLevelLogUtil.logException("Unable to close " + source, e);
                    }
                } catch (IOException e2) {
                    LowLevelLogUtil.logException("Unable to read " + source, e2);
                    try {
                        in.close();
                    } catch (IOException e3) {
                        LowLevelLogUtil.logException("Unable to close " + source, e3);
                    }
                }
            } catch (Throwable th) {
                try {
                    in.close();
                } catch (IOException e4) {
                    LowLevelLogUtil.logException("Unable to close " + source, e4);
                }
                throw th;
            }
        }
        return props;
    }

    public static PropertiesUtil getProperties() {
        return LOG4J_PROPERTIES;
    }

    public boolean hasProperty(final String name) {
        return this.environment.containsKey(name);
    }

    public boolean getBooleanProperty(final String name) {
        return getBooleanProperty(name, false);
    }

    public boolean getBooleanProperty(final String name, final boolean defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    public boolean getBooleanProperty(final String name, final boolean defaultValueIfAbsent, final boolean defaultValueIfPresent) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValueIfAbsent : prop.isEmpty() ? defaultValueIfPresent : "true".equalsIgnoreCase(prop);
    }

    public Boolean getBooleanProperty(final String[] prefixes, String key, Supplier<Boolean> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Boolean.valueOf(getBooleanProperty(prefix + key));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public Charset getCharsetProperty(final String name) {
        return getCharsetProperty(name, Charset.defaultCharset());
    }

    public Charset getCharsetProperty(final String name, final Charset defaultValue) {
        String charsetName = getStringProperty(name);
        if (charsetName == null) {
            return defaultValue;
        }
        if (Charset.isSupported(charsetName)) {
            return Charset.forName(charsetName);
        }
        ResourceBundle bundle = getCharsetsResourceBundle();
        if (bundle.containsKey(name)) {
            String mapped = bundle.getString(name);
            if (Charset.isSupported(mapped)) {
                return Charset.forName(mapped);
            }
        }
        LowLevelLogUtil.log("Unable to get Charset '" + charsetName + "' for property '" + name + "', using default " + defaultValue + " and continuing.");
        return defaultValue;
    }

    public double getDoubleProperty(final String name, final double defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Double.parseDouble(prop);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public int getIntegerProperty(final String name, final int defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Integer.parseInt(prop.trim());
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public Integer getIntegerProperty(final String[] prefixes, String key, Supplier<Integer> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Integer.valueOf(getIntegerProperty(prefix + key, 0));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public long getLongProperty(final String name, final long defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            try {
                return Long.parseLong(prop);
            } catch (Exception e) {
            }
        }
        return defaultValue;
    }

    public Long getLongProperty(final String[] prefixes, String key, Supplier<Long> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return Long.valueOf(getLongProperty(prefix + key, 0L));
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public Duration getDurationProperty(final String name, Duration defaultValue) {
        String prop = getStringProperty(name);
        if (prop != null) {
            return TimeUnit.getDuration(prop);
        }
        return defaultValue;
    }

    public Duration getDurationProperty(final String[] prefixes, String key, Supplier<Duration> supplier) {
        for (String prefix : prefixes) {
            if (hasProperty(prefix + key)) {
                return getDurationProperty(prefix + key, null);
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public String getStringProperty(final String[] prefixes, String key, Supplier<String> supplier) {
        for (String prefix : prefixes) {
            String result = getStringProperty(prefix + key);
            if (result != null) {
                return result;
            }
        }
        if (supplier != null) {
            return supplier.get();
        }
        return null;
    }

    public String getStringProperty(final String name) {
        return this.environment.get(name);
    }

    public String getStringProperty(final String name, final String defaultValue) {
        String prop = getStringProperty(name);
        return prop == null ? defaultValue : prop;
    }

    public static Properties getSystemProperties() {
        try {
            return new Properties(System.getProperties());
        } catch (SecurityException ex) {
            LowLevelLogUtil.logException("Unable to access system properties.", ex);
            return new Properties();
        }
    }

    public void reload() {
        this.environment.reload();
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/PropertiesUtil$Environment.class */
    private static class Environment {
        private final Set<PropertySource> sources;
        private final Map<CharSequence, String> literal;
        private final Map<CharSequence, String> normalized;
        private final Map<List<CharSequence>, String> tokenized;

        private Environment(final PropertySource propertySource) {
            this.sources = new TreeSet(new PropertySource.Comparator());
            this.literal = new ConcurrentHashMap();
            this.normalized = new ConcurrentHashMap();
            this.tokenized = new ConcurrentHashMap();
            PropertyFilePropertySource sysProps = new PropertyFilePropertySource(PropertiesUtil.LOG4J_SYSTEM_PROPERTIES_FILE_NAME);
            try {
                sysProps.forEach((key, value) -> {
                    if (System.getProperty(key) == null) {
                        System.setProperty(key, value);
                    }
                });
            } catch (SecurityException e) {
            }
            this.sources.add(propertySource);
            for (ClassLoader classLoader : LoaderUtil.getClassLoaders()) {
                try {
                    Iterator it = ServiceLoader.load(PropertySource.class, classLoader).iterator();
                    while (it.hasNext()) {
                        PropertySource source = (PropertySource) it.next();
                        this.sources.add(source);
                    }
                } catch (Throwable th) {
                }
            }
            reload();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void reload() {
            this.literal.clear();
            this.normalized.clear();
            this.tokenized.clear();
            for (PropertySource source : this.sources) {
                source.forEach((key, value) -> {
                    if (key != null && value != null) {
                        this.literal.put(key, value);
                        List<CharSequence> tokens = PropertySource.Util.tokenize(key);
                        if (tokens.isEmpty()) {
                            this.normalized.put(source.getNormalForm(Collections.singleton(key)), value);
                        } else {
                            this.normalized.put(source.getNormalForm(tokens), value);
                            this.tokenized.put(tokens, value);
                        }
                    }
                });
            }
        }

        private static boolean hasSystemProperty(final String key) {
            try {
                return System.getProperties().containsKey(key);
            } catch (SecurityException e) {
                return false;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String get(final String key) {
            if (this.normalized.containsKey(key)) {
                return this.normalized.get(key);
            }
            if (this.literal.containsKey(key)) {
                return this.literal.get(key);
            }
            if (hasSystemProperty(key)) {
                return System.getProperty(key);
            }
            for (PropertySource source : this.sources) {
                if (source.containsProperty(key)) {
                    return source.getProperty(key);
                }
            }
            return this.tokenized.get(PropertySource.Util.tokenize(key));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean containsKey(final String key) {
            return this.normalized.containsKey(key) || this.literal.containsKey(key) || hasSystemProperty(key) || this.tokenized.containsKey(PropertySource.Util.tokenize(key));
        }
    }

    public static Properties extractSubset(final Properties properties, final String prefix) {
        Properties subset = new Properties();
        if (prefix == null || prefix.length() == 0) {
            return subset;
        }
        String prefixToMatch = prefix.charAt(prefix.length() - 1) != '.' ? prefix + '.' : prefix;
        List<String> keys = new ArrayList<>();
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith(prefixToMatch)) {
                subset.setProperty(key.substring(prefixToMatch.length()), properties.getProperty(key));
                keys.add(key);
            }
        }
        Iterator<String> it = keys.iterator();
        while (it.hasNext()) {
            properties.remove(it.next());
        }
        return subset;
    }

    static ResourceBundle getCharsetsResourceBundle() {
        return ResourceBundle.getBundle("Log4j-charsets");
    }

    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties) {
        return partitionOnCommonPrefixes(properties, false);
    }

    public static Map<String, Properties> partitionOnCommonPrefixes(final Properties properties, final boolean includeBaseKey) {
        Map<String, Properties> parts = new ConcurrentHashMap<>();
        for (String key : properties.stringPropertyNames()) {
            int idx = key.indexOf(46);
            if (idx < 0) {
                if (includeBaseKey) {
                    if (!parts.containsKey(key)) {
                        parts.put(key, new Properties());
                    }
                    parts.get(key).setProperty("", properties.getProperty(key));
                }
            } else {
                String prefix = key.substring(0, idx);
                if (!parts.containsKey(prefix)) {
                    parts.put(prefix, new Properties());
                }
                parts.get(prefix).setProperty(key.substring(idx + 1), properties.getProperty(key));
            }
        }
        return parts;
    }

    public boolean isOsWindows() {
        return getStringProperty("os.name", "").startsWith("Windows");
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/log4j-api-2.17.2.jar:org/apache/logging/log4j/util/PropertiesUtil$TimeUnit.class */
    private enum TimeUnit {
        NANOS("ns,nano,nanos,nanosecond,nanoseconds", ChronoUnit.NANOS),
        MICROS("us,micro,micros,microsecond,microseconds", ChronoUnit.MICROS),
        MILLIS("ms,milli,millis,millsecond,milliseconds", ChronoUnit.MILLIS),
        SECONDS("s,second,seconds", ChronoUnit.SECONDS),
        MINUTES("m,minute,minutes", ChronoUnit.MINUTES),
        HOURS("h,hour,hours", ChronoUnit.HOURS),
        DAYS("d,day,days", ChronoUnit.DAYS);

        private final String[] descriptions;
        private final ChronoUnit timeUnit;

        TimeUnit(String descriptions, ChronoUnit timeUnit) {
            this.descriptions = descriptions.split(",");
            this.timeUnit = timeUnit;
        }

        ChronoUnit getTimeUnit() {
            return this.timeUnit;
        }

        static Duration getDuration(String time) throws NumberFormatException {
            String value = time.trim();
            TemporalUnit temporalUnit = ChronoUnit.MILLIS;
            long timeVal = 0;
            for (TimeUnit timeUnit : values()) {
                for (String suffix : timeUnit.descriptions) {
                    if (value.endsWith(suffix)) {
                        temporalUnit = timeUnit.timeUnit;
                        timeVal = Long.parseLong(value.substring(0, value.length() - suffix.length()));
                    }
                }
            }
            return Duration.of(timeVal, temporalUnit);
        }
    }
}
