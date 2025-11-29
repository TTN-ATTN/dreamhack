package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NullArgumentException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;
import org.springframework.beans.factory.xml.BeanDefinitionParserDelegate;
import org.springframework.util.ClassUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberSelectorListMemberAccessPolicy.class */
public abstract class MemberSelectorListMemberAccessPolicy implements MemberAccessPolicy {
    private final ListType listType;
    private final MethodMatcher methodMatcher = new MethodMatcher();
    private final ConstructorMatcher constructorMatcher = new ConstructorMatcher();
    private final FieldMatcher fieldMatcher = new FieldMatcher();
    private final Class<? extends Annotation> matchAnnotation;

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberSelectorListMemberAccessPolicy$ListType.class */
    enum ListType {
        WHITELIST,
        BLACKLIST
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/MemberSelectorListMemberAccessPolicy$MemberSelector.class */
    public static final class MemberSelector {
        private final Class<?> upperBoundType;
        private final Method method;
        private final Constructor<?> constructor;
        private final Field field;

        public MemberSelector(Class<?> upperBoundType, Method method) {
            NullArgumentException.check("upperBoundType", upperBoundType);
            NullArgumentException.check("method", method);
            this.upperBoundType = upperBoundType;
            this.method = method;
            this.constructor = null;
            this.field = null;
        }

        public MemberSelector(Class<?> upperBoundType, Constructor<?> constructor) {
            NullArgumentException.check("upperBoundType", upperBoundType);
            NullArgumentException.check(BeanDefinitionParserDelegate.AUTOWIRE_CONSTRUCTOR_VALUE, constructor);
            this.upperBoundType = upperBoundType;
            this.method = null;
            this.constructor = constructor;
            this.field = null;
        }

        public MemberSelector(Class<?> upperBoundType, Field field) {
            NullArgumentException.check("upperBoundType", upperBoundType);
            NullArgumentException.check("field", field);
            this.upperBoundType = upperBoundType;
            this.method = null;
            this.constructor = null;
            this.field = field;
        }

        public Class<?> getUpperBoundType() {
            return this.upperBoundType;
        }

        public Method getMethod() {
            return this.method;
        }

        public Constructor<?> getConstructor() {
            return this.constructor;
        }

        public Field getField() {
            return this.field;
        }

        public static MemberSelector parse(String memberSelectorString, ClassLoader classLoader) throws NoSuchFieldException, NoSuchMethodException, ClassNotFoundException {
            Class<?> clsLoadClass;
            if (memberSelectorString.contains("<") || memberSelectorString.contains(">") || memberSelectorString.contains("...") || memberSelectorString.contains(";")) {
                throw new IllegalArgumentException("Malformed whitelist entry (shouldn't contain \"<\", \">\", \"...\", or \";\"): " + memberSelectorString);
            }
            String cleanedStr = memberSelectorString.trim().replaceAll("\\s*([\\.,\\(\\)\\[\\]])\\s*", "$1");
            int openParenIdx = cleanedStr.indexOf(40);
            boolean hasArgList = openParenIdx != -1;
            int postMemberNameIdx = hasArgList ? openParenIdx : cleanedStr.length();
            int postClassDotIdx = cleanedStr.lastIndexOf(46, postMemberNameIdx);
            if (postClassDotIdx == -1) {
                throw new IllegalArgumentException("Malformed whitelist entry (missing dot): " + memberSelectorString);
            }
            String upperBoundClassStr = cleanedStr.substring(0, postClassDotIdx);
            if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(upperBoundClassStr)) {
                throw new IllegalArgumentException("Malformed whitelist entry (malformed upper bound class name): " + memberSelectorString);
            }
            Class<?> upperBoundClass = classLoader.loadClass(upperBoundClassStr);
            String memberName = cleanedStr.substring(postClassDotIdx + 1, postMemberNameIdx);
            if (!MemberSelectorListMemberAccessPolicy.isWellFormedJavaIdentifier(memberName)) {
                throw new IllegalArgumentException("Malformed whitelist entry (malformed member name): " + memberSelectorString);
            }
            if (hasArgList) {
                if (cleanedStr.charAt(cleanedStr.length() - 1) != ')') {
                    throw new IllegalArgumentException("Malformed whitelist entry (should end with ')'): " + memberSelectorString);
                }
                String argsSpec = cleanedStr.substring(postMemberNameIdx + 1, cleanedStr.length() - 1);
                StringTokenizer tok = new StringTokenizer(argsSpec, ",");
                int argCount = tok.countTokens();
                Class<?>[] argTypes = new Class[argCount];
                for (int i = 0; i < argCount; i++) {
                    String argClassName = tok.nextToken();
                    int arrayDimensions = 0;
                    while (argClassName.endsWith(ClassUtils.ARRAY_SUFFIX)) {
                        arrayDimensions++;
                        argClassName = argClassName.substring(0, argClassName.length() - 2);
                    }
                    Class<?> primArgClass = ClassUtil.resolveIfPrimitiveTypeName(argClassName);
                    if (primArgClass == null) {
                        if (!MemberSelectorListMemberAccessPolicy.isWellFormedClassName(argClassName)) {
                            throw new IllegalArgumentException("Malformed whitelist entry (malformed argument class name): " + memberSelectorString);
                        }
                        clsLoadClass = classLoader.loadClass(argClassName);
                    } else {
                        clsLoadClass = primArgClass;
                    }
                    Class<?> argClass = clsLoadClass;
                    argTypes[i] = ClassUtil.getArrayClass(argClass, arrayDimensions);
                }
                if (memberName.equals(upperBoundClass.getSimpleName())) {
                    return new MemberSelector(upperBoundClass, upperBoundClass.getConstructor(argTypes));
                }
                return new MemberSelector(upperBoundClass, upperBoundClass.getMethod(memberName, argTypes));
            }
            return new MemberSelector(upperBoundClass, upperBoundClass.getField(memberName));
        }

