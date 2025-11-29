package freemarker.core;

import freemarker.ext.beans._MethodUtil;
import freemarker.log.Logger;
import freemarker.template.Template;
import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.StringUtil;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ErrorDescriptionBuilder.class */
public class _ErrorDescriptionBuilder {
    private static final Logger LOG = Logger.getLogger("freemarker.runtime");
    private final String description;
    private final Object[] descriptionParts;
    private Expression blamed;
    private boolean showBlamer;
    private Object tip;
    private Object[] tips;
    private Template template;

    public _ErrorDescriptionBuilder(String description) {
        this.description = description;
        this.descriptionParts = null;
    }

    public _ErrorDescriptionBuilder(Object... descriptionParts) {
        this.descriptionParts = descriptionParts;
        this.description = null;
    }

    public String toString() {
        return toString((TemplateElement) null, true);
    }

    public String toString(TemplateElement parentElement, boolean showTips) {
        Object[] allTips;
        if (this.blamed == null && this.tips == null && this.tip == null && this.descriptionParts == null) {
            return this.description;
        }
        StringBuilder sb = new StringBuilder(200);
        if (parentElement != null && this.blamed != null && this.showBlamer) {
            try {
                Blaming blaming = findBlaming(parentElement, this.blamed, 0);
                if (blaming != null) {
                    sb.append("For ");
                    String nss = blaming.blamer.getNodeTypeSymbol();
                    char q = nss.indexOf(34) == -1 ? '\"' : '`';
                    sb.append(q).append(nss).append(q);
                    sb.append(" ").append(blaming.roleOfblamed).append(": ");
                }
            } catch (Throwable e) {
                LOG.error("Error when searching blamer for better error message.", e);
            }
        }
        if (this.description != null) {
            sb.append(this.description);
        } else {
            appendParts(sb, this.descriptionParts);
        }
        String extraTip = null;
        if (this.blamed != null) {
            for (int idx = sb.length() - 1; idx >= 0 && Character.isWhitespace(sb.charAt(idx)); idx--) {
                sb.deleteCharAt(idx);
            }
            char lastChar = sb.length() > 0 ? sb.charAt(sb.length() - 1) : (char) 0;
            if (lastChar != 0) {
                sb.append('\n');
            }
            if (lastChar != ':') {
                sb.append("The blamed expression:\n");
            }
            String[] lines = splitToLines(this.blamed.toString());
            int i = 0;
            while (i < lines.length) {
                sb.append(i == 0 ? "==> " : "\n    ");
                sb.append(lines[i]);
                i++;
            }
            sb.append("  [");
            sb.append(this.blamed.getStartLocation());
            sb.append(']');
            if (containsSingleInterpolatoinLiteral(this.blamed, 0)) {
                extraTip = "It has been noticed that you are using ${...} as the sole content of a quoted string. That does nothing but forcably converts the value inside ${...} to string (as it inserts it into the enclosing string). If that's not what you meant, just remove the quotation marks, ${ and }; you don't need them. If you indeed wanted to convert to string, use myExpression?string instead.";
            }
        }
        if (showTips) {
            int allTipsLen = (this.tips != null ? this.tips.length : 0) + (this.tip != null ? 1 : 0) + (extraTip != null ? 1 : 0);
            if (this.tips != null && allTipsLen == this.tips.length) {
                allTips = this.tips;
            } else {
                allTips = new Object[allTipsLen];
                int dst = 0;
                if (this.tip != null) {
                    dst = 0 + 1;
                    allTips[0] = this.tip;
                }
                if (this.tips != null) {
                    for (int i2 = 0; i2 < this.tips.length; i2++) {
                        int i3 = dst;
                        dst++;
                        allTips[i3] = this.tips[i2];
                    }
                }
                if (extraTip != null) {
                    int i4 = dst;
                    int i5 = dst + 1;
                    allTips[i4] = extraTip;
                }
            }
            if (allTips != null && allTips.length > 0) {
                sb.append("\n\n");
                for (int i6 = 0; i6 < allTips.length; i6++) {
                    if (i6 != 0) {
                        sb.append('\n');
                    }
                    sb.append(_CoreAPI.ERROR_MESSAGE_HR).append('\n');
                    sb.append("Tip: ");
                    Object tip = allTips[i6];
                    if (!(tip instanceof Object[])) {
                        sb.append(allTips[i6]);
                    } else {
                        appendParts(sb, (Object[]) tip);
                    }
                }
                sb.append('\n').append(_CoreAPI.ERROR_MESSAGE_HR);
            }
        }
        return sb.toString();
    }

    private boolean containsSingleInterpolatoinLiteral(Expression exp, int recursionDepth) {
        if (exp == null || recursionDepth > 20) {
            return false;
        }
        if ((exp instanceof StringLiteral) && ((StringLiteral) exp).isSingleInterpolationLiteral()) {
            return true;
        }
        int paramCnt = exp.getParameterCount();
        for (int i = 0; i < paramCnt; i++) {
            Object paramValue = exp.getParameterValue(i);
            if (paramValue instanceof Expression) {
                boolean result = containsSingleInterpolatoinLiteral((Expression) paramValue, recursionDepth + 1);
                if (result) {
                    return true;
                }
            }
        }
        return false;
    }

