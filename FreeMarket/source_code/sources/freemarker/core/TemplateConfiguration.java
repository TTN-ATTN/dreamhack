package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import freemarker.template._VersionInts;
import freemarker.template.utility.NullArgumentException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateConfiguration.class */
public final class TemplateConfiguration extends Configurable implements ParserConfiguration {
    private boolean parentConfigurationSet;
    private Integer tagSyntax;
    private Integer interpolationSyntax;
    private Integer namingConvention;
    private Boolean whitespaceStripping;
    private Boolean strictSyntaxMode;
    private Integer autoEscapingPolicy;
    private Boolean recognizeStandardFileExtensions;
    private OutputFormat outputFormat;
    private String encoding;
    private Integer tabSize;

    public TemplateConfiguration() {
        super(Configuration.getDefaultConfiguration());
    }

    @Override // freemarker.core.Configurable
    void setParent(Configurable cfg) {
        NullArgumentException.check("cfg", cfg);
        if (!(cfg instanceof Configuration)) {
            throw new IllegalArgumentException("The parent of a TemplateConfiguration can only be a Configuration");
        }
        if (this.parentConfigurationSet) {
            if (getParent() != cfg) {
                throw new IllegalStateException("This TemplateConfiguration is already associated with a different Configuration instance.");
            }
        } else {
            if (((Configuration) cfg).getIncompatibleImprovements().intValue() < _VersionInts.V_2_3_22 && hasAnyConfigurableSet()) {
                throw new IllegalStateException("This TemplateConfiguration can't be associated to a Configuration that has incompatibleImprovements less than 2.3.22, because it changes non-parser settings.");
            }
            super.setParent(cfg);
            this.parentConfigurationSet = true;
        }
    }

    public void setParentConfiguration(Configuration cfg) {
        setParent(cfg);
    }

    public Configuration getParentConfiguration() {
        if (this.parentConfigurationSet) {
            return (Configuration) getParent();
        }
        return null;
    }

    private Configuration getNonNullParentConfiguration() {
        checkParentConfigurationSet();
        return (Configuration) getParent();
    }

