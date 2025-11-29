package org.springframework.boot;

import java.security.AccessControlException;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationShutdownHook.class */
class SpringApplicationShutdownHook implements Runnable {
    private static final int SLEEP = 50;
    private static final long TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    private static final Log logger = LogFactory.getLog((Class<?>) SpringApplicationShutdownHook.class);
    private final Handlers handlers = new Handlers();
    private final Set<ConfigurableApplicationContext> contexts = new LinkedHashSet();
    private final Set<ConfigurableApplicationContext> closedContexts = Collections.newSetFromMap(new WeakHashMap());
    private final ApplicationContextClosedListener contextCloseListener = new ApplicationContextClosedListener();
    private final AtomicBoolean shutdownHookAdded = new AtomicBoolean();
    private boolean inProgress;

    SpringApplicationShutdownHook() {
    }

    SpringApplicationShutdownHandlers getHandlers() {
        return this.handlers;
    }

    void registerApplicationContext(ConfigurableApplicationContext context) {
        addRuntimeShutdownHookIfNecessary();
        synchronized (SpringApplicationShutdownHook.class) {
            assertNotInProgress();
            context.addApplicationListener(this.contextCloseListener);
            this.contexts.add(context);
        }
    }

    private void addRuntimeShutdownHookIfNecessary() {
        if (this.shutdownHookAdded.compareAndSet(false, true)) {
            addRuntimeShutdownHook();
        }
    }

    void addRuntimeShutdownHook() {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(this, "SpringApplicationShutdownHook"));
        } catch (AccessControlException e) {
        }
    }

    void deregisterFailedApplicationContext(ConfigurableApplicationContext applicationContext) {
        synchronized (SpringApplicationShutdownHook.class) {
            Assert.state(!applicationContext.isActive(), "Cannot unregister active application context");
            this.contexts.remove(applicationContext);
        }
    }

    @Override // java.lang.Runnable
    public void run() {
        Set<ConfigurableApplicationContext> contexts;
        Set<ConfigurableApplicationContext> closedContexts;
        Set<Runnable> actions;
        synchronized (SpringApplicationShutdownHook.class) {
            this.inProgress = true;
            contexts = new LinkedHashSet<>(this.contexts);
            closedContexts = new LinkedHashSet<>(this.closedContexts);
            actions = new LinkedHashSet<>(this.handlers.getActions());
        }
        contexts.forEach(this::closeAndWait);
        closedContexts.forEach(this::closeAndWait);
        actions.forEach((v0) -> {
            v0.run();
        });
    }

    boolean isApplicationContextRegistered(ConfigurableApplicationContext context) {
        boolean zContains;
        synchronized (SpringApplicationShutdownHook.class) {
            zContains = this.contexts.contains(context);
        }
        return zContains;
    }

    void reset() {
        synchronized (SpringApplicationShutdownHook.class) {
            this.contexts.clear();
            this.closedContexts.clear();
            this.handlers.getActions().clear();
            this.inProgress = false;
        }
    }

    private void closeAndWait(ConfigurableApplicationContext context) throws InterruptedException, TimeoutException {
        if (!context.isActive()) {
            return;
        }
        context.close();
        int waited = 0;
        while (context.isActive()) {
            try {
                if (waited > TIMEOUT) {
                    throw new TimeoutException();
                }
                Thread.sleep(50L);
                waited += 50;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Interrupted waiting for application context " + context + " to become inactive");
                return;
            } catch (TimeoutException ex) {
                logger.warn("Timed out waiting for application context " + context + " to become inactive", ex);
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assertNotInProgress() {
        Assert.state(!this.inProgress, "Shutdown in progress");
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationShutdownHook$Handlers.class */
    private class Handlers implements SpringApplicationShutdownHandlers {
        private final Set<Runnable> actions;

        private Handlers() {
            this.actions = Collections.newSetFromMap(new IdentityHashMap());
        }

        @Override // org.springframework.boot.SpringApplicationShutdownHandlers
        public void add(Runnable action) {
            Assert.notNull(action, "Action must not be null");
            synchronized (SpringApplicationShutdownHook.class) {
                SpringApplicationShutdownHook.this.assertNotInProgress();
                this.actions.add(action);
            }
        }

        @Override // org.springframework.boot.SpringApplicationShutdownHandlers
        public void remove(Runnable action) {
            Assert.notNull(action, "Action must not be null");
            synchronized (SpringApplicationShutdownHook.class) {
                SpringApplicationShutdownHook.this.assertNotInProgress();
                this.actions.remove(action);
            }
        }

        Set<Runnable> getActions() {
            return this.actions;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/SpringApplicationShutdownHook$ApplicationContextClosedListener.class */
    private class ApplicationContextClosedListener implements ApplicationListener<ContextClosedEvent> {
        private ApplicationContextClosedListener() {
        }

        @Override // org.springframework.context.ApplicationListener
        public void onApplicationEvent(ContextClosedEvent event) {
            synchronized (SpringApplicationShutdownHook.class) {
                ApplicationContext applicationContext = event.getApplicationContext();
                SpringApplicationShutdownHook.this.contexts.remove(applicationContext);
                SpringApplicationShutdownHook.this.closedContexts.add((ConfigurableApplicationContext) applicationContext);
            }
        }
    }
}
