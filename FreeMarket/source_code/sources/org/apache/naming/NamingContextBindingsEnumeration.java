package org.apache.naming;

import java.util.Iterator;
import javax.naming.Binding;
import javax.naming.CompositeName;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/naming/NamingContextBindingsEnumeration.class */
public class NamingContextBindingsEnumeration implements NamingEnumeration<Binding> {
    protected final Iterator<NamingEntry> iterator;
    private final Context ctx;

    public NamingContextBindingsEnumeration(Iterator<NamingEntry> entries, Context ctx) {
        this.iterator = entries;
        this.ctx = ctx;
    }

    /* renamed from: next, reason: merged with bridge method [inline-methods] */
    public Binding m1014next() throws NamingException {
        return nextElementInternal();
    }

    public boolean hasMore() throws NamingException {
        return this.iterator.hasNext();
    }

    public void close() throws NamingException {
    }

    public boolean hasMoreElements() {
        return this.iterator.hasNext();
    }

    /* renamed from: nextElement, reason: merged with bridge method [inline-methods] */
    public Binding m1015nextElement() {
        try {
            return nextElementInternal();
        } catch (NamingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: javax.naming.NamingException */
    private Binding nextElementInternal() throws NamingException {
        Object value;
        NamingEntry entry = this.iterator.next();
        if (entry.type == 2 || entry.type == 1) {
            try {
                value = this.ctx.lookup(new CompositeName(entry.name));
            } catch (NamingException e) {
                throw e;
            } catch (Exception e2) {
                NamingException ne = new NamingException(e2.getMessage());
                ne.initCause(e2);
                throw ne;
            }
        } else {
            value = entry.value;
        }
        return new Binding(entry.name, value.getClass().getName(), value, true);
    }
}
