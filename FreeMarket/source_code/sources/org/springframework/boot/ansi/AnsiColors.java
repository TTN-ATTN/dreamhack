package org.springframework.boot.ansi;

import java.awt.Color;
import java.awt.color.ColorSpace;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tomcat.jni.SSL;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.asm.Opcodes;
import org.springframework.util.Assert;
import org.springframework.util.SocketUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ansi/AnsiColors.class */
public final class AnsiColors {
    private static final Map<AnsiElement, LabColor> ANSI_COLOR_MAP;
    private static final int[] ANSI_8BIT_COLOR_CODE_LOOKUP;
    private final Map<AnsiElement, LabColor> lookup;

    static {
        Map<AnsiColor, LabColor> colorMap = new EnumMap<>(AnsiColor.class);
        colorMap.put(AnsiColor.BLACK, new LabColor((Integer) 0));
        colorMap.put(AnsiColor.RED, new LabColor((Integer) 11141120));
        colorMap.put(AnsiColor.GREEN, new LabColor((Integer) 43520));
        colorMap.put(AnsiColor.YELLOW, new LabColor((Integer) 11162880));
        colorMap.put(AnsiColor.BLUE, new LabColor(Integer.valueOf(Opcodes.TABLESWITCH)));
        colorMap.put(AnsiColor.MAGENTA, new LabColor((Integer) 11141290));
        colorMap.put(AnsiColor.CYAN, new LabColor((Integer) 43690));
        colorMap.put(AnsiColor.WHITE, new LabColor((Integer) 11184810));
        colorMap.put(AnsiColor.BRIGHT_BLACK, new LabColor((Integer) 5592405));
        colorMap.put(AnsiColor.BRIGHT_RED, new LabColor((Integer) 16733525));
        colorMap.put(AnsiColor.BRIGHT_GREEN, new LabColor((Integer) 5635840));
        colorMap.put(AnsiColor.BRIGHT_YELLOW, new LabColor((Integer) 16777045));
        colorMap.put(AnsiColor.BRIGHT_BLUE, new LabColor((Integer) 5592575));
        colorMap.put(AnsiColor.BRIGHT_MAGENTA, new LabColor((Integer) 16733695));
        colorMap.put(AnsiColor.BRIGHT_CYAN, new LabColor((Integer) 5636095));
        colorMap.put(AnsiColor.BRIGHT_WHITE, new LabColor((Integer) 16777215));
        ANSI_COLOR_MAP = Collections.unmodifiableMap(colorMap);
        ANSI_8BIT_COLOR_CODE_LOOKUP = new int[]{0, SSL.SSL_OP_TLS_ROLLBACK_BUG, 32768, 8421376, 128, 8388736, 32896, 12632256, 8421504, 16711680, 65280, 16776960, Const.MAX_ARRAY_DIMENSIONS, 16711935, SocketUtils.PORT_RANGE_MAX, 16777215, 0, 95, 135, Opcodes.DRETURN, 215, Const.MAX_ARRAY_DIMENSIONS, 24320, 24415, 24455, 24495, 24535, 24575, 34560, 34655, 34695, 34735, 34775, 34815, 44800, 44895, 44935, 44975, 45015, 45055, 55040, 55135, 55175, 55215, 55255, 55295, 65280, 65375, 65415, 65455, 65495, SocketUtils.PORT_RANGE_MAX, 6225920, 6226015, 6226055, 6226095, 6226135, 6226175, 6250240, 6250335, 6250375, 6250415, 6250455, 6250495, 6260480, 6260575, 6260615, 6260655, 6260695, 6260735, 6270720, 6270815, 6270855, 6270895, 6270935, 6270975, 6280960, 6281055, 6281095, 6281135, 6281175, 6281215, 6291200, 6291295, 6291335, 6291375, 6291415, 6291455, 8847360, 8847455, 8847495, 8847535, 8847575, 8847615, 8871680, 8871775, 8871815, 8871855, 8871895, 8871935, 8881920, 8882015, 8882055, 8882095, 8882135, 8882175, 8892160, 8892255, 8892295, 8892335, 8892375, 8892415, 8902400, 8902495, 8902535, 8902575, 8902615, 8902655, 8912640, 8912735, 8912775, 8912815, 8912855, 8912895, 11468800, 11468895, 11468935, 11468975, 11469015, 11469055, 11493120, 11493215, 11493255, 11493295, 11493335, 11493375, 11503360, 11503455, 11503495, 11503535, 11503575, 11503615, 11513600, 11513695, 11513735, 11513775, 11513815, 11513855, 11523840, 11523935, 11523975, 11524015, 11524055, 11524095, 11534080, 11534175, 11534215, 11534255, 11534295, 11534335, 14090240, 14090335, 14090375, 14090415, 14090455, 14090495, 14114560, 14114655, 14114695, 14114735, 14114775, 14114815, 14124800, 14124895, 14124935, 14124975, 14125015, 14125055, 14135040, 14135135, 14135175, 14135215, 14135255, 14135295, 14145280, 14145375, 14145415, 14145455, 14145495, 14145535, 14155520, 14155615, 14155655, 14155695, 14155735, 14155775, 16711680, 16711775, 16711815, 16711855, 16711895, 16711935, 16736000, 16736095, 16736135, 16736175, 16736215, 16736255, 16746240, 16746335, 16746375, 16746415, 16746455, 16746495, 16756480, 16756575, 16756615, 16756655, 16756695, 16756735, 16766720, 16766815, 16766855, 16766895, 16766935, 16766975, 16776960, 16777055, 16777095, 16777135, 16777175, 16777215, 526344, 1184274, 1842204, 2500134, 3158064, 3815994, 4473924, 5131854, 5789784, 6447714, 7105644, 7763574, 8421504, 9079434, 9737364, 10395294, 11053224, 11711154, 12369084, 13027014, 13684944, 14342874, 15000804, 15658734};
    }

