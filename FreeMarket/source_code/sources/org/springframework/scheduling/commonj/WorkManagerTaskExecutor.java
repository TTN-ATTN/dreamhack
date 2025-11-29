package org.springframework.scheduling.commonj;

import commonj.work.Work;
import commonj.work.WorkException;
import commonj.work.WorkItem;
import commonj.work.WorkListener;
import commonj.work.WorkManager;
import commonj.work.WorkRejectedException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import javax.naming.NamingException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.TaskDecorator;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.jndi.JndiLocatorSupport;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.SchedulingTaskExecutor;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureTask;

@Deprecated
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/commonj/WorkManagerTaskExecutor.class */
public class WorkManagerTaskExecutor extends JndiLocatorSupport implements AsyncListenableTaskExecutor, SchedulingTaskExecutor, WorkManager, InitializingBean {

    @Nullable
    private WorkManager workManager;

    @Nullable
    private String workManagerName;

    @Nullable
    private WorkListener workListener;

    @Nullable
    private TaskDecorator taskDecorator;

    public void setWorkManager(WorkManager workManager) {
        this.workManager = workManager;
    }

    public void setWorkManagerName(String workManagerName) {
        this.workManagerName = workManagerName;
    }

    public void setWorkListener(WorkListener workListener) {
        this.workListener = workListener;
    }

    public void setTaskDecorator(TaskDecorator taskDecorator) {
        this.taskDecorator = taskDecorator;
    }

    @Override // org.springframework.beans.factory.InitializingBean
    public void afterPropertiesSet() throws NamingException {
        if (this.workManager == null) {
            if (this.workManagerName == null) {
                throw new IllegalArgumentException("Either 'workManager' or 'workManagerName' must be specified");
            }
            this.workManager = (WorkManager) lookup(this.workManagerName, WorkManager.class);
        }
    }

    private WorkManager obtainWorkManager() {
        Assert.state(this.workManager != null, "No WorkManager specified");
        return this.workManager;
    }

    @Override // org.springframework.core.task.TaskExecutor, java.util.concurrent.Executor
    public void execute(Runnable task) {
        Work work = new DelegatingWork(this.taskDecorator != null ? this.taskDecorator.decorate(task) : task);
        try {
            if (this.workListener != null) {
                obtainWorkManager().schedule(work, this.workListener);
            } else {
                obtainWorkManager().schedule(work);
            }
        } catch (WorkRejectedException ex) {
            throw new TaskRejectedException("CommonJ WorkManager did not accept task: " + task, ex);
        } catch (WorkException ex2) {
            throw new SchedulingException("Could not schedule task on CommonJ WorkManager", ex2);
        }
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    @Deprecated
    public void execute(Runnable task, long startTimeout) {
        execute(task);
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public Future<?> submit(Runnable task) {
        FutureTask<Object> future = new FutureTask<>(task, null);
        execute(future);
        return future;
    }

    @Override // org.springframework.core.task.AsyncTaskExecutor
    public <T> Future<T> submit(Callable<T> task) {
        FutureTask<T> future = new FutureTask<>(task);
        execute(future);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public ListenableFuture<?> submitListenable(Runnable task) {
        ListenableFutureTask<Object> future = new ListenableFutureTask<>(task, null);
        execute(future);
        return future;
    }

    @Override // org.springframework.core.task.AsyncListenableTaskExecutor
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        ListenableFutureTask<T> future = new ListenableFutureTask<>(task);
        execute(future);
        return future;
    }

    public WorkItem schedule(Work work) throws IllegalArgumentException, WorkException {
        return obtainWorkManager().schedule(work);
    }

    public WorkItem schedule(Work work, WorkListener workListener) throws WorkException {
        return obtainWorkManager().schedule(work, workListener);
    }

    public boolean waitForAll(Collection workItems, long timeout) throws InterruptedException {
        return obtainWorkManager().waitForAll(workItems, timeout);
    }

    public Collection waitForAny(Collection workItems, long timeout) throws InterruptedException {
        return obtainWorkManager().waitForAny(workItems, timeout);
    }
}
