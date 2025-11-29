package org.springframework.boot.loader.archive;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.jar.Manifest;

/* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/Archive.class */
public interface Archive extends Iterable<Entry>, AutoCloseable {

    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/Archive$Entry.class */
    public interface Entry {
        boolean isDirectory();

        String getName();
    }

    @FunctionalInterface
    /* loaded from: free-market-1.0.0.jar:org/springframework/boot/loader/archive/Archive$EntryFilter.class */
    public interface EntryFilter {
        boolean matches(Entry entry);
    }

    URL getUrl() throws MalformedURLException;

    Manifest getManifest() throws IOException;

    @Override // java.lang.Iterable
    @Deprecated
    Iterator<Entry> iterator();

    default Iterator<Archive> getNestedArchives(EntryFilter searchFilter, EntryFilter includeFilter) throws IOException {
        EntryFilter combinedFilter = entry -> {
            return (searchFilter == null || searchFilter.matches(entry)) && (includeFilter == null || includeFilter.matches(entry));
        };
        List<Archive> nestedArchives = getNestedArchives(combinedFilter);
        return nestedArchives.iterator();
    }

    @Deprecated
    default List<Archive> getNestedArchives(EntryFilter filter) throws IOException {
        throw new IllegalStateException("Unexpected call to getNestedArchives(filter)");
    }

    @Override // java.lang.Iterable
    @Deprecated
    default void forEach(Consumer<? super Entry> action) {
        Objects.requireNonNull(action);
        for (Entry entry : this) {
            action.accept(entry);
        }
    }

    @Override // java.lang.Iterable
    @Deprecated
    default Spliterator<Entry> spliterator() {
        return Spliterators.spliteratorUnknownSize(iterator(), 0);
    }

    default boolean isExploded() {
        return false;
    }

    @Override // java.lang.AutoCloseable
    default void close() throws Exception {
    }
}
