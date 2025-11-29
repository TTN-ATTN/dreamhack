package freemarker.template.utility;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.core.TemplateMarkupOutputModel;
import freemarker.core._CoreAPI;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.DateModel;
import freemarker.ext.beans.EnumerationModel;
import freemarker.ext.beans.IteratorModel;
import freemarker.ext.beans.MapModel;
import freemarker.ext.beans.NumberModel;
import freemarker.ext.beans.OverloadedMethodsModel;
import freemarker.ext.beans.SimpleMethodModel;
import freemarker.ext.beans.StringModel;
import freemarker.ext.util.WrapperTemplateModel;
import freemarker.template.AdapterTemplateModel;
import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateCollectionModel;
import freemarker.template.TemplateCollectionModelEx;
import freemarker.template.TemplateDateModel;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelIterator;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateNodeModelEx;
import freemarker.template.TemplateNumberModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template.TemplateTransformModel;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.slf4j.Marker;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/ClassUtil.class */
public class ClassUtil {
    private static final Map<String, Class<?>> PRIMITIVE_CLASSES_BY_NAME = new HashMap();

    private ClassUtil() {
    }

    public static Class forName(String className) throws ClassNotFoundException {
        try {
            ClassLoader ctcl = Thread.currentThread().getContextClassLoader();
            if (ctcl != null) {
                return Class.forName(className, true, ctcl);
            }
        } catch (ClassNotFoundException e) {
        } catch (SecurityException e2) {
        }
        return Class.forName(className);
    }

