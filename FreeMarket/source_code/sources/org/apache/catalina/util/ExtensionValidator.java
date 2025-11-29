package org.apache.catalina.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.catalina.Context;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-core-9.0.75.jar:org/apache/catalina/util/ExtensionValidator.class */
public final class ExtensionValidator {
    private static final Log log = LogFactory.getLog((Class<?>) ExtensionValidator.class);
    private static final StringManager sm = StringManager.getManager("org.apache.catalina.util");
    private static volatile List<Extension> containerAvailableExtensions = null;
    private static final List<ManifestResource> containerManifestResources = new ArrayList();

    static {
        String systemClasspath = System.getProperty("java.class.path");
        StringTokenizer strTok = new StringTokenizer(systemClasspath, File.pathSeparator);
        while (strTok.hasMoreTokens()) {
            String classpathItem = strTok.nextToken();
            if (classpathItem.toLowerCase(Locale.ENGLISH).endsWith(".jar")) {
                File item = new File(classpathItem);
                if (item.isFile()) {
                    try {
                        addSystemResource(item);
                    } catch (IOException e) {
                        log.error(sm.getString("extensionValidator.failload", item), e);
                    }
                }
            }
        }
        addFolderList("java.ext.dirs");
    }

    public static synchronized boolean validateApplication(WebResourceRoot resources, Context context) throws IOException {
        String appName = context.getName();
        List<ManifestResource> appManifestResources = new ArrayList<>();
        WebResource resource = resources.getResource("/META-INF/MANIFEST.MF");
        if (resource.isFile()) {
            InputStream inputStream = resource.getInputStream();
            try {
                Manifest manifest = new Manifest(inputStream);
                ManifestResource mre = new ManifestResource(sm.getString("extensionValidator.web-application-manifest"), manifest, 2);
                appManifestResources.add(mre);
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (Throwable th) {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        WebResource[] manifestResources = resources.getClassLoaderResources("/META-INF/MANIFEST.MF");
        for (WebResource manifestResource : manifestResources) {
            if (manifestResource.isFile()) {
                String jarName = manifestResource.getURL().toExternalForm();
                Manifest jmanifest = manifestResource.getManifest();
                if (jmanifest != null) {
                    ManifestResource mre2 = new ManifestResource(jarName, jmanifest, 3);
                    appManifestResources.add(mre2);
                }
            }
        }
        return validateManifestResources(appName, appManifestResources);
    }

    public static void addSystemResource(File jarFile) throws IOException {
        InputStream is = new FileInputStream(jarFile);
        try {
            Manifest manifest = getManifest(is);
            if (manifest != null) {
                ManifestResource mre = new ManifestResource(jarFile.getAbsolutePath(), manifest, 1);
                containerManifestResources.add(mre);
            }
            is.close();
        } catch (Throwable th) {
            try {
                is.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static boolean validateManifestResources(String appName, List<ManifestResource> resources) {
        boolean passes = true;
        int failureCount = 0;
        List<Extension> availableExtensions = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> requiredList = mre.getRequiredExtensions();
            if (requiredList != null) {
                if (availableExtensions == null) {
                    availableExtensions = buildAvailableExtensionsList(resources);
                }
                if (containerAvailableExtensions == null) {
                    containerAvailableExtensions = buildAvailableExtensionsList(containerManifestResources);
                }
                Iterator<Extension> it = requiredList.iterator();
                while (it.hasNext()) {
                    Extension requiredExt = it.next();
                    boolean found = false;
                    if (availableExtensions != null) {
                        Iterator<Extension> it2 = availableExtensions.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            Extension targetExt = it2.next();
                            if (targetExt.isCompatibleWith(requiredExt)) {
                                requiredExt.setFulfilled(true);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found && containerAvailableExtensions != null) {
                        Iterator<Extension> it3 = containerAvailableExtensions.iterator();
                        while (true) {
                            if (!it3.hasNext()) {
                                break;
                            }
                            Extension targetExt2 = it3.next();
                            if (targetExt2.isCompatibleWith(requiredExt)) {
                                requiredExt.setFulfilled(true);
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        log.info(sm.getString("extensionValidator.extension-not-found-error", appName, mre.getResourceName(), requiredExt.getExtensionName()));
                        passes = false;
                        failureCount++;
                    }
                }
            }
        }
        if (!passes) {
            log.info(sm.getString("extensionValidator.extension-validation-error", appName, failureCount + ""));
        }
        return passes;
    }

    private static List<Extension> buildAvailableExtensionsList(List<ManifestResource> resources) {
        List<Extension> availableList = null;
        for (ManifestResource mre : resources) {
            ArrayList<Extension> list = mre.getAvailableExtensions();
            if (list != null) {
                Iterator<Extension> it = list.iterator();
                while (it.hasNext()) {
                    Extension ext = it.next();
                    if (availableList == null) {
                        availableList = new ArrayList<>();
                        availableList.add(ext);
                    } else {
                        availableList.add(ext);
                    }
                }
            }
        }
        return availableList;
    }

    private static Manifest getManifest(InputStream inStream) throws IOException {
        JarInputStream jin = new JarInputStream(inStream);
        try {
            Manifest manifest = jin.getManifest();
            jin.close();
            return manifest;
        } catch (Throwable th) {
            try {
                jin.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private static void addFolderList(String property) {
        File[] files;
        String extensionsDir = System.getProperty(property);
        if (extensionsDir != null) {
            StringTokenizer extensionsTok = new StringTokenizer(extensionsDir, File.pathSeparator);
            while (extensionsTok.hasMoreTokens()) {
                File targetDir = new File(extensionsTok.nextToken());
                if (targetDir.isDirectory() && (files = targetDir.listFiles()) != null) {
                    for (File file : files) {
                        if (file.getName().toLowerCase(Locale.ENGLISH).endsWith(".jar") && file.isFile()) {
                            try {
                                addSystemResource(file);
                            } catch (IOException e) {
                                log.error(sm.getString("extensionValidator.failload", file), e);
                            }
                        }
                    }
                }
            }
        }
    }
}
