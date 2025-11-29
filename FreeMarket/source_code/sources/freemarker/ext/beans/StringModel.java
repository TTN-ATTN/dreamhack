package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.TemplateScalarModel;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/StringModel.class */
public class StringModel extends BeanModel implements TemplateScalarModel {
    static final ModelFactory FACTORY = (object, wrapper) -> {
        return new StringModel(object, (BeansWrapper) wrapper);
    };
    static final String TO_STRING_NOT_EXPOSED = "[toString not exposed]";

    public StringModel(Object object, BeansWrapper wrapper) {
        super(object, wrapper);
    }

    @Override // freemarker.template.TemplateScalarModel
    public String getAsString() {
        boolean exposeToString = this.wrapper.getMemberAccessPolicy().isToStringAlwaysExposed() || !this.wrapper.getClassIntrospector().get(this.object.getClass()).containsKey(ClassIntrospector.TO_STRING_HIDDEN_FLAG_KEY);
        return exposeToString ? this.object.toString() : TO_STRING_NOT_EXPOSED;
    }
}