    public AnsiColors(BitDepth bitDepth) {
        this.lookup = getLookup(bitDepth);
    }

    private Map<AnsiElement, LabColor> getLookup(BitDepth bitDepth) {
        if (bitDepth == BitDepth.EIGHT) {
            Map<Ansi8BitColor, LabColor> lookup = new LinkedHashMap<>();
            for (int i = 0; i < ANSI_8BIT_COLOR_CODE_LOOKUP.length; i++) {
                lookup.put(Ansi8BitColor.foreground(i), new LabColor(Integer.valueOf(ANSI_8BIT_COLOR_CODE_LOOKUP[i])));
            }
            return Collections.unmodifiableMap(lookup);
        }
        return ANSI_COLOR_MAP;
    }

    public AnsiElement findClosest(Color color) {
        return findClosest(new LabColor(color));
    }

    private AnsiElement findClosest(LabColor color) {
        AnsiElement closest = null;
        double closestDistance = 3.4028234663852886E38d;
        for (Map.Entry<AnsiElement, LabColor> entry : this.lookup.entrySet()) {
            double candidateDistance = color.getDistance(entry.getValue());
            if (closest == null || candidateDistance < closestDistance) {
                closestDistance = candidateDistance;
                closest = entry.getKey();
            }
        }
        return closest;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ansi/AnsiColors$LabColor.class */
    private static final class LabColor {
        private static final ColorSpace XYZ_COLOR_SPACE = ColorSpace.getInstance(1001);
        private final double l;
        private final double a;
        private final double b;

        LabColor(Integer rgb) {
            this(rgb != null ? new Color(rgb.intValue()) : null);
        }

        LabColor(Color color) {
            Assert.notNull(color, "Color must not be null");
            float[] lab = fromXyz(color.getColorComponents(XYZ_COLOR_SPACE, (float[]) null));
            this.l = lab[0];
            this.a = lab[1];
            this.b = lab[2];
        }

        private float[] fromXyz(float[] xyz) {
            return fromXyz(xyz[0], xyz[1], xyz[2]);
        }

        private float[] fromXyz(float x, float y, float z) {
            double l = (f(y) - 16.0d) * 116.0d;
            double a = (f(x) - f(y)) * 500.0d;
            double b = (f(y) - f(z)) * 200.0d;
            return new float[]{(float) l, (float) a, (float) b};
        }

        private double f(double t) {
            return t > 0.008856451679035631d ? Math.cbrt(t) : (0.3333333333333333d * Math.pow(4.833333333333333d, 2.0d) * t) + 0.13793103448275862d;
        }

        double getDistance(LabColor other) {
            double c1 = Math.sqrt((this.a * this.a) + (this.b * this.b));
            double deltaC = c1 - Math.sqrt((other.a * other.a) + (other.b * other.b));
            double deltaA = this.a - other.a;
            double deltaB = this.b - other.b;
            double deltaH = Math.sqrt(Math.max(0.0d, ((deltaA * deltaA) + (deltaB * deltaB)) - (deltaC * deltaC)));
            return Math.sqrt(Math.max(0.0d, Math.pow((this.l - other.l) / 1.0d, 2.0d) + Math.pow(deltaC / (1.0d + (0.045d * c1)), 2.0d) + Math.pow(deltaH / (1.0d + (0.015d * c1)), 2.0d)));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ansi/AnsiColors$BitDepth.class */
    public enum BitDepth {
        FOUR(4),
        EIGHT(8);

        private final int bits;

        BitDepth(int bits) {
            this.bits = bits;
        }

        public static BitDepth of(int bits) {
            for (BitDepth candidate : values()) {
                if (candidate.bits == bits) {
                    return candidate;
                }
            }
            throw new IllegalArgumentException("Unsupported ANSI bit depth '" + bits + "'");
        }
    }
}
