package ch.qos.logback.core.rolling.helper;

import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.WarnStatus;
import ch.qos.logback.core.util.FileUtil;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/rolling/helper/Compressor.class */
public class Compressor extends ContextAwareBase {
    final CompressionMode compressionMode;
    static final int BUFFER_SIZE = 8192;

    public Compressor(CompressionMode compressionMode) {
        this.compressionMode = compressionMode;
    }

    public void compress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws IOException {
        switch (this.compressionMode) {
            case GZ:
                gzCompress(nameOfFile2Compress, nameOfCompressedFile);
                return;
            case ZIP:
                zipCompress(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
                return;
            case NONE:
                throw new UnsupportedOperationException("compress method called in NONE compression mode");
            default:
                return;
        }
    }

    private void zipCompress(String nameOfFile2zip, String nameOfZippedFile, String innerEntryName) throws IOException {
        File file2zip = new File(nameOfFile2zip);
        if (!file2zip.exists()) {
            addStatus(new WarnStatus("The file to compress named [" + nameOfFile2zip + "] does not exist.", this));
            return;
        }
        if (innerEntryName == null) {
            addStatus(new WarnStatus("The innerEntryName parameter cannot be null", this));
            return;
        }
        if (!nameOfZippedFile.endsWith(".zip")) {
            nameOfZippedFile = nameOfZippedFile + ".zip";
        }
        File zippedFile = new File(nameOfZippedFile);
        if (zippedFile.exists()) {
            addStatus(new WarnStatus("The target compressed file named [" + nameOfZippedFile + "] exist already.", this));
            return;
        }
        addInfo("ZIP compressing [" + file2zip + "] as [" + zippedFile + "]");
        createMissingTargetDirsIfNecessary(zippedFile);
        BufferedInputStream bis = null;
        ZipOutputStream zos = null;
        try {
            try {
                BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(nameOfFile2zip));
                ZipOutputStream zos2 = new ZipOutputStream(new FileOutputStream(nameOfZippedFile));
                ZipEntry zipEntry = computeZipEntry(innerEntryName);
                zos2.putNextEntry(zipEntry);
                byte[] inbuf = new byte[8192];
                while (true) {
                    int n = bis2.read(inbuf);
                    if (n == -1) {
                        break;
                    } else {
                        zos2.write(inbuf, 0, n);
                    }
                }
                bis2.close();
                bis = null;
                zos2.close();
                zos = null;
                if (!file2zip.delete()) {
                    addStatus(new WarnStatus("Could not delete [" + nameOfFile2zip + "].", this));
                }
                if (0 != 0) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                }
                if (0 != 0) {
                    try {
                        zos.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Exception e3) {
                addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2zip + "] into [" + nameOfZippedFile + "].", this, e3));
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e4) {
                    }
                }
                if (zos != null) {
                    try {
                        zos.close();
                    } catch (IOException e5) {
                    }
                }
            }
        } catch (Throwable th) {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e6) {
                }
            }
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e7) {
                }
            }
            throw th;
        }
    }

    ZipEntry computeZipEntry(File zippedFile) {
        return computeZipEntry(zippedFile.getName());
    }

    ZipEntry computeZipEntry(String filename) {
        String nameOfFileNestedWithinArchive = computeFileNameStrWithoutCompSuffix(filename, this.compressionMode);
        return new ZipEntry(nameOfFileNestedWithinArchive);
    }

    private void gzCompress(String nameOfFile2gz, String nameOfgzedFile) throws IOException {
        File file2gz = new File(nameOfFile2gz);
        if (!file2gz.exists()) {
            addStatus(new WarnStatus("The file to compress named [" + nameOfFile2gz + "] does not exist.", this));
            return;
        }
        if (!nameOfgzedFile.endsWith(".gz")) {
            nameOfgzedFile = nameOfgzedFile + ".gz";
        }
        File gzedFile = new File(nameOfgzedFile);
        if (gzedFile.exists()) {
            addWarn("The target compressed file named [" + nameOfgzedFile + "] exist already. Aborting file compression.");
            return;
        }
        addInfo("GZ compressing [" + file2gz + "] as [" + gzedFile + "]");
        createMissingTargetDirsIfNecessary(gzedFile);
        BufferedInputStream bis = null;
        GZIPOutputStream gzos = null;
        try {
            try {
                BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(nameOfFile2gz));
                GZIPOutputStream gzos2 = new GZIPOutputStream(new FileOutputStream(nameOfgzedFile));
                byte[] inbuf = new byte[8192];
                while (true) {
                    int n = bis2.read(inbuf);
                    if (n == -1) {
                        break;
                    } else {
                        gzos2.write(inbuf, 0, n);
                    }
                }
                bis2.close();
                bis = null;
                gzos2.close();
                gzos = null;
                if (!file2gz.delete()) {
                    addStatus(new WarnStatus("Could not delete [" + nameOfFile2gz + "].", this));
                }
                if (0 != 0) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                    }
                }
                if (0 != 0) {
                    try {
                        gzos.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (Throwable th) {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e3) {
                    }
                }
                if (gzos != null) {
                    try {
                        gzos.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
        } catch (Exception e5) {
            addStatus(new ErrorStatus("Error occurred while compressing [" + nameOfFile2gz + "] into [" + nameOfgzedFile + "].", this, e5));
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e6) {
                }
            }
            if (gzos != null) {
                try {
                    gzos.close();
                } catch (IOException e7) {
                }
            }
        }
    }

    public static String computeFileNameStrWithoutCompSuffix(String fileNamePatternStr, CompressionMode compressionMode) {
        int len = fileNamePatternStr.length();
        switch (compressionMode) {
            case GZ:
                if (fileNamePatternStr.endsWith(".gz")) {
                    return fileNamePatternStr.substring(0, len - 3);
                }
                return fileNamePatternStr;
            case ZIP:
                if (fileNamePatternStr.endsWith(".zip")) {
                    return fileNamePatternStr.substring(0, len - 4);
                }
                return fileNamePatternStr;
            case NONE:
                return fileNamePatternStr;
            default:
                throw new IllegalStateException("Execution should not reach this point");
        }
    }

    void createMissingTargetDirsIfNecessary(File file) {
        boolean result = FileUtil.createMissingParentDirectories(file);
        if (!result) {
            addError("Failed to create parent directories for [" + file.getAbsolutePath() + "]");
        }
    }

    public String toString() {
        return getClass().getName();
    }

    public Future<?> asyncCompress(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) throws RolloverFailure {
        Runnable runnable = new CompressionRunnable(nameOfFile2Compress, nameOfCompressedFile, innerEntryName);
        ExecutorService executorService = this.context.getScheduledExecutorService();
        Future<?> future = executorService.submit(runnable);
        return future;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/logback-core-1.2.12.jar:ch/qos/logback/core/rolling/helper/Compressor$CompressionRunnable.class */
    class CompressionRunnable implements Runnable {
        final String nameOfFile2Compress;
        final String nameOfCompressedFile;
        final String innerEntryName;

        public CompressionRunnable(String nameOfFile2Compress, String nameOfCompressedFile, String innerEntryName) {
            this.nameOfFile2Compress = nameOfFile2Compress;
            this.nameOfCompressedFile = nameOfCompressedFile;
            this.innerEntryName = innerEntryName;
        }

        @Override // java.lang.Runnable
        public void run() throws IOException {
            Compressor.this.compress(this.nameOfFile2Compress, this.nameOfCompressedFile, this.innerEntryName);
        }
    }
}
