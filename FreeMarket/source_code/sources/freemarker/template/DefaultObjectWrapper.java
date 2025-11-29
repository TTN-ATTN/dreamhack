package freemarker.template;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperConfiguration;
import freemarker.ext.beans.DefaultMemberAccessPolicy;
import freemarker.ext.beans.LegacyDefaultMemberAccessPolicy;
import freemarker.ext.beans.MemberAccessPolicy;
import freemarker.ext.dom.NodeModel;
import freemarker.log.Logger;
import java.lang.reflect.Array;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.w3c.dom.Node;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/template/DefaultObjectWrapper.class */
public class DefaultObjectWrapper extends BeansWrapper {

    @Deprecated
    static final DefaultObjectWrapper instance = new DefaultObjectWrapper();
    private static final Class<?> JYTHON_OBJ_CLASS;
    private static final ObjectWrapper JYTHON_WRAPPER;
    private boolean useAdaptersForContainers;
    private boolean forceLegacyNonListCollections;
    private boolean iterableSupport;
    private boolean domNodeSupport;
    private boolean jythonSupport;
    private final boolean useAdapterForEnumerations;

    static {
        Class<?> cl;
        ObjectWrapper ow;
        try {
            cl = Class.forName("org.python.core.PyObject");
            ow = (ObjectWrapper) Class.forName("freemarker.ext.jython.JythonWrapper").getField("INSTANCE").get(null);
        } catch (Throwable e) {
            cl = null;
            ow = null;
            if (!(e instanceof ClassNotFoundException)) {
                try {
                    Logger.getLogger("freemarker.template.DefaultObjectWrapper").error("Failed to init Jython support, so it was disabled.", e);
                } catch (Throwable th) {
                }
            }
        }
        JYTHON_OBJ_CLASS = cl;
        JYTHON_WRAPPER = ow;
    }

