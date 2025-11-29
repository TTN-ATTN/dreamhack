package freemarker.ext.beans;

import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MethodMatcher.class */
final class MethodMatcher extends MemberMatcher<Method, ExecutableMemberSignature> {
    MethodMatcher() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.ext.beans.MemberMatcher
    public ExecutableMemberSignature toMemberSignature(Method member) {
        return new ExecutableMemberSignature(member);
    }

    @Override // freemarker.ext.beans.MemberMatcher
    protected boolean matchInUpperBoundTypeSubtypes() {
        return true;
    }
}
