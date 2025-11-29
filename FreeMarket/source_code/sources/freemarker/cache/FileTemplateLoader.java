package freemarker.cache;

import freemarker.log.Logger;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/FileTemplateLoader.class */
public class FileTemplateLoader implements TemplateLoader {
    public static String SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM = "org.freemarker.emulateCaseSensitiveFileSystem";
    private static final boolean EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
    private static final int CASE_CHECH_CACHE_HARD_SIZE = 50;
    private static final int CASE_CHECK_CACHE__SOFT_SIZE = 1000;
    private static final boolean SEP_IS_SLASH;
    private static final Logger LOG;
    public final File baseDir;
    private final String canonicalBasePath;
    private boolean emulateCaseSensitiveFileSystem;
    private MruCacheStorage correctCasePaths;

    static {
        boolean emuCaseSensFS;
        String s = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM, "false");
        try {
            emuCaseSensFS = StringUtil.getYesNo(s);
        } catch (Exception e) {
            emuCaseSensFS = false;
        }
        EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT = emuCaseSensFS;
        SEP_IS_SLASH = File.separatorChar == '/';
        LOG = Logger.getLogger("freemarker.cache");
    }

    @Deprecated
    public FileTemplateLoader() throws IOException {
        this(new File(SecurityUtilities.getSystemProperty("user.dir")));
    }

    public FileTemplateLoader(File baseDir) throws IOException {
        this(baseDir, false);
    }

    public FileTemplateLoader(final File baseDir, final boolean disableCanonicalPathCheck) throws IOException {
        try {
            Object[] retval = (Object[]) AccessController.doPrivileged(new PrivilegedExceptionAction<Object[]>() { // from class: freemarker.cache.FileTemplateLoader.1
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedExceptionAction
                public Object[] run() throws IOException {
                    if (!baseDir.exists()) {
                        throw new FileNotFoundException(baseDir + " does not exist.");
                    }
                    if (!baseDir.isDirectory()) {
                        throw new IOException(baseDir + " is not a directory.");
                    }
                    Object[] retval2 = new Object[2];
                    if (disableCanonicalPathCheck) {
                        retval2[0] = baseDir;
                        retval2[1] = null;
                    } else {
                        retval2[0] = baseDir.getCanonicalFile();
                        String basePath = ((File) retval2[0]).getPath();
                        if (!basePath.endsWith(File.separator)) {
                            basePath = basePath + File.separatorChar;
                        }
                        retval2[1] = basePath;
                    }
                    return retval2;
                }
            });
            this.baseDir = (File) retval[0];
            this.canonicalBasePath = (String) retval[1];
            setEmulateCaseSensitiveFileSystem(getEmulateCaseSensitiveFileSystemDefault());
        } catch (PrivilegedActionException e) {
            throw ((IOException) e.getException());
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public Object findTemplateSource(final String name) throws IOException {
        try {
            return AccessController.doPrivileged(new PrivilegedExceptionAction<File>() { // from class: freemarker.cache.FileTemplateLoader.2
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedExceptionAction
                public File run() throws IOException {
                    File source = new File(FileTemplateLoader.this.baseDir, FileTemplateLoader.SEP_IS_SLASH ? name : name.replace('/', File.separatorChar));
                    if (source.isFile()) {
                        if (FileTemplateLoader.this.canonicalBasePath != null) {
                            String normalized = source.getCanonicalPath();
                            if (!normalized.startsWith(FileTemplateLoader.this.canonicalBasePath)) {
                                throw new SecurityException(source.getAbsolutePath() + " resolves to " + normalized + " which  doesn't start with " + FileTemplateLoader.this.canonicalBasePath);
                            }
                        }
                        if (FileTemplateLoader.this.emulateCaseSensitiveFileSystem && !FileTemplateLoader.this.isNameCaseCorrect(source)) {
                            return null;
                        }
                        return source;
                    }
                    return null;
                }
            });
        } catch (PrivilegedActionException e) {
            throw ((IOException) e.getException());
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public long getLastModified(final Object templateSource) {
        return ((Long) AccessController.doPrivileged(new PrivilegedAction<Long>() { // from class: freemarker.cache.FileTemplateLoader.3
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // java.security.PrivilegedAction
            public Long run() {
                return Long.valueOf(((File) templateSource).lastModified());
            }
        })).longValue();
    }

    @Override // freemarker.cache.TemplateLoader
    public Reader getReader(final Object templateSource, final String encoding) throws IOException {
        try {
            return (Reader) AccessController.doPrivileged(new PrivilegedExceptionAction<Reader>() { // from class: freemarker.cache.FileTemplateLoader.4
                /* JADX WARN: Can't rename method to resolve collision */
                @Override // java.security.PrivilegedExceptionAction
                public Reader run() throws IOException {
                    if (!(templateSource instanceof File)) {
                        throw new IllegalArgumentException("templateSource wasn't a File, but a: " + templateSource.getClass().getName());
                    }
                    return new InputStreamReader(new FileInputStream((File) templateSource), encoding);
                }
            });
        } catch (PrivilegedActionException e) {
            throw ((IOException) e.getException());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isNameCaseCorrect(File source) throws IOException {
        String sourcePath = source.getPath();
        synchronized (this.correctCasePaths) {
            if (this.correctCasePaths.get(sourcePath) != null) {
                return true;
            }
            File parentDir = source.getParentFile();
            if (parentDir != null) {
                if (!this.baseDir.equals(parentDir) && !isNameCaseCorrect(parentDir)) {
                    return false;
                }
                String[] listing = parentDir.list();
                if (listing != null) {
                    String fileName = source.getName();
                    boolean identicalNameFound = false;
                    for (int i = 0; !identicalNameFound && i < listing.length; i++) {
                        if (fileName.equals(listing[i])) {
                            identicalNameFound = true;
                        }
                    }
                    if (!identicalNameFound) {
                        for (String listingEntry : listing) {
                            if (fileName.equalsIgnoreCase(listingEntry)) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Emulating file-not-found because of letter case differences to the real file, for: " + sourcePath);
                                    return false;
                                }
                                return false;
                            }
                        }
                    }
                }
            }
            synchronized (this.correctCasePaths) {
                this.correctCasePaths.put(sourcePath, Boolean.TRUE);
            }
            return true;
        }
    }

    @Override // freemarker.cache.TemplateLoader
    public void closeTemplateSource(Object templateSource) {
    }

    public File getBaseDirectory() {
        return this.baseDir;
    }

    public void setEmulateCaseSensitiveFileSystem(boolean nameCaseChecked) {
        if (nameCaseChecked) {
            if (this.correctCasePaths == null) {
                this.correctCasePaths = new MruCacheStorage(50, 1000);
            }
        } else {
            this.correctCasePaths = null;
        }
        this.emulateCaseSensitiveFileSystem = nameCaseChecked;
    }

    public boolean getEmulateCaseSensitiveFileSystem() {
        return this.emulateCaseSensitiveFileSystem;
    }

    protected boolean getEmulateCaseSensitiveFileSystemDefault() {
        return EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
    }

    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this) + "(baseDir=\"" + this.baseDir + "\"" + (this.canonicalBasePath != null ? ", canonicalBasePath=\"" + this.canonicalBasePath + "\"" : "") + (this.emulateCaseSensitiveFileSystem ? ", emulateCaseSensitiveFileSystem=true" : "") + ")";
    }
}
