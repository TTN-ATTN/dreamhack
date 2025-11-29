package org.springframework.scheduling.quartz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Calendar;
import org.quartz.JobDetail;
import org.quartz.JobListener;
import org.quartz.ListenerManager;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;
import org.quartz.TriggerListener;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.xml.XMLSchedulingDataProcessor;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-support-5.3.27.jar:org/springframework/scheduling/quartz/SchedulerAccessor.class */
public abstract class SchedulerAccessor implements ResourceLoaderAware {
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean overwriteExistingJobs = false;

    @Nullable
    private String[] jobSchedulingDataLocations;

    @Nullable
    private List<JobDetail> jobDetails;

    @Nullable
    private Map<String, Calendar> calendars;

    @Nullable
    private List<Trigger> triggers;

    @Nullable
    private SchedulerListener[] schedulerListeners;

    @Nullable
    private JobListener[] globalJobListeners;

    @Nullable
    private TriggerListener[] globalTriggerListeners;

    @Nullable
    private PlatformTransactionManager transactionManager;

    @Nullable
    protected ResourceLoader resourceLoader;

    protected abstract Scheduler getScheduler();

    public void setOverwriteExistingJobs(boolean overwriteExistingJobs) {
        this.overwriteExistingJobs = overwriteExistingJobs;
    }

    public void setJobSchedulingDataLocation(String jobSchedulingDataLocation) {
        this.jobSchedulingDataLocations = new String[]{jobSchedulingDataLocation};
    }

    public void setJobSchedulingDataLocations(String... jobSchedulingDataLocations) {
        this.jobSchedulingDataLocations = jobSchedulingDataLocations;
    }

    public void setJobDetails(JobDetail... jobDetails) {
        this.jobDetails = new ArrayList(Arrays.asList(jobDetails));
    }

    public void setCalendars(Map<String, Calendar> calendars) {
        this.calendars = calendars;
    }

    public void setTriggers(Trigger... triggers) {
        this.triggers = Arrays.asList(triggers);
    }

    public void setSchedulerListeners(SchedulerListener... schedulerListeners) {
        this.schedulerListeners = schedulerListeners;
    }

    public void setGlobalJobListeners(JobListener... globalJobListeners) {
        this.globalJobListeners = globalJobListeners;
    }

    public void setGlobalTriggerListeners(TriggerListener... globalTriggerListeners) {
        this.globalTriggerListeners = globalTriggerListeners;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override // org.springframework.context.ResourceLoaderAware
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /* JADX INFO: Thrown type has an unknown type hierarchy: org.quartz.SchedulerException */
    /* JADX INFO: Thrown type has an unknown type hierarchy: org.springframework.transaction.TransactionException */
    protected void registerJobsAndTriggers() throws SchedulerException, TransactionException {
        TransactionStatus transactionStatus = null;
        if (this.transactionManager != null) {
            transactionStatus = this.transactionManager.getTransaction(TransactionDefinition.withDefaults());
        }
        try {
            if (this.jobSchedulingDataLocations != null) {
                ClassLoadHelper clh = new ResourceLoaderClassLoadHelper(this.resourceLoader);
                clh.initialize();
                XMLSchedulingDataProcessor dataProcessor = new XMLSchedulingDataProcessor(clh);
                for (String location : this.jobSchedulingDataLocations) {
                    dataProcessor.processFileAndScheduleJobs(location, getScheduler());
                }
            }
            if (this.jobDetails != null) {
                for (JobDetail jobDetail : this.jobDetails) {
                    addJobToScheduler(jobDetail);
                }
            } else {
                this.jobDetails = new ArrayList();
            }
            if (this.calendars != null) {
                for (String calendarName : this.calendars.keySet()) {
                    Calendar calendar = this.calendars.get(calendarName);
                    getScheduler().addCalendar(calendarName, calendar, true, true);
                }
            }
            if (this.triggers != null) {
                for (Trigger trigger : this.triggers) {
                    addTriggerToScheduler(trigger);
                }
            }
            if (transactionStatus != null) {
                this.transactionManager.commit(transactionStatus);
            }
        } catch (Throwable th) {
            if (transactionStatus != null) {
                try {
                    this.transactionManager.rollback(transactionStatus);
                } catch (TransactionException tex) {
                    this.logger.error("Job registration exception overridden by rollback exception", th);
                    throw tex;
                }
            }
            if (th instanceof SchedulerException) {
                throw th;
            }
            if (th instanceof Exception) {
                throw new SchedulerException("Registration of jobs and triggers failed: " + th.getMessage(), th);
            }
            throw new SchedulerException("Registration of jobs and triggers failed: " + th.getMessage());
        }
    }

    private boolean addJobToScheduler(JobDetail jobDetail) throws SchedulerException {
        if (this.overwriteExistingJobs || getScheduler().getJobDetail(jobDetail.getKey()) == null) {
            getScheduler().addJob(jobDetail, true);
            return true;
        }
        return false;
    }

    /* JADX WARN: Removed duplicated region for block: B:40:0x00f8 A[Catch: ObjectAlreadyExistsException -> 0x0106, TryCatch #1 {ObjectAlreadyExistsException -> 0x0106, blocks: (B:31:0x00b1, B:33:0x00b8, B:35:0x00c5, B:37:0x00cc, B:39:0x00de, B:40:0x00f8), top: B:53:0x00b1 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean addTriggerToScheduler(org.quartz.Trigger r5) throws org.quartz.SchedulerException {
        /*
            Method dump skipped, instructions count: 339
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.scheduling.quartz.SchedulerAccessor.addTriggerToScheduler(org.quartz.Trigger):boolean");
    }

    protected void registerListeners() throws SchedulerException {
        ListenerManager listenerManager = getScheduler().getListenerManager();
        if (this.schedulerListeners != null) {
            for (SchedulerListener listener : this.schedulerListeners) {
                listenerManager.addSchedulerListener(listener);
            }
        }
        if (this.globalJobListeners != null) {
            for (JobListener listener2 : this.globalJobListeners) {
                listenerManager.addJobListener(listener2);
            }
        }
        if (this.globalTriggerListeners != null) {
            for (TriggerListener listener3 : this.globalTriggerListeners) {
                listenerManager.addTriggerListener(listener3);
            }
        }
    }
}
