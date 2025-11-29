package freemarker.debug;

import java.util.EventObject;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/EnvironmentSuspendedEvent.class */
public class EnvironmentSuspendedEvent extends EventObject {
    private static final long serialVersionUID = 1;
    private final String name;
    private final int line;
    private final DebuggedEnvironment env;

    public EnvironmentSuspendedEvent(Object source, String templateName, int line, DebuggedEnvironment env) {
        super(source);
        this.name = templateName;
        this.line = line;
        this.env = env;
    }

    public String getName() {
        return this.name;
    }

    public int getLine() {
        return this.line;
    }

    public DebuggedEnvironment getEnvironment() {
        return this.env;
    }
}
