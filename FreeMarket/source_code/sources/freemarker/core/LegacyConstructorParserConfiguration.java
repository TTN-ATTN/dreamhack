package freemarker.core;

import freemarker.template.Version;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/LegacyConstructorParserConfiguration.class */
class LegacyConstructorParserConfiguration implements ParserConfiguration {
    private final int tagSyntax;
    private final int interpolationSyntax;
    private final int namingConvention;
    private final boolean whitespaceStripping;
    private final boolean strictSyntaxMode;
    private ArithmeticEngine arithmeticEngine;
    private Integer autoEscapingPolicy;
    private OutputFormat outputFormat;
    private Boolean recognizeStandardFileExtensions;
    private Integer tabSize;
    private final Version incompatibleImprovements;

    LegacyConstructorParserConfiguration(boolean strictSyntaxMode, boolean whitespaceStripping, int tagSyntax, int interpolationSyntax, int namingConvention, Integer autoEscaping, OutputFormat outputFormat, Boolean recognizeStandardFileExtensions, Integer tabSize, Version incompatibleImprovements, ArithmeticEngine arithmeticEngine) {
        this.tagSyntax = tagSyntax;
        this.interpolationSyntax = interpolationSyntax;
        this.namingConvention = namingConvention;
        this.whitespaceStripping = whitespaceStripping;
        this.strictSyntaxMode = strictSyntaxMode;
        this.autoEscapingPolicy = autoEscaping;
        this.outputFormat = outputFormat;
        this.recognizeStandardFileExtensions = recognizeStandardFileExtensions;
        this.tabSize = tabSize;
        this.incompatibleImprovements = incompatibleImprovements;
        this.arithmeticEngine = arithmeticEngine;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTagSyntax() {
        return this.tagSyntax;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getInterpolationSyntax() {
        return this.interpolationSyntax;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getNamingConvention() {
        return this.namingConvention;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getWhitespaceStripping() {
        return this.whitespaceStripping;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getStrictSyntaxMode() {
        return this.strictSyntaxMode;
    }

    @Override // freemarker.core.ParserConfiguration
    public Version getIncompatibleImprovements() {
        return this.incompatibleImprovements;
    }

    @Override // freemarker.core.ParserConfiguration
    public ArithmeticEngine getArithmeticEngine() {
        if (this.arithmeticEngine == null) {
            throw new IllegalStateException();
        }
        return this.arithmeticEngine;
    }

    void setArithmeticEngineIfNotSet(ArithmeticEngine arithmeticEngine) {
        if (this.arithmeticEngine == null) {
            this.arithmeticEngine = arithmeticEngine;
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public int getAutoEscapingPolicy() {
        if (this.autoEscapingPolicy == null) {
            throw new IllegalStateException();
        }
        return this.autoEscapingPolicy.intValue();
    }

    void setAutoEscapingPolicyIfNotSet(int autoEscapingPolicy) {
        if (this.autoEscapingPolicy == null) {
            this.autoEscapingPolicy = Integer.valueOf(autoEscapingPolicy);
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public OutputFormat getOutputFormat() {
        if (this.outputFormat == null) {
            throw new IllegalStateException();
        }
        return this.outputFormat;
    }

    void setOutputFormatIfNotSet(OutputFormat outputFormat) {
        if (this.outputFormat == null) {
            this.outputFormat = outputFormat;
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getRecognizeStandardFileExtensions() {
        if (this.recognizeStandardFileExtensions == null) {
            throw new IllegalStateException();
        }
        return this.recognizeStandardFileExtensions.booleanValue();
    }

    void setRecognizeStandardFileExtensionsIfNotSet(boolean recognizeStandardFileExtensions) {
        if (this.recognizeStandardFileExtensions == null) {
            this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
        }
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTabSize() {
        if (this.tabSize == null) {
            throw new IllegalStateException();
        }
        return this.tabSize.intValue();
    }

    void setTabSizeIfNotSet(int tabSize) {
        if (this.tabSize == null) {
            this.tabSize = Integer.valueOf(tabSize);
        }
    }
}
