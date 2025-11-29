package org.springframework.core.env;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/ProfilesParser.class */
final class ProfilesParser {

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/ProfilesParser$Context.class */
    private enum Context {
        NONE,
        INVERT,
        BRACKET
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/ProfilesParser$Operator.class */
    private enum Operator {
        AND,
        OR
    }

    private ProfilesParser() {
    }

    static Profiles parse(String... expressions) {
        Assert.notEmpty(expressions, "Must specify at least one profile");
        Profiles[] parsed = new Profiles[expressions.length];
        for (int i = 0; i < expressions.length; i++) {
            parsed[i] = parseExpression(expressions[i]);
        }
        return new ParsedProfiles(expressions, parsed);
    }

    private static Profiles parseExpression(String expression) {
        Assert.hasText(expression, (Supplier<String>) () -> {
            return "Invalid profile expression [" + expression + "]: must contain text";
        });
        StringTokenizer tokens = new StringTokenizer(expression, "()&|!", true);
        return parseTokens(expression, tokens);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens) {
        return parseTokens(expression, tokens, Context.NONE);
    }

    private static Profiles parseTokens(String expression, StringTokenizer tokens, Context context) {
        List<Profiles> elements = new ArrayList<>();
        Operator operator = null;
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken().trim();
            if (!token.isEmpty()) {
                switch (token) {
                    case "(":
                        Profiles contents = parseTokens(expression, tokens, Context.BRACKET);
                        if (context == Context.INVERT) {
                            return contents;
                        }
                        elements.add(contents);
                        break;
                    case "&":
                        assertWellFormed(expression, operator == null || operator == Operator.AND);
                        operator = Operator.AND;
                        break;
                    case "|":
                        assertWellFormed(expression, operator == null || operator == Operator.OR);
                        operator = Operator.OR;
                        break;
                    case "!":
                        elements.add(not(parseTokens(expression, tokens, Context.INVERT)));
                        break;
                    case ")":
                        Profiles merged = merge(expression, elements, operator);
                        if (context == Context.BRACKET) {
                            return merged;
                        }
                        elements.clear();
                        elements.add(merged);
                        operator = null;
                        break;
                    default:
                        Profiles value = equals(token);
                        if (context == Context.INVERT) {
                            return value;
                        }
                        elements.add(value);
                        break;
                }
            }
        }
        return merge(expression, elements, operator);
    }

    private static Profiles merge(String expression, List<Profiles> elements, @Nullable Operator operator) {
        assertWellFormed(expression, !elements.isEmpty());
        if (elements.size() == 1) {
            return elements.get(0);
        }
        Profiles[] profiles = (Profiles[]) elements.toArray(new Profiles[0]);
        return operator == Operator.AND ? and(profiles) : or(profiles);
    }

    private static void assertWellFormed(String expression, boolean wellFormed) {
        Assert.isTrue(wellFormed, (Supplier<String>) () -> {
            return "Malformed profile expression [" + expression + "]";
        });
    }

    private static Profiles or(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).anyMatch(isMatch(activeProfile));
        };
    }

    private static Profiles and(Profiles... profiles) {
        return activeProfile -> {
            return Arrays.stream(profiles).allMatch(isMatch(activeProfile));
        };
    }

    private static Profiles not(Profiles profiles) {
        return activeProfile -> {
            return !profiles.matches(activeProfile);
        };
    }

    private static Profiles equals(String profile) {
        return activeProfile -> {
            return activeProfile.test(profile);
        };
    }

    private static Predicate<Profiles> isMatch(Predicate<String> activeProfile) {
        return profiles -> {
            return profiles.matches(activeProfile);
        };
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-core-5.3.27.jar:org/springframework/core/env/ProfilesParser$ParsedProfiles.class */
    private static class ParsedProfiles implements Profiles {
        private final Set<String> expressions = new LinkedHashSet();
        private final Profiles[] parsed;

        ParsedProfiles(String[] expressions, Profiles[] parsed) {
            Collections.addAll(this.expressions, expressions);
            this.parsed = parsed;
        }

        @Override // org.springframework.core.env.Profiles
        public boolean matches(Predicate<String> activeProfiles) {
            for (Profiles candidate : this.parsed) {
                if (candidate.matches(activeProfiles)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return this.expressions.hashCode();
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ParsedProfiles that = (ParsedProfiles) obj;
            return this.expressions.equals(that.expressions);
        }

        public String toString() {
            return StringUtils.collectionToDelimitedString(this.expressions, " or ");
        }
    }
}
