package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/snakeyaml-1.30.jar:org/yaml/snakeyaml/parser/Parser.class */
public interface Parser {
    boolean checkEvent(Event.ID id);

    Event peekEvent();

    Event getEvent();
}
