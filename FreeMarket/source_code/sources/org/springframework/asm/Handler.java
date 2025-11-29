package org.springframework.asm;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/asm/Handler.class */
final class Handler {
    final Label startPc;
    final Label endPc;
    final Label handlerPc;
    final int catchType;
    final String catchTypeDescriptor;
    Handler nextHandler;

    Handler(final Label startPc, final Label endPc, final Label handlerPc, final int catchType, final String catchTypeDescriptor) {
        this.startPc = startPc;
        this.endPc = endPc;
        this.handlerPc = handlerPc;
        this.catchType = catchType;
        this.catchTypeDescriptor = catchTypeDescriptor;
    }

    Handler(final Handler handler, final Label startPc, final Label endPc) {
        this(startPc, endPc, handler.handlerPc, handler.catchType, handler.catchTypeDescriptor);
        this.nextHandler = handler.nextHandler;
    }

    static Handler removeRange(final Handler firstHandler, final Label start, final Label end) {
        if (firstHandler == null) {
            return null;
        }
        firstHandler.nextHandler = removeRange(firstHandler.nextHandler, start, end);
        int handlerStart = firstHandler.startPc.bytecodeOffset;
        int handlerEnd = firstHandler.endPc.bytecodeOffset;
        int rangeStart = start.bytecodeOffset;
        int rangeEnd = end == null ? Integer.MAX_VALUE : end.bytecodeOffset;
        if (rangeStart >= handlerEnd || rangeEnd <= handlerStart) {
            return firstHandler;
        }
        if (rangeStart <= handlerStart) {
            if (rangeEnd >= handlerEnd) {
                return firstHandler.nextHandler;
            }
            return new Handler(firstHandler, end, firstHandler.endPc);
        }
        if (rangeEnd >= handlerEnd) {
            return new Handler(firstHandler, firstHandler.startPc, start);
        }
        firstHandler.nextHandler = new Handler(firstHandler, end, firstHandler.endPc);
        return new Handler(firstHandler, firstHandler.startPc, start);
    }

    static int getExceptionTableLength(final Handler firstHandler) {
        int length = 0;
        Handler handler = firstHandler;
        while (true) {
            Handler handler2 = handler;
            if (handler2 != null) {
                length++;
                handler = handler2.nextHandler;
            } else {
                return length;
            }
        }
    }

    static int getExceptionTableSize(final Handler firstHandler) {
        return 2 + (8 * getExceptionTableLength(firstHandler));
    }

    static void putExceptionTable(final Handler firstHandler, final ByteVector output) {
        output.putShort(getExceptionTableLength(firstHandler));
        Handler handler = firstHandler;
        while (true) {
            Handler handler2 = handler;
            if (handler2 != null) {
                output.putShort(handler2.startPc.bytecodeOffset).putShort(handler2.endPc.bytecodeOffset).putShort(handler2.handlerPc.bytecodeOffset).putShort(handler2.catchType);
                handler = handler2.nextHandler;
            } else {
                return;
            }
        }
    }
}
