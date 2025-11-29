package freemarker.log;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/log/Logger.class */
public abstract class Logger {
    public static final String SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY = "org.freemarker.loggerLibrary";
    public static final int LIBRARY_AUTO = -1;
    private static final int MIN_LIBRARY_ENUM = -1;
    public static final String LIBRARY_NAME_AUTO = "auto";
    public static final int LIBRARY_NONE = 0;
    public static final String LIBRARY_NAME_NONE = "none";
    public static final int LIBRARY_JAVA = 1;

    @Deprecated
    public static final int LIBRARY_AVALON = 2;
    public static final int LIBRARY_LOG4J = 3;
    public static final int LIBRARY_COMMONS = 4;
    public static final int LIBRARY_SLF4J = 5;
    private static final int MAX_LIBRARY_ENUM = 5;
    private static final String REAL_LOG4J_PRESENCE_CLASS = "org.apache.log4j.FileAppender";
    private static final String LOG4J_OVER_SLF4J_TESTER_CLASS = "freemarker.log._Log4jOverSLF4JTester";
    private static int libraryEnum;
    private static LoggerFactory loggerFactory;
    private static boolean initializedFromSystemProperty;
    private static String categoryPrefix;
    private static final Map loggersByCategory;
    public static final String LIBRARY_NAME_JUL = "JUL";

    @Deprecated
    public static final String LIBRARY_NAME_AVALON = "Avalon";
    public static final String LIBRARY_NAME_LOG4J = "Log4j";
    public static final String LIBRARY_NAME_COMMONS_LOGGING = "CommonsLogging";
    public static final String LIBRARY_NAME_SLF4J = "SLF4J";
    private static final String[] LIBRARIES_BY_PRIORITY = {null, LIBRARY_NAME_JUL, "org.apache.log.Logger", LIBRARY_NAME_AVALON, "org.apache.log4j.Logger", LIBRARY_NAME_LOG4J, "org.apache.commons.logging.Log", LIBRARY_NAME_COMMONS_LOGGING, "org.slf4j.Logger", LIBRARY_NAME_SLF4J};

    public abstract void debug(String str);

    public abstract void debug(String str, Throwable th);

    public abstract void info(String str);

    public abstract void info(String str, Throwable th);

    public abstract void warn(String str);

    public abstract void warn(String str, Throwable th);

    public abstract void error(String str);

    public abstract void error(String str, Throwable th);

    public abstract boolean isDebugEnabled();

    public abstract boolean isInfoEnabled();

    public abstract boolean isWarnEnabled();

    public abstract boolean isErrorEnabled();

    public abstract boolean isFatalEnabled();

    static {
        if (LIBRARIES_BY_PRIORITY.length / 2 != 5) {
            throw new AssertionError();
        }
        categoryPrefix = "";
        loggersByCategory = new HashMap();
    }

    private static String getAvailabilityCheckClassName(int libraryEnum2) {
        if (libraryEnum2 == -1 || libraryEnum2 == 0) {
            return null;
        }
        return LIBRARIES_BY_PRIORITY[(libraryEnum2 - 1) * 2];
    }

    private static String getLibraryName(int libraryEnum2) {
        if (libraryEnum2 == -1) {
            return LIBRARY_NAME_AUTO;
        }
        if (libraryEnum2 == 0) {
            return "none";
        }
        return LIBRARIES_BY_PRIORITY[((libraryEnum2 - 1) * 2) + 1];
    }

    private static boolean isAutoDetected(int libraryEnum2) {
        return (libraryEnum2 == -1 || libraryEnum2 == 0 || libraryEnum2 == 5 || libraryEnum2 == 4) ? false : true;
    }

    @Deprecated
    public static void selectLoggerLibrary(int libraryEnum2) throws ClassNotFoundException {
        if (libraryEnum2 < -1 || libraryEnum2 > 5) {
            throw new IllegalArgumentException("Library enum value out of range");
        }
        synchronized (Logger.class) {
            boolean loggerFactoryWasAlreadySet = loggerFactory != null;
            if (!loggerFactoryWasAlreadySet || libraryEnum2 != libraryEnum) {
                ensureLoggerFactorySet(true);
                if (!initializedFromSystemProperty || loggerFactory == null) {
                    int replacedLibraryEnum = libraryEnum;
                    setLibrary(libraryEnum2);
                    loggersByCategory.clear();
                    if (loggerFactoryWasAlreadySet) {
                        logWarnInLogger("Logger library was already set earlier to \"" + getLibraryName(replacedLibraryEnum) + "\"; change to \"" + getLibraryName(libraryEnum2) + "\" won't effect loggers created earlier.");
                    }
                } else if (libraryEnum2 != libraryEnum) {
                    logWarnInLogger("Ignored " + Logger.class.getName() + ".selectLoggerLibrary(\"" + getLibraryName(libraryEnum2) + "\") call, because the \"" + SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY + "\" system property is set to \"" + getLibraryName(libraryEnum) + "\".");
                }
            }
        }
    }

    @Deprecated
    public static void setCategoryPrefix(String prefix) {
        synchronized (Logger.class) {
            if (prefix == null) {
                throw new IllegalArgumentException();
            }
            categoryPrefix = prefix;
        }
    }

