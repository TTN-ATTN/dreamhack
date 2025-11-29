package org.apache.tomcat.util.json;

import freemarker.core.FMParserConstants;
import java.io.IOException;
import java.io.PrintStream;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;
import org.springframework.beans.PropertyAccessor;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/tomcat/util/json/JSONParserTokenManager.class */
public class JSONParserTokenManager implements JSONParserConstants {
    public PrintStream debugStream;
    int curLexState;
    int defaultLexState;
    int jjnewStateCnt;
    int jjround;
    int jjmatchedPos;
    int jjmatchedKind;
    protected JavaCharStream input_stream;
    private final int[] jjrounds;
    private final int[] jjstateSet;
    private final StringBuilder jjimage;
    private StringBuilder image;
    private int jjimageLen;
    private int lengthOfMatch;
    protected int curChar;
    static final long[] jjbitVec0 = {-2, -1, -1, -1};
    static final long[] jjbitVec2 = {0, 0, -1, -1};
    public static final String[] jjstrLiteralImages = {"", null, null, null, null, null, ",", "{", "}", ":", PropertyAccessor.PROPERTY_KEY_PREFIX, "]", null, null, null, null, null, null, null, null, null, null, "''", "\"\"", null, null, null, null, null};
    static final int[] jjnextStates = {25, 26, 28, 34, 17, 20, 27, 35, 29, 25, 28, 29, 6, 7, 9, 11, 12, 14, 1, 2, 18, 19, 21, 23, 32, 33};
    public static final String[] lexStateNames = {"DEFAULT"};
    public static final int[] jjnewLexState = {-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1};
    static final long[] jjtoToken = {483364801};
    static final long[] jjtoSkip = {62};
    static final long[] jjtoSpecial = {0};
    static final long[] jjtoMore = {0};

    public void setDebugStream(PrintStream ds) {
        this.debugStream = ds;
    }

