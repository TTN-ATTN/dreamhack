package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BreakOrContinueException.class */
class BreakOrContinueException extends FlowControlException {
    static final BreakOrContinueException BREAK_INSTANCE = new BreakOrContinueException();
    static final BreakOrContinueException CONTINUE_INSTANCE = new BreakOrContinueException();

    private BreakOrContinueException() {
    }
}