    public static Logger getLogger(String category) {
        Logger logger;
        if (categoryPrefix.length() != 0) {
            category = categoryPrefix + category;
        }
        synchronized (loggersByCategory) {
            Logger logger2 = (Logger) loggersByCategory.get(category);
            if (logger2 == null) {
                ensureLoggerFactorySet(false);
                logger2 = loggerFactory.getLogger(category);
                loggersByCategory.put(category, logger2);
            }
            logger = logger2;
        }
        return logger;
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x00c6 A[DONT_GENERATE, FINALLY_INSNS] */
    /* JADX WARN: Removed duplicated region for block: B:55:0x00cb A[DONT_GENERATE, FINALLY_INSNS] */
    /* JADX WARN: Removed duplicated region for block: B:71:0x00dd A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static void ensureLoggerFactorySet(boolean r5) {
        /*
            Method dump skipped, instructions count: 255
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: freemarker.log.Logger.ensureLoggerFactorySet(boolean):void");
    }

    private static LoggerFactory createLoggerFactory(int libraryEnum2) throws ClassNotFoundException {
        if (libraryEnum2 == -1) {
            int libraryEnumToTry = 5;
            while (libraryEnumToTry >= -1) {
                if (isAutoDetected(libraryEnumToTry)) {
                    if (libraryEnumToTry == 3 && hasLog4LibraryThatDelegatesToWorkingSLF4J()) {
                        libraryEnumToTry = 5;
                    }
                    try {
                        return createLoggerFactoryForNonAuto(libraryEnumToTry);
                    } catch (ClassNotFoundException e) {
                    } catch (Throwable e2) {
                        logErrorInLogger("Unexpected error when initializing logging for \"" + getLibraryName(libraryEnumToTry) + "\".", e2);
                    }
                }
                libraryEnumToTry--;
            }
            logWarnInLogger("Auto detecton couldn't set up any logger libraries; FreeMarker logging suppressed.");
            return new _NullLoggerFactory();
        }
        return createLoggerFactoryForNonAuto(libraryEnum2);
    }

    private static LoggerFactory createLoggerFactoryForNonAuto(int libraryEnum2) throws ClassNotFoundException {
        String availabilityCheckClassName = getAvailabilityCheckClassName(libraryEnum2);
        if (availabilityCheckClassName != null) {
            Class.forName(availabilityCheckClassName);
            String libraryName = getLibraryName(libraryEnum2);
            try {
                return (LoggerFactory) Class.forName("freemarker.log._" + libraryName + "LoggerFactory").newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unexpected error when creating logger factory for \"" + libraryName + "\".", e);
            }
        }
        if (libraryEnum2 == 1) {
            return new _JULLoggerFactory();
        }
        if (libraryEnum2 == 0) {
            return new _NullLoggerFactory();
        }
        throw new RuntimeException("Bug");
    }

    private static boolean hasLog4LibraryThatDelegatesToWorkingSLF4J() throws ClassNotFoundException {
        try {
            Class.forName(getAvailabilityCheckClassName(3));
            Class.forName(getAvailabilityCheckClassName(5));
            try {
                Class.forName(REAL_LOG4J_PRESENCE_CLASS);
                return false;
            } catch (ClassNotFoundException e) {
                try {
                    Object r = Class.forName(LOG4J_OVER_SLF4J_TESTER_CLASS).getMethod("test", new Class[0]).invoke(null, new Object[0]);
                    return ((Boolean) r).booleanValue();
                } catch (Throwable th) {
                    return false;
                }
            }
        } catch (Throwable th2) {
            return false;
        }
    }

    private static synchronized void setLibrary(int libraryEnum2) throws ClassNotFoundException {
        loggerFactory = createLoggerFactory(libraryEnum2);
        libraryEnum = libraryEnum2;
    }

    private static void logWarnInLogger(String message) {
        logInLogger(false, message, null);
    }

    private static void logErrorInLogger(String message, Throwable exception) {
        logInLogger(true, message, exception);
    }

    private static void logInLogger(boolean error, String message, Throwable exception) {
        boolean canUseRealLogger;
        synchronized (Logger.class) {
            canUseRealLogger = (loggerFactory == null || (loggerFactory instanceof _NullLoggerFactory)) ? false : true;
        }
        if (canUseRealLogger) {
            try {
                Logger logger = getLogger("freemarker.logger");
                if (error) {
                    logger.error(message);
                } else {
                    logger.warn(message);
                }
            } catch (Throwable th) {
                canUseRealLogger = false;
            }
        }
        if (!canUseRealLogger) {
            System.err.println((error ? "ERROR" : "WARN") + " " + LoggerFactory.class.getName() + ": " + message);
            if (exception != null) {
                System.err.println("\tException: " + tryToString(exception));
                while (exception.getCause() != null) {
                    exception = exception.getCause();
                    System.err.println("\tCaused by: " + tryToString(exception));
                }
            }
        }
    }

    private static String getSystemProperty(final String key) {
        try {
            return (String) AccessController.doPrivileged(new PrivilegedAction() { // from class: freemarker.log.Logger.1
                @Override // java.security.PrivilegedAction
                public Object run() {
                    return System.getProperty(key, null);
                }
            });
        } catch (AccessControlException e) {
            logWarnInLogger("Insufficient permissions to read system property \"" + key + "\".");
            return null;
        } catch (Throwable e2) {
            logErrorInLogger("Failed to read system property \"" + key + "\".", e2);
            return null;
        }
    }

    private static String tryToString(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return object.toString();
        } catch (Throwable th) {
            return object.getClass().getName();
        }
    }
}
