package freemarker.ext.beans;

import freemarker.ext.util.ModelFactory;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateModel;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/DateModel.class */
public class DateModel extends BeanModel implements TemplateDateModel {
    static final ModelFactory FACTORY = new ModelFactory() { // from class: freemarker.ext.beans.DateModel.1
        @Override // freemarker.ext.util.ModelFactory
        public TemplateModel create(Object object, ObjectWrapper wrapper) {
            return new DateModel((Date) object, (BeansWrapper) wrapper);
        }
    };
    private final int type;

    public DateModel(Date date, BeansWrapper wrapper) {
        super(date, wrapper);
        if (date instanceof java.sql.Date) {
            this.type = 2;
            return;
        }
        if (date instanceof Time) {
            this.type = 1;
        } else if (date instanceof Timestamp) {
            this.type = 3;
        } else {
            this.type = wrapper.getDefaultDateType();
        }
    }

    @Override // freemarker.template.TemplateDateModel
    public Date getAsDate() {
        return (Date) this.object;
    }

    @Override // freemarker.template.TemplateDateModel
    public int getDateType() {
        return this.type;
    }
}
