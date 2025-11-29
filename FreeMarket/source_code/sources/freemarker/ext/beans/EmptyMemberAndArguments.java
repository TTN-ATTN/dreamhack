package freemarker.ext.beans;

import freemarker.core._DelayedOrdinal;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/EmptyMemberAndArguments.class */
final class EmptyMemberAndArguments extends MaybeEmptyMemberAndArguments {
    static final EmptyMemberAndArguments WRONG_NUMBER_OF_ARGUMENTS = new EmptyMemberAndArguments("No compatible overloaded variation was found; wrong number of arguments.", true, null);
    private final Object errorDescription;
    private final boolean numberOfArgumentsWrong;
    private final Object[] unwrappedArguments;

    private EmptyMemberAndArguments(Object errorDescription, boolean numberOfArgumentsWrong, Object[] unwrappedArguments) {
        this.errorDescription = errorDescription;
        this.numberOfArgumentsWrong = numberOfArgumentsWrong;
        this.unwrappedArguments = unwrappedArguments;
    }

    static EmptyMemberAndArguments noCompatibleOverload(int unwrappableIndex) {
        return new EmptyMemberAndArguments(new Object[]{"No compatible overloaded variation was found; can't convert (unwrap) the ", new _DelayedOrdinal(Integer.valueOf(unwrappableIndex)), " argument to the desired Java type."}, false, null);
    }

    static EmptyMemberAndArguments noCompatibleOverload(Object[] unwrappedArgs) {
        return new EmptyMemberAndArguments("No compatible overloaded variation was found; declared parameter types and argument value types mismatch.", false, unwrappedArgs);
    }

    static EmptyMemberAndArguments ambiguous(Object[] unwrappedArgs) {
        return new EmptyMemberAndArguments("Multiple compatible overloaded variations were found with the same priority.", false, unwrappedArgs);
    }

    static MaybeEmptyMemberAndArguments from(EmptyCallableMemberDescriptor emtpyMemberDesc, Object[] unwrappedArgs) {
        if (emtpyMemberDesc == EmptyCallableMemberDescriptor.NO_SUCH_METHOD) {
            return noCompatibleOverload(unwrappedArgs);
        }
        if (emtpyMemberDesc == EmptyCallableMemberDescriptor.AMBIGUOUS_METHOD) {
            return ambiguous(unwrappedArgs);
        }
        throw new IllegalArgumentException("Unrecognized constant: " + emtpyMemberDesc);
    }

    Object getErrorDescription() {
        return this.errorDescription;
    }

    Object[] getUnwrappedArguments() {
        return this.unwrappedArguments;
    }

    public boolean isNumberOfArgumentsWrong() {
        return this.numberOfArgumentsWrong;
    }
}
