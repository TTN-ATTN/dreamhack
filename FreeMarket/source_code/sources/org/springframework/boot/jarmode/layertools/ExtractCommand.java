package org.springframework.boot.jarmode.layertools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.boot.jarmode.layertools.Command;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-2.7.12.jar:org/springframework/boot/jarmode/layertools/ExtractCommand.class */
class ExtractCommand extends Command {
    static final Command.Option DESTINATION_OPTION = Command.Option.of("destination", "string", "The destination to extract files to");
    private final Context context;
    private final Layers layers;

    ExtractCommand(Context context) {
        this(context, Layers.get(context));
    }

    ExtractCommand(Context context, Layers layers) {
        super("extract", "Extracts layers from the jar for image creation", Command.Options.of(DESTINATION_OPTION), Command.Parameters.of("[<layer>...]"));
        this.context = context;
        this.layers = layers;
    }

    @Override // org.springframework.boot.jarmode.layertools.Command
    protected void run(Map<Command.Option, String> options, List<String> parameters) throws IOException {
        try {
            File destination = options.containsKey(DESTINATION_OPTION) ? new File(options.get(DESTINATION_OPTION)) : this.context.getWorkingDir();
            for (String layer : this.layers) {
                if (parameters.isEmpty() || parameters.contains(layer)) {
                    mkDirs(new File(destination, layer));
                }
            }
            ZipInputStream zip = new ZipInputStream(new FileInputStream(this.context.getArchiveFile()));
            Throwable th = null;
            try {
                try {
                    ZipEntry entry = zip.getNextEntry();
                    Assert.state(entry != null, "File '" + this.context.getArchiveFile().toString() + "' is not compatible with layertools; ensure jar file is valid and launch script is not enabled");
                    while (entry != null) {
                        if (!entry.isDirectory()) {
                            String layer2 = this.layers.getLayer(entry);
                            if (parameters.isEmpty() || parameters.contains(layer2)) {
                                write(zip, entry, new File(destination, layer2));
                            }
                        }
                        entry = zip.getNextEntry();
                    }
                    if (zip != null) {
                        if (0 != 0) {
                            try {
                                zip.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                        } else {
                            zip.close();
                        }
                    }
                } finally {
                }
            } finally {
            }
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void write(ZipInputStream zip, ZipEntry entry, File destination) throws IOException {
        String canonicalOutputPath = destination.getCanonicalPath() + File.separator;
        File file = new File(destination, entry.getName());
        String canonicalEntryPath = file.getCanonicalPath();
        Assert.state(canonicalEntryPath.startsWith(canonicalOutputPath), (Supplier<String>) () -> {
            return "Entry '" + entry.getName() + "' would be written to '" + canonicalEntryPath + "'. This is outside the output location of '" + canonicalOutputPath + "'. Verify the contents of your archive.";
        });
        mkParentDirs(file);
        OutputStream out = new FileOutputStream(file);
        Throwable th = null;
        try {
            try {
                StreamUtils.copy(zip, out);
                if (out != null) {
                    if (0 != 0) {
                        try {
                            out.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        out.close();
                    }
                }
                try {
                    ((BasicFileAttributeView) Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class, new LinkOption[0])).setTimes(entry.getLastModifiedTime(), entry.getLastAccessTime(), entry.getCreationTime());
                } catch (IOException e) {
                }
            } catch (Throwable th3) {
                th = th3;
                throw th3;
            }
        } catch (Throwable th4) {
            if (out != null) {
                if (th != null) {
                    try {
                        out.close();
                    } catch (Throwable th5) {
                        th.addSuppressed(th5);
                    }
                } else {
                    out.close();
                }
            }
            throw th4;
        }
    }

    private void mkParentDirs(File file) throws IOException {
        mkDirs(file.getParentFile());
    }

    private void mkDirs(File file) throws IOException {
        if (!file.exists() && !file.mkdirs()) {
            throw new IOException("Unable to create directory " + file);
        }
    }
}
