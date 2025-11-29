package org.springframework.scripting;

import java.io.IOException;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scripting/ScriptFactory.class */
public interface ScriptFactory {
    String getScriptSourceLocator();

    @Nullable
    Class<?>[] getScriptInterfaces();

    boolean requiresConfigInterface();

    @Nullable
    Object getScriptedObject(ScriptSource scriptSource, @Nullable Class<?>... actualInterfaces) throws ScriptCompilationException, IOException;

    @Nullable
    Class<?> getScriptedObjectType(ScriptSource scriptSource) throws ScriptCompilationException, IOException;

    boolean requiresScriptedObjectRefresh(ScriptSource scriptSource);
}
