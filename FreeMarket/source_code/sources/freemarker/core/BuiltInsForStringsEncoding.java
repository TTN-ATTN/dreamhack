package freemarker.core;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.io.UnsupportedEncodingException;
import java.util.List;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding.class */
class BuiltInsForStringsEncoding {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$htmlBI.class */
    static class htmlBI extends BuiltInForLegacyEscaping implements ICIChainMember {
        private final BIBeforeICI2d3d20 prevICIObj = new BIBeforeICI2d3d20();

        htmlBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$htmlBI$BIBeforeICI2d3d20.class */
        static class BIBeforeICI2d3d20 extends BuiltInForLegacyEscaping {
            BIBeforeICI2d3d20() {
            }

            @Override // freemarker.core.BuiltInForLegacyEscaping
            TemplateModel calculateResult(String s, Environment env) {
                return new SimpleScalar(StringUtil.HTMLEnc(s));
            }
        }

        @Override // freemarker.core.BuiltInForLegacyEscaping
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XHTMLEnc(s));
        }

        @Override // freemarker.core.ICIChainMember
        public int getMinimumICIVersion() {
            return _VersionInts.V_2_3_20;
        }

        @Override // freemarker.core.ICIChainMember
        public Object getPreviousICIChainMember() {
            return this.prevICIObj;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$j_stringBI.class */
    static class j_stringBI extends BuiltInForString {
        j_stringBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaStringEnc(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$js_stringBI.class */
    static class js_stringBI extends BuiltInForString {
        js_stringBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.javaScriptStringEnc(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$json_stringBI.class */
    static class json_stringBI extends BuiltInForString {
        json_stringBI() {
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.jsonStringEnc(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$rtfBI.class */
    static class rtfBI extends BuiltInForLegacyEscaping {
        rtfBI() {
        }

        @Override // freemarker.core.BuiltInForLegacyEscaping
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.RTFEnc(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$urlBI.class */
    static class urlBI extends BuiltInForString {
        urlBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$urlBI$UrlBIResult.class */
        static class UrlBIResult extends AbstractUrlBIResult {
            protected UrlBIResult(BuiltIn parent, String target, Environment env) {
                super(parent, target, env);
            }

            @Override // freemarker.core.BuiltInsForStringsEncoding.AbstractUrlBIResult
            protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
                return StringUtil.URLEnc(this.targetAsString, cs);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new UrlBIResult(this, s, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$urlPathBI.class */
    static class urlPathBI extends BuiltInForString {
        urlPathBI() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$urlPathBI$UrlPathBIResult.class */
        static class UrlPathBIResult extends AbstractUrlBIResult {
            protected UrlPathBIResult(BuiltIn parent, String target, Environment env) {
                super(parent, target, env);
            }

            @Override // freemarker.core.BuiltInsForStringsEncoding.AbstractUrlBIResult
            protected String encodeWithCharset(String cs) throws UnsupportedEncodingException {
                return StringUtil.URLPathEnc(this.targetAsString, cs);
            }
        }

        @Override // freemarker.core.BuiltInForString
        TemplateModel calculateResult(String s, Environment env) {
            return new UrlPathBIResult(this, s, env);
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$xhtmlBI.class */
    static class xhtmlBI extends BuiltInForLegacyEscaping {
        xhtmlBI() {
        }

        @Override // freemarker.core.BuiltInForLegacyEscaping
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XHTMLEnc(s));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$xmlBI.class */
    static class xmlBI extends BuiltInForLegacyEscaping {
        xmlBI() {
        }

        @Override // freemarker.core.BuiltInForLegacyEscaping
        TemplateModel calculateResult(String s, Environment env) {
            return new SimpleScalar(StringUtil.XMLEnc(s));
        }
    }

    private BuiltInsForStringsEncoding() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/BuiltInsForStringsEncoding$AbstractUrlBIResult.class */
    static abstract class AbstractUrlBIResult implements TemplateScalarModel, TemplateMethodModel {
        protected final BuiltIn parent;
        protected final String targetAsString;
        private final Environment env;
        private String cachedResult;

        protected abstract String encodeWithCharset(String str) throws UnsupportedEncodingException;

        protected AbstractUrlBIResult(BuiltIn parent, String target, Environment env) {
            this.parent = parent;
            this.targetAsString = target;
            this.env = env;
        }

        @Override // freemarker.template.TemplateMethodModel
        public Object exec(List args) throws TemplateModelException {
            this.parent.checkMethodArgCount(args.size(), 1);
            try {
                return new SimpleScalar(encodeWithCharset((String) args.get(0)));
            } catch (UnsupportedEncodingException e) {
                throw new _TemplateModelException(e, "Failed to execute URL encoding.");
            }
        }

        @Override // freemarker.template.TemplateScalarModel
        public String getAsString() throws TemplateModelException {
            if (this.cachedResult == null) {
                String cs = this.env.getEffectiveURLEscapingCharset();
                if (cs == null) {
                    throw new _TemplateModelException("To do URL encoding, the framework that encloses FreeMarker must specify the \"", "output_encoding", "\" setting or the \"", "url_escaping_charset", "\" setting, so ask the programmers to set them. Or, as a last chance, you can set the url_encoding_charset setting in the template, e.g. <#setting ", "url_escaping_charset", "='ISO-8859-1'>, or give the charset explicitly to the built-in, e.g. foo?url('ISO-8859-1').");
                }
                try {
                    this.cachedResult = encodeWithCharset(cs);
                } catch (UnsupportedEncodingException e) {
                    throw new _TemplateModelException(e, "Failed to execute URL encoding.");
                }
            }
            return this.cachedResult;
        }
    }
}
