package freemarker.core;

import freemarker.core.TemplateMarkupOutputModel;
import freemarker.template.TemplateModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateMarkupOutputModel.class */
public interface TemplateMarkupOutputModel<MO extends TemplateMarkupOutputModel<MO>> extends TemplateModel {
    MarkupOutputFormat<MO> getOutputFormat();
}
