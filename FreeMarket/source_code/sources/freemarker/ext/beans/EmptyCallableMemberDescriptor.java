package freemarker.ext.beans;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/EmptyCallableMemberDescriptor.class */
final class EmptyCallableMemberDescriptor extends MaybeEmptyCallableMemberDescriptor {
    static final EmptyCallableMemberDescriptor NO_SUCH_METHOD = new EmptyCallableMemberDescriptor();
    static final EmptyCallableMemberDescriptor AMBIGUOUS_METHOD = new EmptyCallableMemberDescriptor();

    private EmptyCallableMemberDescriptor() {
    }
}
