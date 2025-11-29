package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Policy;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.ObjectName;
import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.DistributedManager;
import org.apache.catalina.Globals;
import org.apache.catalina.Host;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.Manager;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.security.DeployXmlPermission;
import org.apache.catalina.util.ContextName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.ExceptionUtils;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.modeler.Registry;
import org.apache.tomcat.util.res.StringManager;
import org.springframework.web.context.support.XmlWebApplicationContext;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig.class */
public class HostConfig implements LifecycleListener {
    private static final Log log = LogFactory.getLog((Class<?>) HostConfig.class);
    protected static final StringManager sm = StringManager.getManager((Class<?>) HostConfig.class);
    protected static final long FILE_MODIFICATION_RESOLUTION_MS = 1000;
    protected String contextClass = "org.apache.catalina.core.StandardContext";
    protected Host host = null;
    protected ObjectName oname = null;
    protected boolean deployXML = false;
    protected boolean copyXML = false;
    protected boolean unpackWARs = false;
    protected final Map<String, DeployedApplication> deployed = new ConcurrentHashMap();

    @Deprecated
    protected final ArrayList<String> serviced = new ArrayList<>();
    private Set<String> servicedSet = ConcurrentHashMap.newKeySet();
    protected Digester digester = createDigester(this.contextClass);
    private final Object digesterLock = new Object();
    protected final Set<String> invalidWars = new HashSet();

    public String getContextClass() {
        return this.contextClass;
    }

    public void setContextClass(String contextClass) {
        String oldContextClass = this.contextClass;
        this.contextClass = contextClass;
        if (!oldContextClass.equals(contextClass)) {
            synchronized (this.digesterLock) {
                this.digester = createDigester(getContextClass());
            }
        }
    }

    public boolean isDeployXML() {
        return this.deployXML;
    }

    public void setDeployXML(boolean deployXML) {
        this.deployXML = deployXML;
    }

    private boolean isDeployThisXML(File docBase, ContextName cn) throws MalformedURLException {
        Policy currentPolicy;
        boolean deployThisXML = isDeployXML();
        if (Globals.IS_SECURITY_ENABLED && !deployThisXML && (currentPolicy = Policy.getPolicy()) != null) {
            try {
                URL contextRootUrl = docBase.toURI().toURL();
                CodeSource cs = new CodeSource(contextRootUrl, (Certificate[]) null);
                PermissionCollection pc = currentPolicy.getPermissions(cs);
                Permission p = new DeployXmlPermission(cn.getBaseName());
                if (pc.implies(p)) {
                    deployThisXML = true;
                }
            } catch (MalformedURLException e) {
                log.warn(sm.getString("hostConfig.docBaseUrlInvalid"), e);
            }
        }
        return deployThisXML;
    }

    public boolean isCopyXML() {
        return this.copyXML;
    }

    public void setCopyXML(boolean copyXML) {
        this.copyXML = copyXML;
    }

    public boolean isUnpackWARs() {
        return this.unpackWARs;
    }

    public void setUnpackWARs(boolean unpackWARs) {
        this.unpackWARs = unpackWARs;
    }

    @Override // org.apache.catalina.LifecycleListener
    public void lifecycleEvent(LifecycleEvent event) throws ExecutionException, InterruptedException {
        try {
            this.host = (Host) event.getLifecycle();
            if (this.host instanceof StandardHost) {
                setCopyXML(((StandardHost) this.host).isCopyXML());
                setDeployXML(((StandardHost) this.host).isDeployXML());
                setUnpackWARs(((StandardHost) this.host).isUnpackWARs());
                setContextClass(((StandardHost) this.host).getContextClass());
            }
            if (event.getType().equals(Lifecycle.PERIODIC_EVENT)) {
                check();
                return;
            }
            if (event.getType().equals(Lifecycle.BEFORE_START_EVENT)) {
                beforeStart();
            } else if (event.getType().equals(Lifecycle.START_EVENT)) {
                start();
            } else if (event.getType().equals(Lifecycle.STOP_EVENT)) {
                stop();
            }
        } catch (ClassCastException e) {
            log.error(sm.getString("hostConfig.cce", event.getLifecycle()), e);
        }
    }

    public boolean tryAddServiced(String name) {
        if (this.servicedSet.add(name)) {
            synchronized (this) {
                this.serviced.add(name);
            }
            return true;
        }
        return false;
    }

    @Deprecated
    public void addServiced(String name) {
        this.servicedSet.add(name);
        synchronized (this) {
            this.serviced.add(name);
        }
    }

    @Deprecated
    public boolean isServiced(String name) {
        return this.servicedSet.contains(name);
    }

    public void removeServiced(String name) {
        this.servicedSet.remove(name);
        synchronized (this) {
            this.serviced.remove(name);
        }
    }