    private Blaming findBlaming(TemplateObject parent, Expression blamed, int recursionDepth) {
        Blaming blaming;
        if (recursionDepth > 50) {
            return null;
        }
        int paramCnt = parent.getParameterCount();
        for (int i = 0; i < paramCnt; i++) {
            Object paramValue = parent.getParameterValue(i);
            if (paramValue == blamed) {
                Blaming blaming2 = new Blaming();
                blaming2.blamer = parent;
                blaming2.roleOfblamed = parent.getParameterRole(i);
                return blaming2;
            }
            if ((paramValue instanceof TemplateObject) && (blaming = findBlaming((TemplateObject) paramValue, blamed, recursionDepth + 1)) != null) {
                return blaming;
            }
        }
        return null;
    }

    private void appendParts(StringBuilder sb, Object[] parts) {
        Template template = this.template != null ? this.template : this.blamed != null ? this.blamed.getTemplate() : null;
        for (Object partObj : parts) {
            if (partObj instanceof Object[]) {
                appendParts(sb, (Object[]) partObj);
            } else {
                String partStr = tryToString(partObj);
                if (partStr == null) {
                    partStr = BeanDefinitionParserDelegate.NULL_ELEMENT;
                }
                if (template != null) {
                    if (partStr.length() > 4 && partStr.charAt(0) == '<' && ((partStr.charAt(1) == '#' || partStr.charAt(1) == '@' || (partStr.charAt(1) == '/' && (partStr.charAt(2) == '#' || partStr.charAt(2) == '@'))) && partStr.charAt(partStr.length() - 1) == '>')) {
                        if (template.getActualTagSyntax() == 2) {
                            sb.append('[');
                            sb.append(partStr.substring(1, partStr.length() - 1));
                            sb.append(']');
                        } else {
                            sb.append(partStr);
                        }
                    } else {
                        sb.append(partStr);
                    }
                } else {
                    sb.append(partStr);
                }
            }
        }
    }

    public static String toString(Object partObj) {
        return toString(partObj, false);
    }

    public static String tryToString(Object partObj) {
        return toString(partObj, true);
    }

    private static String toString(Object partObj, boolean suppressToStringException) {
        String partStr;
        if (partObj == null) {
            return null;
        }
        if (partObj instanceof Class) {
            partStr = ClassUtil.getShortClassName((Class) partObj);
        } else if ((partObj instanceof Method) || (partObj instanceof Constructor)) {
            partStr = _MethodUtil.toString((Member) partObj);
        } else {
            partStr = suppressToStringException ? StringUtil.tryToString(partObj) : partObj.toString();
        }
        return partStr;
    }

    private String[] splitToLines(String s) {
        String[] lines = StringUtil.split(StringUtil.replace(StringUtil.replace(s, "\r\n", "\n"), "\r", "\n"), '\n');
        return lines;
    }

    public _ErrorDescriptionBuilder template(Template template) {
        this.template = template;
        return this;
    }

    public _ErrorDescriptionBuilder blame(Expression blamedExpr) {
        this.blamed = blamedExpr;
        return this;
    }

    public _ErrorDescriptionBuilder showBlamer(boolean showBlamer) {
        this.showBlamer = showBlamer;
        return this;
    }

    public _ErrorDescriptionBuilder tip(String tip) {
        tip((Object) tip);
        return this;
    }

    public _ErrorDescriptionBuilder tip(Object... tip) {
        tip((Object) tip);
        return this;
    }

    private _ErrorDescriptionBuilder tip(Object tip) {
        if (tip == null) {
            return this;
        }
        if (this.tip == null) {
            this.tip = tip;
        } else if (this.tips == null) {
            this.tips = new Object[]{tip};
        } else {
            int origTipsLen = this.tips.length;
            Object[] newTips = new Object[origTipsLen + 1];
            for (int i = 0; i < origTipsLen; i++) {
                newTips[i] = this.tips[i];
            }
            newTips[origTipsLen] = tip;
            this.tips = newTips;
        }
        return this;
    }

    public _ErrorDescriptionBuilder tips(Object... tips) {
        if (tips == null || tips.length == 0) {
            return this;
        }
        if (this.tips == null) {
            this.tips = tips;
        } else {
            int origTipsLen = this.tips.length;
            int additionalTipsLen = tips.length;
            Object[] newTips = new Object[origTipsLen + additionalTipsLen];
            for (int i = 0; i < origTipsLen; i++) {
                newTips[i] = this.tips[i];
            }
            for (int i2 = 0; i2 < additionalTipsLen; i2++) {
                newTips[origTipsLen + i2] = tips[i2];
            }
            this.tips = newTips;
        }
        return this;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/_ErrorDescriptionBuilder$Blaming.class */
    private static class Blaming {
        TemplateObject blamer;
        ParameterRole roleOfblamed;

        private Blaming() {
        }
    }
}
