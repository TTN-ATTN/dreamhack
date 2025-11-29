package freemarker.core;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateHashModelEx2;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;
import freemarker.template._ObjectWrappers;
import freemarker.template.utility.TemplateModelUtils;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/GetOptionalTemplateMethod.class */
class GetOptionalTemplateMethod implements TemplateMethodModelEx {
    static final GetOptionalTemplateMethod INSTANCE = new GetOptionalTemplateMethod("get_optional_template");
    static final GetOptionalTemplateMethod INSTANCE_CC = new GetOptionalTemplateMethod("getOptionalTemplate");
    private static final String OPTION_ENCODING = "encoding";
    private static final String OPTION_PARSE = "parse";
    private static final String RESULT_INCLUDE = "include";
    private static final String RESULT_IMPORT = "import";
    private static final String RESULT_EXISTS = "exists";
    private final String methodName;

    private GetOptionalTemplateMethod(String builtInVarName) {
        this.methodName = "." + builtInVarName;
    }

    @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
    public Object exec(List args) throws TemplateModelException {
        TemplateHashModelEx options;
        int argCnt = args.size();
        if (argCnt < 1 || argCnt > 2) {
            throw _MessageUtil.newArgCntError(this.methodName, argCnt, 1, 2);
        }
        final Environment env = Environment.getCurrentEnvironment();
        if (env == null) {
            throw new IllegalStateException("No freemarer.core.Environment is associated to the current thread.");
        }
        TemplateModel arg = (TemplateModel) args.get(0);
        if (!(arg instanceof TemplateScalarModel)) {
            throw _MessageUtil.newMethodArgMustBeStringException(this.methodName, 0, arg);
        }
        String templateName = EvalUtil.modelToString((TemplateScalarModel) arg, null, env);
        try {
            String absTemplateName = env.toFullTemplateName(env.getCurrentTemplate().getName(), templateName);
            if (argCnt > 1) {
                TemplateModel arg2 = (TemplateModel) args.get(1);
                if (!(arg2 instanceof TemplateHashModelEx)) {
                    throw _MessageUtil.newMethodArgMustBeExtendedHashException(this.methodName, 1, arg2);
                }
                options = (TemplateHashModelEx) arg2;
            } else {
                options = null;
            }
            String encoding = null;
            boolean parse = true;
            if (options != null) {
                TemplateHashModelEx2.KeyValuePairIterator kvpi = TemplateModelUtils.getKeyValuePairIterator(options);
                while (kvpi.hasNext()) {
                    TemplateHashModelEx2.KeyValuePair kvp = kvpi.next();
                    TemplateModel optNameTM = kvp.getKey();
                    if (!(optNameTM instanceof TemplateScalarModel)) {
                        throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "All keys in the options hash must be strings, but found ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(optNameTM)));
                    }
                    String optName = ((TemplateScalarModel) optNameTM).getAsString();
                    TemplateModel optValue = kvp.getValue();
                    if (OPTION_ENCODING.equals(optName)) {
                        encoding = getStringOption(OPTION_ENCODING, optValue);
                    } else {
                        if (!OPTION_PARSE.equals(optName)) {
                            throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "Unsupported option ", new _DelayedJQuote(optName), "; valid names are: ", new _DelayedJQuote(OPTION_ENCODING), ", ", new _DelayedJQuote(OPTION_PARSE), ".");
                        }
                        parse = getBooleanOption(OPTION_PARSE, optValue);
                    }
                }
            }
            try {
                final Template template = env.getTemplateForInclusion(absTemplateName, encoding, parse, true);
                SimpleHash result = new SimpleHash(_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                result.put(RESULT_EXISTS, template != null);
                if (template != null) {
                    result.put(RESULT_INCLUDE, new TemplateDirectiveModel() { // from class: freemarker.core.GetOptionalTemplateMethod.1
                        @Override // freemarker.template.TemplateDirectiveModel
                        public void execute(Environment env2, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException {
                            if (!params.isEmpty()) {
                                throw new TemplateException("This directive supports no parameters.", env2);
                            }
                            if (loopVars.length != 0) {
                                throw new TemplateException("This directive supports no loop variables.", env2);
                            }
                            if (body != null) {
                                throw new TemplateException("This directive supports no nested content.", env2);
                            }
                            env2.include(template);
                        }
                    });
                    result.put("import", new TemplateMethodModelEx() { // from class: freemarker.core.GetOptionalTemplateMethod.2
                        @Override // freemarker.template.TemplateMethodModelEx, freemarker.template.TemplateMethodModel
                        public Object exec(List args2) throws TemplateModelException {
                            if (!args2.isEmpty()) {
                                throw new TemplateModelException("This method supports no parameters.");
                            }
                            try {
                                return env.importLib(template, (String) null);
                            } catch (TemplateException | IOException e) {
                                throw new _TemplateModelException(e, "Failed to import loaded template; see cause exception");
                            }
                        }
                    });
                }
                return result;
            } catch (IOException e) {
                throw new _TemplateModelException(e, "I/O error when trying to load optional template ", new _DelayedJQuote(absTemplateName), "; see cause exception");
            }
        } catch (MalformedTemplateNameException e2) {
            throw new _TemplateModelException(e2, "Failed to convert template path to full path; see cause exception.");
        }
    }

    private boolean getBooleanOption(String optionName, TemplateModel value) throws TemplateModelException {
        if (value instanceof TemplateBooleanModel) {
            return ((TemplateBooleanModel) value).getAsBoolean();
        }
        throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a boolean, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
    }

    private String getStringOption(String optionName, TemplateModel value) throws TemplateModelException {
        if (value instanceof TemplateScalarModel) {
            return EvalUtil.modelToString((TemplateScalarModel) value, null, null);
        }
        throw _MessageUtil.newMethodArgInvalidValueException(this.methodName, 1, "The value of the ", new _DelayedJQuote(optionName), " option must be a string, but it was ", new _DelayedAOrAn(new _DelayedFTLTypeDescription(value)), ".");
    }
}
