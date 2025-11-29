package javax.el;

import java.lang.reflect.Method;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/tomcat-embed-el-9.0.75.jar:javax/el/FunctionMapper.class */
public abstract class FunctionMapper {
    public abstract Method resolveFunction(String str, String str2);

    public void mapFunction(String prefix, String localName, Method method) {
    }
}
