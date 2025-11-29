package freemarker.core;

import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TruncateBuiltinAlgorithm.class */
public abstract class TruncateBuiltinAlgorithm {
    public abstract TemplateModel truncateM(String str, int i, TemplateModel templateModel, Integer num, Environment environment) throws TemplateException;

    public abstract TemplateScalarModel truncate(String str, int i, TemplateScalarModel templateScalarModel, Integer num, Environment environment) throws TemplateException;

    public abstract TemplateScalarModel truncateW(String str, int i, TemplateScalarModel templateScalarModel, Integer num, Environment environment) throws TemplateException;

    public abstract TemplateModel truncateWM(String str, int i, TemplateModel templateModel, Integer num, Environment environment) throws TemplateException;

    public abstract TemplateScalarModel truncateC(String str, int i, TemplateScalarModel templateScalarModel, Integer num, Environment environment) throws TemplateException;

    public abstract TemplateModel truncateCM(String str, int i, TemplateModel templateModel, Integer num, Environment environment) throws TemplateException;
}