    private final int jjStopStringLiteralDfa_0(int pos, long active0) {
        switch (pos) {
            case 0:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    break;
                } else if ((active0 & 4194304) == 0) {
                    if ((active0 & 8388608) != 0) {
                    }
                }
                break;
            case 1:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 1;
                    break;
                }
                break;
            case 2:
                if ((active0 & 917504) != 0) {
                    this.jjmatchedKind = 28;
                    this.jjmatchedPos = 2;
                    break;
                }
                break;
            case 3:
                if ((active0 & 655360) == 0) {
                    if ((active0 & 262144) != 0) {
                        this.jjmatchedKind = 28;
                        this.jjmatchedPos = 3;
                        break;
                    }
                }
                break;
        }
        return 15;
    }

    private final int jjStartNfa_0(int pos, long active0) {
        return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
    }

    private int jjStopAtPos(int pos, int kind) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        return pos + 1;
    }

    private int jjMoveStringLiteralDfa0_0() {
        switch (this.curChar) {
            case 34:
                return jjMoveStringLiteralDfa1_0(8388608L);
            case 39:
                return jjMoveStringLiteralDfa1_0(4194304L);
            case 44:
                return jjStopAtPos(0, 6);
            case 58:
                return jjStopAtPos(0, 9);
            case 70:
            case 102:
                return jjMoveStringLiteralDfa1_0(262144L);
            case FMParserConstants.UNKNOWN_DIRECTIVE /* 78 */:
            case 110:
                return jjMoveStringLiteralDfa1_0(524288L);
            case 84:
            case 116:
                return jjMoveStringLiteralDfa1_0(131072L);
            case 91:
                return jjStopAtPos(0, 10);
            case 93:
                return jjStopAtPos(0, 11);
            case 123:
                return jjStopAtPos(0, 7);
            case 125:
                return jjStopAtPos(0, 8);
            default:
                return jjMoveNfa_0(0, 0);
        }
    }

    private int jjMoveStringLiteralDfa1_0(long active0) {
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 34:
                    if ((active0 & 8388608) != 0) {
                        return jjStopAtPos(1, 23);
                    }
                    break;
                case 39:
                    if ((active0 & 4194304) != 0) {
                        return jjStopAtPos(1, 22);
                    }
                    break;
                case 65:
                case 97:
                    return jjMoveStringLiteralDfa2_0(active0, 262144L);
                case 82:
                case 114:
                    return jjMoveStringLiteralDfa2_0(active0, 131072L);
                case Opcodes.CASTORE /* 85 */:
                case 117:
                    return jjMoveStringLiteralDfa2_0(active0, 524288L);
            }
            return jjStartNfa_0(0, active0);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(0, active0);
            return 1;
        }
    }

    private int jjMoveStringLiteralDfa2_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(0, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 76:
                case 108:
                    return jjMoveStringLiteralDfa3_0(active02, 786432L);
                case Opcodes.CASTORE /* 85 */:
                case 117:
                    return jjMoveStringLiteralDfa3_0(active02, 131072L);
                default:
                    return jjStartNfa_0(1, active02);
            }
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(1, active02);
            return 2;
        }
    }

    private int jjMoveStringLiteralDfa3_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(1, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 69:
                case 101:
                    if ((active02 & 131072) != 0) {
                        return jjStartNfaWithStates_0(3, 17, 15);
                    }
                    break;
                case 76:
                case 108:
                    if ((active02 & 524288) != 0) {
                        return jjStartNfaWithStates_0(3, 19, 15);
                    }
                    break;
                case 83:
                case 115:
                    return jjMoveStringLiteralDfa4_0(active02, 262144L);
            }
            return jjStartNfa_0(2, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(2, active02);
            return 3;
        }
    }

    private int jjMoveStringLiteralDfa4_0(long old0, long active0) {
        long active02 = active0 & old0;
        if (active02 == 0) {
            return jjStartNfa_0(2, old0);
        }
        try {
            this.curChar = this.input_stream.readChar();
            switch (this.curChar) {
                case 69:
                case 101:
                    if ((active02 & 262144) != 0) {
                        return jjStartNfaWithStates_0(4, 18, 15);
                    }
                    break;
            }
            return jjStartNfa_0(3, active02);
        } catch (IOException e) {
            jjStopStringLiteralDfa_0(3, active02);
            return 4;
        }
    }

    private int jjStartNfaWithStates_0(int pos, int kind, int state) {
        this.jjmatchedKind = kind;
        this.jjmatchedPos = pos;
        try {
            this.curChar = this.input_stream.readChar();
            return jjMoveNfa_0(state, pos + 1);
        } catch (IOException e) {
            return pos + 1;
        }
    }

    private int jjMoveNfa_0(int startState, int curPos) {
        int startsAt = 0;
        this.jjnewStateCnt = 38;
        int i = 1;
        this.jjstateSet[0] = startState;
        int kind = Integer.MAX_VALUE;
        while (true) {
            int i2 = this.jjround + 1;
            this.jjround = i2;
            if (i2 == Integer.MAX_VALUE) {
                ReInitRounds();
            }
            if (this.curChar < 64) {
                long l = 1 << this.curChar;
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 28) {
                                    kind = 28;
                                }
                                jjCheckNAdd(15);
                            } else if ((13312 & l) != 0) {
                                if (kind > 5) {
                                    kind = 5;
                                }
                            } else if ((4294967808L & l) != 0) {
                                if (kind > 4) {
                                    kind = 4;
                                }
                            } else if (this.curChar == 45) {
                                jjCheckNAddStates(0, 3);
                            } else if (this.curChar == 47) {
                                jjAddStates(4, 5);
                            } else if (this.curChar == 34) {
                                jjCheckNAddTwoStates(11, 12);
                            } else if (this.curChar == 39) {
                                jjCheckNAddTwoStates(6, 7);
                            } else if (this.curChar == 35) {
                                jjCheckNAddTwoStates(1, 2);
                            }
                            if ((287667426198290432L & l) != 0) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAddStates(6, 8);
                                break;
                            } else if (this.curChar == 48) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAddStates(9, 11);
                                break;
                            }
                            break;
                        case 1:
                            if (((-13313) & l) != 0) {
                                jjCheckNAddTwoStates(1, 2);
                                break;
                            }
                            break;
                        case 2:
                            if ((13312 & l) != 0 && kind > 3) {
                                kind = 3;
                                break;
                            }
                            break;
                        case 3:
                            if ((4294967808L & l) != 0 && kind > 4) {
                                kind = 4;
                                break;
                            }
                            break;
                        case 4:
                            if ((13312 & l) != 0 && kind > 5) {
                                kind = 5;
                                break;
                            }
                            break;
                        case 5:
                            if (this.curChar == 39) {
                                jjCheckNAddTwoStates(6, 7);
                                break;
                            }
                            break;
                        case 6:
                        case 38:
                            if (((-549755827713L) & l) != 0) {
                                jjCheckNAddStates(12, 14);
                                break;
                            }
                            break;
                        case 8:
                            if ((141287244169216L & l) != 0) {
                                jjCheckNAddStates(12, 14);
                                break;
                            }
                            break;
                        case 9:
                            if (this.curChar == 39 && kind > 26) {
                                kind = 26;
                                break;
                            }
                            break;
                        case 10:
                            if (this.curChar == 34) {
                                jjCheckNAddTwoStates(11, 12);
                                break;
                            }
                            break;
                        case 11:
                        case 39:
                            if (((-17179883009L) & l) != 0) {
                                jjCheckNAddStates(15, 17);
                                break;
                            }
                            break;
                        case 13:
                            if ((140754668224512L & l) != 0) {
                                jjCheckNAddStates(15, 17);
                                break;
                            }
                            break;
                        case 14:
                            if (this.curChar == 34 && kind > 27) {
                                kind = 27;
                                break;
                            }
                            break;
                        case 15:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 28) {
                                    kind = 28;
                                }
                                jjCheckNAdd(15);
                                break;
                            }
                            break;
                        case 16:
                            if (this.curChar == 47) {
                                jjAddStates(4, 5);
                                break;
                            }
                            break;
                        case 17:
                            if (this.curChar == 47) {
                                jjCheckNAddTwoStates(18, 19);
                                break;
                            }
                            break;
                        case 18:
                            if (((-13313) & l) != 0) {
                                jjCheckNAddTwoStates(18, 19);
                                break;
                            }
                            break;
                        case 19:
                            if ((13312 & l) != 0 && kind > 1) {
                                kind = 1;
                                break;
                            }
                            break;
                        case 20:
                            if (this.curChar == 42) {
                                jjCheckNAddTwoStates(21, 23);
                                break;
                            }
                            break;
                        case 21:
                            jjCheckNAddTwoStates(21, 23);
                            break;
                        case 22:
                            if (this.curChar == 47 && kind > 2) {
                                kind = 2;
                                break;
                            }
                            break;
                        case 23:
                            if (this.curChar == 42) {
                                int[] iArr = this.jjstateSet;
                                int i3 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i3 + 1;
                                iArr[i3] = 22;
                                break;
                            }
                            break;
                        case 24:
                            if (this.curChar == 45) {
                                jjCheckNAddStates(0, 3);
                                break;
                            }
                            break;
                        case 25:
                            if (this.curChar == 48) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAdd(25);
                                break;
                            }
                            break;
                        case 26:
                            if ((287667426198290432L & l) != 0) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAdd(27);
                                break;
                            }
                            break;
                        case 27:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAdd(27);
                                break;
                            }
                            break;
                        case 28:
                            if (this.curChar == 48) {
                                jjCheckNAddTwoStates(28, 29);
                                break;
                            }
                            break;
                        case 29:
                            if (this.curChar == 46) {
                                jjCheckNAdd(30);
                                break;
                            }
                            break;
                        case 30:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 16) {
                                    kind = 16;
                                }
                                jjCheckNAddTwoStates(30, 31);
                                break;
                            }
                            break;
                        case 32:
                            if ((43980465111040L & l) != 0) {
                                jjCheckNAdd(33);
                                break;
                            }
                            break;
                        case 33:
                            if ((287948901175001088L & l) != 0) {
                                if (kind > 16) {
                                    kind = 16;
                                }
                                jjCheckNAdd(33);
                                break;
                            }
                            break;
                        case 34:
                            if ((287667426198290432L & l) != 0) {
                                jjCheckNAddTwoStates(35, 29);
                                break;
                            }
                            break;
                        case 35:
                            if ((287948901175001088L & l) != 0) {
                                jjCheckNAddTwoStates(35, 29);
                                break;
                            }
                            break;
                        case 36:
                            if (this.curChar == 48) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAddStates(9, 11);
                                break;
                            }
                            break;
                        case 37:
                            if ((287667426198290432L & l) != 0) {
                                if (kind > 15) {
                                    kind = 15;
                                }
                                jjCheckNAddStates(6, 8);
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else if (this.curChar < 128) {
                long l2 = 1 << (this.curChar & 63);
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 0:
                        case 15:
                            if ((576460743847706622L & l2) != 0) {
                                if (kind > 28) {
                                    kind = 28;
                                }
                                jjCheckNAdd(15);
                                break;
                            }
                            break;
                        case 1:
                            jjAddStates(18, 19);
                            break;
                        case 6:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(12, 14);
                                break;
                            }
                            break;
                        case 7:
                            if (this.curChar == 92) {
                                int[] iArr2 = this.jjstateSet;
                                int i4 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i4 + 1;
                                iArr2[i4] = 8;
                                break;
                            }
                            break;
                        case 8:
                            if ((5700160605929540L & l2) != 0) {
                                jjCheckNAddStates(12, 14);
                                break;
                            }
                            break;
                        case 11:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(15, 17);
                                break;
                            }
                            break;
                        case 12:
                            if (this.curChar == 92) {
                                int[] iArr3 = this.jjstateSet;
                                int i5 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i5 + 1;
                                iArr3[i5] = 13;
                                break;
                            }
                            break;
                        case 13:
                            if ((5700160605929540L & l2) != 0) {
                                jjCheckNAddStates(15, 17);
                                break;
                            }
                            break;
                        case 18:
                            jjAddStates(20, 21);
                            break;
                        case 21:
                            jjAddStates(22, 23);
                            break;
                        case 31:
                            if ((137438953504L & l2) != 0) {
                                jjAddStates(24, 25);
                                break;
                            }
                            break;
                        case 38:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(12, 14);
                                break;
                            } else if (this.curChar == 92) {
                                int[] iArr4 = this.jjstateSet;
                                int i6 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i6 + 1;
                                iArr4[i6] = 8;
                                break;
                            }
                            break;
                        case 39:
                            if (((-268435457) & l2) != 0) {
                                jjCheckNAddStates(15, 17);
                                break;
                            } else if (this.curChar == 92) {
                                int[] iArr5 = this.jjstateSet;
                                int i7 = this.jjnewStateCnt;
                                this.jjnewStateCnt = i7 + 1;
                                iArr5[i7] = 13;
                                break;
                            }
                            break;
                    }
                } while (i != startsAt);
            } else {
                int hiByte = this.curChar >> 8;
                int i1 = hiByte >> 6;
                long l1 = 1 << (hiByte & 63);
                int i22 = (this.curChar & Const.MAX_ARRAY_DIMENSIONS) >> 6;
                long l22 = 1 << (this.curChar & 63);
                do {
                    i--;
                    switch (this.jjstateSet[i]) {
                        case 1:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(18, 19);
                                break;
                            }
                            break;
                        case 6:
                        case 38:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(12, 14);
                                break;
                            }
                            break;
                        case 11:
                        case 39:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjCheckNAddStates(15, 17);
                                break;
                            }
                            break;
                        case 18:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(20, 21);
                                break;
                            }
                            break;
                        case 21:
                            if (jjCanMove_0(hiByte, i1, i22, l1, l22)) {
                                jjAddStates(22, 23);
                                break;
                            }
                            break;
                        default:
                            if (i1 == 0 || l1 == 0 || i22 == 0 || l22 == 0) {
                            }
                            break;
                    }
                } while (i != startsAt);
            }
            if (kind != Integer.MAX_VALUE) {
                this.jjmatchedKind = kind;
                this.jjmatchedPos = curPos;
                kind = Integer.MAX_VALUE;
            }
            curPos++;
            int i8 = this.jjnewStateCnt;
            i = i8;
            int i9 = startsAt;
            this.jjnewStateCnt = i9;
            int i10 = 38 - i9;
            startsAt = i10;
            if (i8 == i10) {
                return curPos;
            }
            try {
                this.curChar = this.input_stream.readChar();
            } catch (IOException e) {
                return curPos;
            }
        }
    }

    protected Token jjFillToken() {
        String im = jjstrLiteralImages[this.jjmatchedKind];
        String curTokenImage = im == null ? this.input_stream.GetImage() : im;
        int beginLine = this.input_stream.getBeginLine();
        int beginColumn = this.input_stream.getBeginColumn();
        int endLine = this.input_stream.getEndLine();
        int endColumn = this.input_stream.getEndColumn();
        Token t = Token.newToken(this.jjmatchedKind, curTokenImage);
        t.beginLine = beginLine;
        t.endLine = endLine;
        t.beginColumn = beginColumn;
        t.endColumn = endColumn;
        return t;
    }

    private static final boolean jjCanMove_0(int hiByte, int i1, int i2, long l1, long l2) {
        switch (hiByte) {
            case 0:
                return (jjbitVec2[i2] & l2) != 0;
            default:
                if ((jjbitVec0[i1] & l1) != 0) {
                    return true;
                }
                return false;
        }
    }

    public Token getNextToken() {
        do {
            try {
                this.curChar = this.input_stream.BeginToken();
                this.jjmatchedKind = Integer.MAX_VALUE;
                this.jjmatchedPos = 0;
                int curPos = jjMoveStringLiteralDfa0_0();
                if (this.jjmatchedKind != Integer.MAX_VALUE) {
                    if (this.jjmatchedPos + 1 < curPos) {
                        this.input_stream.backup((curPos - this.jjmatchedPos) - 1);
                    }
                } else {
                    int error_line = this.input_stream.getEndLine();
                    int error_column = this.input_stream.getEndColumn();
                    String error_after = null;
                    boolean EOFSeen = false;
                    try {
                        this.input_stream.readChar();
                        this.input_stream.backup(1);
                    } catch (IOException e) {
                        EOFSeen = true;
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                        if (this.curChar == 10 || this.curChar == 13) {
                            error_line++;
                            error_column = 0;
                        } else {
                            error_column++;
                        }
                    }
                    if (!EOFSeen) {
                        this.input_stream.backup(1);
                        error_after = curPos <= 1 ? "" : this.input_stream.GetImage();
                    }
                    throw new TokenMgrError(EOFSeen, this.curLexState, error_line, error_column, error_after, this.curChar, 0);
                }
            } catch (Exception e2) {
                this.jjmatchedKind = 0;
                this.jjmatchedPos = -1;
                Token matchedToken = jjFillToken();
                return matchedToken;
            }
        } while ((jjtoToken[this.jjmatchedKind >> 6] & (1 << (this.jjmatchedKind & 63))) == 0);
        Token matchedToken2 = jjFillToken();
        return matchedToken2;
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0004. Please report as an issue. */
    void SkipLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0018. Please report as an issue. */
    void MoreLexicalActions() {
        int i = this.jjimageLen;
        int i2 = this.jjmatchedPos + 1;
        this.lengthOfMatch = i2;
        this.jjimageLen = i + i2;
        switch (this.jjmatchedKind) {
        }
    }

    /* JADX WARN: Failed to find 'out' block for switch in B:3:0x0004. Please report as an issue. */
    void TokenLexicalActions(Token matchedToken) {
        switch (this.jjmatchedKind) {
        }
    }

    private void jjCheckNAdd(int state) {
        if (this.jjrounds[state] != this.jjround) {
            int[] iArr = this.jjstateSet;
            int i = this.jjnewStateCnt;
            this.jjnewStateCnt = i + 1;
            iArr[i] = state;
            this.jjrounds[state] = this.jjround;
        }
    }

    private void jjAddStates(int start, int end) {
        int i;
        do {
            int[] iArr = this.jjstateSet;
            int i2 = this.jjnewStateCnt;
            this.jjnewStateCnt = i2 + 1;
            iArr[i2] = jjnextStates[start];
            i = start;
            start++;
        } while (i != end);
    }

    private void jjCheckNAddTwoStates(int state1, int state2) {
        jjCheckNAdd(state1);
        jjCheckNAdd(state2);
    }

    private void jjCheckNAddStates(int start, int end) {
        int i;
        do {
            jjCheckNAdd(jjnextStates[start]);
            i = start;
            start++;
        } while (i != end);
    }

    public JSONParserTokenManager(JavaCharStream stream) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[38];
        this.jjstateSet = new int[76];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        this.input_stream = stream;
    }

    public JSONParserTokenManager(JavaCharStream stream, int lexState) {
        this.debugStream = System.out;
        this.curLexState = 0;
        this.defaultLexState = 0;
        this.jjrounds = new int[38];
        this.jjstateSet = new int[76];
        this.jjimage = new StringBuilder();
        this.image = this.jjimage;
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void ReInit(JavaCharStream stream) {
        this.jjnewStateCnt = 0;
        this.jjmatchedPos = 0;
        this.curLexState = this.defaultLexState;
        this.input_stream = stream;
        ReInitRounds();
    }

    private void ReInitRounds() {
        this.jjround = RandomValuePropertySourceEnvironmentPostProcessor.ORDER;
        int i = 38;
        while (true) {
            int i2 = i;
            i--;
            if (i2 > 0) {
                this.jjrounds[i] = Integer.MIN_VALUE;
            } else {
                return;
            }
        }
    }

    public void ReInit(JavaCharStream stream, int lexState) {
        ReInit(stream);
        SwitchTo(lexState);
    }

    public void SwitchTo(int lexState) {
        if (lexState >= 1 || lexState < 0) {
            throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", 2);
        }
        this.curLexState = lexState;
    }
}
