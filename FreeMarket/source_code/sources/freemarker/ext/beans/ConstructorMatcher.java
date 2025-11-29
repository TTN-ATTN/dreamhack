package freemarker.ext.beans;

import java.lang.reflect.Constructor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/ConstructorMatcher.class */
final class ConstructorMatcher extends MemberMatcher<Constructor<?>, ExecutableMemberSignature> {
    ConstructorMatcher() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.ext.beans.MemberMatcher
    public ExecutableMemberSignature toMemberSignature(Constructor<?> member) {
        return new ExecutableMemberSignature(member);
    }

    @Override // freemarker.ext.beans.MemberMatcher
    protected boolean matchInUpperBoundTypeSubtypes() {
        return false;
    }
}
