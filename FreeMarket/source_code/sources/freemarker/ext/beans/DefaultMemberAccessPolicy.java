package freemarker.ext.beans;

import freemarker.ext.beans.MemberSelectorListMemberAccessPolicy;
import freemarker.template.Version;
import freemarker.template._TemplateAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/ext/beans/DefaultMemberAccessPolicy.class */
public final class DefaultMemberAccessPolicy implements MemberAccessPolicy {
    private static final DefaultMemberAccessPolicy INSTANCE = new DefaultMemberAccessPolicy();
    private final Set<Class<?>> whitelistRuleFinalClasses;
    private final Set<Class<?>> whitelistRuleNonFinalClasses;
    private final WhitelistMemberAccessPolicy whitelistMemberAccessPolicy;
    private final BlacklistMemberAccessPolicy blacklistMemberAccessPolicy;
    private final boolean toStringAlwaysExposed;

    public static DefaultMemberAccessPolicy getInstance(Version incompatibleImprovements) {
        _TemplateAPI.checkVersionNotNullAndSupported(incompatibleImprovements);
        return INSTANCE;
    }

    private DefaultMemberAccessPolicy() throws ClassNotFoundException, SecurityException {
        MemberSelectorListMemberAccessPolicy.MemberSelector memberSelector;
        Class<?> upperBoundType;
        Class<?> upperBoundType2;
        try {
            ClassLoader classLoader = DefaultMemberAccessPolicy.class.getClassLoader();
            this.whitelistRuleFinalClasses = new HashSet();
            this.whitelistRuleNonFinalClasses = new HashSet();
            Set<Class<?>> typesWithBlacklistUnlistedRule = new HashSet<>();
            List<MemberSelectorListMemberAccessPolicy.MemberSelector> whitelistMemberSelectors = new ArrayList<>();
            Iterator<String> it = loadMemberSelectorFileLines().iterator();
            while (it.hasNext()) {
                String line = it.next().trim();
                if (!MemberSelectorListMemberAccessPolicy.MemberSelector.isIgnoredLine(line)) {
                    if (line.startsWith("@")) {
                        String[] lineParts = line.split("\\s+");
                        if (lineParts.length != 2) {
                            throw new IllegalStateException("Malformed @ line: " + line);
                        }
                        String typeName = lineParts[1];
                        try {
                            upperBoundType2 = classLoader.loadClass(typeName);
                        } catch (ClassNotFoundException e) {
                            upperBoundType2 = null;
                        }
                        String rule = lineParts[0].substring(1);
                        if (rule.equals("whitelistPolicyIfAssignable")) {
                            if (upperBoundType2 != null) {
                                Set<Class<?>> targetSet = (upperBoundType2.getModifiers() & 16) != 0 ? this.whitelistRuleFinalClasses : this.whitelistRuleNonFinalClasses;
                                targetSet.add(upperBoundType2);
                            }
                        } else if (rule.equals("blacklistUnlistedMembers")) {
                            if (upperBoundType2 != null) {
                                typesWithBlacklistUnlistedRule.add(upperBoundType2);
                            }
                        } else {
                            throw new IllegalStateException("Unhandled rule: " + rule);
                        }
                    } else {
                        try {
                            memberSelector = MemberSelectorListMemberAccessPolicy.MemberSelector.parse(line, classLoader);
                        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException e2) {
                            memberSelector = null;
                        }
                        if (memberSelector != null && (upperBoundType = memberSelector.getUpperBoundType()) != null) {
                            if (!this.whitelistRuleFinalClasses.contains(upperBoundType) && !this.whitelistRuleNonFinalClasses.contains(upperBoundType) && !typesWithBlacklistUnlistedRule.contains(upperBoundType)) {
                                throw new IllegalStateException("Type without rule: " + upperBoundType.getName());
                            }
                            whitelistMemberSelectors.add(memberSelector);
                        }
                    }
                }
            }
            this.whitelistMemberAccessPolicy = new WhitelistMemberAccessPolicy(whitelistMemberSelectors);
            List<MemberSelectorListMemberAccessPolicy.MemberSelector> blacklistMemberSelectors = new ArrayList<>();
            for (Class<?> blacklistUnlistedRuleType : typesWithBlacklistUnlistedRule) {
                ClassMemberAccessPolicy classPolicy = this.whitelistMemberAccessPolicy.forClass(blacklistUnlistedRuleType);
                for (Method method : blacklistUnlistedRuleType.getMethods()) {
                    if (!classPolicy.isMethodExposed(method)) {
                        blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, method));
                    }
                }
                for (Constructor<?> constructor : blacklistUnlistedRuleType.getConstructors()) {
                    if (!classPolicy.isConstructorExposed(constructor)) {
                        blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, constructor));
                    }
                }
                for (Field field : blacklistUnlistedRuleType.getFields()) {
                    if (!classPolicy.isFieldExposed(field)) {
                        blacklistMemberSelectors.add(new MemberSelectorListMemberAccessPolicy.MemberSelector(blacklistUnlistedRuleType, field));
                    }
                }
            }
            this.blacklistMemberAccessPolicy = new BlacklistMemberAccessPolicy(blacklistMemberSelectors);
            this.toStringAlwaysExposed = this.whitelistMemberAccessPolicy.isToStringAlwaysExposed() && this.blacklistMemberAccessPolicy.isToStringAlwaysExposed();
        } catch (Exception e3) {
            throw new IllegalStateException("Couldn't init " + getClass().getName() + " instance", e3);
        }
    }

    private static List<String> loadMemberSelectorFileLines() throws IOException {
        List<String> whitelist = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(DefaultMemberAccessPolicy.class.getResourceAsStream("DefaultMemberAccessPolicy-rules"), "UTF-8"));
        Throwable th = null;
        while (true) {
            try {
                try {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    whitelist.add(line);
                } finally {
                }
            } catch (Throwable th2) {
                if (reader != null) {
                    if (th != null) {
                        try {
                            reader.close();
                        } catch (Throwable th3) {
                            th.addSuppressed(th3);
                        }
                    } else {
                        reader.close();
                    }
                }
                throw th2;
            }
        }
        if (reader != null) {
            if (0 != 0) {
                try {
                    reader.close();
                } catch (Throwable th4) {
                    th.addSuppressed(th4);
                }
            } else {
                reader.close();
            }
        }
        return whitelist;
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public ClassMemberAccessPolicy forClass(Class<?> contextClass) {
        if (isTypeWithWhitelistRule(contextClass)) {
            return this.whitelistMemberAccessPolicy.forClass(contextClass);
        }
        return this.blacklistMemberAccessPolicy.forClass(contextClass);
    }

    @Override // freemarker.ext.beans.MemberAccessPolicy
    public boolean isToStringAlwaysExposed() {
        return this.toStringAlwaysExposed;
    }

    private boolean isTypeWithWhitelistRule(Class<?> contextClass) {
        if (this.whitelistRuleFinalClasses.contains(contextClass)) {
            return true;
        }
        for (Class<?> nonFinalClass : this.whitelistRuleNonFinalClasses) {
            if (nonFinalClass.isAssignableFrom(contextClass)) {
                return true;
            }
        }
        return false;
    }
}