    public void merge(TemplateConfiguration tc) {
        if (tc.isAPIBuiltinEnabledSet()) {
            setAPIBuiltinEnabled(tc.isAPIBuiltinEnabled());
        }
        if (tc.isArithmeticEngineSet()) {
            setArithmeticEngine(tc.getArithmeticEngine());
        }
        if (tc.isAutoEscapingPolicySet()) {
            setAutoEscapingPolicy(tc.getAutoEscapingPolicy());
        }
        if (tc.isAutoFlushSet()) {
            setAutoFlush(tc.getAutoFlush());
        }
        if (tc.isBooleanFormatSet()) {
            setBooleanFormat(tc.getBooleanFormat());
        }
        if (tc.isClassicCompatibleSet()) {
            setClassicCompatibleAsInt(tc.getClassicCompatibleAsInt());
        }
        if (tc.isCustomDateFormatsSet()) {
            setCustomDateFormats(mergeMaps(getCustomDateFormats(), tc.getCustomDateFormats(), false));
        }
        if (tc.isCustomNumberFormatsSet()) {
            setCustomNumberFormats(mergeMaps(getCustomNumberFormats(), tc.getCustomNumberFormats(), false));
        }
        if (tc.isDateFormatSet()) {
            setDateFormat(tc.getDateFormat());
        }
        if (tc.isDateTimeFormatSet()) {
            setDateTimeFormat(tc.getDateTimeFormat());
        }
        if (tc.isCFormatSet()) {
            setCFormat(tc.getCFormat());
        }
        if (tc.isEncodingSet()) {
            setEncoding(tc.getEncoding());
        }
        if (tc.isLocaleSet()) {
            setLocale(tc.getLocale());
        }
        if (tc.isLogTemplateExceptionsSet()) {
            setLogTemplateExceptions(tc.getLogTemplateExceptions());
        }
        if (tc.isWrapUncheckedExceptionsSet()) {
            setWrapUncheckedExceptions(tc.getWrapUncheckedExceptions());
        }
        if (tc.isNamingConventionSet()) {
            setNamingConvention(tc.getNamingConvention());
        }
        if (tc.isNewBuiltinClassResolverSet()) {
            setNewBuiltinClassResolver(tc.getNewBuiltinClassResolver());
        }
        if (tc.isTruncateBuiltinAlgorithmSet()) {
            setTruncateBuiltinAlgorithm(tc.getTruncateBuiltinAlgorithm());
        }
        if (tc.isNumberFormatSet()) {
            setNumberFormat(tc.getNumberFormat());
        }
        if (tc.isObjectWrapperSet()) {
            setObjectWrapper(tc.getObjectWrapper());
        }
        if (tc.isOutputEncodingSet()) {
            setOutputEncoding(tc.getOutputEncoding());
        }
        if (tc.isOutputFormatSet()) {
            setOutputFormat(tc.getOutputFormat());
        }
        if (tc.isRecognizeStandardFileExtensionsSet()) {
            setRecognizeStandardFileExtensions(tc.getRecognizeStandardFileExtensions());
        }
        if (tc.isShowErrorTipsSet()) {
            setShowErrorTips(tc.getShowErrorTips());
        }
        if (tc.isSQLDateAndTimeTimeZoneSet()) {
            setSQLDateAndTimeTimeZone(tc.getSQLDateAndTimeTimeZone());
        }
        if (tc.isStrictSyntaxModeSet()) {
            setStrictSyntaxMode(tc.getStrictSyntaxMode());
        }
        if (tc.isTagSyntaxSet()) {
            setTagSyntax(tc.getTagSyntax());
        }
        if (tc.isInterpolationSyntaxSet()) {
            setInterpolationSyntax(tc.getInterpolationSyntax());
        }
        if (tc.isTemplateExceptionHandlerSet()) {
            setTemplateExceptionHandler(tc.getTemplateExceptionHandler());
        }
        if (tc.isAttemptExceptionReporterSet()) {
            setAttemptExceptionReporter(tc.getAttemptExceptionReporter());
        }
        if (tc.isTimeFormatSet()) {
            setTimeFormat(tc.getTimeFormat());
        }
        if (tc.isTimeZoneSet()) {
            setTimeZone(tc.getTimeZone());
        }
        if (tc.isURLEscapingCharsetSet()) {
            setURLEscapingCharset(tc.getURLEscapingCharset());
        }
        if (tc.isWhitespaceStrippingSet()) {
            setWhitespaceStripping(tc.getWhitespaceStripping());
        }
        if (tc.isTabSizeSet()) {
            setTabSize(tc.getTabSize());
        }
        if (tc.isLazyImportsSet()) {
            setLazyImports(tc.getLazyImports());
        }
        if (tc.isLazyAutoImportsSet()) {
            setLazyAutoImports(tc.getLazyAutoImports());
        }
        if (tc.isAutoImportsSet()) {
            setAutoImports(mergeMaps(getAutoImportsWithoutFallback(), tc.getAutoImportsWithoutFallback(), true));
        }
        if (tc.isAutoIncludesSet()) {
            setAutoIncludes(mergeLists(getAutoIncludesWithoutFallback(), tc.getAutoIncludesWithoutFallback()));
        }
        tc.copyDirectCustomAttributes(this, true);
    }

