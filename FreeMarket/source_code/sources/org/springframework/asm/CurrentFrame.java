package org.springframework.asm;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/asm/CurrentFrame.class */
final class CurrentFrame extends Frame {
    CurrentFrame(final Label owner) {
        super(owner);
    }

    @Override // org.springframework.asm.Frame
    void execute(final int opcode, final int arg, final Symbol symbolArg, final SymbolTable symbolTable) {
        super.execute(opcode, arg, symbolArg, symbolTable);
        Frame successor = new Frame(null);
        merge(symbolTable, successor, 0);
        copyFrom(successor);
    }
}
