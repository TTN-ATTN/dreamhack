package org.springframework.boot.loader.jar;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/jar/JarEntryFilter.class */
interface JarEntryFilter {
    AsciiBytes apply(AsciiBytes name);
}
