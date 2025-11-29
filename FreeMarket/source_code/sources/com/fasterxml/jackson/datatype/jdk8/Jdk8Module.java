package com.fasterxml.jackson.datatype.jdk8;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-datatype-jdk8-2.13.5.jar:com/fasterxml/jackson/datatype/jdk8/Jdk8Module.class */
public class Jdk8Module extends Module {
    protected boolean _cfgHandleAbsentAsNull = false;

    @Override // com.fasterxml.jackson.databind.Module
    public void setupModule(Module.SetupContext context) {
        context.addSerializers(new Jdk8Serializers());
        context.addDeserializers(new Jdk8Deserializers());
        context.addTypeModifier(new Jdk8TypeModifier());
        if (this._cfgHandleAbsentAsNull) {
            context.addBeanSerializerModifier(new Jdk8BeanSerializerModifier());
        }
    }

    @Override // com.fasterxml.jackson.databind.Module, com.fasterxml.jackson.core.Versioned
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Deprecated
    public Jdk8Module configureAbsentsAsNulls(boolean state) {
        this._cfgHandleAbsentAsNull = state;
        return this;
    }

    public int hashCode() {
        return getClass().hashCode();
    }

    public boolean equals(Object o) {
        return this == o;
    }

    @Override // com.fasterxml.jackson.databind.Module
    public String getModuleName() {
        return "Jdk8Module";
    }
}
