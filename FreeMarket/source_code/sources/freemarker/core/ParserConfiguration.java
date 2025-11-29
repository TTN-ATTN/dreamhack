package freemarker.core;

import freemarker.template.Version;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/ParserConfiguration.class */
public interface ParserConfiguration {
    int getTagSyntax();

    int getInterpolationSyntax();

    int getNamingConvention();

    boolean getWhitespaceStripping();

    ArithmeticEngine getArithmeticEngine();

    boolean getStrictSyntaxMode();

    int getAutoEscapingPolicy();

    OutputFormat getOutputFormat();

    boolean getRecognizeStandardFileExtensions();

    Version getIncompatibleImprovements();

    int getTabSize();
}
