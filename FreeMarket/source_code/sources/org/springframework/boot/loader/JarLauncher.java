package org.springframework.boot.loader;

import org.springframework.boot.loader.archive.Archive;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/JarLauncher.class */
public class JarLauncher extends ExecutableArchiveLauncher {
    static final Archive.EntryFilter NESTED_ARCHIVE_ENTRY_FILTER = entry -> {
        if (entry.isDirectory()) {
            return entry.getName().equals("BOOT-INF/classes/");
        }
        return entry.getName().startsWith("BOOT-INF/lib/");
    };

    public JarLauncher() {
    }

    protected JarLauncher(Archive archive) {
        super(archive);
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected boolean isPostProcessingClassPathArchives() {
        return false;
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected boolean isNestedArchive(Archive.Entry entry) {
        return NESTED_ARCHIVE_ENTRY_FILTER.matches(entry);
    }

    @Override // org.springframework.boot.loader.ExecutableArchiveLauncher
    protected String getArchiveEntryPathPrefix() {
        return "BOOT-INF/";
    }

    public static void main(String[] args) throws Exception {
        new JarLauncher().launch(args);
    }
}
