package freemarker.template.utility;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/NullArgumentException.class */
public class NullArgumentException extends IllegalArgumentException {
    public NullArgumentException() {
        super("The argument can't be null");
    }

    public NullArgumentException(String argumentName) {
        super("The \"" + argumentName + "\" argument can't be null");
    }

    public NullArgumentException(String argumentName, String details) {
        super("The \"" + argumentName + "\" argument can't be null. " + details);
    }

    public static void check(String argumentName, Object argumentValue) {
        if (argumentValue == null) {
            throw new NullArgumentException(argumentName);
        }
    }

    public static void check(Object argumentValue) {
        if (argumentValue == null) {
            throw new NullArgumentException();
        }
    }
}