        public static List<MemberSelector> parse(Collection<String> memberSelectors, boolean ignoreMissingClassOrMember, ClassLoader classLoader) throws ReflectiveOperationException {
            List<MemberSelector> parsedMemberSelectors = new ArrayList<>(memberSelectors.size());
            for (String memberSelector : memberSelectors) {
                if (!isIgnoredLine(memberSelector)) {
                    try {
                        parsedMemberSelectors.add(parse(memberSelector, classLoader));
                    } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e) {
                        if (!ignoreMissingClassOrMember) {
                            throw e;
                        }
                    }
                }
            }
            return parsedMemberSelectors;
        }

        public static boolean isIgnoredLine(String line) {
            String trimmedLine = line.trim();
            return trimmedLine.length() == 0 || trimmedLine.startsWith("#") || trimmedLine.startsWith("//");
        }
    }

    MemberSelectorListMemberAccessPolicy(Collection<? extends MemberSelector> memberSelectors, ListType listType, Class<? extends Annotation> matchAnnotation) {
        this.listType = listType;
        this.matchAnnotation = matchAnnotation;
        for (MemberSelector memberSelector : memberSelectors) {
            Class<?> upperBoundClass = memberSelector.upperBoundType;
            if (memberSelector.constructor != null) {
                this.constructorMatcher.addMatching(upperBoundClass, memberSelector.constructor);
            } else if (memberSelector.method != null) {
                this.methodMatcher.addMatching(upperBoundClass, memberSelector.method);
            } else if (memberSelector.field != null) {
                this.fieldMatcher.addMatching(upperBoundClass, memberSelector.field);
            } else {
                throw new AssertionError();
            }
        }
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public final ClassMemberAccessPolicy forClass(final Class<?> contextClass) {
        return new ClassMemberAccessPolicy() { // from class: freemarker.ext.beans.MemberSelectorListMemberAccessPolicy.1
            @Override // freemarker.ext.beans.ClassMemberAccessPolicy
            public boolean isMethodExposed(Method method) {
                return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.methodMatcher.matches(contextClass, method) || !(MemberSelectorListMemberAccessPolicy.this.matchAnnotation == null || _MethodUtil.getInheritableAnnotation((Class<?>) contextClass, method, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) == null));
            }

            @Override // freemarker.ext.beans.ClassMemberAccessPolicy
            public boolean isConstructorExposed(Constructor<?> constructor) {
                return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.constructorMatcher.matches(contextClass, constructor) || !(MemberSelectorListMemberAccessPolicy.this.matchAnnotation == null || _MethodUtil.getInheritableAnnotation((Class<?>) contextClass, constructor, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) == null));
            }

            @Override // freemarker.ext.beans.ClassMemberAccessPolicy
            public boolean isFieldExposed(Field field) {
                return MemberSelectorListMemberAccessPolicy.this.matchResultToIsExposedResult(MemberSelectorListMemberAccessPolicy.this.fieldMatcher.matches(contextClass, field) || !(MemberSelectorListMemberAccessPolicy.this.matchAnnotation == null || _MethodUtil.getInheritableAnnotation((Class<?>) contextClass, field, MemberSelectorListMemberAccessPolicy.this.matchAnnotation) == null));
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean matchResultToIsExposedResult(boolean matches) {
        if (this.listType == ListType.WHITELIST) {
            return matches;
        }
        if (this.listType == ListType.BLACKLIST) {
            return !matches;
        }
        throw new AssertionError();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isWellFormedClassName(String s) {
        if (s.length() == 0) {
            return false;
        }
        int identifierStartIdx = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (i == identifierStartIdx) {
                if (!Character.isJavaIdentifierStart(c)) {
                    return false;
                }
            } else if (c == '.' && i != s.length() - 1) {
                identifierStartIdx = i + 1;
            } else if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isWellFormedJavaIdentifier(String s) {
        if (s.length() == 0 || !Character.isJavaIdentifierStart(s.charAt(0))) {
            return false;
        }
        for (int i = 1; i < s.length(); i++) {
            if (!Character.isJavaIdentifierPart(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
