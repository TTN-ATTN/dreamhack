package org.springframework.web.jsf;

import java.util.Collection;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-web-5.3.27.jar:org/springframework/web/jsf/DelegatingPhaseListenerMulticaster.class */
public class DelegatingPhaseListenerMulticaster implements PhaseListener {
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent event) {
        for (PhaseListener listener : getDelegates(event.getFacesContext())) {
            listener.beforePhase(event);
        }
    }

    public void afterPhase(PhaseEvent event) {
        for (PhaseListener listener : getDelegates(event.getFacesContext())) {
            listener.afterPhase(event);
        }
    }

    protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
        ListableBeanFactory bf = getBeanFactory(facesContext);
        return BeanFactoryUtils.beansOfTypeIncludingAncestors(bf, PhaseListener.class, true, false).values();
    }

    protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
        return getWebApplicationContext(facesContext);
    }

    protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}
