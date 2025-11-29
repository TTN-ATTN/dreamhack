package freemarker.template.utility;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/UnsupportedNumberClassException.class */
public class UnsupportedNumberClassException extends RuntimeException {
    private final Class fClass;

    public UnsupportedNumberClassException(Class pClass) {
        super("Unsupported number class: " + pClass.getName());
        this.fClass = pClass;
    }

    public Class getUnsupportedClass() {
        return this.fClass;
    }
}
