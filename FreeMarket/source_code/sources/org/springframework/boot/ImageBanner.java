package org.springframework.boot;

import ch.qos.logback.core.pattern.color.ANSIConstants;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tomcat.util.bcel.Const;
import org.springframework.boot.ansi.AnsiBackground;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiColors;
import org.springframework.boot.ansi.AnsiElement;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogMessage;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ImageBanner.class */
public class ImageBanner implements Banner {
    private static final String SYSTEM_PROPERTY_JAVA_AWT_HEADLESS = "java.awt.headless";
    private static final String PROPERTY_PREFIX = "spring.banner.image.";
    private static final Log logger = LogFactory.getLog((Class<?>) ImageBanner.class);
    private static final double[] RGB_WEIGHT = {0.2126d, 0.7152d, 0.0722d};
    private final Resource image;

    public ImageBanner(Resource image) {
        Assert.notNull(image, "Image must not be null");
        Assert.isTrue(image.exists(), "Image must exist");
        this.image = image;
    }

    @Override // org.springframework.boot.Banner
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
        try {
            if (System.getProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS) == null) {
                System.setProperty(SYSTEM_PROPERTY_JAVA_AWT_HEADLESS, "true");
            }
            printBanner(environment, out);
        } catch (Throwable ex) {
            logger.warn(LogMessage.format("Image banner not printable: %s (%s: '%s')", this.image, ex.getClass(), ex.getMessage()));
            logger.debug("Image banner printing failure", ex);
        }
    }

    private void printBanner(Environment environment, PrintStream out) throws InterruptedException, IOException {
        int width = ((Integer) getProperty(environment, "width", Integer.class, 76)).intValue();
        int height = ((Integer) getProperty(environment, "height", Integer.class, 0)).intValue();
        int margin = ((Integer) getProperty(environment, "margin", Integer.class, 2)).intValue();
        boolean invert = ((Boolean) getProperty(environment, "invert", Boolean.class, false)).booleanValue();
        AnsiColors.BitDepth bitDepth = getBitDepthProperty(environment);
        PixelMode pixelMode = getPixelModeProperty(environment);
        Frame[] frames = readFrames(width, height);
        for (int i = 0; i < frames.length; i++) {
            if (i > 0) {
                resetCursor(frames[i - 1].getImage(), out);
            }
            printBanner(frames[i].getImage(), margin, invert, bitDepth, pixelMode, out);
            sleep(frames[i].getDelayTime());
        }
    }

    private AnsiColors.BitDepth getBitDepthProperty(Environment environment) {
        Integer bitDepth = (Integer) getProperty(environment, "bitdepth", Integer.class, null);
        return bitDepth != null ? AnsiColors.BitDepth.of(bitDepth.intValue()) : AnsiColors.BitDepth.FOUR;
    }

    private PixelMode getPixelModeProperty(Environment environment) {
        String pixelMode = (String) getProperty(environment, "pixelmode", String.class, null);
        return pixelMode != null ? PixelMode.valueOf(pixelMode.trim().toUpperCase()) : PixelMode.TEXT;
    }

    private <T> T getProperty(Environment environment, String str, Class<T> cls, T t) {
        return (T) environment.getProperty(PROPERTY_PREFIX + str, cls, t);
    }

    private Frame[] readFrames(int width, int height) throws IOException {
        InputStream inputStream = this.image.getInputStream();
        Throwable th = null;
        try {
            ImageInputStream imageStream = ImageIO.createImageInputStream(inputStream);
            Throwable th2 = null;
            try {
                try {
                    Frame[] frames = readFrames(width, height, imageStream);
                    if (imageStream != null) {
                        if (0 != 0) {
                            try {
                                imageStream.close();
                            } catch (Throwable th3) {
                                th2.addSuppressed(th3);
                            }
                        } else {
                            imageStream.close();
                        }
                    }
                    return frames;
                } finally {
                }
            } catch (Throwable th4) {
                if (imageStream != null) {
                    if (th2 != null) {
                        try {
                            imageStream.close();
                        } catch (Throwable th5) {
                            th2.addSuppressed(th5);
                        }
                    } else {
                        imageStream.close();
                    }
                }
                throw th4;
            }
        } finally {
            if (inputStream != null) {
                if (0 != 0) {
                    try {
                        inputStream.close();
                    } catch (Throwable th6) {
                        th.addSuppressed(th6);
                    }
                } else {
                    inputStream.close();
                }
            }
        }
    }

    private Frame[] readFrames(int width, int height, ImageInputStream stream) throws IOException {
        Iterator<ImageReader> readers = ImageIO.getImageReaders(stream);
        Assert.state(readers.hasNext(), "Unable to read image banner source");
        ImageReader reader = readers.next();
        try {
            ImageReadParam readParam = reader.getDefaultReadParam();
            reader.setInput(stream);
            int frameCount = reader.getNumImages(true);
            Frame[] frames = new Frame[frameCount];
            for (int i = 0; i < frameCount; i++) {
                frames[i] = readFrame(width, height, reader, i, readParam);
            }
            return frames;
        } finally {
            reader.dispose();
        }
    }

    private Frame readFrame(int width, int height, ImageReader reader, int imageIndex, ImageReadParam readParam) throws IOException {
        BufferedImage image = reader.read(imageIndex, readParam);
        BufferedImage resized = resizeImage(image, width, height);
        int delayTime = getDelayTime(reader, imageIndex);
        return new Frame(resized, delayTime);
    }

    private int getDelayTime(ImageReader reader, int imageIndex) throws IOException {
        IIOMetadata metadata = reader.getImageMetadata(imageIndex);
        IIOMetadataNode root = metadata.getAsTree(metadata.getNativeMetadataFormatName());
        IIOMetadataNode extension = findNode(root, "GraphicControlExtension");
        String attribute = extension != null ? extension.getAttribute("delayTime") : null;
        if (attribute != null) {
            return Integer.parseInt(attribute) * 10;
        }
        return 0;
    }

    private static IIOMetadataNode findNode(IIOMetadataNode rootNode, String nodeName) {
        if (rootNode == null) {
            return null;
        }
        for (int i = 0; i < rootNode.getLength(); i++) {
            if (rootNode.item(i).getNodeName().equalsIgnoreCase(nodeName)) {
                return rootNode.item(i);
            }
        }
        return null;
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        if (width < 1) {
            width = 1;
        }
        if (height <= 0) {
            double aspectRatio = (width / image.getWidth()) * 0.5d;
            height = (int) Math.ceil(image.getHeight() * aspectRatio);
        }
        BufferedImage resized = new BufferedImage(width, height, 1);
        Image scaled = image.getScaledInstance(width, height, 1);
        resized.getGraphics().drawImage(scaled, 0, 0, (ImageObserver) null);
        return resized;
    }

    private void resetCursor(BufferedImage image, PrintStream out) {
        int lines = image.getHeight() + 3;
        out.print(ANSIConstants.ESC_START + lines + "A\r");
    }

    private void printBanner(BufferedImage image, int margin, boolean invert, AnsiColors.BitDepth bitDepth, PixelMode pixelMode, PrintStream out) {
        AnsiElement background = invert ? AnsiBackground.BLACK : AnsiBackground.DEFAULT;
        out.print(AnsiOutput.encode(AnsiColor.DEFAULT));
        out.print(AnsiOutput.encode(background));
        out.println();
        out.println();
        AnsiElement lastColor = AnsiColor.DEFAULT;
        AnsiColors colors = new AnsiColors(bitDepth);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int i = 0; i < margin; i++) {
                out.print(" ");
            }
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), false);
                AnsiElement ansiColor = colors.findClosest(color);
                if (ansiColor != lastColor) {
                    out.print(AnsiOutput.encode(ansiColor));
                    lastColor = ansiColor;
                }
                out.print(getAsciiPixel(color, invert, pixelMode));
            }
            out.println();
        }
        out.print(AnsiOutput.encode(AnsiColor.DEFAULT));
        out.print(AnsiOutput.encode(AnsiBackground.DEFAULT));
        out.println();
    }

    private char getAsciiPixel(Color color, boolean dark, PixelMode pixelMode) {
        char[] pixels = pixelMode.getPixels();
        int increment = (10 / pixels.length) * 10;
        int start = increment * pixels.length;
        double luminance = getLuminance(color, dark);
        for (int i = 0; i < pixels.length; i++) {
            if (luminance >= start - (i * increment)) {
                return pixels[i];
            }
        }
        return pixels[pixels.length - 1];
    }

    private int getLuminance(Color color, boolean inverse) {
        double luminance = 0.0d + getLuminance(color.getRed(), inverse, RGB_WEIGHT[0]);
        return (int) Math.ceil((((luminance + getLuminance(color.getGreen(), inverse, RGB_WEIGHT[1])) + getLuminance(color.getBlue(), inverse, RGB_WEIGHT[2])) / 255.0d) * 100.0d);
    }

    private double getLuminance(int component, boolean inverse, double weight) {
        return (inverse ? Const.MAX_ARRAY_DIMENSIONS - component : component) * weight;
    }

    private void sleep(int delay) throws InterruptedException {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ImageBanner$Frame.class */
    private static class Frame {
        private final BufferedImage image;
        private final int delayTime;

        Frame(BufferedImage image, int delayTime) {
            this.image = image;
            this.delayTime = delayTime;
        }

        BufferedImage getImage() {
            return this.image;
        }

        int getDelayTime() {
            return this.delayTime;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ImageBanner$PixelMode.class */
    public enum PixelMode {
        TEXT(' ', '.', '*', ':', 'o', '&', '8', '#', '@'),
        BLOCK(' ', 9617, 9618, 9619, 9608);

        private char[] pixels;

        PixelMode(char... pixels) {
            this.pixels = pixels;
        }

        char[] getPixels() {
            return this.pixels;
        }
    }
}
