package freemarker.ext.beans;

import freemarker.template.TemplateCollectionModel;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/SetAdapter.class */
class SetAdapter extends CollectionAdapter implements Set {
    SetAdapter(TemplateCollectionModel model, BeansWrapper wrapper) {
        super(model, wrapper);
    }
}
