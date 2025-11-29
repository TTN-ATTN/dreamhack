package freemarker.core;

import freemarker.template.Template;
import freemarker.template.utility.ObjectFactory;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/DirectiveCallPlace.class */
public interface DirectiveCallPlace {
    Template getTemplate();

    int getBeginColumn();

    int getBeginLine();

    int getEndColumn();

    int getEndLine();

    Object getOrCreateCustomData(Object obj, ObjectFactory objectFactory) throws CallPlaceCustomDataInitializationException;

    boolean isNestedOutputCacheable();
}
