package freemarker.debug;

import java.io.Serializable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/debug/Breakpoint.class */
public class Breakpoint implements Serializable, Comparable {
    private static final long serialVersionUID = 1;
    private final String templateName;
    private final int line;

    public Breakpoint(String templateName, int line) {
        this.templateName = templateName;
        this.line = line;
    }

    public int getLine() {
        return this.line;
    }

    public String getTemplateName() {
        return this.templateName;
    }

    public int hashCode() {
        return this.templateName.hashCode() + (31 * this.line);
    }

    public boolean equals(Object o) {
        if (o instanceof Breakpoint) {
            Breakpoint b = (Breakpoint) o;
            return b.templateName.equals(this.templateName) && b.line == this.line;
        }
        return false;
    }

    @Override // java.lang.Comparable
    public int compareTo(Object o) {
        Breakpoint b = (Breakpoint) o;
        int r = this.templateName.compareTo(b.templateName);
        return r == 0 ? this.line - b.line : r;
    }

    public String getLocationString() {
        return this.templateName + ":" + this.line;
    }
}
