package org.springframework.beans.factory.support;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.core.AttributeAccessor;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.log.LogMessage;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.core.metrics.StartupStep;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/AbstractBeanFactory.class */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

    @Nullable
    private BeanFactory parentBeanFactory;

    @Nullable
    private ClassLoader tempClassLoader;

    @Nullable
    private BeanExpressionResolver beanExpressionResolver;

    @Nullable
    private ConversionService conversionService;

    @Nullable
    private TypeConverter typeConverter;

    @Nullable
    private BeanPostProcessorCache beanPostProcessorCache;

    @Nullable
    private SecurityContextProvider securityContextProvider;

    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private boolean cacheBeanMetadata = true;
    private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet(4);
    private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap(4);
    private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList();
    private final List<BeanPostProcessor> beanPostProcessors = new BeanPostProcessorCacheAwareList();
    private final Map<String, Scope> scopes = new LinkedHashMap(8);
    private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap(256);
    private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap(256));
    private final ThreadLocal<Object> prototypesCurrentlyInCreation = new NamedThreadLocal("Prototype beans currently in creation");
    private ApplicationStartup applicationStartup = ApplicationStartup.DEFAULT;

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract boolean containsBeanDefinition(String str);

    /* JADX INFO: Access modifiers changed from: protected */
    public abstract BeanDefinition getBeanDefinition(String str) throws BeansException;

    protected abstract Object createBean(String str, RootBeanDefinition rootBeanDefinition, @Nullable Object[] objArr) throws BeanCreationException;

    public AbstractBeanFactory() {
    }

    public AbstractBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name) throws BeansException {
        return doGetBean(name, null, null, false);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public <T> T getBean(String str, Class<T> cls) throws BeansException {
        return (T) doGetBean(str, cls, null, false);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public Object getBean(String name, Object... args) throws BeansException {
        return doGetBean(name, null, args, false);
    }

    public <T> T getBean(String str, @Nullable Class<T> cls, @Nullable Object... objArr) throws BeansException {
        return (T) doGetBean(str, cls, objArr, false);
    }

    protected <T> T doGetBean(String str, @Nullable Class<T> cls, @Nullable Object[] objArr, boolean z) throws BeansException {
        Object objectForBeanInstance;
        String strTransformedBeanName = transformedBeanName(str);
        Object singleton = getSingleton(strTransformedBeanName);
        if (singleton != null && objArr == null) {
            if (this.logger.isTraceEnabled()) {
                if (isSingletonCurrentlyInCreation(strTransformedBeanName)) {
                    this.logger.trace("Returning eagerly cached instance of singleton bean '" + strTransformedBeanName + "' that is not fully initialized yet - a consequence of a circular reference");
                } else {
                    this.logger.trace("Returning cached instance of singleton bean '" + strTransformedBeanName + "'");
                }
            }
            objectForBeanInstance = getObjectForBeanInstance(singleton, str, strTransformedBeanName, null);
        } else {
            if (isPrototypeCurrentlyInCreation(strTransformedBeanName)) {
                throw new BeanCurrentlyInCreationException(strTransformedBeanName);
            }
            BeanFactory parentBeanFactory = getParentBeanFactory();
            if (parentBeanFactory != null && !containsBeanDefinition(strTransformedBeanName)) {
                String strOriginalBeanName = originalBeanName(str);
                if (parentBeanFactory instanceof AbstractBeanFactory) {
                    return (T) ((AbstractBeanFactory) parentBeanFactory).doGetBean(strOriginalBeanName, cls, objArr, z);
                }
                if (objArr != null) {
                    return (T) parentBeanFactory.getBean(strOriginalBeanName, objArr);
                }
                if (cls != null) {
                    return (T) parentBeanFactory.getBean(strOriginalBeanName, cls);
                }
                return (T) parentBeanFactory.getBean(strOriginalBeanName);
            }
            if (!z) {
                markBeanAsCreated(strTransformedBeanName);
            }
            StartupStep startupStepTag = this.applicationStartup.start("spring.beans.instantiate").tag("beanName", str);
            try {
                if (cls != null) {
                    try {
                        cls.getClass();
                        startupStepTag.tag("beanType", cls::toString);
                    } catch (BeansException e) {
                        startupStepTag.tag(SimpleMappingExceptionResolver.DEFAULT_EXCEPTION_ATTRIBUTE, e.getClass().toString());
                        startupStepTag.tag("message", String.valueOf(e.getMessage()));
                        cleanupAfterBeanCreationFailure(strTransformedBeanName);
                        throw e;
                    }
                }
                RootBeanDefinition mergedLocalBeanDefinition = getMergedLocalBeanDefinition(strTransformedBeanName);
                checkMergedBeanDefinition(mergedLocalBeanDefinition, strTransformedBeanName, objArr);
                String[] dependsOn = mergedLocalBeanDefinition.getDependsOn();
                if (dependsOn != null) {
                    for (String str2 : dependsOn) {
                        if (isDependent(strTransformedBeanName, str2)) {
                            throw new BeanCreationException(mergedLocalBeanDefinition.getResourceDescription(), strTransformedBeanName, "Circular depends-on relationship between '" + strTransformedBeanName + "' and '" + str2 + "'");
                        }
                        registerDependentBean(str2, strTransformedBeanName);
                        try {
                            getBean(str2);
                        } catch (NoSuchBeanDefinitionException e2) {
                            throw new BeanCreationException(mergedLocalBeanDefinition.getResourceDescription(), strTransformedBeanName, "'" + strTransformedBeanName + "' depends on missing bean '" + str2 + "'", e2);
                        }
                    }
                }
                if (mergedLocalBeanDefinition.isSingleton()) {
                    objectForBeanInstance = getObjectForBeanInstance(getSingleton(strTransformedBeanName, () -> {
                        try {
                            return createBean(strTransformedBeanName, mergedLocalBeanDefinition, objArr);
                        } catch (BeansException ex) {
                            destroySingleton(strTransformedBeanName);
                            throw ex;
                        }
                    }), str, strTransformedBeanName, mergedLocalBeanDefinition);
                } else if (mergedLocalBeanDefinition.isPrototype()) {
                    try {
                        beforePrototypeCreation(strTransformedBeanName);
                        Object objCreateBean = createBean(strTransformedBeanName, mergedLocalBeanDefinition, objArr);
                        afterPrototypeCreation(strTransformedBeanName);
                        objectForBeanInstance = getObjectForBeanInstance(objCreateBean, str, strTransformedBeanName, mergedLocalBeanDefinition);
                    } catch (Throwable th) {
                        afterPrototypeCreation(strTransformedBeanName);
                        throw th;
                    }
                } else {
                    String scope = mergedLocalBeanDefinition.getScope();
                    if (!StringUtils.hasLength(scope)) {
                        throw new IllegalStateException("No scope name defined for bean '" + strTransformedBeanName + "'");
                    }
                    Scope scope2 = this.scopes.get(scope);
                    if (scope2 == null) {
                        throw new IllegalStateException("No Scope registered for scope name '" + scope + "'");
                    }
                    try {
                        objectForBeanInstance = getObjectForBeanInstance(scope2.get(strTransformedBeanName, () -> {
                            beforePrototypeCreation(strTransformedBeanName);
                            try {
                                Object objCreateBean2 = createBean(strTransformedBeanName, mergedLocalBeanDefinition, objArr);
                                afterPrototypeCreation(strTransformedBeanName);
                                return objCreateBean2;
                            } catch (Throwable th2) {
                                afterPrototypeCreation(strTransformedBeanName);
                                throw th2;
                            }
                        }), str, strTransformedBeanName, mergedLocalBeanDefinition);
                    } catch (IllegalStateException e3) {
                        throw new ScopeNotActiveException(strTransformedBeanName, scope, e3);
                    }
                }
            } finally {
                startupStepTag.end();
            }
        }
        return (T) adaptBeanInstance(str, objectForBeanInstance, cls);
    }

    /* JADX WARN: Multi-variable type inference failed */
    <T> T adaptBeanInstance(String str, Object obj, @Nullable Class<?> cls) {
        if (cls != null && !cls.isInstance(obj)) {
            try {
                T t = (T) getTypeConverter().convertIfNecessary(obj, cls);
                if (t == null) {
                    throw new BeanNotOfRequiredTypeException(str, cls, obj.getClass());
                }
                return t;
            } catch (TypeMismatchException e) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Failed to convert bean '" + str + "' to required type '" + ClassUtils.getQualifiedName(cls) + "'", e);
                }
                throw new BeanNotOfRequiredTypeException(str, cls, obj.getClass());
            }
        }
        return obj;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean containsBean(String name) {
        String beanName = transformedBeanName(name);
        if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
            return !BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name);
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        return parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isSingleton(String name) throws BeansException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean ? BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean) beanInstance).isSingleton() : !BeanFactoryUtils.isFactoryDereference(name);
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            return parentBeanFactory.isSingleton(originalBeanName(name));
        }
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton()) {
            if (!isFactoryBean(beanName, mbd)) {
                return !BeanFactoryUtils.isFactoryDereference(name);
            }
            if (BeanFactoryUtils.isFactoryDereference(name)) {
                return true;
            }
            FactoryBean<?> factoryBean = (FactoryBean) getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
            return factoryBean.isSingleton();
        }
        return false;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isPrototype(String name) throws BeansException {
        String beanName = transformedBeanName(name);
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            return parentBeanFactory.isPrototype(originalBeanName(name));
        }
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isPrototype()) {
            return !BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName, mbd);
        }
        if (!BeanFactoryUtils.isFactoryDereference(name) && isFactoryBean(beanName, mbd)) {
            FactoryBean<?> fb = (FactoryBean) getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
            if (System.getSecurityManager() != null) {
                return ((Boolean) AccessController.doPrivileged(() -> {
                    return Boolean.valueOf(((fb instanceof SmartFactoryBean) && ((SmartFactoryBean) fb).isPrototype()) || !fb.isSingleton());
                }, getAccessControlContext())).booleanValue();
            }
            return ((fb instanceof SmartFactoryBean) && ((SmartFactoryBean) fb).isPrototype()) || !fb.isSingleton();
        }
        return false;
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        return isTypeMatch(name, typeToMatch, true);
    }

    protected boolean isTypeMatch(String name, ResolvableType typeToMatch, boolean allowFactoryBeanInit) throws BeansException {
        String beanName = transformedBeanName(name);
        boolean isFactoryDereference = BeanFactoryUtils.isFactoryDereference(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if (beanInstance instanceof FactoryBean) {
                if (!isFactoryDereference) {
                    Class<?> type = getTypeForFactoryBean((FactoryBean) beanInstance);
                    return type != null && typeToMatch.isAssignableFrom(type);
                }
                return typeToMatch.isInstance(beanInstance);
            }
            if (!isFactoryDereference) {
                if (typeToMatch.isInstance(beanInstance)) {
                    return true;
                }
                if (typeToMatch.hasGenerics() && containsBeanDefinition(beanName)) {
                    RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
                    Class<?> targetType = mbd.getTargetType();
                    if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance)) {
                        Class<?> classToMatch = typeToMatch.resolve();
                        if (classToMatch != null && !classToMatch.isInstance(beanInstance)) {
                            return false;
                        }
                        if (typeToMatch.isAssignableFrom(targetType)) {
                            return true;
                        }
                    }
                    ResolvableType resolvableType = mbd.targetType;
                    if (resolvableType == null) {
                        resolvableType = mbd.factoryMethodReturnType;
                    }
                    return resolvableType != null && typeToMatch.isAssignableFrom(resolvableType);
                }
                return false;
            }
            return false;
        }
        if (containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
            return false;
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            return parentBeanFactory.isTypeMatch(originalBeanName(name), typeToMatch);
        }
        RootBeanDefinition mbd2 = getMergedLocalBeanDefinition(beanName);
        BeanDefinitionHolder dbd = mbd2.getDecoratedDefinition();
        Class<?> classToMatch2 = typeToMatch.resolve();
        if (classToMatch2 == null) {
            classToMatch2 = FactoryBean.class;
        }
        Class<?>[] typesToMatch = FactoryBean.class == classToMatch2 ? new Class[]{classToMatch2} : new Class[]{FactoryBean.class, classToMatch2};
        Class<?> predictedType = null;
        if (!isFactoryDereference && dbd != null && isFactoryBean(beanName, mbd2) && (!mbd2.isLazyInit() || allowFactoryBeanInit)) {
            RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd2);
            Class<?> targetType2 = predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
            if (targetType2 != null && !FactoryBean.class.isAssignableFrom(targetType2)) {
                predictedType = targetType2;
            }
        }
        if (predictedType == null) {
            predictedType = predictBeanType(beanName, mbd2, typesToMatch);
            if (predictedType == null) {
                return false;
            }
        }
        ResolvableType beanType = null;
        if (FactoryBean.class.isAssignableFrom(predictedType)) {
            if (beanInstance == null && !isFactoryDereference) {
                beanType = getTypeForFactoryBean(beanName, mbd2, allowFactoryBeanInit);
                predictedType = beanType.resolve();
                if (predictedType == null) {
                    return false;
                }
            }
        } else if (isFactoryDereference) {
            predictedType = predictBeanType(beanName, mbd2, FactoryBean.class);
            if (predictedType == null || !FactoryBean.class.isAssignableFrom(predictedType)) {
                return false;
            }
        }
        if (beanType == null) {
            ResolvableType definedType = mbd2.targetType;
            if (definedType == null) {
                definedType = mbd2.factoryMethodReturnType;
            }
            if (definedType != null && definedType.resolve() == predictedType) {
                beanType = definedType;
            }
        }
        if (beanType != null) {
            return typeToMatch.isAssignableFrom(beanType);
        }
        return typeToMatch.isAssignableFrom(predictedType);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getType(name, true);
    }

    @Override // org.springframework.beans.factory.BeanFactory
    @Nullable
    public Class<?> getType(String name, boolean allowFactoryBeanInit) throws BeansException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
            if ((beanInstance instanceof FactoryBean) && !BeanFactoryUtils.isFactoryDereference(name)) {
                return getTypeForFactoryBean((FactoryBean) beanInstance);
            }
            return beanInstance.getClass();
        }
        BeanFactory parentBeanFactory = getParentBeanFactory();
        if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
            return parentBeanFactory.getType(originalBeanName(name));
        }
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
        if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
            RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
            Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd, new Class[0]);
            if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
                return targetClass;
            }
        }
        Class<?> beanClass = predictBeanType(beanName, mbd, new Class[0]);
        if (beanClass != null && FactoryBean.class.isAssignableFrom(beanClass)) {
            if (!BeanFactoryUtils.isFactoryDereference(name)) {
                return getTypeForFactoryBean(beanName, mbd, allowFactoryBeanInit).resolve();
            }
            return beanClass;
        }
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            return null;
        }
        return beanClass;
    }

    @Override // org.springframework.core.SimpleAliasRegistry, org.springframework.core.AliasRegistry, org.springframework.beans.factory.BeanFactory
    public String[] getAliases(String name) {
        BeanFactory parentBeanFactory;
        String beanName = transformedBeanName(name);
        List<String> aliases = new ArrayList<>();
        boolean factoryPrefix = name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX);
        String fullBeanName = beanName;
        if (factoryPrefix) {
            fullBeanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
        }
        if (!fullBeanName.equals(name)) {
            aliases.add(fullBeanName);
        }
        String[] retrievedAliases = super.getAliases(beanName);
        String prefix = factoryPrefix ? BeanFactory.FACTORY_BEAN_PREFIX : "";
        for (String retrievedAlias : retrievedAliases) {
            String alias = prefix + retrievedAlias;
            if (!alias.equals(name)) {
                aliases.add(alias);
            }
        }
        if (!containsSingleton(beanName) && !containsBeanDefinition(beanName) && (parentBeanFactory = getParentBeanFactory()) != null) {
            aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));
        }
        return StringUtils.toStringArray(aliases);
    }

    @Override // org.springframework.beans.factory.HierarchicalBeanFactory
    @Nullable
    public BeanFactory getParentBeanFactory() {
        return this.parentBeanFactory;
    }

    @Override // org.springframework.beans.factory.HierarchicalBeanFactory
    public boolean containsLocalBean(String name) {
        String beanName = transformedBeanName(name);
        return (containsSingleton(beanName) || containsBeanDefinition(beanName)) && (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName));
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setParentBeanFactory(@Nullable BeanFactory parentBeanFactory) {
        if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
            throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
        }
        if (this == parentBeanFactory) {
            throw new IllegalStateException("Cannot set parent bean factory to self");
        }
        this.parentBeanFactory = parentBeanFactory;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
        this.beanClassLoader = beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setTempClassLoader(@Nullable ClassLoader tempClassLoader) {
        this.tempClassLoader = tempClassLoader;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public ClassLoader getTempClassLoader() {
        return this.tempClassLoader;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
        this.cacheBeanMetadata = cacheBeanMetadata;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public boolean isCacheBeanMetadata() {
        return this.cacheBeanMetadata;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver) {
        this.beanExpressionResolver = resolver;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public BeanExpressionResolver getBeanExpressionResolver() {
        return this.beanExpressionResolver;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setConversionService(@Nullable ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
        Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
        this.propertyEditorRegistrars.add(registrar);
    }

    public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
        return this.propertyEditorRegistrars;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
        Assert.notNull(requiredType, "Required type must not be null");
        Assert.notNull(propertyEditorClass, "PropertyEditor class must not be null");
        this.customEditors.put(requiredType, propertyEditorClass);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
        registerCustomEditors(registry);
    }

    public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
        return this.customEditors;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Nullable
    protected TypeConverter getCustomTypeConverter() {
        return this.typeConverter;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public TypeConverter getTypeConverter() {
        TypeConverter customConverter = getCustomTypeConverter();
        if (customConverter != null) {
            return customConverter;
        }
        SimpleTypeConverter typeConverter = new SimpleTypeConverter();
        typeConverter.setConversionService(getConversionService());
        registerCustomEditors(typeConverter);
        return typeConverter;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        Assert.notNull(valueResolver, "StringValueResolver must not be null");
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public boolean hasEmbeddedValueResolver() {
        return !this.embeddedValueResolvers.isEmpty();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public String resolveEmbeddedValue(@Nullable String value) {
        if (value == null) {
            return null;
        }
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            result = resolver.resolveStringValue(result);
            if (result == null) {
                return null;
            }
        }
        return result;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
        synchronized (this.beanPostProcessors) {
            this.beanPostProcessors.remove(beanPostProcessor);
            this.beanPostProcessors.add(beanPostProcessor);
        }
    }

    public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
        synchronized (this.beanPostProcessors) {
            this.beanPostProcessors.removeAll(beanPostProcessors);
            this.beanPostProcessors.addAll(beanPostProcessors);
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public int getBeanPostProcessorCount() {
        return this.beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }

    BeanPostProcessorCache getBeanPostProcessorCache() {
        BeanPostProcessorCache beanPostProcessorCache;
        synchronized (this.beanPostProcessors) {
            BeanPostProcessorCache bppCache = this.beanPostProcessorCache;
            if (bppCache == null) {
                bppCache = new BeanPostProcessorCache();
                for (BeanPostProcessor bpp : this.beanPostProcessors) {
                    if (bpp instanceof InstantiationAwareBeanPostProcessor) {
                        bppCache.instantiationAware.add((InstantiationAwareBeanPostProcessor) bpp);
                        if (bpp instanceof SmartInstantiationAwareBeanPostProcessor) {
                            bppCache.smartInstantiationAware.add((SmartInstantiationAwareBeanPostProcessor) bpp);
                        }
                    }
                    if (bpp instanceof DestructionAwareBeanPostProcessor) {
                        bppCache.destructionAware.add((DestructionAwareBeanPostProcessor) bpp);
                    }
                    if (bpp instanceof MergedBeanDefinitionPostProcessor) {
                        bppCache.mergedDefinition.add((MergedBeanDefinitionPostProcessor) bpp);
                    }
                }
                this.beanPostProcessorCache = bppCache;
            }
            beanPostProcessorCache = bppCache;
        }
        return beanPostProcessorCache;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBeanPostProcessorCache() {
        synchronized (this.beanPostProcessors) {
            this.beanPostProcessorCache = null;
        }
    }

    protected boolean hasInstantiationAwareBeanPostProcessors() {
        return !getBeanPostProcessorCache().instantiationAware.isEmpty();
    }

    protected boolean hasDestructionAwareBeanPostProcessors() {
        return !getBeanPostProcessorCache().destructionAware.isEmpty();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void registerScope(String scopeName, Scope scope) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        Assert.notNull(scope, "Scope must not be null");
        if ("singleton".equals(scopeName) || "prototype".equals(scopeName)) {
            throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
        }
        Scope previous = this.scopes.put(scopeName, scope);
        if (previous != null && previous != scope) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]");
            }
        } else if (this.logger.isTraceEnabled()) {
            this.logger.trace("Registering scope '" + scopeName + "' with implementation [" + scope + "]");
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public String[] getRegisteredScopeNames() {
        return StringUtils.toStringArray(this.scopes.keySet());
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    @Nullable
    public Scope getRegisteredScope(String scopeName) {
        Assert.notNull(scopeName, "Scope identifier must not be null");
        return this.scopes.get(scopeName);
    }

    public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
        this.securityContextProvider = securityProvider;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void setApplicationStartup(ApplicationStartup applicationStartup) {
        Assert.notNull(applicationStartup, "applicationStartup should not be null");
        this.applicationStartup = applicationStartup;
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public ApplicationStartup getApplicationStartup() {
        return this.applicationStartup;
    }

    @Override // org.springframework.beans.factory.support.FactoryBeanRegistrySupport, org.springframework.beans.factory.config.ConfigurableBeanFactory
    public AccessControlContext getAccessControlContext() {
        if (this.securityContextProvider != null) {
            return this.securityContextProvider.getAccessControlContext();
        }
        return AccessController.getContext();
    }

    public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
        Assert.notNull(otherFactory, "BeanFactory must not be null");
        setBeanClassLoader(otherFactory.getBeanClassLoader());
        setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
        setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
        setConversionService(otherFactory.getConversionService());
        if (otherFactory instanceof AbstractBeanFactory) {
            AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory) otherFactory;
            this.propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
            this.customEditors.putAll(otherAbstractFactory.customEditors);
            this.typeConverter = otherAbstractFactory.typeConverter;
            this.beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
            this.scopes.putAll(otherAbstractFactory.scopes);
            this.securityContextProvider = otherAbstractFactory.securityContextProvider;
            return;
        }
        setTypeConverter(otherFactory.getTypeConverter());
        String[] otherScopeNames = otherFactory.getRegisteredScopeNames();
        for (String scopeName : otherScopeNames) {
            this.scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
        String beanName = transformedBeanName(name);
        if (!containsBeanDefinition(beanName) && (getParentBeanFactory() instanceof ConfigurableBeanFactory)) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
        }
        return getMergedLocalBeanDefinition(beanName);
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
        String beanName = transformedBeanName(name);
        Object beanInstance = getSingleton(beanName, false);
        if (beanInstance != null) {
            return beanInstance instanceof FactoryBean;
        }
        if (!containsBeanDefinition(beanName) && (getParentBeanFactory() instanceof ConfigurableBeanFactory)) {
            return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
        }
        return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
    }

    @Override // org.springframework.beans.factory.support.DefaultSingletonBeanRegistry
    public boolean isActuallyInCreation(String beanName) {
        return isSingletonCurrentlyInCreation(beanName) || isPrototypeCurrentlyInCreation(beanName);
    }

    protected boolean isPrototypeCurrentlyInCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        return curVal != null && (curVal.equals(beanName) || ((curVal instanceof Set) && ((Set) curVal).contains(beanName)));
    }

    protected void beforePrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal == null) {
            this.prototypesCurrentlyInCreation.set(beanName);
            return;
        }
        if (curVal instanceof String) {
            Set<String> beanNameSet = new HashSet<>(2);
            beanNameSet.add((String) curVal);
            beanNameSet.add(beanName);
            this.prototypesCurrentlyInCreation.set(beanNameSet);
            return;
        }
        ((Set) curVal).add(beanName);
    }

    protected void afterPrototypeCreation(String beanName) {
        Object curVal = this.prototypesCurrentlyInCreation.get();
        if (curVal instanceof String) {
            this.prototypesCurrentlyInCreation.remove();
            return;
        }
        if (curVal instanceof Set) {
            Set<String> beanNameSet = (Set) curVal;
            beanNameSet.remove(beanName);
            if (beanNameSet.isEmpty()) {
                this.prototypesCurrentlyInCreation.remove();
            }
        }
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void destroyBean(String beanName, Object beanInstance) {
        destroyBean(beanName, beanInstance, getMergedLocalBeanDefinition(beanName));
    }

    protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
        new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, getAccessControlContext()).destroy();
    }

    @Override // org.springframework.beans.factory.config.ConfigurableBeanFactory
    public void destroyScopedBean(String beanName) throws BeansException {
        RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
        if (mbd.isSingleton() || mbd.isPrototype()) {
            throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
        }
        String scopeName = mbd.getScope();
        Scope scope = this.scopes.get(scopeName);
        if (scope == null) {
            throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
        }
        Object bean = scope.remove(beanName);
        if (bean != null) {
            destroyBean(beanName, bean, mbd);
        }
    }

    protected String transformedBeanName(String name) {
        return canonicalName(BeanFactoryUtils.transformedBeanName(name));
    }

    protected String originalBeanName(String name) {
        String beanName = transformedBeanName(name);
        if (name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
            beanName = BeanFactory.FACTORY_BEAN_PREFIX + beanName;
        }
        return beanName;
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        bw.setConversionService(getConversionService());
        registerCustomEditors(bw);
    }

    protected void registerCustomEditors(PropertyEditorRegistry registry) {
        if (registry instanceof PropertyEditorRegistrySupport) {
            ((PropertyEditorRegistrySupport) registry).useConfigValueEditors();
        }
        if (!this.propertyEditorRegistrars.isEmpty()) {
            for (PropertyEditorRegistrar registrar : this.propertyEditorRegistrars) {
                try {
                    registrar.registerCustomEditors(registry);
                } catch (BeanCreationException ex) {
                    Throwable rootCause = ex.getMostSpecificCause();
                    if (rootCause instanceof BeanCurrentlyInCreationException) {
                        BeanCreationException bce = (BeanCreationException) rootCause;
                        String bceBeanName = bce.getBeanName();
                        if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
                            if (this.logger.isDebugEnabled()) {
                                this.logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + ex.getBeanName() + "': " + ex.getMessage());
                            }
                            onSuppressedException(ex);
                        }
                    }
                    throw ex;
                }
            }
        }
        if (!this.customEditors.isEmpty()) {
            this.customEditors.forEach((requiredType, editorClass) -> {
                registry.registerCustomEditor(requiredType, (PropertyEditor) BeanUtils.instantiateClass(editorClass));
            });
        }
    }

    protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
        RootBeanDefinition mbd = this.mergedBeanDefinitions.get(beanName);
        if (mbd != null && !mbd.stale) {
            return mbd;
        }
        return getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
    }

    protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
        return getMergedBeanDefinition(beanName, bd, null);
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x003b A[Catch: all -> 0x016b, TryCatch #1 {, blocks: (B:6:0x0012, B:9:0x0026, B:47:0x015d, B:49:0x0169, B:11:0x002e, B:13:0x003b, B:15:0x0042, B:30:0x010e, B:32:0x0119, B:35:0x0124, B:37:0x012d, B:39:0x0135, B:42:0x0144, B:44:0x014b, B:16:0x004e, B:17:0x005b, B:19:0x0070, B:29:0x00fd, B:20:0x007b, B:22:0x0089, B:23:0x009a, B:24:0x00c8, B:27:0x00ce, B:28:0x00fc), top: B:57:0x0012, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:32:0x0119 A[Catch: all -> 0x016b, TryCatch #1 {, blocks: (B:6:0x0012, B:9:0x0026, B:47:0x015d, B:49:0x0169, B:11:0x002e, B:13:0x003b, B:15:0x0042, B:30:0x010e, B:32:0x0119, B:35:0x0124, B:37:0x012d, B:39:0x0135, B:42:0x0144, B:44:0x014b, B:16:0x004e, B:17:0x005b, B:19:0x0070, B:29:0x00fd, B:20:0x007b, B:22:0x0089, B:23:0x009a, B:24:0x00c8, B:27:0x00ce, B:28:0x00fc), top: B:57:0x0012, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:47:0x015d A[Catch: all -> 0x016b, TryCatch #1 {, blocks: (B:6:0x0012, B:9:0x0026, B:47:0x015d, B:49:0x0169, B:11:0x002e, B:13:0x003b, B:15:0x0042, B:30:0x010e, B:32:0x0119, B:35:0x0124, B:37:0x012d, B:39:0x0135, B:42:0x0144, B:44:0x014b, B:16:0x004e, B:17:0x005b, B:19:0x0070, B:29:0x00fd, B:20:0x007b, B:22:0x0089, B:23:0x009a, B:24:0x00c8, B:27:0x00ce, B:28:0x00fc), top: B:57:0x0012, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:56:0x005b A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected org.springframework.beans.factory.support.RootBeanDefinition getMergedBeanDefinition(java.lang.String r8, org.springframework.beans.factory.config.BeanDefinition r9, @org.springframework.lang.Nullable org.springframework.beans.factory.config.BeanDefinition r10) throws org.springframework.beans.factory.BeanDefinitionStoreException {
        /*
            Method dump skipped, instructions count: 371
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.springframework.beans.factory.support.AbstractBeanFactory.getMergedBeanDefinition(java.lang.String, org.springframework.beans.factory.config.BeanDefinition, org.springframework.beans.factory.config.BeanDefinition):org.springframework.beans.factory.support.RootBeanDefinition");
    }

    private void copyRelevantMergedBeanDefinitionCaches(RootBeanDefinition previous, RootBeanDefinition mbd) {
        if (ObjectUtils.nullSafeEquals(mbd.getBeanClassName(), previous.getBeanClassName()) && ObjectUtils.nullSafeEquals(mbd.getFactoryBeanName(), previous.getFactoryBeanName()) && ObjectUtils.nullSafeEquals(mbd.getFactoryMethodName(), previous.getFactoryMethodName())) {
            ResolvableType targetType = mbd.targetType;
            ResolvableType previousTargetType = previous.targetType;
            if (targetType == null || targetType.equals(previousTargetType)) {
                mbd.targetType = previousTargetType;
                mbd.isFactoryBean = previous.isFactoryBean;
                mbd.resolvedTargetType = previous.resolvedTargetType;
                mbd.factoryMethodReturnType = previous.factoryMethodReturnType;
                mbd.factoryMethodToIntrospect = previous.factoryMethodToIntrospect;
            }
        }
    }

    protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args) throws BeanDefinitionStoreException {
        if (mbd.isAbstract()) {
            throw new BeanIsAbstractException(beanName);
        }
    }

    protected void clearMergedBeanDefinition(String beanName) {
        RootBeanDefinition bd = this.mergedBeanDefinitions.get(beanName);
        if (bd != null) {
            bd.stale = true;
        }
    }

    public void clearMetadataCache() {
        this.mergedBeanDefinitions.forEach((beanName, bd) -> {
            if (!isBeanEligibleForMetadataCaching(beanName)) {
                bd.stale = true;
            }
        });
    }

    @Nullable
    protected Class<?> resolveBeanClass(RootBeanDefinition mbd, String beanName, Class<?>... typesToMatch) throws CannotLoadBeanClassException {
        try {
            if (mbd.hasBeanClass()) {
                return mbd.getBeanClass();
            }
            if (System.getSecurityManager() != null) {
                return (Class) AccessController.doPrivileged(() -> {
                    return doResolveBeanClass(mbd, typesToMatch);
                }, getAccessControlContext());
            }
            return doResolveBeanClass(mbd, typesToMatch);
        } catch (ClassNotFoundException ex) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
        } catch (LinkageError err) {
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
        } catch (PrivilegedActionException pae) {
            ClassNotFoundException ex2 = (ClassNotFoundException) pae.getException();
            throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex2);
        }
    }

    @Nullable
    private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch) throws ClassNotFoundException {
        ClassLoader tempClassLoader;
        ClassLoader beanClassLoader = getBeanClassLoader();
        ClassLoader dynamicLoader = beanClassLoader;
        boolean freshResolve = false;
        if (!ObjectUtils.isEmpty((Object[]) typesToMatch) && (tempClassLoader = getTempClassLoader()) != null) {
            dynamicLoader = tempClassLoader;
            freshResolve = true;
            if (tempClassLoader instanceof DecoratingClassLoader) {
                DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
                for (Class<?> typeToMatch : typesToMatch) {
                    dcl.excludeClass(typeToMatch.getName());
                }
            }
        }
        String className = mbd.getBeanClassName();
        if (className != null) {
            Object evaluated = evaluateBeanDefinitionString(className, mbd);
            if (!className.equals(evaluated)) {
                if (evaluated instanceof Class) {
                    return (Class) evaluated;
                }
                if (evaluated instanceof String) {
                    className = (String) evaluated;
                    freshResolve = true;
                } else {
                    throw new IllegalStateException("Invalid class name expression result: " + evaluated);
                }
            }
            if (freshResolve) {
                if (dynamicLoader != null) {
                    try {
                        return dynamicLoader.loadClass(className);
                    } catch (ClassNotFoundException ex) {
                        if (this.logger.isTraceEnabled()) {
                            this.logger.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
                        }
                    }
                }
                return ClassUtils.forName(className, dynamicLoader);
            }
        }
        return mbd.resolveBeanClass(beanClassLoader);
    }

    @Nullable
    protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
        String scopeName;
        if (this.beanExpressionResolver == null) {
            return value;
        }
        Scope scope = null;
        if (beanDefinition != null && (scopeName = beanDefinition.getScope()) != null) {
            scope = getRegisteredScope(scopeName);
        }
        return this.beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
    }

    @Nullable
    protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
        Class<?> targetType = mbd.getTargetType();
        if (targetType != null) {
            return targetType;
        }
        if (mbd.getFactoryMethodName() != null) {
            return null;
        }
        return resolveBeanClass(mbd, beanName, typesToMatch);
    }

    protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
        Boolean result = mbd.isFactoryBean;
        if (result == null) {
            Class<?> beanType = predictBeanType(beanName, mbd, FactoryBean.class);
            result = Boolean.valueOf(beanType != null && FactoryBean.class.isAssignableFrom(beanType));
            mbd.isFactoryBean = result;
        }
        return result.booleanValue();
    }

    protected ResolvableType getTypeForFactoryBean(String beanName, RootBeanDefinition mbd, boolean allowInit) {
        ResolvableType result = getTypeForFactoryBeanFromAttributes(mbd);
        if (result != ResolvableType.NONE) {
            return result;
        }
        if (allowInit && mbd.isSingleton()) {
            try {
                FactoryBean<?> factoryBean = (FactoryBean) doGetBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName, FactoryBean.class, null, true);
                Class<?> objectType = getTypeForFactoryBean(factoryBean);
                return objectType != null ? ResolvableType.forClass(objectType) : ResolvableType.NONE;
            } catch (BeanCreationException ex) {
                if (ex.contains(BeanCurrentlyInCreationException.class)) {
                    this.logger.trace(LogMessage.format("Bean currently in creation on FactoryBean type check: %s", ex));
                } else if (mbd.isLazyInit()) {
                    this.logger.trace(LogMessage.format("Bean creation exception on lazy FactoryBean type check: %s", ex));
                } else {
                    this.logger.debug(LogMessage.format("Bean creation exception on eager FactoryBean type check: %s", ex));
                }
                onSuppressedException(ex);
            }
        }
        return ResolvableType.NONE;
    }

    ResolvableType getTypeForFactoryBeanFromAttributes(AttributeAccessor attributes) {
        Object attribute = attributes.getAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE);
        if (attribute instanceof ResolvableType) {
            return (ResolvableType) attribute;
        }
        if (attribute instanceof Class) {
            return ResolvableType.forClass((Class) attribute);
        }
        return ResolvableType.NONE;
    }

    @Nullable
    @Deprecated
    protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
        return getTypeForFactoryBean(beanName, mbd, true).resolve();
    }

    protected void markBeanAsCreated(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            synchronized (this.mergedBeanDefinitions) {
                if (!this.alreadyCreated.contains(beanName)) {
                    clearMergedBeanDefinition(beanName);
                    this.alreadyCreated.add(beanName);
                }
            }
        }
    }

    protected void cleanupAfterBeanCreationFailure(String beanName) {
        synchronized (this.mergedBeanDefinitions) {
            this.alreadyCreated.remove(beanName);
        }
    }

    protected boolean isBeanEligibleForMetadataCaching(String beanName) {
        return this.alreadyCreated.contains(beanName);
    }

    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        if (!this.alreadyCreated.contains(beanName)) {
            removeSingleton(beanName);
            return true;
        }
        return false;
    }

    protected boolean hasBeanCreationStarted() {
        return !this.alreadyCreated.isEmpty();
    }

    protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) throws BeansException {
        if (BeanFactoryUtils.isFactoryDereference(name)) {
            if (beanInstance instanceof NullBean) {
                return beanInstance;
            }
            if (!(beanInstance instanceof FactoryBean)) {
                throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
            }
            if (mbd != null) {
                mbd.isFactoryBean = true;
            }
            return beanInstance;
        }
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }
        Object object = null;
        if (mbd != null) {
            mbd.isFactoryBean = true;
        } else {
            object = getCachedObjectForFactoryBean(beanName);
        }
        if (object == null) {
            FactoryBean<?> factory = (FactoryBean) beanInstance;
            if (mbd == null && containsBeanDefinition(beanName)) {
                mbd = getMergedLocalBeanDefinition(beanName);
            }
            boolean synthetic = mbd != null && mbd.isSynthetic();
            object = getObjectFromFactoryBean(factory, beanName, !synthetic);
        }
        return object;
    }

    public boolean isBeanNameInUse(String beanName) {
        return isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName);
    }

    protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
        return bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd) || (hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(bean, getBeanPostProcessorCache().destructionAware)));
    }

    protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
        AccessControlContext acc = System.getSecurityManager() != null ? getAccessControlContext() : null;
        if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
            if (mbd.isSingleton()) {
                registerDisposableBean(beanName, new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
                return;
            }
            Scope scope = this.scopes.get(mbd.getScope());
            if (scope == null) {
                throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
            }
            scope.registerDestructionCallback(beanName, new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessorCache().destructionAware, acc));
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/AbstractBeanFactory$BeanPostProcessorCacheAwareList.class */
    private class BeanPostProcessorCacheAwareList extends CopyOnWriteArrayList<BeanPostProcessor> {
        private BeanPostProcessorCacheAwareList() {
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List
        public BeanPostProcessor set(int index, BeanPostProcessor element) {
            BeanPostProcessor result = (BeanPostProcessor) super.set(index, (int) element);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return result;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List, java.util.Collection
        public boolean add(BeanPostProcessor o) {
            boolean success = super.add((BeanPostProcessorCacheAwareList) o);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List
        public void add(int index, BeanPostProcessor element) {
            super.add(index, (int) element);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List
        public BeanPostProcessor remove(int index) {
            BeanPostProcessor result = (BeanPostProcessor) super.remove(index);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
            return result;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List, java.util.Collection
        public boolean remove(Object o) {
            boolean success = super.remove(o);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List, java.util.Collection
        public boolean removeAll(Collection<?> c) {
            boolean success = super.removeAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List, java.util.Collection
        public boolean retainAll(Collection<?> c) {
            boolean success = super.retainAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List, java.util.Collection
        public boolean addAll(Collection<? extends BeanPostProcessor> c) {
            boolean success = super.addAll(c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List
        public boolean addAll(int index, Collection<? extends BeanPostProcessor> c) {
            boolean success = super.addAll(index, c);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.Collection
        public boolean removeIf(Predicate<? super BeanPostProcessor> filter) {
            boolean success = super.removeIf(filter);
            if (success) {
                AbstractBeanFactory.this.resetBeanPostProcessorCache();
            }
            return success;
        }

        @Override // java.util.concurrent.CopyOnWriteArrayList, java.util.List
        public void replaceAll(UnaryOperator<BeanPostProcessor> operator) {
            super.replaceAll(operator);
            AbstractBeanFactory.this.resetBeanPostProcessorCache();
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-beans-5.3.27.jar:org/springframework/beans/factory/support/AbstractBeanFactory$BeanPostProcessorCache.class */
    static class BeanPostProcessorCache {
        final List<InstantiationAwareBeanPostProcessor> instantiationAware = new ArrayList();
        final List<SmartInstantiationAwareBeanPostProcessor> smartInstantiationAware = new ArrayList();
        final List<DestructionAwareBeanPostProcessor> destructionAware = new ArrayList();
        final List<MergedBeanDefinitionPostProcessor> mergedDefinition = new ArrayList();

        BeanPostProcessorCache() {
        }
    }
}
