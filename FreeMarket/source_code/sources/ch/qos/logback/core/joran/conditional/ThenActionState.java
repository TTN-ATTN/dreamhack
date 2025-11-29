package ch.qos.logback.core.joran.conditional;

import ch.qos.logback.core.joran.event.InPlayListener;
import ch.qos.logback.core.joran.event.SaxEvent;
import java.util.ArrayList;
import java.util.List;

/* compiled from: ThenOrElseActionBase.java */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/joran/conditional/ThenActionState.class */
class ThenActionState implements InPlayListener {
    List<SaxEvent> eventList = new ArrayList();
    boolean isRegistered = false;

    ThenActionState() {
    }

    @Override // ch.qos.logback.core.joran.event.InPlayListener
    public void inPlay(SaxEvent event) {
        this.eventList.add(event);
    }
}
