package org.springframework.boot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ExitCodeGenerators.class */
class ExitCodeGenerators implements Iterable<ExitCodeGenerator> {
    private List<ExitCodeGenerator> generators = new ArrayList();

    ExitCodeGenerators() {
    }

    void addAll(Throwable exception, ExitCodeExceptionMapper... mappers) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mappers, "Mappers must not be null");
        addAll(exception, Arrays.asList(mappers));
    }

    void addAll(Throwable exception, Iterable<? extends ExitCodeExceptionMapper> mappers) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mappers, "Mappers must not be null");
        for (ExitCodeExceptionMapper mapper : mappers) {
            add(exception, mapper);
        }
    }

    void add(Throwable exception, ExitCodeExceptionMapper mapper) {
        Assert.notNull(exception, "Exception must not be null");
        Assert.notNull(mapper, "Mapper must not be null");
        add(new MappedExitCodeGenerator(exception, mapper));
    }

    void addAll(ExitCodeGenerator... generators) {
        Assert.notNull(generators, "Generators must not be null");
        addAll(Arrays.asList(generators));
    }

    void addAll(Iterable<? extends ExitCodeGenerator> generators) {
        Assert.notNull(generators, "Generators must not be null");
        for (ExitCodeGenerator generator : generators) {
            add(generator);
        }
    }

    void add(ExitCodeGenerator generator) {
        Assert.notNull(generator, "Generator must not be null");
        this.generators.add(generator);
        AnnotationAwareOrderComparator.sort(this.generators);
    }

    @Override // java.lang.Iterable
    public Iterator<ExitCodeGenerator> iterator() {
        return this.generators.iterator();
    }

    int getExitCode() {
        int value;
        int exitCode = 0;
        Iterator<ExitCodeGenerator> it = this.generators.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            ExitCodeGenerator generator = it.next();
            try {
                value = generator.getExitCode();
            } catch (Exception ex) {
                exitCode = 1;
                ex.printStackTrace();
            }
            if (value != 0) {
                exitCode = value;
                break;
            }
        }
        return exitCode;
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ExitCodeGenerators$MappedExitCodeGenerator.class */
    private static class MappedExitCodeGenerator implements ExitCodeGenerator {
        private final Throwable exception;
        private final ExitCodeExceptionMapper mapper;

        MappedExitCodeGenerator(Throwable exception, ExitCodeExceptionMapper mapper) {
            this.exception = exception;
            this.mapper = mapper;
        }

        @Override // org.springframework.boot.ExitCodeGenerator
        public int getExitCode() {
            return this.mapper.getExitCode(this.exception);
        }
    }
}
