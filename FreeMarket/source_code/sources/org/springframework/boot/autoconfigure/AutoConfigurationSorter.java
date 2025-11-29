package org.springframework.boot.autoconfigure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/AutoConfigurationSorter.class */
class AutoConfigurationSorter {
    private final MetadataReaderFactory metadataReaderFactory;
    private final AutoConfigurationMetadata autoConfigurationMetadata;

    AutoConfigurationSorter(MetadataReaderFactory metadataReaderFactory, AutoConfigurationMetadata autoConfigurationMetadata) {
        Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
        this.metadataReaderFactory = metadataReaderFactory;
        this.autoConfigurationMetadata = autoConfigurationMetadata;
    }

    List<String> getInPriorityOrder(Collection<String> classNames) {
        AutoConfigurationClasses classes = new AutoConfigurationClasses(this.metadataReaderFactory, this.autoConfigurationMetadata, classNames);
        List<String> orderedClassNames = new ArrayList<>(classNames);
        Collections.sort(orderedClassNames);
        orderedClassNames.sort((o1, o2) -> {
            int i1 = classes.get(o1).getOrder();
            int i2 = classes.get(o2).getOrder();
            return Integer.compare(i1, i2);
        });
        return sortByAnnotation(classes, orderedClassNames);
    }

    private List<String> sortByAnnotation(AutoConfigurationClasses classes, List<String> classNames) {
        List<String> toSort = new ArrayList<>(classNames);
        toSort.addAll(classes.getAllNames());
        Set<String> sorted = new LinkedHashSet<>();
        Set<String> processing = new LinkedHashSet<>();
        while (!toSort.isEmpty()) {
            doSortByAfterAnnotation(classes, toSort, sorted, processing, null);
        }
        sorted.retainAll(classNames);
        return new ArrayList(sorted);
    }

    private void doSortByAfterAnnotation(AutoConfigurationClasses classes, List<String> toSort, Set<String> sorted, Set<String> processing, String current) {
        if (current == null) {
            current = toSort.remove(0);
        }
        processing.add(current);
        for (String after : classes.getClassesRequestedAfter(current)) {
            checkForCycles(processing, current, after);
            if (!sorted.contains(after) && toSort.contains(after)) {
                doSortByAfterAnnotation(classes, toSort, sorted, processing, after);
            }
        }
        processing.remove(current);
        sorted.add(current);
    }

    private void checkForCycles(Set<String> processing, String current, String after) {
        Assert.state(!processing.contains(after), (Supplier<String>) () -> {
            return "AutoConfigure cycle detected between " + current + " and " + after;
        });
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/AutoConfigurationSorter$AutoConfigurationClasses.class */
    private static class AutoConfigurationClasses {
        private final Map<String, AutoConfigurationClass> classes = new HashMap();

        AutoConfigurationClasses(MetadataReaderFactory metadataReaderFactory, AutoConfigurationMetadata autoConfigurationMetadata, Collection<String> classNames) {
            addToClasses(metadataReaderFactory, autoConfigurationMetadata, classNames, true);
        }

        Set<String> getAllNames() {
            return this.classes.keySet();
        }

        private void addToClasses(MetadataReaderFactory metadataReaderFactory, AutoConfigurationMetadata autoConfigurationMetadata, Collection<String> classNames, boolean required) {
            for (String className : classNames) {
                if (!this.classes.containsKey(className)) {
                    AutoConfigurationClass autoConfigurationClass = new AutoConfigurationClass(className, metadataReaderFactory, autoConfigurationMetadata);
                    boolean available = autoConfigurationClass.isAvailable();
                    if (required || available) {
                        this.classes.put(className, autoConfigurationClass);
                    }
                    if (available) {
                        addToClasses(metadataReaderFactory, autoConfigurationMetadata, autoConfigurationClass.getBefore(), false);
                        addToClasses(metadataReaderFactory, autoConfigurationMetadata, autoConfigurationClass.getAfter(), false);
                    }
                }
            }
        }

        AutoConfigurationClass get(String className) {
            return this.classes.get(className);
        }

        Set<String> getClassesRequestedAfter(String className) {
            Set<String> classesRequestedAfter = new LinkedHashSet<>(get(className).getAfter());
            this.classes.forEach((name, autoConfigurationClass) -> {
                if (autoConfigurationClass.getBefore().contains(className)) {
                    classesRequestedAfter.add(name);
                }
            });
            return classesRequestedAfter;
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/AutoConfigurationSorter$AutoConfigurationClass.class */
    private static class AutoConfigurationClass {
        private final String className;
        private final MetadataReaderFactory metadataReaderFactory;
        private final AutoConfigurationMetadata autoConfigurationMetadata;
        private volatile AnnotationMetadata annotationMetadata;
        private volatile Set<String> before;
        private volatile Set<String> after;

        AutoConfigurationClass(String className, MetadataReaderFactory metadataReaderFactory, AutoConfigurationMetadata autoConfigurationMetadata) {
            this.className = className;
            this.metadataReaderFactory = metadataReaderFactory;
            this.autoConfigurationMetadata = autoConfigurationMetadata;
        }

        boolean isAvailable() {
            try {
                if (!wasProcessed()) {
                    getAnnotationMetadata();
                    return true;
                }
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        Set<String> getBefore() {
            Set<String> annotationValue;
            if (this.before == null) {
                if (wasProcessed()) {
                    annotationValue = this.autoConfigurationMetadata.getSet(this.className, "AutoConfigureBefore", Collections.emptySet());
                } else {
                    annotationValue = getAnnotationValue(AutoConfigureBefore.class);
                }
                this.before = annotationValue;
            }
            return this.before;
        }

        Set<String> getAfter() {
            Set<String> annotationValue;
            if (this.after == null) {
                if (wasProcessed()) {
                    annotationValue = this.autoConfigurationMetadata.getSet(this.className, "AutoConfigureAfter", Collections.emptySet());
                } else {
                    annotationValue = getAnnotationValue(AutoConfigureAfter.class);
                }
                this.after = annotationValue;
            }
            return this.after;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getOrder() {
            if (wasProcessed()) {
                return this.autoConfigurationMetadata.getInteger(this.className, "AutoConfigureOrder", 0).intValue();
            }
            Map<String, Object> attributes = getAnnotationMetadata().getAnnotationAttributes(AutoConfigureOrder.class.getName());
            if (attributes != null) {
                return ((Integer) attributes.get("value")).intValue();
            }
            return 0;
        }

        private boolean wasProcessed() {
            return this.autoConfigurationMetadata != null && this.autoConfigurationMetadata.wasProcessed(this.className);
        }

        private Set<String> getAnnotationValue(Class<?> annotation) {
            Map<String, Object> attributes = getAnnotationMetadata().getAnnotationAttributes(annotation.getName(), true);
            if (attributes == null) {
                return Collections.emptySet();
            }
            Set<String> value = new LinkedHashSet<>();
            Collections.addAll(value, (String[]) attributes.get("value"));
            Collections.addAll(value, (String[]) attributes.get("name"));
            return value;
        }

        private AnnotationMetadata getAnnotationMetadata() {
            if (this.annotationMetadata == null) {
                try {
                    MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(this.className);
                    this.annotationMetadata = metadataReader.getAnnotationMetadata();
                } catch (IOException ex) {
                    throw new IllegalStateException("Unable to read meta-data for class " + this.className, ex);
                }
            }
            return this.annotationMetadata;
        }
    }
}