    static {
        PRIMITIVE_CLASSES_BY_NAME.put("boolean", Boolean.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("byte", Byte.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("char", Character.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("short", Short.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("int", Integer.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("long", Long.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("float", Float.TYPE);
        PRIMITIVE_CLASSES_BY_NAME.put("double", Double.TYPE);
    }

    public static Class<?> resolveIfPrimitiveTypeName(String typeName) {
        return PRIMITIVE_CLASSES_BY_NAME.get(typeName);
    }

    public static Class<?> getArrayClass(Class<?> elementType, int dimensions) {
        return dimensions == 0 ? elementType : Array.newInstance(elementType, new int[dimensions]).getClass();
    }

    public static String getShortClassName(Class pClass) {
        return getShortClassName(pClass, false);
    }

    public static String getShortClassName(Class pClass, boolean shortenFreeMarkerClasses) {
        if (pClass == null) {
            return null;
        }
        if (pClass.isArray()) {
            return getShortClassName(pClass.getComponentType()) + ClassUtils.ARRAY_SUFFIX;
        }
        String cn = pClass.getName();
        if (cn.startsWith("java.lang.") || cn.startsWith("java.util.")) {
            return cn.substring(10);
        }
        if (shortenFreeMarkerClasses) {
            if (cn.startsWith("freemarker.template.")) {
                return "f.t" + cn.substring(19);
            }
            if (cn.startsWith("freemarker.ext.beans.")) {
                return "f.e.b" + cn.substring(20);
            }
            if (cn.startsWith("freemarker.core.")) {
                return "f.c" + cn.substring(15);
            }
            if (cn.startsWith("freemarker.ext.")) {
                return "f.e" + cn.substring(14);
            }
            if (cn.startsWith("freemarker.")) {
                return "f" + cn.substring(10);
            }
        }
        return cn;
    }

    public static String getShortClassNameOfObject(Object obj) {
        return getShortClassNameOfObject(obj, false);
    }

    public static String getShortClassNameOfObject(Object obj, boolean shortenFreeMarkerClasses) {
        if (obj == null) {
            return "Null";
        }
        return getShortClassName(obj.getClass(), shortenFreeMarkerClasses);
    }

    private static Class getPrimaryTemplateModelInterface(TemplateModel tm) {
        if (tm instanceof BeanModel) {
            if (tm instanceof CollectionModel) {
                return TemplateSequenceModel.class;
            }
            if ((tm instanceof IteratorModel) || (tm instanceof EnumerationModel)) {
                return TemplateCollectionModel.class;
            }
            if (tm instanceof MapModel) {
                return TemplateHashModelEx.class;
            }
            if (tm instanceof NumberModel) {
                return TemplateNumberModel.class;
            }
            if (tm instanceof BooleanModel) {
                return TemplateBooleanModel.class;
            }
            if (tm instanceof DateModel) {
                return TemplateDateModel.class;
            }
            if (tm instanceof StringModel) {
                Object wrapped = ((BeanModel) tm).getWrappedObject();
                if (wrapped instanceof String) {
                    return TemplateScalarModel.class;
                }
                if (tm instanceof TemplateHashModelEx) {
                    return TemplateHashModelEx.class;
                }
                return null;
            }
            return null;
        }
        if ((tm instanceof SimpleMethodModel) || (tm instanceof OverloadedMethodsModel)) {
            return TemplateMethodModelEx.class;
        }
        if ((tm instanceof TemplateCollectionModel) && _CoreAPI.isLazilyGeneratedSequenceModel((TemplateCollectionModel) tm)) {
            return TemplateSequenceModel.class;
        }
        return null;
    }

    private static void appendTemplateModelTypeName(StringBuilder sb, Set typeNamesAppended, Class cl) {
        int initalLength = sb.length();
        if (TemplateNodeModelEx.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "extended node");
        } else if (TemplateNodeModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "node");
        }
        if (TemplateDirectiveModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "directive");
        } else if (TemplateTransformModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "transform");
        }
        if (TemplateSequenceModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "sequence");
        } else if (TemplateCollectionModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, TemplateCollectionModelEx.class.isAssignableFrom(cl) ? "extended_collection" : "collection");
        } else if (TemplateModelIterator.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "iterator");
        }
        if (TemplateMethodModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "method");
        }
        if (Environment.Namespace.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "namespace");
        } else if (TemplateHashModelEx.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "extended_hash");
        } else if (TemplateHashModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "hash");
        }
        if (TemplateNumberModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "number");
        }
        if (TemplateDateModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "date_or_time_or_datetime");
        }
        if (TemplateBooleanModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "boolean");
        }
        if (TemplateScalarModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "string");
        }
        if (TemplateMarkupOutputModel.class.isAssignableFrom(cl)) {
            appendTypeName(sb, typeNamesAppended, "markup_output");
        }
        if (sb.length() == initalLength) {
            appendTypeName(sb, typeNamesAppended, "misc_template_model");
        }
    }

    private static Class getUnwrappedClass(TemplateModel tm) {
        Object unwrapped;
        try {
            if (tm instanceof WrapperTemplateModel) {
                unwrapped = ((WrapperTemplateModel) tm).getWrappedObject();
            } else if (tm instanceof AdapterTemplateModel) {
                unwrapped = ((AdapterTemplateModel) tm).getAdaptedObject(Object.class);
            } else {
                unwrapped = null;
            }
        } catch (Throwable th) {
            unwrapped = null;
        }
        if (unwrapped != null) {
            return unwrapped.getClass();
        }
        return null;
    }

    private static void appendTypeName(StringBuilder sb, Set typeNamesAppended, String name) {
        if (!typeNamesAppended.contains(name)) {
            if (sb.length() != 0) {
                sb.append(Marker.ANY_NON_NULL_MARKER);
            }
            sb.append(name);
            typeNamesAppended.add(name);
        }
    }

    public static String getFTLTypeDescription(TemplateModel tm) {
        String javaClassName;
        if (tm == null) {
            return "Null";
        }
        Set typeNamesAppended = new HashSet();
        StringBuilder sb = new StringBuilder();
        Class primaryInterface = getPrimaryTemplateModelInterface(tm);
        if (primaryInterface != null) {
            appendTemplateModelTypeName(sb, typeNamesAppended, primaryInterface);
        }
        if (tm instanceof Macro) {
            appendTypeName(sb, typeNamesAppended, ((Macro) tm).isFunction() ? "function" : "macro");
        }
        appendTemplateModelTypeName(sb, typeNamesAppended, tm.getClass());
        Class unwrappedClass = getUnwrappedClass(tm);
        if (unwrappedClass != null) {
            javaClassName = getShortClassName(unwrappedClass, true);
        } else {
            javaClassName = null;
        }
        sb.append(" (");
        String modelClassName = getShortClassName(tm.getClass(), true);
        if (javaClassName == null) {
            sb.append("wrapper: ");
            sb.append(modelClassName);
        } else {
            sb.append(javaClassName);
            sb.append(" wrapped into ");
            sb.append(modelClassName);
        }
        sb.append(")");
        return sb.toString();
    }

    public static Class primitiveClassToBoxingClass(Class primitiveClass) {
        return primitiveClass == Integer.TYPE ? Integer.class : primitiveClass == Boolean.TYPE ? Boolean.class : primitiveClass == Long.TYPE ? Long.class : primitiveClass == Double.TYPE ? Double.class : primitiveClass == Character.TYPE ? Character.class : primitiveClass == Float.TYPE ? Float.class : primitiveClass == Byte.TYPE ? Byte.class : primitiveClass == Short.TYPE ? Short.class : primitiveClass == Void.TYPE ? Void.class : primitiveClass;
    }

    public static Class boxingClassToPrimitiveClass(Class boxingClass) {
        return boxingClass == Integer.class ? Integer.TYPE : boxingClass == Boolean.class ? Boolean.TYPE : boxingClass == Long.class ? Long.TYPE : boxingClass == Double.class ? Double.TYPE : boxingClass == Character.class ? Character.TYPE : boxingClass == Float.class ? Float.TYPE : boxingClass == Byte.class ? Byte.TYPE : boxingClass == Short.class ? Short.TYPE : boxingClass == Void.class ? Void.TYPE : boxingClass;
    }

    public static boolean isNumerical(Class type) {
        return Number.class.isAssignableFrom(type) || !(!type.isPrimitive() || type == Boolean.TYPE || type == Character.TYPE || type == Void.TYPE);
    }

    public static InputStream getReasourceAsStream(Class<?> baseClass, String resource, boolean optional) throws IOException {
        InputStream ins;
        try {
            ins = baseClass.getResourceAsStream(resource);
        } catch (Exception e) {
            URL url = baseClass.getResource(resource);
            ins = url != null ? url.openStream() : null;
        }
        if (!optional) {
            checkInputStreamNotNull(ins, baseClass, resource);
        }
        return ins;
    }

    public static InputStream getReasourceAsStream(ClassLoader classLoader, String resource, boolean optional) throws IOException {
        InputStream ins;
        try {
            ins = classLoader.getResourceAsStream(resource);
        } catch (Exception e) {
            URL url = classLoader.getResource(resource);
            ins = url != null ? url.openStream() : null;
        }
        if (ins == null && !optional) {
            throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base ClassLoader was: " + classLoader);
        }
        return ins;
    }

    /* JADX WARN: Finally extract failed */
    public static Properties loadProperties(Class<?> baseClass, String resource) throws IOException {
        Properties props = new Properties();
        InputStream ins = null;
        try {
        } catch (MaybeZipFileClosedException e) {
            URL url = baseClass.getResource(resource);
            InputStream ins2 = url != null ? url.openStream() : null;
            checkInputStreamNotNull(ins2, baseClass, resource);
            props.load(ins2);
            if (ins2 != null) {
                try {
                    ins2.close();
                } catch (Exception e2) {
                }
            }
        }
        try {
            try {
                InputStream ins3 = baseClass.getResourceAsStream(resource);
                checkInputStreamNotNull(ins3, baseClass, resource);
                try {
                    try {
                        props.load(ins3);
                        try {
                            ins3.close();
                        } catch (Exception e3) {
                        }
                        InputStream ins4 = null;
                        if (0 != 0) {
                            try {
                                ins4.close();
                            } catch (Exception e4) {
                            }
                        }
                        return props;
                    } catch (Throwable th) {
                        try {
                            ins3.close();
                        } catch (Exception e5) {
                        }
                        throw th;
                    }
                } catch (Exception e6) {
                    throw new MaybeZipFileClosedException();
                }
            } catch (Throwable th2) {
                if (0 != 0) {
                    try {
                        ins.close();
                    } catch (Exception e7) {
                    }
                }
                throw th2;
            }
        } catch (Exception e8) {
            throw new MaybeZipFileClosedException();
        }
    }

    private static void checkInputStreamNotNull(InputStream ins, Class<?> baseClass, String resource) throws IOException {
        if (ins == null) {
            throw new IOException("Class-loader resource not found (shown quoted): " + StringUtil.jQuote(resource) + ". The base class was " + baseClass.getName() + ".");
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/utility/ClassUtil$MaybeZipFileClosedException.class */
    private static class MaybeZipFileClosedException extends Exception {
        private MaybeZipFileClosedException() {
        }
    }
}
