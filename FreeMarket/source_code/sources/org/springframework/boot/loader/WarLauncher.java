package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/WarLauncher.class */
public class WarLauncher extends ExecutableArchiveLauncher {
    public WarLauncher() {
    }

    protected WarLauncher(Archive archive) {
        super(archive);
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected boolean isPostProcessingClassPathArchives() {
        return false;
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    public boolean isNestedArchive(Archive.Entry entry) {
        if (entry.isDirectory()) {
            return entry.getName().equals("WEB-INF/classes/");
        }
        return entry.getName().startsWith("WEB-INF/lib/") || entry.getName().startsWith("WEB-INF/lib-provided/");
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected String getArchiveEntryPathPrefix() {
        return "WEB-INF/";
    }

    public static void main(String[] args) throws Exception {
        new WarLauncher().launch(args);
    }
}
