package freemarker.ext.beans;

import freemarker.template.TemplateBooleanModel;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/BooleanModel.class */
public class BooleanModel extends BeanModel implements TemplateBooleanModel {
    private final boolean value;

    public BooleanModel(Boolean bool, BeansWrapper wrapper) {
        super(bool, wrapper, false);
        this.value = bool.booleanValue();
    }

    @Override // freemarker.template.TemplateBooleanModel
    public boolean getAsBoolean() {
        return this.value;
    }
}
