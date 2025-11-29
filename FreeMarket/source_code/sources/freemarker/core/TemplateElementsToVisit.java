package freemarker.core;

import java.util.Collection;
import java.util.Collections;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/core/TemplateElementsToVisit.class */
class TemplateElementsToVisit {
    private final Collection<TemplateElement> templateElements;

    TemplateElementsToVisit(Collection<TemplateElement> templateElements) {
        this.templateElements = null != templateElements ? templateElements : Collections.emptyList();
    }

    TemplateElementsToVisit(TemplateElement nestedBlock) {
        this(Collections.singleton(nestedBlock));
    }

    Collection<TemplateElement> getTemplateElements() {
        return this.templateElements;
    }
}
