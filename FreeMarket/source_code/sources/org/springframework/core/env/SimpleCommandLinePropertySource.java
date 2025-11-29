package org.springframework.core.env;

import java.util.List;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/SimpleCommandLinePropertySource.class */
public class SimpleCommandLinePropertySource extends CommandLinePropertySource<CommandLineArgs> {
    public SimpleCommandLinePropertySource(String... args) {
        super(new SimpleCommandLineArgsParser().parse(args));
    }

    public SimpleCommandLinePropertySource(String name, String[] args) {
        super(name, new SimpleCommandLineArgsParser().parse(args));
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.core.env.EnumerablePropertySource
    public String[] getPropertyNames() {
        return StringUtils.toStringArray(((CommandLineArgs) this.source).getOptionNames());
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.core.env.CommandLinePropertySource
    protected boolean containsOption(String name) {
        return ((CommandLineArgs) this.source).containsOption(name);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.core.env.CommandLinePropertySource
    @Nullable
    protected List<String> getOptionValues(String name) {
        return ((CommandLineArgs) this.source).getOptionValues(name);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // org.springframework.core.env.CommandLinePropertySource
    protected List<String> getNonOptionArgs() {
        return ((CommandLineArgs) this.source).getNonOptionArgs();
    }
}
