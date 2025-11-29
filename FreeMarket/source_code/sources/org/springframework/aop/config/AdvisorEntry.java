package org.springframework.aop.config;

import org.springframework.beans.factory.parsing.ParseState;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-aop-5.3.27.jar:org/springframework/aop/config/AdvisorEntry.class */
public class AdvisorEntry implements ParseState.Entry {
    private final String name;

    public AdvisorEntry(String name) {
        this.name = name;
    }

    public String toString() {
        return "Advisor '" + this.name + "'";
    }
}
