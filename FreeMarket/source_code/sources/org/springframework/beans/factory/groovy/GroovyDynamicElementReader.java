package org.springframework.beans.factory.groovy;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.Generated;
import groovy.xml.StreamingMarkupBuilder;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.w3c.dom.Element;

/* compiled from: GroovyDynamicElementReader.groovy */
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/groovy/GroovyDynamicElementReader.class */
class GroovyDynamicElementReader extends GroovyObjectSupport {
    private final String rootNamespace;
    private final Map<String, String> xmlNamespaces;
    private final BeanDefinitionParserDelegate delegate;
    private final GroovyBeanDefinitionWrapper beanDefinition;
    protected final boolean decorating;
    private boolean callAfterInvocation;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (getClass() != GroovyDynamicElementReader.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            ClassInfo classInfo2 = ClassInfo.getClassInfo(getClass());
            classInfo = classInfo2;
            $staticClassInfo = classInfo2;
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object super$1$invokeMethod(String str, Object obj) {
        return invokeMethod(str, obj);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
        strArr[0] = "equals";
        strArr[1] = "getAt";
        strArr[2] = "DELEGATE_FIRST";
        strArr[3] = "call";
        strArr[4] = "afterInvocation";
        strArr[5] = "<$constructor$>";
        strArr[6] = "DELEGATE_FIRST";
        strArr[7] = "bind";
        strArr[8] = "<$constructor$>";
        strArr[9] = "writeTo";
        strArr[10] = "documentElement";
        strArr[11] = "readDocumentFromString";
        strArr[12] = "readerContext";
        strArr[13] = "toString";
        strArr[14] = "initDefaults";
        strArr[15] = "beanDefinitionHolder";
        strArr[16] = "decorateIfRequired";
        strArr[17] = "setBeanDefinitionHolder";
        strArr[18] = "parseCustomElement";
        strArr[19] = "setBeanDefinition";
        strArr[20] = "afterInvocation";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] strArr = new String[21];
        $createCallSiteArray_1(strArr);
        return new CallSiteArray(GroovyDynamicElementReader.class, strArr);
    }

