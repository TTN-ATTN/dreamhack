package com.fasterxml.jackson.module.paramnames;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.Versioned;
import com.fasterxml.jackson.core.util.VersionUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-module-parameter-names-2.13.5.jar:com/fasterxml/jackson/module/paramnames/PackageVersion.class */
public final class PackageVersion implements Versioned {
    public static final Version VERSION = VersionUtil.parseVersion("2.13.5", "com.fasterxml.jackson.module", "jackson-module-parameter-names");

    @Override // com.fasterxml.jackson.core.Versioned
    public Version version() {
        return VERSION;
    }
}
