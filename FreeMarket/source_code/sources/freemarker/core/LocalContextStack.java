package freemarker.core;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LocalContextStack.class */
final class LocalContextStack {
    private LocalContext[] buffer = new LocalContext[8];
    private int size;

    LocalContextStack() {
    }

    void push(LocalContext localContext) {
        int newSize = this.size + 1;
        this.size = newSize;
        LocalContext[] buffer = this.buffer;
        if (buffer.length < newSize) {
            LocalContext[] newBuffer = new LocalContext[newSize * 2];
            for (int i = 0; i < buffer.length; i++) {
                newBuffer[i] = buffer[i];
            }
            buffer = newBuffer;
            this.buffer = newBuffer;
        }
        buffer[newSize - 1] = localContext;
    }

    void pop() {
        LocalContext[] localContextArr = this.buffer;
        int i = this.size - 1;
        this.size = i;
        localContextArr[i] = null;
    }

    public LocalContext get(int index) {
        return this.buffer[index];
    }

    public int size() {
        return this.size;
    }
}