    /* JADX WARN: Removed duplicated region for block: B:6:0x0014  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static /* synthetic */ org.codehaus.groovy.runtime.callsite.CallSite[] $getCallSiteArray() {
        /*
            java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray
            if (r0 == 0) goto L14
            java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray
            java.lang.Object r0 = r0.get()
            org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = (org.codehaus.groovy.runtime.callsite.CallSiteArray) r0
            r1 = r0
            r4 = r1
            if (r0 != 0) goto L23
        L14:
            org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = $createCallSiteArray()
            r4 = r0
            java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference
            r1 = r0
            r2 = r4
            r1.<init>(r2)
            org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$callSiteArray = r0
        L23:
            r0 = r4
            org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.groovy.GroovyDynamicElementReader.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
    }

    public GroovyDynamicElementReader(String namespace, Map<String, String> map, BeanDefinitionParserDelegate delegate, GroovyBeanDefinitionWrapper beanDefinition, boolean decorating) {
        $getCallSiteArray();
        this.callAfterInvocation = true;
        this.rootNamespace = namespace;
        this.xmlNamespaces = map;
        this.delegate = delegate;
        this.beanDefinition = beanDefinition;
        this.decorating = decorating;
    }

    /* JADX WARN: Unreachable blocks removed: 2, instructions: 2 */
    public Object invokeMethod(String name, Object args) {
        Reference name2 = new Reference(name);
        Reference args2 = new Reference(args);
        CallSite[] callSiteArr$getCallSiteArray = $getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArr$getCallSiteArray[0].call((String) name2.get(), "doCall"))) {
            Object callable = callSiteArr$getCallSiteArray[1].call(args2.get(), 0);
            ScriptBytecodeAdapter.setProperty(callSiteArr$getCallSiteArray[2].callGetProperty(Closure.class), (Class) null, callable, "resolveStrategy");
            ScriptBytecodeAdapter.setProperty(this, (Class) null, callable, "delegate");
            Object result = callSiteArr$getCallSiteArray[3].call(callable);
            if (this.callAfterInvocation) {
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    callSiteArr$getCallSiteArray[4].callCurrent(this);
                } else {
                    afterInvocation();
                }
                this.callAfterInvocation = false;
            }
            return result;
        }
        Reference builder = new Reference((StreamingMarkupBuilder) ScriptBytecodeAdapter.castToType(callSiteArr$getCallSiteArray[5].callConstructor(StreamingMarkupBuilder.class), StreamingMarkupBuilder.class));
        Reference myNamespace = new Reference(this.rootNamespace);
        Reference myNamespaces = new Reference(this.xmlNamespaces);
        Object callable2 = new _invokeMethod_closure1(this, this, myNamespaces, args2, builder, myNamespace, name2);
        ScriptBytecodeAdapter.setProperty(callSiteArr$getCallSiteArray[6].callGetProperty(Closure.class), (Class) null, callable2, "resolveStrategy");
        ScriptBytecodeAdapter.setProperty((StreamingMarkupBuilder) builder.get(), (Class) null, callable2, "delegate");
        Object writable = callSiteArr$getCallSiteArray[7].call((StreamingMarkupBuilder) builder.get(), callable2);
        Object sw = callSiteArr$getCallSiteArray[8].callConstructor(StringWriter.class);
        callSiteArr$getCallSiteArray[9].call(writable, sw);
        Element element = (Element) ScriptBytecodeAdapter.castToType(callSiteArr$getCallSiteArray[10].callGetProperty(callSiteArr$getCallSiteArray[11].call(callSiteArr$getCallSiteArray[12].callGetProperty(this.delegate), callSiteArr$getCallSiteArray[13].call(sw))), Element.class);
        callSiteArr$getCallSiteArray[14].call(this.delegate, element);
        if (this.decorating) {
            BeanDefinitionHolder holder = (BeanDefinitionHolder) ScriptBytecodeAdapter.castToType(callSiteArr$getCallSiteArray[15].callGetProperty(this.beanDefinition), BeanDefinitionHolder.class);
            callSiteArr$getCallSiteArray[17].call(this.beanDefinition, (BeanDefinitionHolder) ScriptBytecodeAdapter.castToType(callSiteArr$getCallSiteArray[16].call(this.delegate, element, holder, (Object) null), BeanDefinitionHolder.class));
        } else {
            Object beanDefinition = callSiteArr$getCallSiteArray[18].call(this.delegate, element);
            if (DefaultTypeTransformation.booleanUnbox(beanDefinition)) {
                callSiteArr$getCallSiteArray[19].call(this.beanDefinition, beanDefinition);
            }
        }
        if (this.callAfterInvocation) {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArr$getCallSiteArray[20].callCurrent(this);
            } else {
                afterInvocation();
            }
            this.callAfterInvocation = false;
        }
        return element;
    }

    /* compiled from: GroovyDynamicElementReader.groovy */
    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/groovy/GroovyDynamicElementReader$_invokeMethod_closure1.class */
    public final class _invokeMethod_closure1 extends Closure implements GeneratedClosure {
        private /* synthetic */ Reference myNamespaces;
        private /* synthetic */ Reference args;
        private /* synthetic */ Reference builder;
        private /* synthetic */ Reference myNamespace;
        private /* synthetic */ Reference name;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public _invokeMethod_closure1(Object _outerInstance, Object _thisObject, Reference myNamespaces, Reference args, Reference builder, Reference myNamespace, Reference name) {
            super(_outerInstance, _thisObject);
            $getCallSiteArray();
            this.myNamespaces = myNamespaces;
            this.args = args;
            this.builder = builder;
            this.myNamespace = myNamespace;
            this.name = name;
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Generated
        public Object getMyNamespaces() {
            $getCallSiteArray();
            return this.myNamespaces.get();
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Generated
        public Object getArgs() {
            $getCallSiteArray();
            return this.args.get();
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Generated
        public StreamingMarkupBuilder getBuilder() {
            $getCallSiteArray();
            return (StreamingMarkupBuilder) ScriptBytecodeAdapter.castToType(this.builder.get(), StreamingMarkupBuilder.class);
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Generated
        public Object getMyNamespace() {
            $getCallSiteArray();
            return this.myNamespace.get();
        }

        /* JADX WARN: Unreachable blocks removed: 1, instructions: 1 */
        @Generated
        public String getName() {
            $getCallSiteArray();
            return ShortTypeHandling.castToString(this.name.get());
        }

        @Generated
        public Object doCall() {
            $getCallSiteArray();
            return doCall(null);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (getClass() != _invokeMethod_closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                ClassInfo classInfo2 = ClassInfo.getClassInfo(getClass());
                classInfo = classInfo2;
                $staticClassInfo = classInfo2;
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] strArr) {
            strArr[0] = "iterator";
            strArr[1] = "declareNamespace";
            strArr[2] = "mkp";
            strArr[3] = "key";
            strArr[4] = "value";
            strArr[5] = "getAt";
            strArr[6] = "DELEGATE_FIRST";
            strArr[7] = "getAt";
            strArr[8] = "getAt";
            strArr[9] = "delegate";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] strArr = new String[10];
            $createCallSiteArray_1(strArr);
            return new CallSiteArray(_invokeMethod_closure1.class, strArr);
        }

        /* JADX WARN: Removed duplicated region for block: B:6:0x0014  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private static /* synthetic */ org.codehaus.groovy.runtime.callsite.CallSite[] $getCallSiteArray() {
            /*
                java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray
                if (r0 == 0) goto L14
                java.lang.ref.SoftReference r0 = org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray
                java.lang.Object r0 = r0.get()
                org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = (org.codehaus.groovy.runtime.callsite.CallSiteArray) r0
                r1 = r0
                r4 = r1
                if (r0 != 0) goto L23
            L14:
                org.codehaus.groovy.runtime.callsite.CallSiteArray r0 = $createCallSiteArray()
                r4 = r0
                java.lang.ref.SoftReference r0 = new java.lang.ref.SoftReference
                r1 = r0
                r2 = r4
                r1.<init>(r2)
                org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$callSiteArray = r0
            L23:
                r0 = r4
                org.codehaus.groovy.runtime.callsite.CallSite[] r0 = r0.array
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.groovy.GroovyDynamicElementReader._invokeMethod_closure1.$getCallSiteArray():org.codehaus.groovy.runtime.callsite.CallSite[]");
        }

        public Object doCall(Object it) {
            CallSite[] callSiteArr$getCallSiteArray = $getCallSiteArray();
            Iterator it2 = (Iterator) ScriptBytecodeAdapter.castToType(callSiteArr$getCallSiteArray[0].call(this.myNamespaces.get()), Iterator.class);
            while (it2.hasNext()) {
                Object namespace = it2.next();
                callSiteArr$getCallSiteArray[1].call(callSiteArr$getCallSiteArray[2].callGroovyObjectGetProperty(this), ScriptBytecodeAdapter.createMap(new Object[]{callSiteArr$getCallSiteArray[3].callGetProperty(namespace), callSiteArr$getCallSiteArray[4].callGetProperty(namespace)}));
            }
            if (DefaultTypeTransformation.booleanUnbox(this.args.get()) && (callSiteArr$getCallSiteArray[5].call(this.args.get(), -1) instanceof Closure)) {
                ScriptBytecodeAdapter.setProperty(callSiteArr$getCallSiteArray[6].callGetProperty(Closure.class), (Class) null, callSiteArr$getCallSiteArray[7].call(this.args.get(), -1), "resolveStrategy");
                ScriptBytecodeAdapter.setProperty(this.builder.get(), (Class) null, callSiteArr$getCallSiteArray[8].call(this.args.get(), -1), "delegate");
            }
            return ScriptBytecodeAdapter.invokeMethodN(_invokeMethod_closure1.class, ScriptBytecodeAdapter.getProperty(_invokeMethod_closure1.class, callSiteArr$getCallSiteArray[9].callGroovyObjectGetProperty(this), ShortTypeHandling.castToString(new GStringImpl(new Object[]{this.myNamespace.get()}, new String[]{"", ""}))), ShortTypeHandling.castToString(new GStringImpl(new Object[]{this.name.get()}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{this.args.get()}, new int[]{0}));
        }
    }

    protected void afterInvocation() {
        $getCallSiteArray();
    }
}
