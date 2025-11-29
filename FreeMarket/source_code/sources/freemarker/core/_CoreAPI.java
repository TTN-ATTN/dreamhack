package freemarker.core;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;
import freemarker.template._TemplateAPI;
import freemarker.template.utility.ClassUtil;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;
import org.apache.catalina.Lifecycle;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_CoreAPI.class */
public class _CoreAPI {
    public static final String ERROR_MESSAGE_HR = "----";
    public static final Set<String> ALL_BUILT_IN_DIRECTIVE_NAMES;
    public static final Set<String> LEGACY_BUILT_IN_DIRECTIVE_NAMES;
    public static final Set<String> CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES;

    private _CoreAPI() {
    }

    private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String commonName) {
        allNames.add(commonName);
        lcNames.add(commonName);
        ccNames.add(commonName);
    }

    private static void addName(Set<String> allNames, Set<String> lcNames, Set<String> ccNames, String lcName, String ccName) {
        allNames.add(lcName);
        allNames.add(ccName);
        lcNames.add(lcName);
        ccNames.add(ccName);
    }

    static {
        Set<String> allNames = new TreeSet<>();
        Set<String> lcNames = new TreeSet<>();
        Set<String> ccNames = new TreeSet<>();
        addName(allNames, lcNames, ccNames, "assign");
        addName(allNames, lcNames, ccNames, "attempt");
        addName(allNames, lcNames, ccNames, "autoesc", "autoEsc");
        addName(allNames, lcNames, ccNames, "break");
        addName(allNames, lcNames, ccNames, "call");
        addName(allNames, lcNames, ccNames, "case");
        addName(allNames, lcNames, ccNames, "comment");
        addName(allNames, lcNames, ccNames, "compress");
        addName(allNames, lcNames, ccNames, "continue");
        addName(allNames, lcNames, ccNames, "default");
        addName(allNames, lcNames, ccNames, "else");
        addName(allNames, lcNames, ccNames, "elseif", "elseIf");
        addName(allNames, lcNames, ccNames, "escape");
        addName(allNames, lcNames, ccNames, "fallback");
        addName(allNames, lcNames, ccNames, "flush");
        addName(allNames, lcNames, ccNames, "foreach", "forEach");
        addName(allNames, lcNames, ccNames, "ftl");
        addName(allNames, lcNames, ccNames, "function");
        addName(allNames, lcNames, ccNames, "global");
        addName(allNames, lcNames, ccNames, "if");
        addName(allNames, lcNames, ccNames, DefaultBeanDefinitionDocumentReader.IMPORT_ELEMENT);
        addName(allNames, lcNames, ccNames, "include");
        addName(allNames, lcNames, ccNames, "items");
        addName(allNames, lcNames, ccNames, BeanDefinitionParserDelegate.LIST_ELEMENT);
        addName(allNames, lcNames, ccNames, "local");
        addName(allNames, lcNames, ccNames, "lt");
        addName(allNames, lcNames, ccNames, "macro");
        addName(allNames, lcNames, ccNames, "nested");
        addName(allNames, lcNames, ccNames, "noautoesc", "noAutoEsc");
        addName(allNames, lcNames, ccNames, "noescape", "noEscape");
        addName(allNames, lcNames, ccNames, "noparse", "noParse");
        addName(allNames, lcNames, ccNames, "nt");
        addName(allNames, lcNames, ccNames, "outputformat", Configuration.OUTPUT_FORMAT_KEY_CAMEL_CASE);
        addName(allNames, lcNames, ccNames, "recover");
        addName(allNames, lcNames, ccNames, "recurse");
        addName(allNames, lcNames, ccNames, "return");
        addName(allNames, lcNames, ccNames, "rt");
        addName(allNames, lcNames, ccNames, "sep");
        addName(allNames, lcNames, ccNames, "setting");
        addName(allNames, lcNames, ccNames, Lifecycle.STOP_EVENT);
        addName(allNames, lcNames, ccNames, "switch");
        addName(allNames, lcNames, ccNames, "t");
        addName(allNames, lcNames, ccNames, "transform");
        addName(allNames, lcNames, ccNames, "visit");
        ALL_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(allNames);
        LEGACY_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(lcNames);
        CAMEL_CASE_BUILT_IN_DIRECTIVE_NAMES = Collections.unmodifiableSet(ccNames);
    }

    public static Set<String> getSupportedBuiltInNames(int namingConvention) {
        Set<String> names;
        if (namingConvention == 10) {
            names = BuiltIn.BUILT_INS_BY_NAME.keySet();
        } else if (namingConvention == 11) {
            names = BuiltIn.SNAKE_CASE_NAMES;
        } else if (namingConvention == 12) {
            names = BuiltIn.CAMEL_CASE_NAMES;
        } else {
            throw new IllegalArgumentException("Unsupported naming convention constant: " + namingConvention);
        }
        return Collections.unmodifiableSet(names);
    }

    public static void appendInstructionStackItem(TemplateElement stackEl, StringBuilder sb) {
        Environment.appendInstructionStackItem(stackEl, sb);
    }

    public static TemplateElement[] getInstructionStackSnapshot(Environment env) {
        return env.getInstructionStackSnapshot();
    }

    public static void outputInstructionStack(TemplateElement[] instructionStackSnapshot, boolean terseMode, Writer pw) throws IOException {
        Environment.outputInstructionStack(instructionStackSnapshot, terseMode, pw);
    }

    public static final void addThreadInterruptedChecks(Template template) {
        try {
            new ThreadInterruptionSupportTemplatePostProcessor().postProcess(template);
        } catch (TemplatePostProcessorException e) {
            throw new RuntimeException("Template post-processing failed", e);
        }
    }

    public static final void checkHasNoNestedContent(TemplateDirectiveBody body) throws NestedContentNotSupportedException {
        NestedContentNotSupportedException.check(body);
    }

    public static final void replaceText(TextBlock textBlock, String text) {
        textBlock.replaceText(text);
    }

    public static void checkSettingValueItemsType(String somethingsSentenceStart, Class<?> expectedClass, Collection<?> values) {
        if (values == null) {
            return;
        }
        for (Object value : values) {
            if (!expectedClass.isInstance(value)) {
                throw new IllegalArgumentException(somethingsSentenceStart + " must be instances of " + ClassUtil.getShortClassName(expectedClass) + ", but one of them was a(n) " + ClassUtil.getShortClassNameOfObject(value) + ".");
            }
        }
    }

    public static TemplateModelException ensureIsTemplateModelException(String modelOpMsg, TemplateException e) {
        if (e instanceof TemplateModelException) {
            return (TemplateModelException) e;
        }
        return new _TemplateModelException(_TemplateAPI.getBlamedExpression(e), e.getCause(), e.getEnvironment(), modelOpMsg);
    }

    public static TemplateElement getParentElement(TemplateElement te) {
        return te.getParentElement();
    }

    public static TemplateElement getChildElement(TemplateElement te, int index) {
        return te.getChild(index);
    }

    public static void setPreventStrippings(FMParser parser, boolean preventStrippings) {
        parser.setPreventStrippings(preventStrippings);
    }

    public static boolean isLazilyGeneratedSequenceModel(TemplateCollectionModel model) {
        return (model instanceof LazilyGeneratedCollectionModel) && ((LazilyGeneratedCollectionModel) model).isSequence();
    }
}
