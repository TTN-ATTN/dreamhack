package freemarker.core;

import ch.qos.logback.classic.pattern.CallerDataConverter;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/OptInTemplateClassResolver.class */
public class OptInTemplateClassResolver implements TemplateClassResolver {
    private final Set allowedClasses;
    private final List trustedTemplatePrefixes;
    private final Set trustedTemplateNames;

    public OptInTemplateClassResolver(Set allowedClasses, List trustedTemplates) {
        this.allowedClasses = allowedClasses != null ? allowedClasses : Collections.EMPTY_SET;
        if (trustedTemplates != null) {
            this.trustedTemplateNames = new HashSet();
            this.trustedTemplatePrefixes = new ArrayList();
            Iterator it = trustedTemplates.iterator();
            while (it.hasNext()) {
                String li = (String) it.next();
                li = li.startsWith("/") ? li.substring(1) : li;
                if (li.endsWith("*")) {
                    this.trustedTemplatePrefixes.add(li.substring(0, li.length() - 1));
                } else {
                    this.trustedTemplateNames.add(li);
                }
            }
            return;
        }
        this.trustedTemplateNames = Collections.EMPTY_SET;
        this.trustedTemplatePrefixes = Collections.EMPTY_LIST;
    }

    @Override // freemarker.core.TemplateClassResolver
    public Class resolve(String className, Environment env, Template template) throws TemplateException {
        String templateName = safeGetTemplateName(template);
        if (templateName != null && (this.trustedTemplateNames.contains(templateName) || hasMatchingPrefix(templateName))) {
            return TemplateClassResolver.SAFER_RESOLVER.resolve(className, env, template);
        }
        if (!this.allowedClasses.contains(className)) {
            throw new _MiscTemplateException(env, "Instantiating ", className, " is not allowed in the template for security reasons. (If you run into this problem when using ?new in a template, you may want to check the \"", "new_builtin_class_resolver", "\" setting in the FreeMarker configuration.)");
        }
        try {
            return ClassUtil.forName(className);
        } catch (ClassNotFoundException e) {
            throw new _MiscTemplateException(e, env);
        }
    }

    protected String safeGetTemplateName(Template template) {
        String name;
        if (template == null || (name = template.getName()) == null) {
            return null;
        }
        String decodedName = name;
        if (decodedName.indexOf(37) != -1) {
            decodedName = StringUtil.replace(StringUtil.replace(StringUtil.replace(StringUtil.replace(StringUtil.replace(StringUtil.replace(decodedName, "%2e", ".", false, false), "%2E", ".", false, false), "%2f", "/", false, false), "%2F", "/", false, false), "%5c", "\\", false, false), "%5C", "\\", false, false);
        }
        int dotDotIdx = decodedName.indexOf(CallerDataConverter.DEFAULT_RANGE_DELIMITER);
        if (dotDotIdx != -1) {
            int before = dotDotIdx - 1 >= 0 ? decodedName.charAt(dotDotIdx - 1) : -1;
            int after = dotDotIdx + 2 < decodedName.length() ? decodedName.charAt(dotDotIdx + 2) : -1;
            if ((before == -1 || before == 47 || before == 92) && (after == -1 || after == 47 || after == 92)) {
                return null;
            }
        }
        return name.startsWith("/") ? name.substring(1) : name;
    }

    private boolean hasMatchingPrefix(String name) {
        for (int i = 0; i < this.trustedTemplatePrefixes.size(); i++) {
            String prefix = (String) this.trustedTemplatePrefixes.get(i);
            if (name.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
