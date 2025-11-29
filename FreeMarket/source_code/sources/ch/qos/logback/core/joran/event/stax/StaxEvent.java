package ch.qos.logback.core.joran.event.stax;

import javax.xml.stream.Location;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/joran/event/stax/StaxEvent.class */
public class StaxEvent {
    final String name;
    final Location location;

    StaxEvent(String name, Location location) {
        this.name = name;
        this.location = location;
    }

    public String getName() {
        return this.name;
    }

    public Location getLocation() {
        return this.location;
    }
}