    public void apply(Template template) {
        Configuration cfg = getNonNullParentConfiguration();
        if (template.getConfiguration() != cfg) {
            throw new IllegalArgumentException("The argument Template doesn't belong to the same Configuration as the TemplateConfiguration");
        }
        if (isAPIBuiltinEnabledSet() && !template.isAPIBuiltinEnabledSet()) {
            template.setAPIBuiltinEnabled(isAPIBuiltinEnabled());
        }
        if (isArithmeticEngineSet() && !template.isArithmeticEngineSet()) {
            template.setArithmeticEngine(getArithmeticEngine());
        }
        if (isAutoFlushSet() && !template.isAutoFlushSet()) {
            template.setAutoFlush(getAutoFlush());
        }
        if (isBooleanFormatSet() && !template.isBooleanFormatSet()) {
            template.setBooleanFormat(getBooleanFormat());
        }
        if (isClassicCompatibleSet() && !template.isClassicCompatibleSet()) {
            template.setClassicCompatibleAsInt(getClassicCompatibleAsInt());
        }
        if (isCustomDateFormatsSet()) {
            template.setCustomDateFormats(mergeMaps(getCustomDateFormats(), template.getCustomDateFormatsWithoutFallback(), false));
        }
        if (isCustomNumberFormatsSet()) {
            template.setCustomNumberFormats(mergeMaps(getCustomNumberFormats(), template.getCustomNumberFormatsWithoutFallback(), false));
        }
        if (isDateFormatSet() && !template.isDateFormatSet()) {
            template.setDateFormat(getDateFormat());
        }
        if (isDateTimeFormatSet() && !template.isDateTimeFormatSet()) {
            template.setDateTimeFormat(getDateTimeFormat());
        }
        if (isCFormatSet() && !template.isCFormatSet()) {
            template.setCFormat(getCFormat());
        }
        if (isEncodingSet() && template.getEncoding() == null) {
            template.setEncoding(getEncoding());
        }
        if (isLocaleSet() && !template.isLocaleSet()) {
            template.setLocale(getLocale());
        }
        if (isLogTemplateExceptionsSet() && !template.isLogTemplateExceptionsSet()) {
            template.setLogTemplateExceptions(getLogTemplateExceptions());
        }
        if (isWrapUncheckedExceptionsSet() && !template.isWrapUncheckedExceptionsSet()) {
            template.setWrapUncheckedExceptions(getWrapUncheckedExceptions());
        }
        if (isNewBuiltinClassResolverSet() && !template.isNewBuiltinClassResolverSet()) {
            template.setNewBuiltinClassResolver(getNewBuiltinClassResolver());
        }
        if (isTruncateBuiltinAlgorithmSet() && !template.isTruncateBuiltinAlgorithmSet()) {
            template.setTruncateBuiltinAlgorithm(getTruncateBuiltinAlgorithm());
        }
        if (isNumberFormatSet() && !template.isNumberFormatSet()) {
            template.setNumberFormat(getNumberFormat());
        }
        if (isObjectWrapperSet() && !template.isObjectWrapperSet()) {
            template.setObjectWrapper(getObjectWrapper());
        }
        if (isOutputEncodingSet() && !template.isOutputEncodingSet()) {
            template.setOutputEncoding(getOutputEncoding());
        }
        if (isShowErrorTipsSet() && !template.isShowErrorTipsSet()) {
            template.setShowErrorTips(getShowErrorTips());
        }
        if (isSQLDateAndTimeTimeZoneSet() && !template.isSQLDateAndTimeTimeZoneSet()) {
            template.setSQLDateAndTimeTimeZone(getSQLDateAndTimeTimeZone());
        }
        if (isTemplateExceptionHandlerSet() && !template.isTemplateExceptionHandlerSet()) {
            template.setTemplateExceptionHandler(getTemplateExceptionHandler());
        }
        if (isAttemptExceptionReporterSet() && !template.isAttemptExceptionReporterSet()) {
            template.setAttemptExceptionReporter(getAttemptExceptionReporter());
        }
        if (isTimeFormatSet() && !template.isTimeFormatSet()) {
            template.setTimeFormat(getTimeFormat());
        }
        if (isTimeZoneSet() && !template.isTimeZoneSet()) {
            template.setTimeZone(getTimeZone());
        }
        if (isURLEscapingCharsetSet() && !template.isURLEscapingCharsetSet()) {
            template.setURLEscapingCharset(getURLEscapingCharset());
        }
        if (isLazyImportsSet() && !template.isLazyImportsSet()) {
            template.setLazyImports(getLazyImports());
        }
        if (isLazyAutoImportsSet() && !template.isLazyAutoImportsSet()) {
            template.setLazyAutoImports(getLazyAutoImports());
        }
        if (isAutoImportsSet()) {
            template.setAutoImports(mergeMaps(getAutoImports(), template.getAutoImportsWithoutFallback(), true));
        }
        if (isAutoIncludesSet()) {
            template.setAutoIncludes(mergeLists(getAutoIncludes(), template.getAutoIncludesWithoutFallback()));
        }
        copyDirectCustomAttributes(template, false);
    }

