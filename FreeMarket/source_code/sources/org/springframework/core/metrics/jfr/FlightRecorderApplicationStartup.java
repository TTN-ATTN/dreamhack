package org.springframework.core.metrics.jfr;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/metrics/jfr/FlightRecorderApplicationStartup.class */
public class FlightRecorderApplicationStartup implements ApplicationStartup {
    private final AtomicLong currentSequenceId = new AtomicLong(0);
    private final Deque<Long> currentSteps = new ConcurrentLinkedDeque();

    public FlightRecorderApplicationStartup() {
        this.currentSteps.offerFirst(Long.valueOf(this.currentSequenceId.get()));
    }

    @Override // org.springframework.core.metrics.ApplicationStartup
    public StartupStep start(String name) {
        long sequenceId = this.currentSequenceId.incrementAndGet();
        this.currentSteps.offerFirst(Long.valueOf(sequenceId));
        return new FlightRecorderStartupStep(sequenceId, name, this.currentSteps.getFirst().longValue(), committedStep -> {
            this.currentSteps.removeFirstOccurrence(Long.valueOf(sequenceId));
        });
    }
}