    @Deprecated
    public DefaultObjectWrapper() {
        this(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    }

    public DefaultObjectWrapper(Version incompatibleImprovements) {
        this(new DefaultObjectWrapperConfiguration(incompatibleImprovements) { // from class: freemarker.template.DefaultObjectWrapper.1
        }, false);
    }

    protected DefaultObjectWrapper(BeansWrapperConfiguration bwCfg, boolean writeProtected) {
        super(bwCfg, writeProtected, false);
        DefaultObjectWrapperConfiguration dowDowCfg = bwCfg instanceof DefaultObjectWrapperConfiguration ? (DefaultObjectWrapperConfiguration) bwCfg : new DefaultObjectWrapperConfiguration(bwCfg.getIncompatibleImprovements()) { // from class: freemarker.template.DefaultObjectWrapper.2
        };
        this.useAdaptersForContainers = dowDowCfg.getUseAdaptersForContainers();
        this.useAdapterForEnumerations = this.useAdaptersForContainers && getIncompatibleImprovements().intValue() >= _VersionInts.V_2_3_26;
        this.forceLegacyNonListCollections = dowDowCfg.getForceLegacyNonListCollections();
        this.iterableSupport = dowDowCfg.getIterableSupport();
        this.domNodeSupport = dowDowCfg.getDOMNodeSupport();
        this.jythonSupport = dowDowCfg.getJythonSupport();
        finalizeConstruction(writeProtected);
    }

    protected DefaultObjectWrapper(DefaultObjectWrapperConfiguration dowCfg, boolean writeProtected) {
        this((BeansWrapperConfiguration) dowCfg, writeProtected);
    }

    @Override // freemarker.ext.beans.BeansWrapper, freemarker.template.ObjectWrapper
    public TemplateModel wrap(Object obj) throws TemplateModelException {
        if (obj == null) {
            return super.wrap(null);
        }
        if (obj instanceof TemplateModel) {
            return (TemplateModel) obj;
        }
        if (obj instanceof String) {
            return new SimpleScalar((String) obj);
        }
        if (obj instanceof Number) {
            return new SimpleNumber((Number) obj);
        }
        if (obj instanceof Date) {
            if (obj instanceof java.sql.Date) {
                return new SimpleDate((java.sql.Date) obj);
            }
            if (obj instanceof Time) {
                return new SimpleDate((Time) obj);
            }
            if (obj instanceof Timestamp) {
                return new SimpleDate((Timestamp) obj);
            }
            return new SimpleDate((Date) obj, getDefaultDateType());
        }
        Class<?> objClass = obj.getClass();
        if (objClass.isArray()) {
            if (this.useAdaptersForContainers) {
                return DefaultArrayAdapter.adapt(obj, this);
            }
            obj = convertArray(obj);
        }
        if (obj instanceof Collection) {
            if (this.useAdaptersForContainers) {
                if (obj instanceof List) {
                    return DefaultListAdapter.adapt((List) obj, this);
                }
                return this.forceLegacyNonListCollections ? new SimpleSequence((Collection) obj, this) : DefaultNonListCollectionAdapter.adapt((Collection) obj, this);
            }
            return new SimpleSequence((Collection) obj, this);
        }
        if (obj instanceof Map) {
            return this.useAdaptersForContainers ? DefaultMapAdapter.adapt((Map) obj, this) : new SimpleHash((Map) obj, this);
        }
        if (obj instanceof Boolean) {
            return obj.equals(Boolean.TRUE) ? TemplateBooleanModel.TRUE : TemplateBooleanModel.FALSE;
        }
        if (obj instanceof Iterator) {
            return this.useAdaptersForContainers ? DefaultIteratorAdapter.adapt((Iterator) obj, this) : new SimpleCollection((Iterator) obj, this);
        }
        if (this.useAdapterForEnumerations && (obj instanceof Enumeration)) {
            return DefaultEnumerationAdapter.adapt((Enumeration) obj, this);
        }
        if (this.iterableSupport && (obj instanceof Iterable)) {
            return DefaultIterableAdapter.adapt((Iterable) obj, this);
        }
        return handleUnknownType(obj);
    }

    protected TemplateModel handleUnknownType(Object obj) throws TemplateModelException {
        if (this.domNodeSupport && (obj instanceof Node)) {
            return wrapDomNode(obj);
        }
        if (this.jythonSupport) {
            MemberAccessPolicy memberAccessPolicy = getMemberAccessPolicy();
            if (((memberAccessPolicy instanceof DefaultMemberAccessPolicy) || (memberAccessPolicy instanceof LegacyDefaultMemberAccessPolicy)) && JYTHON_WRAPPER != null && JYTHON_OBJ_CLASS.isInstance(obj)) {
                return JYTHON_WRAPPER.wrap(obj);
            }
        }
        return super.wrap(obj);
    }

    public TemplateModel wrapDomNode(Object obj) {
        return NodeModel.wrap((Node) obj);
    }

    protected Object convertArray(Object arr) {
        int size = Array.getLength(arr);
        ArrayList list = new ArrayList(size);
        for (int i = 0; i < size; i++) {
            list.add(Array.get(arr, i));
        }
        return list;
    }

    public boolean getUseAdaptersForContainers() {
        return this.useAdaptersForContainers;
    }

    public void setUseAdaptersForContainers(boolean useAdaptersForContainers) {
        checkModifiable();
        this.useAdaptersForContainers = useAdaptersForContainers;
    }

    public boolean getForceLegacyNonListCollections() {
        return this.forceLegacyNonListCollections;
    }

    public void setForceLegacyNonListCollections(boolean forceLegacyNonListCollections) {
        checkModifiable();
        this.forceLegacyNonListCollections = forceLegacyNonListCollections;
    }

    public boolean getIterableSupport() {
        return this.iterableSupport;
    }

    public void setIterableSupport(boolean iterableSupport) {
        checkModifiable();
        this.iterableSupport = iterableSupport;
    }

    public final boolean getDOMNodeSupport() {
        return this.domNodeSupport;
    }

    public void setDOMNodeSupport(boolean domNodeSupport) {
        checkModifiable();
        this.domNodeSupport = domNodeSupport;
    }

    public final boolean getJythonSupport() {
        return this.jythonSupport;
    }

    public void setJythonSupport(boolean jythonSupport) {
        checkModifiable();
        this.jythonSupport = jythonSupport;
    }

    protected static Version normalizeIncompatibleImprovementsVersion(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        Version bwIcI = BeansWrapper.normalizeIncompatibleImprovementsVersion(incompatibleImprovements);
        return (incompatibleImprovements.intValue() < _VersionInts.V_2_3_22 || bwIcI.intValue() >= _VersionInts.V_2_3_22) ? bwIcI : Configuration.VERSION_2_3_22;
    }

    @Override // freemarker.ext.beans.BeansWrapper
    protected String toPropertiesString() {
        int smwEnd;
        String bwProps = super.toPropertiesString();
        if (bwProps.startsWith("simpleMapWrapper") && (smwEnd = bwProps.indexOf(44)) != -1) {
            bwProps = bwProps.substring(smwEnd + 1).trim();
        }
        return "useAdaptersForContainers=" + this.useAdaptersForContainers + ", forceLegacyNonListCollections=" + this.forceLegacyNonListCollections + ", iterableSupport=" + this.iterableSupport + ", domNodeSupport=" + this.domNodeSupport + ", jythonSupport=" + this.jythonSupport + bwProps;
    }
}
