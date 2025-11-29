package org.springframework.boot.autoconfigure.batch;

import org.springframework.batch.core.JobExecution;
import org.springframework.context.ApplicationEvent;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/batch/JobExecutionEvent.class */
public class JobExecutionEvent extends ApplicationEvent {
    private final JobExecution execution;

    public JobExecutionEvent(JobExecution execution) {
        super(execution);
        this.execution = execution;
    }

    public JobExecution getJobExecution() {
        return this.execution;
    }
}