    public long getDeploymentTime(String name) {
        DeployedApplication app = this.deployed.get(name);
        if (app == null) {
            return 0L;
        }
        return app.timestamp;
    }

    public boolean isDeployed(String name) {
        return this.deployed.containsKey(name);
    }

    protected static Digester createDigester(String contextClassName) {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.addObjectCreate("Context", contextClassName, "className");
        digester.addSetProperties("Context");
        return digester;
    }

    protected File returnCanonicalPath(String path) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(this.host.getCatalinaBase(), path);
        }
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            return file;
        }
    }

    public String getConfigBaseName() {
        return this.host.getConfigBaseFile().getAbsolutePath();
    }

    protected void deployApps() throws ExecutionException, InterruptedException {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        String[] filteredAppPaths = filterAppPaths(appBase.list());
        deployDescriptors(configBase, configBase.list());
        deployWARs(appBase, filteredAppPaths);
        deployDirectories(appBase, filteredAppPaths);
    }

    protected String[] filterAppPaths(String[] unfilteredAppPaths) {
        Pattern filter = this.host.getDeployIgnorePattern();
        if (filter == null || unfilteredAppPaths == null) {
            return unfilteredAppPaths;
        }
        List<String> filteredList = new ArrayList<>();
        Matcher matcher = null;
        for (String appPath : unfilteredAppPaths) {
            if (matcher == null) {
                matcher = filter.matcher(appPath);
            } else {
                matcher.reset(appPath);
            }
            if (matcher.matches()) {
                if (log.isDebugEnabled()) {
                    log.debug(sm.getString("hostConfig.ignorePath", appPath));
                }
            } else {
                filteredList.add(appPath);
            }
        }
        return (String[]) filteredList.toArray(new String[0]);
    }

    protected void deployApps(String name) throws MalformedURLException {
        File appBase = this.host.getAppBaseFile();
        File configBase = this.host.getConfigBaseFile();
        ContextName cn = new ContextName(name, false);
        String baseName = cn.getBaseName();
        if (deploymentExists(cn.getName())) {
            return;
        }
        File xml = new File(configBase, baseName + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX);
        if (xml.exists()) {
            deployDescriptor(cn, xml);
            return;
        }
        File war = new File(appBase, baseName + ".war");
        if (war.exists()) {
            deployWAR(cn, war);
            return;
        }
        File dir = new File(appBase, baseName);
        if (dir.exists()) {
            deployDirectory(cn, dir);
        }
    }

    protected void deployDescriptors(File configBase, String[] files) throws ExecutionException, InterruptedException {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            File contextXml = new File(configBase, file);
            if (file.toLowerCase(Locale.ENGLISH).endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                ContextName cn = new ContextName(file, true);
                if (tryAddServiced(cn.getName())) {
                    try {
                        if (deploymentExists(cn.getName())) {
                            removeServiced(cn.getName());
                        } else {
                            results.add(es.submit(new DeployDescriptor(this, cn, contextXml)));
                        }
                    } catch (Throwable t) {
                        ExceptionUtils.handleThrowable(t);
                        removeServiced(cn.getName());
                        throw t;
                    }
                } else {
                    continue;
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDescriptor.threaded.error"), e);
            }
        }
    }

    protected void deployDescriptor(ContextName cn, File contextXml) {
        DeployedApplication deployedApp = new DeployedApplication(cn.getName(), true);
        long startTime = 0;
        if (log.isInfoEnabled()) {
            startTime = System.currentTimeMillis();
            log.info(sm.getString("hostConfig.deployDescriptor", contextXml.getAbsolutePath()));
        }
        Context context = null;
        boolean isExternalWar = false;
        boolean isExternal = false;
        try {
            try {
                FileInputStream fis = new FileInputStream(contextXml);
                try {
                    synchronized (this.digesterLock) {
                        try {
                            try {
                                context = (Context) this.digester.parse(fis);
                                this.digester.reset();
                                if (context == null) {
                                    context = new FailedContext();
                                }
                            } catch (Exception e) {
                                log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), e);
                                this.digester.reset();
                                if (context == null) {
                                    context = new FailedContext();
                                }
                            }
                        } catch (Throwable th) {
                            this.digester.reset();
                            if (context == null) {
                                new FailedContext();
                            }
                            throw th;
                        }
                    }
                    if (context.getPath() != null) {
                        log.warn(sm.getString("hostConfig.deployDescriptor.path", context.getPath(), contextXml.getAbsolutePath()));
                    }
                    Class<?> clazz = Class.forName(this.host.getConfigClass());
                    LifecycleListener listener = (LifecycleListener) clazz.getConstructor(new Class[0]).newInstance(new Object[0]);
                    context.addLifecycleListener(listener);
                    context.setConfigFile(contextXml.toURI().toURL());
                    context.setName(cn.getName());
                    context.setPath(cn.getPath());
                    context.setWebappVersion(cn.getVersion());
                    if (context.getDocBase() != null) {
                        File docBase = new File(context.getDocBase());
                        if (!docBase.isAbsolute()) {
                            docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                        }
                        if (docBase.getCanonicalFile().toPath().startsWith(this.host.getAppBaseFile().toPath())) {
                            log.warn(sm.getString("hostConfig.deployDescriptor.localDocBaseSpecified", docBase));
                            context.setDocBase(null);
                        } else {
                            isExternal = true;
                            deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                            deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
                            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                                isExternalWar = true;
                            }
                            File war = new File(this.host.getAppBaseFile(), cn.getBaseName() + ".war");
                            if (war.exists()) {
                                log.warn(sm.getString("hostConfig.deployDescriptor.hiddenWar", contextXml.getAbsolutePath(), war.getAbsolutePath()));
                            }
                            File dir = new File(this.host.getAppBaseFile(), cn.getBaseName());
                            if (dir.exists()) {
                                log.warn(sm.getString("hostConfig.deployDescriptor.hiddenDir", contextXml.getAbsolutePath(), dir.getAbsolutePath()));
                            }
                        }
                    }
                    this.host.addChild(context);
                    fis.close();
                    File expandedDocBase = new File(this.host.getAppBaseFile(), cn.getBaseName());
                    if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        expandedDocBase = new File(context.getDocBase());
                        if (!expandedDocBase.isAbsolute()) {
                            expandedDocBase = new File(this.host.getAppBaseFile(), context.getDocBase());
                        }
                    }
                    boolean unpackWAR = this.unpackWARs;
                    if (unpackWAR && (context instanceof StandardContext)) {
                        unpackWAR = ((StandardContext) context).getUnpackWAR();
                    }
                    if (!isExternalWar) {
                        if (!isExternal) {
                            File warDocBase = new File(expandedDocBase.getAbsolutePath() + ".war");
                            if (warDocBase.exists()) {
                                deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), Long.valueOf(warDocBase.lastModified()));
                            } else {
                                deployedApp.redeployResources.put(warDocBase.getAbsolutePath(), 0L);
                            }
                        }
                        if (unpackWAR) {
                            deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                            addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                        } else {
                            addWatchedResources(deployedApp, null, context);
                        }
                        if (!isExternal) {
                            deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                        }
                    } else if (unpackWAR) {
                        deployedApp.redeployResources.put(expandedDocBase.getAbsolutePath(), Long.valueOf(expandedDocBase.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase.getAbsolutePath(), context);
                    } else {
                        addWatchedResources(deployedApp, null, context);
                    }
                    addGlobalRedeployResources(deployedApp);
                } catch (Throwable th2) {
                    try {
                        fis.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable t) {
                ExceptionUtils.handleThrowable(t);
                log.error(sm.getString("hostConfig.deployDescriptor.error", contextXml.getAbsolutePath()), t);
                File expandedDocBase2 = new File(this.host.getAppBaseFile(), cn.getBaseName());
                if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                    expandedDocBase2 = new File(context.getDocBase());
                    if (!expandedDocBase2.isAbsolute()) {
                        expandedDocBase2 = new File(this.host.getAppBaseFile(), context.getDocBase());
                    }
                }
                boolean unpackWAR2 = this.unpackWARs;
                if (unpackWAR2 && (context instanceof StandardContext)) {
                    unpackWAR2 = ((StandardContext) null).getUnpackWAR();
                }
                if (0 == 0) {
                    if (0 == 0) {
                        File warDocBase2 = new File(expandedDocBase2.getAbsolutePath() + ".war");
                        if (warDocBase2.exists()) {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), Long.valueOf(warDocBase2.lastModified()));
                        } else {
                            deployedApp.redeployResources.put(warDocBase2.getAbsolutePath(), 0L);
                        }
                    }
                    if (unpackWAR2) {
                        deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                        addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), null);
                    } else {
                        addWatchedResources(deployedApp, null, null);
                    }
                    if (0 == 0) {
                        deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                    }
                } else if (unpackWAR2) {
                    deployedApp.redeployResources.put(expandedDocBase2.getAbsolutePath(), Long.valueOf(expandedDocBase2.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase2.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                addGlobalRedeployResources(deployedApp);
            }
            if (this.host.findChild(context.getName()) != null) {
                this.deployed.put(context.getName(), deployedApp);
            }
            if (log.isInfoEnabled()) {
                log.info(sm.getString("hostConfig.deployDescriptor.finished", contextXml.getAbsolutePath(), Long.valueOf(System.currentTimeMillis() - startTime)));
            }
        } catch (Throwable th4) {
            File expandedDocBase3 = new File(this.host.getAppBaseFile(), cn.getBaseName());
            if (context.getDocBase() != null && !context.getDocBase().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                expandedDocBase3 = new File(context.getDocBase());
                if (!expandedDocBase3.isAbsolute()) {
                    expandedDocBase3 = new File(this.host.getAppBaseFile(), context.getDocBase());
                }
            }
            boolean unpackWAR3 = this.unpackWARs;
            if (unpackWAR3 && (context instanceof StandardContext)) {
                unpackWAR3 = ((StandardContext) null).getUnpackWAR();
            }
            if (0 == 0) {
                if (0 == 0) {
                    File warDocBase3 = new File(expandedDocBase3.getAbsolutePath() + ".war");
                    if (warDocBase3.exists()) {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), Long.valueOf(warDocBase3.lastModified()));
                    } else {
                        deployedApp.redeployResources.put(warDocBase3.getAbsolutePath(), 0L);
                    }
                }
                if (unpackWAR3) {
                    deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                    addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), null);
                } else {
                    addWatchedResources(deployedApp, null, null);
                }
                if (0 == 0) {
                    deployedApp.redeployResources.put(contextXml.getAbsolutePath(), Long.valueOf(contextXml.lastModified()));
                }
            } else if (unpackWAR3) {
                deployedApp.redeployResources.put(expandedDocBase3.getAbsolutePath(), Long.valueOf(expandedDocBase3.lastModified()));
                addWatchedResources(deployedApp, expandedDocBase3.getAbsolutePath(), null);
            } else {
                addWatchedResources(deployedApp, null, null);
            }
            addGlobalRedeployResources(deployedApp);
            throw th4;
        }
    }

    protected void deployWARs(File appBase, String[] files) throws ExecutionException, InterruptedException {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            if (!file.equalsIgnoreCase("META-INF") && !file.equalsIgnoreCase("WEB-INF")) {
                File war = new File(appBase, file);
                if (file.toLowerCase(Locale.ENGLISH).endsWith(".war") && war.isFile() && !this.invalidWars.contains(file)) {
                    ContextName cn = new ContextName(file, true);
                    if (tryAddServiced(cn.getName())) {
                        try {
                            if (deploymentExists(cn.getName())) {
                                DeployedApplication app = this.deployed.get(cn.getName());
                                boolean unpackWAR = this.unpackWARs;
                                if (unpackWAR && (this.host.findChild(cn.getName()) instanceof StandardContext)) {
                                    unpackWAR = ((StandardContext) this.host.findChild(cn.getName())).getUnpackWAR();
                                }
                                if (!unpackWAR && app != null) {
                                    File dir = new File(appBase, cn.getBaseName());
                                    if (dir.exists()) {
                                        if (!app.loggedDirWarning) {
                                            log.warn(sm.getString("hostConfig.deployWar.hiddenDir", dir.getAbsoluteFile(), war.getAbsoluteFile()));
                                            app.loggedDirWarning = true;
                                        }
                                    } else {
                                        app.loggedDirWarning = false;
                                    }
                                }
                                removeServiced(cn.getName());
                            } else if (!validateContextPath(appBase, cn.getBaseName())) {
                                log.error(sm.getString("hostConfig.illegalWarName", file));
                                this.invalidWars.add(file);
                                removeServiced(cn.getName());
                            } else {
                                results.add(es.submit(new DeployWar(this, cn, war)));
                            }
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            removeServiced(cn.getName());
                            throw t;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployWar.threaded.error"), e);
            }
        }
    }

    private boolean validateContextPath(File appBase, String contextPath) throws IOException {
        try {
            String canonicalAppBase = appBase.getCanonicalPath();
            StringBuilder docBase = new StringBuilder(canonicalAppBase);
            if (canonicalAppBase.endsWith(File.separator)) {
                docBase.append(contextPath.substring(1).replace('/', File.separatorChar));
            } else {
                docBase.append(contextPath.replace('/', File.separatorChar));
            }
            String canonicalDocBase = new File(docBase.toString()).getCanonicalPath();
            if (canonicalDocBase.endsWith(File.separator)) {
                docBase.append(File.separator);
            }
            return canonicalDocBase.equals(docBase.toString());
        } catch (IOException e) {
            return false;
        }
    }

    /* JADX WARN: Finally extract failed */
    /* JADX WARN: Removed duplicated region for block: B:122:0x030f  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x0177  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void deployWAR(org.apache.catalina.util.ContextName r13, java.io.File r14) throws java.net.MalformedURLException {
        /*
            Method dump skipped, instructions count: 2028
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployWAR(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected void deployDirectories(File appBase, String[] files) throws ExecutionException, InterruptedException {
        if (files == null) {
            return;
        }
        ExecutorService es = this.host.getStartStopExecutor();
        List<Future<?>> results = new ArrayList<>();
        for (String file : files) {
            if (!file.equalsIgnoreCase("META-INF") && !file.equalsIgnoreCase("WEB-INF")) {
                File dir = new File(appBase, file);
                if (dir.isDirectory()) {
                    ContextName cn = new ContextName(file, false);
                    if (tryAddServiced(cn.getName())) {
                        try {
                            if (deploymentExists(cn.getName())) {
                                removeServiced(cn.getName());
                            } else {
                                results.add(es.submit(new DeployDirectory(this, cn, dir)));
                            }
                        } catch (Throwable t) {
                            ExceptionUtils.handleThrowable(t);
                            removeServiced(cn.getName());
                            throw t;
                        }
                    } else {
                        continue;
                    }
                } else {
                    continue;
                }
            }
        }
        for (Future<?> result : results) {
            try {
                result.get();
            } catch (Exception e) {
                log.error(sm.getString("hostConfig.deployDir.threaded.error"), e);
            }
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0164  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void deployDirectory(org.apache.catalina.util.ContextName r12, java.io.File r13) throws java.net.MalformedURLException {
        /*
            Method dump skipped, instructions count: 1373
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.deployDirectory(org.apache.catalina.util.ContextName, java.io.File):void");
    }

    protected boolean deploymentExists(String contextName) {
        return this.deployed.containsKey(contextName) || this.host.findChild(contextName) != null;
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x00b2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void addWatchedResources(org.apache.catalina.startup.HostConfig.DeployedApplication r6, java.lang.String r7, org.apache.catalina.Context r8) {
        /*
            r5 = this;
            r0 = 0
            r9 = r0
            r0 = r7
            if (r0 == 0) goto L2c
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r7
            r1.<init>(r2)
            r9 = r0
            r0 = r9
            boolean r0 = r0.isAbsolute()
            if (r0 != 0) goto L2c
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r5
            org.apache.catalina.Host r2 = r2.host
            java.io.File r2 = r2.getAppBaseFile()
            r3 = r7
            r1.<init>(r2, r3)
            r9 = r0
        L2c:
            r0 = r8
            java.lang.String[] r0 = r0.findWatchedResources()
            r10 = r0
            r0 = r10
            r11 = r0
            r0 = r11
            int r0 = r0.length
            r12 = r0
            r0 = 0
            r13 = r0
        L40:
            r0 = r13
            r1 = r12
            if (r0 >= r1) goto Lf1
            r0 = r11
            r1 = r13
            r0 = r0[r1]
            r14 = r0
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r14
            r1.<init>(r2)
            r15 = r0
            r0 = r15
            boolean r0 = r0.isAbsolute()
            if (r0 != 0) goto La7
            r0 = r7
            if (r0 == 0) goto L75
            java.io.File r0 = new java.io.File
            r1 = r0
            r2 = r9
            r3 = r14
            r1.<init>(r2, r3)
            r15 = r0
            goto La7
        L75:
            org.apache.juli.logging.Log r0 = org.apache.catalina.startup.HostConfig.log
            boolean r0 = r0.isDebugEnabled()
            if (r0 == 0) goto Leb
            org.apache.juli.logging.Log r0 = org.apache.catalina.startup.HostConfig.log
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Ignoring non-existent WatchedResource '"
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r15
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "'"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.debug(r1)
            goto Leb
        La7:
            org.apache.juli.logging.Log r0 = org.apache.catalina.startup.HostConfig.log
            boolean r0 = r0.isDebugEnabled()
            if (r0 == 0) goto Ld6
            org.apache.juli.logging.Log r0 = org.apache.catalina.startup.HostConfig.log
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r2 = r1
            r2.<init>()
            java.lang.String r2 = "Watching WatchedResource '"
            java.lang.StringBuilder r1 = r1.append(r2)
            r2 = r15
            java.lang.String r2 = r2.getAbsolutePath()
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r2 = "'"
            java.lang.StringBuilder r1 = r1.append(r2)
            java.lang.String r1 = r1.toString()
            r0.debug(r1)
        Ld6:
            r0 = r6
            java.util.HashMap<java.lang.String, java.lang.Long> r0 = r0.reloadResources
            r1 = r15
            java.lang.String r1 = r1.getAbsolutePath()
            r2 = r15
            long r2 = r2.lastModified()
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            java.lang.Object r0 = r0.put(r1, r2)
        Leb:
            int r13 = r13 + 1
            goto L40
        Lf1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.apache.catalina.startup.HostConfig.addWatchedResources(org.apache.catalina.startup.HostConfig$DeployedApplication, java.lang.String, org.apache.catalina.Context):void");
    }

    protected void addGlobalRedeployResources(DeployedApplication app) {
        File hostContextXml = new File(getConfigBaseName(), Constants.HostContextXml);
        if (hostContextXml.isFile()) {
            app.redeployResources.put(hostContextXml.getAbsolutePath(), Long.valueOf(hostContextXml.lastModified()));
        }
        File globalContextXml = returnCanonicalPath(Constants.DefaultContextXml);
        if (globalContextXml.isFile()) {
            app.redeployResources.put(globalContextXml.getAbsolutePath(), Long.valueOf(globalContextXml.lastModified()));
        }
    }

    protected synchronized void checkResources(DeployedApplication app, boolean skipFileModificationResolutionCheck) throws InterruptedException {
        String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
        long currentTimeWithResolutionOffset = System.currentTimeMillis() - 1000;
        for (int i = 0; i < resources.length; i++) {
            File resource = new File(resources[i]);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] redeploy resource " + resource);
            }
            long lastModified = app.redeployResources.get(resources[i]).longValue();
            if (!resource.exists() && lastModified != 0) {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                }
                if (!resource.exists()) {
                    undeploy(app);
                    deleteRedeployResources(app, resources, i, true);
                    return;
                }
            } else if (resource.lastModified() != lastModified && (!this.host.getAutoDeploy() || resource.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) {
                if (resource.isDirectory()) {
                    app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                } else {
                    if (app.hasDescriptor && resource.getName().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                        Context context = (Context) this.host.findChild(app.name);
                        String docBase = context.getDocBase();
                        if (!docBase.toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                            File docBaseFile = new File(docBase);
                            if (!docBaseFile.isAbsolute()) {
                                docBaseFile = new File(this.host.getAppBaseFile(), docBase);
                            }
                            reload(app, docBaseFile, resource.getAbsolutePath());
                        } else {
                            reload(app, null, null);
                        }
                        app.redeployResources.put(resources[i], Long.valueOf(resource.lastModified()));
                        app.timestamp = System.currentTimeMillis();
                        boolean unpackWAR = this.unpackWARs;
                        if (unpackWAR && (context instanceof StandardContext)) {
                            unpackWAR = ((StandardContext) context).getUnpackWAR();
                        }
                        if (unpackWAR) {
                            addWatchedResources(app, context.getDocBase(), context);
                            return;
                        } else {
                            addWatchedResources(app, null, context);
                            return;
                        }
                    }
                    undeploy(app);
                    deleteRedeployResources(app, resources, i, false);
                    return;
                }
            }
        }
        boolean update = false;
        for (String s : (String[]) app.reloadResources.keySet().toArray(new String[0])) {
            File resource2 = new File(s);
            if (log.isDebugEnabled()) {
                log.debug("Checking context[" + app.name + "] reload resource " + resource2);
            }
            if ((resource2.lastModified() != app.reloadResources.get(s).longValue() && (!this.host.getAutoDeploy() || resource2.lastModified() < currentTimeWithResolutionOffset || skipFileModificationResolutionCheck)) || update) {
                if (!update) {
                    reload(app, null, null);
                    update = true;
                }
                app.reloadResources.put(s, Long.valueOf(resource2.lastModified()));
            }
            app.timestamp = System.currentTimeMillis();
        }
    }

    private void reload(DeployedApplication app, File fileToRemove, String newDocBase) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.reload", app.name));
        }
        Context context = (Context) this.host.findChild(app.name);
        if (context.getState().isAvailable()) {
            if (fileToRemove != null && newDocBase != null) {
                context.addLifecycleListener(new ExpandedDirectoryRemovalListener(fileToRemove, newDocBase));
            }
            context.reload();
            return;
        }
        if (fileToRemove != null && newDocBase != null) {
            ExpandWar.delete(fileToRemove);
            context.setDocBase(newDocBase);
        }
        try {
            context.start();
        } catch (Exception e) {
            log.error(sm.getString("hostConfig.context.restart", app.name), e);
        }
    }

    private void undeploy(DeployedApplication app) {
        if (log.isInfoEnabled()) {
            log.info(sm.getString("hostConfig.undeploy", app.name));
        }
        Container context = this.host.findChild(app.name);
        try {
            this.host.removeChild(context);
        } catch (Throwable t) {
            ExceptionUtils.handleThrowable(t);
            log.warn(sm.getString("hostConfig.context.remove", app.name), t);
        }
        this.deployed.remove(app.name);
    }

    private void deleteRedeployResources(DeployedApplication app, String[] resources, int i, boolean deleteReloadResources) {
        for (int j = i + 1; j < resources.length; j++) {
            File current = new File(resources[j]);
            if (!Constants.HostContextXml.equals(current.getName()) && isDeletableResource(app, current)) {
                if (log.isDebugEnabled()) {
                    log.debug("Delete " + current);
                }
                ExpandWar.delete(current);
            }
        }
        if (deleteReloadResources) {
            String[] resources2 = (String[]) app.reloadResources.keySet().toArray(new String[0]);
            for (String s : resources2) {
                File current2 = new File(s);
                if (!Constants.HostContextXml.equals(current2.getName()) && isDeletableResource(app, current2)) {
                    if (log.isDebugEnabled()) {
                        log.debug("Delete " + current2);
                    }
                    ExpandWar.delete(current2);
                }
            }
        }
    }

    private boolean isDeletableResource(DeployedApplication app, File resource) throws IOException {
        if (!resource.isAbsolute()) {
            log.warn(sm.getString("hostConfig.resourceNotAbsolute", app.name, resource));
            return false;
        }
        try {
            String canonicalLocation = resource.getParentFile().getCanonicalPath();
            try {
                String canonicalAppBase = this.host.getAppBaseFile().getCanonicalPath();
                if (canonicalLocation.equals(canonicalAppBase)) {
                    return true;
                }
                try {
                    String canonicalConfigBase = this.host.getConfigBaseFile().getCanonicalPath();
                    if (canonicalLocation.equals(canonicalConfigBase) && resource.getName().endsWith(XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX)) {
                        return true;
                    }
                    return false;
                } catch (IOException e) {
                    log.warn(sm.getString("hostConfig.canonicalizing", this.host.getConfigBaseFile(), app.name), e);
                    return false;
                }
            } catch (IOException e2) {
                log.warn(sm.getString("hostConfig.canonicalizing", this.host.getAppBaseFile(), app.name), e2);
                return false;
            }
        } catch (IOException e3) {
            log.warn(sm.getString("hostConfig.canonicalizing", resource.getParentFile(), app.name), e3);
            return false;
        }
    }

    public void beforeStart() {
        if (this.host.getCreateDirs()) {
            File[] dirs = {this.host.getAppBaseFile(), this.host.getConfigBaseFile()};
            for (File dir : dirs) {
                if (!dir.mkdirs() && !dir.isDirectory()) {
                    log.error(sm.getString("hostConfig.createDirs", dir));
                }
            }
        }
    }

    public void start() throws ExecutionException, InterruptedException {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.start"));
        }
        try {
            ObjectName hostON = this.host.getObjectName();
            this.oname = new ObjectName(hostON.getDomain() + ":type=Deployer,host=" + this.host.getName());
            Registry.getRegistry(null, null).registerComponent(this, this.oname, getClass().getName());
        } catch (Exception e) {
            log.warn(sm.getString("hostConfig.jmx.register", this.oname), e);
        }
        if (!this.host.getAppBaseFile().isDirectory()) {
            log.error(sm.getString("hostConfig.appBase", this.host.getName(), this.host.getAppBaseFile().getPath()));
            this.host.setDeployOnStartup(false);
            this.host.setAutoDeploy(false);
        }
        if (this.host.getDeployOnStartup()) {
            deployApps();
        }
    }

    public void stop() {
        if (log.isDebugEnabled()) {
            log.debug(sm.getString("hostConfig.stop"));
        }
        if (this.oname != null) {
            try {
                Registry.getRegistry(null, null).unregisterComponent(this.oname);
            } catch (Exception e) {
                log.warn(sm.getString("hostConfig.jmx.unregister", this.oname), e);
            }
        }
        this.oname = null;
    }

    protected void check() throws ExecutionException, InterruptedException {
        if (this.host.getAutoDeploy()) {
            DeployedApplication[] apps = (DeployedApplication[]) this.deployed.values().toArray(new DeployedApplication[0]);
            for (DeployedApplication app : apps) {
                if (tryAddServiced(app.name)) {
                    try {
                        checkResources(app, false);
                        removeServiced(app.name);
                    } catch (Throwable th) {
                        removeServiced(app.name);
                        throw th;
                    }
                }
            }
            if (this.host.getUndeployOldVersions()) {
                checkUndeploy();
            }
            deployApps();
        }
    }

    public void check(String name) {
        if (tryAddServiced(name)) {
            try {
                DeployedApplication app = this.deployed.get(name);
                if (app != null) {
                    checkResources(app, true);
                }
                deployApps(name);
            } finally {
                removeServiced(name);
            }
        }
    }

    public synchronized void checkUndeploy() {
        int sessionCount;
        if (this.deployed.size() < 2) {
            return;
        }
        SortedSet<String> sortedAppNames = new TreeSet<>(this.deployed.keySet());
        Iterator<String> iter = sortedAppNames.iterator();
        ContextName previous = new ContextName(iter.next(), false);
        do {
            ContextName current = new ContextName(iter.next(), false);
            if (current.getPath().equals(previous.getPath())) {
                Context previousContext = (Context) this.host.findChild(previous.getName());
                Context currentContext = (Context) this.host.findChild(current.getName());
                if (previousContext != null && currentContext != null && currentContext.getState().isAvailable() && tryAddServiced(previous.getName())) {
                    try {
                        Manager manager = previousContext.getManager();
                        if (manager != null) {
                            if (manager instanceof DistributedManager) {
                                sessionCount = ((DistributedManager) manager).getActiveSessionsFull();
                            } else {
                                sessionCount = manager.getActiveSessions();
                            }
                            if (sessionCount == 0) {
                                if (log.isInfoEnabled()) {
                                    log.info(sm.getString("hostConfig.undeployVersion", previous.getName()));
                                }
                                DeployedApplication app = this.deployed.get(previous.getName());
                                String[] resources = (String[]) app.redeployResources.keySet().toArray(new String[0]);
                                undeploy(app);
                                deleteRedeployResources(app, resources, -1, true);
                            }
                        }
                    } finally {
                        removeServiced(previous.getName());
                    }
                }
            }
            previous = current;
        } while (iter.hasNext());
    }

    public void manageApp(Context context) {
        String contextName = context.getName();
        if (this.deployed.containsKey(contextName)) {
            return;
        }
        DeployedApplication deployedApp = new DeployedApplication(contextName, false);
        boolean isWar = false;
        if (context.getDocBase() != null) {
            File docBase = new File(context.getDocBase());
            if (!docBase.isAbsolute()) {
                docBase = new File(this.host.getAppBaseFile(), context.getDocBase());
            }
            deployedApp.redeployResources.put(docBase.getAbsolutePath(), Long.valueOf(docBase.lastModified()));
            if (docBase.getAbsolutePath().toLowerCase(Locale.ENGLISH).endsWith(".war")) {
                isWar = true;
            }
        }
        this.host.addChild(context);
        boolean unpackWAR = this.unpackWARs;
        if (unpackWAR && (context instanceof StandardContext)) {
            unpackWAR = ((StandardContext) context).getUnpackWAR();
        }
        if (isWar && unpackWAR) {
            File docBase2 = new File(this.host.getAppBaseFile(), context.getBaseName());
            deployedApp.redeployResources.put(docBase2.getAbsolutePath(), Long.valueOf(docBase2.lastModified()));
            addWatchedResources(deployedApp, docBase2.getAbsolutePath(), context);
        } else {
            addWatchedResources(deployedApp, null, context);
        }
        this.deployed.put(contextName, deployedApp);
    }

    public void unmanageApp(String contextName) {
        this.deployed.remove(contextName);
        this.host.removeChild(this.host.findChild(contextName));
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig$DeployedApplication.class */
    protected static class DeployedApplication {
        public final String name;
        public final boolean hasDescriptor;
        public final LinkedHashMap<String, Long> redeployResources = new LinkedHashMap<>();
        public final HashMap<String, Long> reloadResources = new HashMap<>();
        public long timestamp = System.currentTimeMillis();
        public boolean loggedDirWarning = false;

        public DeployedApplication(String name, boolean hasDescriptor) {
            this.name = name;
            this.hasDescriptor = hasDescriptor;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig$DeployDescriptor.class */
    private static class DeployDescriptor implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File descriptor;

        DeployDescriptor(HostConfig config, ContextName cn, File descriptor) {
            this.config = config;
            this.cn = cn;
            this.descriptor = descriptor;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployDescriptor(this.cn, this.descriptor);
            } finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig$DeployWar.class */
    private static class DeployWar implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File war;

        DeployWar(HostConfig config, ContextName cn, File war) {
            this.config = config;
            this.cn = cn;
            this.war = war;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployWAR(this.cn, this.war);
            } finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig$DeployDirectory.class */
    private static class DeployDirectory implements Runnable {
        private HostConfig config;
        private ContextName cn;
        private File dir;

        DeployDirectory(HostConfig config, ContextName cn, File dir) {
            this.config = config;
            this.cn = cn;
            this.dir = dir;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.config.deployDirectory(this.cn, this.dir);
            } finally {
                this.config.removeServiced(this.cn.getName());
            }
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/startup/HostConfig$ExpandedDirectoryRemovalListener.class */
    private static class ExpandedDirectoryRemovalListener implements LifecycleListener {
        private final File toDelete;
        private final String newDocBase;

        ExpandedDirectoryRemovalListener(File toDelete, String newDocBase) {
            this.toDelete = toDelete;
            this.newDocBase = newDocBase;
        }

        @Override // org.apache.catalina.LifecycleListener
        public void lifecycleEvent(LifecycleEvent event) {
            if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
                Context context = (Context) event.getLifecycle();
                ExpandWar.delete(this.toDelete);
                context.setDocBase(this.newDocBase);
                context.removeLifecycleListener(this);
            }
        }
    }
}
