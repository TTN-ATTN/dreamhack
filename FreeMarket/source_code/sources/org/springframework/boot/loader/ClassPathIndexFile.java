package org.springframework.boot.loader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/ClassPathIndexFile.class */
final class ClassPathIndexFile {
    private final File root;
    private final List<String> lines;

    private ClassPathIndexFile(File root, List<String> lines) {
        this.root = root;
        this.lines = (List) lines.stream().map(this::extractName).collect(Collectors.toList());
    }

    private String extractName(String line) {
        if (line.startsWith("- \"") && line.endsWith("\"")) {
            return line.substring(3, line.length() - 1);
        }
        throw new IllegalStateException("Malformed classpath index line [" + line + "]");
    }

    int size() {
        return this.lines.size();
    }

    boolean containsEntry(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        return this.lines.contains(name);
    }

    List<URL> getUrls() {
        return Collections.unmodifiableList((List) this.lines.stream().map(this::asUrl).collect(Collectors.toList()));
    }

    private URL asUrl(String line) {
        try {
            return new File(this.root, line).toURI().toURL();
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(ex);
        }
    }

    static ClassPathIndexFile loadIfPossible(URL root, String location) throws IOException {
        return loadIfPossible(asFile(root), location);
    }

    private static ClassPathIndexFile loadIfPossible(File root, String location) throws IOException {
        return loadIfPossible(root, new File(root, location));
    }

    private static ClassPathIndexFile loadIfPossible(File root, File indexFile) throws IOException {
        if (indexFile.exists() && indexFile.isFile()) {
            InputStream inputStream = new FileInputStream(indexFile);
            Throwable th = null;
            try {
                ClassPathIndexFile classPathIndexFile = new ClassPathIndexFile(root, loadLines(inputStream));
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                return classPathIndexFile;
            } catch (Throwable th3) {
                if (inputStream != null) {
                    if (0 != 0) {
                        try {
                            inputStream.close();
                        } catch (Throwable th4) {
                            th.addSuppressed(th4);
                        }
                    } else {
                        inputStream.close();
                    }
                }
                throw th3;
            }
        }
        return null;
    }

    private static List<String> loadLines(InputStream inputStream) throws IOException {
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = reader.readLine();
        while (true) {
            String line2 = line;
            if (line2 != null) {
                if (!line2.trim().isEmpty()) {
                    lines.add(line2);
                }
                line = reader.readLine();
            } else {
                return Collections.unmodifiableList(lines);
            }
        }
    }

    private static File asFile(URL url) {
        if (!"file".equals(url.getProtocol())) {
            throw new IllegalArgumentException("URL does not reference a file");
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            return new File(url.getPath());
        }
    }
}
