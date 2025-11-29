package freemarker.ext.beans;

import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BlacklistMemberAccessPolicy.class */
public class BlacklistMemberAccessPolicy extends MemberSelectorListMemberAccessPolicy {
    private final boolean toStringAlwaysExposed;

    public BlacklistMemberAccessPolicy(Collection<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> memberSelectors) {
        super(memberSelectors, MemberSelectorListMemberAccessPolicy.ListType.BLACKLIST, null);
        boolean toStringBlacklistedAnywhere = false;
        Iterator<? extends MemberSelectorListMemberAccessPolicy.MemberSelector> it = memberSelectors.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector = it.next();
            Method method = memberSelector.getMethod();
            if (method != null && method.getName().equals("toString") && method.getParameterTypes().length == 0) {
                toStringBlacklistedAnywhere = true;
                break;
            }
        }
        this.toStringAlwaysExposed = !toStringBlacklistedAnywhere;
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }
}
