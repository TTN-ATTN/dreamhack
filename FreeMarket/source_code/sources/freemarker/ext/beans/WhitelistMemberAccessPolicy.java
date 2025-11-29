package freemarker.ext.beans;

import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import java.lang.reflect.Method;
import java.util.Collection;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/WhitelistMemberAccessPolicy.class */
public class WhitelistMemberAccessPolicy extends MemberSelectorListMemberAccessPolicy {
    private static final Method TO_STRING_METHOD;
    private final boolean toStringAlwaysExposed;

    static {
        try {
            TO_STRING_METHOD = Object.class.getMethod("toString", new Class[0]);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public WhitelistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
        super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.WHITELIST, TemplateAccessible.class);
        this.toStringAlwaysExposed = forClass(Object.class).isMethodExposed(TO_STRING_METHOD);
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }
}
