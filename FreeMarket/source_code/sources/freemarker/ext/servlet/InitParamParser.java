package freemarker.ext.servlet;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.core._ObjectBuilderSettingEvaluator;
import freemarker.core._SettingEvaluationEnvironment;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template._VersionInts;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import org.springframework.beans.PropertyAccessor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/servlet/InitParamParser.class */
final class InitParamParser {
    static final String TEMPLATE_PATH_PREFIX_CLASS = "class://";
    static final String TEMPLATE_PATH_PREFIX_CLASSPATH = "classpath:";
    static final String TEMPLATE_PATH_PREFIX_FILE = "file://";
    static final String TEMPLATE_PATH_SETTINGS_BI_NAME = "settings";
    private static final Logger LOG = Logger.getLogger("freemarker.servlet");

    private InitParamParser() {
    }

    static TemplateLoader createTemplateLoader(String templatePath, Configuration cfg, Class classLoaderClass, ServletContext srvCtx) throws IOException {
        TemplateLoader templateLoader;
        int settingAssignmentsStart = findTemplatePathSettingAssignmentsStart(templatePath);
        String pureTemplatePath = (settingAssignmentsStart == -1 ? templatePath : templatePath.substring(0, settingAssignmentsStart)).trim();
        if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_CLASS)) {
            String packagePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_CLASS.length());
            templateLoader = new ClassTemplateLoader((Class<?>) classLoaderClass, normalizeToAbsolutePackagePath(packagePath));
        } else if (pureTemplatePath.startsWith("classpath:")) {
            String packagePath2 = pureTemplatePath.substring("classpath:".length());
            String packagePath3 = normalizeToAbsolutePackagePath(packagePath2);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                LOG.warn("No Thread Context Class Loader was found. Falling back to the class loader of " + classLoaderClass.getName() + ".");
                classLoader = classLoaderClass.getClassLoader();
            }
            templateLoader = new ClassTemplateLoader(classLoader, packagePath3);
        } else if (pureTemplatePath.startsWith(TEMPLATE_PATH_PREFIX_FILE)) {
            String filePath = pureTemplatePath.substring(TEMPLATE_PATH_PREFIX_FILE.length());
            templateLoader = new FileTemplateLoader(new File(filePath));
        } else if (pureTemplatePath.startsWith(PropertyAccessor.PROPERTY_KEY_PREFIX) && cfg.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_22) {
            if (!pureTemplatePath.endsWith("]")) {
                throw new TemplatePathParsingException("Failed to parse template path; closing \"]\" is missing.");
            }
            String commaSepItems = pureTemplatePath.substring(1, pureTemplatePath.length() - 1).trim();
            List listItems = parseCommaSeparatedTemplatePaths(commaSepItems);
            TemplateLoader[] templateLoaders = new TemplateLoader[listItems.size()];
            for (int i = 0; i < listItems.size(); i++) {
                String pathItem = (String) listItems.get(i);
                templateLoaders[i] = createTemplateLoader(pathItem, cfg, classLoaderClass, srvCtx);
            }
            templateLoader = new MultiTemplateLoader(templateLoaders);
        } else {
            if (pureTemplatePath.startsWith("{") && cfg.getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_22) {
                throw new TemplatePathParsingException("Template paths starting with \"{\" are reseved for future purposes");
            }
            templateLoader = new WebappTemplateLoader(srvCtx, pureTemplatePath);
        }
        if (settingAssignmentsStart != -1) {
            try {
                int nextPos = _ObjectBuilderSettingEvaluator.configureBean(templatePath, templatePath.indexOf(40, settingAssignmentsStart) + 1, templateLoader, _SettingEvaluationEnvironment.getCurrent());
                if (nextPos != templatePath.length()) {
                    throw new TemplatePathParsingException("Template path should end after the setting list in: " + templatePath);
                }
            } catch (Exception e) {
                throw new TemplatePathParsingException("Failed to set properties in: " + templatePath, e);
            }
        }
        return templateLoader;
    }

    static String normalizeToAbsolutePackagePath(String path) {
        while (path.startsWith("/")) {
            path = path.substring(1);
        }
        return "/" + path;
    }

    static List parseCommaSeparatedList(String value) throws ParseException {
        List valuesList = new ArrayList();
        String[] values = StringUtil.split(value, ',');
        for (int i = 0; i < values.length; i++) {
            String s = values[i].trim();
            if (s.length() != 0) {
                valuesList.add(s);
            } else if (i != values.length - 1) {
                throw new ParseException("Missing list item berfore a comma", -1);
            }
        }
        return valuesList;
    }

    static List parseCommaSeparatedPatterns(String value) throws ParseException {
        List values = parseCommaSeparatedList(value);
        List patterns = new ArrayList(values.size());
        for (int i = 0; i < values.size(); i++) {
            patterns.add(Pattern.compile((String) values.get(i)));
        }
        return patterns;
    }

    static List parseCommaSeparatedTemplatePaths(String commaSepItems) {
        List listItems = new ArrayList();
        while (commaSepItems.length() != 0) {
            int itemSettingAssignmentsStart = findTemplatePathSettingAssignmentsStart(commaSepItems);
            int pureItemEnd = itemSettingAssignmentsStart != -1 ? itemSettingAssignmentsStart : commaSepItems.length();
            int prevComaIdx = commaSepItems.lastIndexOf(44, pureItemEnd - 1);
            int itemStart = prevComaIdx != -1 ? prevComaIdx + 1 : 0;
            String item = commaSepItems.substring(itemStart).trim();
            if (item.length() != 0) {
                listItems.add(0, item);
            } else if (listItems.size() > 0) {
                throw new TemplatePathParsingException("Missing list item before a comma");
            }
            commaSepItems = prevComaIdx != -1 ? commaSepItems.substring(0, prevComaIdx).trim() : "";
        }
        return listItems;
    }

    static int findTemplatePathSettingAssignmentsStart(String s) {
        int pos = s.length() - 1;
        while (pos >= 0 && Character.isWhitespace(s.charAt(pos))) {
            pos--;
        }
        if (pos < 0 || s.charAt(pos) != ')') {
            return -1;
        }
        int pos2 = pos - 1;
        int parLevel = 1;
        int mode = 0;
        while (parLevel > 0) {
            if (pos2 < 0) {
                return -1;
            }
            char c = s.charAt(pos2);
            switch (mode) {
                case 0:
                    switch (c) {
                        case '\"':
                            mode = 2;
                            break;
                        case '\'':
                            mode = 1;
                            break;
                        case '(':
                            parLevel--;
                            break;
                        case ')':
                            parLevel++;
                            break;
                    }
                case 1:
                    if (c == '\'' && (pos2 <= 0 || s.charAt(pos2 - 1) != '\\')) {
                        mode = 0;
                        break;
                    } else {
                        break;
                    }
                case 2:
                    if (c == '\"' && (pos2 <= 0 || s.charAt(pos2 - 1) != '\\')) {
                        mode = 0;
                        break;
                    } else {
                        break;
                    }
                    break;
            }
            pos2--;
        }
        while (pos2 >= 0 && Character.isWhitespace(s.charAt(pos2))) {
            pos2--;
        }
        int biNameEnd = pos2 + 1;
        while (pos2 >= 0 && Character.isJavaIdentifierPart(s.charAt(pos2))) {
            pos2--;
        }
        int biNameStart = pos2 + 1;
        if (biNameStart == biNameEnd) {
            return -1;
        }
        String biName = s.substring(biNameStart, biNameEnd);
        while (pos2 >= 0 && Character.isWhitespace(s.charAt(pos2))) {
            pos2--;
        }
        if (pos2 < 0 || s.charAt(pos2) != '?') {
            return -1;
        }
        if (!biName.equals(TEMPLATE_PATH_SETTINGS_BI_NAME)) {
            throw new TemplatePathParsingException(StringUtil.jQuote(biName) + " is unexpected after the \"?\". Expected \"" + TEMPLATE_PATH_SETTINGS_BI_NAME + "\".");
        }
        return pos2;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/servlet/InitParamParser$TemplatePathParsingException.class */
    private static final class TemplatePathParsingException extends RuntimeException {
        public TemplatePathParsingException(String message, Throwable cause) {
            super(message, cause);
        }

        public TemplatePathParsingException(String message) {
            super(message);
        }
    }
}
