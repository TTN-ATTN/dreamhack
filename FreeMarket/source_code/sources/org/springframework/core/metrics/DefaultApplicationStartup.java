package org.springframework.core.metrics;

import java.util.Collections;
import java.util.Iterator;
import java.util.function.Supplier;
import org.springframework.core.metrics.StartupStep;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/metrics/DefaultApplicationStartup.class */
class DefaultApplicationStartup implements ApplicationStartup {
    private static final DefaultStartupStep DEFAULT_STARTUP_STEP = new DefaultStartupStep();

    DefaultApplicationStartup() {
    }

    @Override // org.springframework.core.metrics.ApplicationStartup
    public DefaultStartupStep start(String name) {
        return DEFAULT_STARTUP_STEP;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/metrics/DefaultApplicationStartup$DefaultStartupStep.class */
    static class DefaultStartupStep implements StartupStep {
        private final DefaultTags TAGS = new DefaultTags();

        DefaultStartupStep() {
        }

        @Override // org.springframework.core.metrics.StartupStep
        public String getName() {
            return "default";
        }

        @Override // org.springframework.core.metrics.StartupStep
        public long getId() {
            return 0L;
        }

        @Override // org.springframework.core.metrics.StartupStep
        public Long getParentId() {
            return null;
        }

        @Override // org.springframework.core.metrics.StartupStep
        public StartupStep.Tags getTags() {
            return this.TAGS;
        }

        @Override // org.springframework.core.metrics.StartupStep
        public StartupStep tag(String key, String value) {
            return this;
        }

        @Override // org.springframework.core.metrics.StartupStep
        public StartupStep tag(String key, Supplier<String> value) {
            return this;
        }

        @Override // org.springframework.core.metrics.StartupStep
        public void end() {
        }

        /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/metrics/DefaultApplicationStartup$DefaultStartupStep$DefaultTags.class */
        static class DefaultTags implements StartupStep.Tags {
            DefaultTags() {
            }

            @Override // java.lang.Iterable
            public Iterator<StartupStep.Tag> iterator() {
                return Collections.emptyIterator();
            }
        }
    }
}
