package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/joran/event/stax/BodyEvent.class */
public class BodyEvent extends StaxEvent {
    private String text;

    BodyEvent(String text, Location location) {
        super(null, location);
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    void append(String txt) {
        this.text += txt;
    }

    public String toString() {
        return "BodyEvent(" + getText() + ")" + this.location.getLineNumber() + "," + this.location.getColumnNumber();
    }
}
