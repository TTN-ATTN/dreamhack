package freemarker.core;

import freemarker.template.Version;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ParserConfigurationWithInheritedFormat.class */
public final class _ParserConfigurationWithInheritedFormat implements ParserConfiguration {
    private final OutputFormat outputFormat;
    private final Integer autoEscapingPolicy;
    private final ParserConfiguration wrappedPCfg;

    public _ParserConfigurationWithInheritedFormat(ParserConfiguration wrappedPCfg, OutputFormat outputFormat, Integer autoEscapingPolicy) {
        this.outputFormat = outputFormat;
        this.autoEscapingPolicy = autoEscapingPolicy;
        this.wrappedPCfg = wrappedPCfg;
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getWhitespaceStripping() {
        return this.wrappedPCfg.getWhitespaceStripping();
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTagSyntax() {
        return this.wrappedPCfg.getTagSyntax();
    }

    @Override // freemarker.core.ParserConfiguration
    public int getInterpolationSyntax() {
        return this.wrappedPCfg.getInterpolationSyntax();
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getStrictSyntaxMode() {
        return this.wrappedPCfg.getStrictSyntaxMode();
    }

    @Override // freemarker.core.ParserConfiguration
    public OutputFormat getOutputFormat() {
        return this.outputFormat != null ? this.outputFormat : this.wrappedPCfg.getOutputFormat();
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getRecognizeStandardFileExtensions() {
        return false;
    }

    @Override // freemarker.core.ParserConfiguration
    public int getNamingConvention() {
        return this.wrappedPCfg.getNamingConvention();
    }

    @Override // freemarker.core.ParserConfiguration
    public Version getIncompatibleImprovements() {
        return this.wrappedPCfg.getIncompatibleImprovements();
    }

    @Override // freemarker.core.ParserConfiguration
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy != null ? this.autoEscapingPolicy.intValue() : this.wrappedPCfg.getAutoEscapingPolicy();
    }

    @Override // freemarker.core.ParserConfiguration
    public ArithmeticEngine getArithmeticEngine() {
        return this.wrappedPCfg.getArithmeticEngine();
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTabSize() {
        return this.wrappedPCfg.getTabSize();
    }
}