    public void setTagSyntax(int tagSyntax) {
        _TemplateAPI.valideTagSyntaxValue(tagSyntax);
        this.tagSyntax = Integer.valueOf(tagSyntax);
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTagSyntax() {
        return this.tagSyntax != null ? this.tagSyntax.intValue() : getNonNullParentConfiguration().getTagSyntax();
    }

    public boolean isTagSyntaxSet() {
        return this.tagSyntax != null;
    }

    public void setInterpolationSyntax(int interpolationSyntax) {
        _TemplateAPI.valideInterpolationSyntaxValue(interpolationSyntax);
        this.interpolationSyntax = Integer.valueOf(interpolationSyntax);
    }

    @Override // freemarker.core.ParserConfiguration
    public int getInterpolationSyntax() {
        return this.interpolationSyntax != null ? this.interpolationSyntax.intValue() : getNonNullParentConfiguration().getInterpolationSyntax();
    }

    public boolean isInterpolationSyntaxSet() {
        return this.interpolationSyntax != null;
    }

    public void setNamingConvention(int namingConvention) {
        _TemplateAPI.validateNamingConventionValue(namingConvention);
        this.namingConvention = Integer.valueOf(namingConvention);
    }

    @Override // freemarker.core.ParserConfiguration
    public int getNamingConvention() {
        return this.namingConvention != null ? this.namingConvention.intValue() : getNonNullParentConfiguration().getNamingConvention();
    }

    public boolean isNamingConventionSet() {
        return this.namingConvention != null;
    }

    public void setWhitespaceStripping(boolean whitespaceStripping) {
        this.whitespaceStripping = Boolean.valueOf(whitespaceStripping);
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getWhitespaceStripping() {
        return this.whitespaceStripping != null ? this.whitespaceStripping.booleanValue() : getNonNullParentConfiguration().getWhitespaceStripping();
    }

    public boolean isWhitespaceStrippingSet() {
        return this.whitespaceStripping != null;
    }

    public void setAutoEscapingPolicy(int autoEscapingPolicy) {
        _TemplateAPI.validateAutoEscapingPolicyValue(autoEscapingPolicy);
        this.autoEscapingPolicy = Integer.valueOf(autoEscapingPolicy);
    }

    @Override // freemarker.core.ParserConfiguration
    public int getAutoEscapingPolicy() {
        return this.autoEscapingPolicy != null ? this.autoEscapingPolicy.intValue() : getNonNullParentConfiguration().getAutoEscapingPolicy();
    }

    public boolean isAutoEscapingPolicySet() {
        return this.autoEscapingPolicy != null;
    }

    public void setOutputFormat(OutputFormat outputFormat) {
        NullArgumentException.check(Configuration.OUTPUT_FORMAT_KEY_CAMEL_CASE, outputFormat);
        this.outputFormat = outputFormat;
    }

    @Override // freemarker.core.ParserConfiguration
    public OutputFormat getOutputFormat() {
        return this.outputFormat != null ? this.outputFormat : getNonNullParentConfiguration().getOutputFormat();
    }

    public boolean isOutputFormatSet() {
        return this.outputFormat != null;
    }

    public void setRecognizeStandardFileExtensions(boolean recognizeStandardFileExtensions) {
        this.recognizeStandardFileExtensions = Boolean.valueOf(recognizeStandardFileExtensions);
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getRecognizeStandardFileExtensions() {
        return this.recognizeStandardFileExtensions != null ? this.recognizeStandardFileExtensions.booleanValue() : getNonNullParentConfiguration().getRecognizeStandardFileExtensions();
    }

    public boolean isRecognizeStandardFileExtensionsSet() {
        return this.recognizeStandardFileExtensions != null;
    }

    public void setStrictSyntaxMode(boolean strictSyntaxMode) {
        this.strictSyntaxMode = Boolean.valueOf(strictSyntaxMode);
    }

    @Override // freemarker.core.ParserConfiguration
    public boolean getStrictSyntaxMode() {
        return this.strictSyntaxMode != null ? this.strictSyntaxMode.booleanValue() : getNonNullParentConfiguration().getStrictSyntaxMode();
    }

    public boolean isStrictSyntaxModeSet() {
        return this.strictSyntaxMode != null;
    }

    @Override // freemarker.core.Configurable
    public void setStrictBeanModels(boolean strict) {
        throw new UnsupportedOperationException("Setting strictBeanModels on " + TemplateConfiguration.class.getSimpleName() + " level isn't supported.");
    }

    public String getEncoding() {
        return this.encoding != null ? this.encoding : getNonNullParentConfiguration().getDefaultEncoding();
    }

    public void setEncoding(String encoding) {
        NullArgumentException.check("encoding", encoding);
        this.encoding = encoding;
    }

    public boolean isEncodingSet() {
        return this.encoding != null;
    }

    public void setTabSize(int tabSize) {
        this.tabSize = Integer.valueOf(tabSize);
    }

    @Override // freemarker.core.ParserConfiguration
    public int getTabSize() {
        return this.tabSize != null ? this.tabSize.intValue() : getNonNullParentConfiguration().getTabSize();
    }

    public boolean isTabSizeSet() {
        return this.tabSize != null;
    }

    @Override // freemarker.core.ParserConfiguration
    public Version getIncompatibleImprovements() {
        return getNonNullParentConfiguration().getIncompatibleImprovements();
    }

    private void checkParentConfigurationSet() {
        if (!this.parentConfigurationSet) {
            throw new IllegalStateException("The TemplateConfiguration wasn't associated with a Configuration yet.");
        }
    }

    private boolean hasAnyConfigurableSet() {
        return isAPIBuiltinEnabledSet() || isArithmeticEngineSet() || isAutoFlushSet() || isAutoImportsSet() || isAutoIncludesSet() || isBooleanFormatSet() || isClassicCompatibleSet() || isCustomDateFormatsSet() || isCustomNumberFormatsSet() || isDateFormatSet() || isDateTimeFormatSet() || isCFormatSet() || isLazyImportsSet() || isLazyAutoImportsSet() || isLocaleSet() || isLogTemplateExceptionsSet() || isWrapUncheckedExceptionsSet() || isNewBuiltinClassResolverSet() || isTruncateBuiltinAlgorithmSet() || isNumberFormatSet() || isObjectWrapperSet() || isOutputEncodingSet() || isShowErrorTipsSet() || isSQLDateAndTimeTimeZoneSet() || isTemplateExceptionHandlerSet() || isAttemptExceptionReporterSet() || isTimeFormatSet() || isTimeZoneSet() || isURLEscapingCharsetSet();
    }

    private Map mergeMaps(Map m1, Map m2, boolean overwriteUpdatesOrder) {
        if (m1 == null) {
            return m2;
        }
        if (m2 == null) {
            return m1;
        }
        if (m1.isEmpty()) {
            return m2;
        }
        if (m2.isEmpty()) {
            return m1;
        }
        LinkedHashMap mergedM = new LinkedHashMap((((m1.size() + m2.size()) * 4) / 3) + 1, 0.75f);
        mergedM.putAll(m1);
        for (Object m2Key : m2.keySet()) {
            mergedM.remove(m2Key);
        }
        mergedM.putAll(m2);
        return mergedM;
    }

    private List<String> mergeLists(List<String> list1, List<String> list2) {
        if (list1 == null) {
            return list2;
        }
        if (list2 == null) {
            return list1;
        }
        if (list1.isEmpty()) {
            return list2;
        }
        if (list2.isEmpty()) {
            return list1;
        }
        ArrayList<String> mergedList = new ArrayList<>(list1.size() + list2.size());
        mergedList.addAll(list1);
        mergedList.addAll(list2);
        return mergedList;
    }
}
