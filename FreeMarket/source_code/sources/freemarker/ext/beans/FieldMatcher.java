package freemarker.ext.beans;

import java.lang.reflect.Field;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/FieldMatcher.class */
final class FieldMatcher extends MemberMatcher<Field, String> {
    FieldMatcher() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // freemarker.ext.beans.MemberMatcher
    public String toMemberSignature(Field member) {
        return member.getName();
    }

    @Override // freemarker.ext.beans.MemberMatcher
    protected boolean matchInUpperBoundTypeSubtypes() {
        return true;
    }
}
