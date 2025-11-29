package com.fasterxml.jackson.core.sym;

import java.util.Arrays;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/sym/NameN.class */
public final class NameN extends Name {
    private final int q1;
    private final int q2;
    private final int q3;
    private final int q4;
    private final int qlen;
    private final int[] q;

    NameN(String name, int hash, int q1, int q2, int q3, int q4, int[] quads, int quadLen) {
        super(name, hash);
        this.q1 = q1;
        this.q2 = q2;
        this.q3 = q3;
        this.q4 = q4;
        this.q = quads;
        this.qlen = quadLen;
    }

    public static NameN construct(String name, int hash, int[] q, int qlen) {
        int[] buf;
        if (qlen < 4) {
            throw new IllegalArgumentException();
        }
        int q1 = q[0];
        int q2 = q[1];
        int q3 = q[2];
        int q4 = q[3];
        int rem = qlen - 4;
        if (rem > 0) {
            buf = Arrays.copyOfRange(q, 4, qlen);
        } else {
            buf = null;
        }
        return new NameN(name, hash, q1, q2, q3, q4, buf, qlen);
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int quad1, int quad2, int quad3) {
        return false;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] quads, int len) {
        if (len != this.qlen || quads[0] != this.q1 || quads[1] != this.q2 || quads[2] != this.q3 || quads[3] != this.q4) {
            return false;
        }
        switch (len) {
            case 5:
                if (quads[4] == this.q[0]) {
                }
                break;
            case 6:
                if (quads[5] != this.q[1]) {
                }
                if (quads[4] == this.q[0]) {
                }
                break;
            case 7:
                if (quads[6] != this.q[2]) {
                }
                if (quads[5] != this.q[1]) {
                }
                if (quads[4] == this.q[0]) {
                }
                break;
            case 8:
                if (quads[7] != this.q[3]) {
                }
                if (quads[6] != this.q[2]) {
                }
                if (quads[5] != this.q[1]) {
                }
                if (quads[4] == this.q[0]) {
                }
                break;
        }
        return false;
    }

    private final boolean _equals2(int[] quads) {
        int end = this.qlen - 4;
        for (int i = 0; i < end; i++) {
            if (quads[i + 4] != this.q[i]) {
                return false;
            }
        }
        return true;
    }
}
