package freemarker.cache;

import freemarker.template.MalformedTemplateNameException;
import freemarker.template.utility.StringUtil;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateNameFormat.class */
public abstract class TemplateNameFormat {
    public static final TemplateNameFormat DEFAULT_2_3_0 = new Default020300();
    public static final TemplateNameFormat DEFAULT_2_4_0 = new Default020400();

    abstract String toRootBasedName(String str, String str2) throws MalformedTemplateNameException;

    abstract String normalizeRootBasedName(String str) throws MalformedTemplateNameException;

    abstract String rootBasedNameToAbsoluteName(String str) throws MalformedTemplateNameException;

    private TemplateNameFormat() {
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateNameFormat$Default020300.class */
    private static final class Default020300 extends TemplateNameFormat {
        private Default020300() {
            super();
        }

        @Override // freemarker.cache.TemplateNameFormat
        String toRootBasedName(String baseName, String targetName) {
            if (targetName.indexOf("://") > 0) {
                return targetName;
            }
            if (targetName.startsWith("/")) {
                int schemeSepIdx = baseName.indexOf("://");
                if (schemeSepIdx > 0) {
                    return baseName.substring(0, schemeSepIdx + 2) + targetName;
                }
                return targetName.substring(1);
            }
            if (!baseName.endsWith("/")) {
                baseName = baseName.substring(0, baseName.lastIndexOf("/") + 1);
            }
            return baseName + targetName;
        }

        @Override // freemarker.cache.TemplateNameFormat
        String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
            TemplateNameFormat.checkNameHasNoNullCharacter(name);
            String str = name;
            while (true) {
                String path = str;
                int parentDirPathLoc = path.indexOf("/../");
                if (parentDirPathLoc == 0) {
                    throw TemplateNameFormat.newRootLeavingException(name);
                }
                if (parentDirPathLoc == -1) {
                    if (path.startsWith("../")) {
                        throw TemplateNameFormat.newRootLeavingException(name);
                    }
                    while (true) {
                        int currentDirPathLoc = path.indexOf("/./");
                        if (currentDirPathLoc == -1) {
                            break;
                        }
                        path = path.substring(0, currentDirPathLoc) + path.substring((currentDirPathLoc + "/./".length()) - 1);
                    }
                    if (path.startsWith("./")) {
                        path = path.substring("./".length());
                    }
                    if (path.length() > 1 && path.charAt(0) == '/') {
                        path = path.substring(1);
                    }
                    return path;
                }
                int previousSlashLoc = path.lastIndexOf(47, parentDirPathLoc - 1);
                str = path.substring(0, previousSlashLoc + 1) + path.substring(parentDirPathLoc + "/../".length());
            }
        }

        @Override // freemarker.cache.TemplateNameFormat
        String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
            if (name.indexOf("://") > 0) {
                return name;
            }
            if (!name.startsWith("/")) {
                return "/" + name;
            }
            return name;
        }

        public String toString() {
            return "TemplateNameFormat.DEFAULT_2_3_0";
        }
    }

    /* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/freemarker-2.3.33.jar:freemarker/cache/TemplateNameFormat$Default020400.class */
    private static final class Default020400 extends TemplateNameFormat {
        private Default020400() {
            super();
        }

        @Override // freemarker.cache.TemplateNameFormat
        String toRootBasedName(String baseName, String targetName) {
            if (findSchemeSectionEnd(targetName) != 0) {
                return targetName;
            }
            if (targetName.startsWith("/")) {
                String targetNameAsRelative = targetName.substring(1);
                int schemeSectionEnd = findSchemeSectionEnd(baseName);
                if (schemeSectionEnd == 0) {
                    return targetNameAsRelative;
                }
                return baseName.substring(0, schemeSectionEnd) + targetNameAsRelative;
            }
            if (!baseName.endsWith("/")) {
                int baseEnd = baseName.lastIndexOf("/") + 1;
                if (baseEnd == 0) {
                    baseEnd = findSchemeSectionEnd(baseName);
                }
                baseName = baseName.substring(0, baseEnd);
            }
            return baseName + targetName;
        }

        @Override // freemarker.cache.TemplateNameFormat
        String normalizeRootBasedName(String name) throws MalformedTemplateNameException {
            String scheme;
            String path;
            TemplateNameFormat.checkNameHasNoNullCharacter(name);
            if (name.indexOf(92) != -1) {
                throw new MalformedTemplateNameException(name, "Backslash (\"\\\") is not allowed in template names. Use slash (\"/\") instead.");
            }
            int schemeSectionEnd = findSchemeSectionEnd(name);
            if (schemeSectionEnd == 0) {
                scheme = null;
                path = name;
            } else {
                scheme = name.substring(0, schemeSectionEnd);
                path = name.substring(schemeSectionEnd);
            }
            if (path.indexOf(58) != -1) {
                throw new MalformedTemplateNameException(name, "The ':' character can only be used after the scheme name (if there's any), not in the path part");
            }
            String path2 = removeRedundantStarSteps(resolveDotDotSteps(removeDotSteps(removeRedundantSlashes(path)), name));
            return scheme == null ? path2 : scheme + path2;
        }

        private int findSchemeSectionEnd(String name) {
            int schemeColonIdx = name.indexOf(":");
            if (schemeColonIdx == -1 || name.lastIndexOf(47, schemeColonIdx - 1) != -1) {
                return 0;
            }
            if (schemeColonIdx + 2 < name.length() && name.charAt(schemeColonIdx + 1) == '/' && name.charAt(schemeColonIdx + 2) == '/') {
                return schemeColonIdx + 3;
            }
            return schemeColonIdx + 1;
        }

        private String removeRedundantSlashes(String path) {
            String prevName;
            do {
                prevName = path;
                path = StringUtil.replace(path, "//", "/");
            } while (prevName != path);
            return path.startsWith("/") ? path.substring(1) : path;
        }

        private String removeDotSteps(String path) {
            boolean slashRight;
            int nextFromIdx = path.length() - 1;
            while (true) {
                int dotIdx = path.lastIndexOf(46, nextFromIdx);
                if (dotIdx < 0) {
                    return path;
                }
                nextFromIdx = dotIdx - 1;
                if (dotIdx == 0 || path.charAt(dotIdx - 1) == '/') {
                    if (dotIdx + 1 == path.length()) {
                        slashRight = false;
                    } else if (path.charAt(dotIdx + 1) == '/') {
                        slashRight = true;
                    }
                    if (slashRight) {
                        path = path.substring(0, dotIdx) + path.substring(dotIdx + 2);
                    } else {
                        path = path.substring(0, path.length() - 1);
                    }
                }
            }
        }

        /* JADX WARN: Code restructure failed: missing block: B:39:0x00bb, code lost:
        
            r0 = new java.lang.StringBuilder().append(r7.substring(0, r0 + 1));
         */
        /* JADX WARN: Code restructure failed: missing block: B:40:0x00d0, code lost:
        
            if (r13 == false) goto L42;
         */
        /* JADX WARN: Code restructure failed: missing block: B:41:0x00d3, code lost:
        
            r1 = org.springframework.util.ResourceUtils.WAR_URL_SEPARATOR;
         */
        /* JADX WARN: Code restructure failed: missing block: B:42:0x00d8, code lost:
        
            r1 = "";
         */
        /* JADX WARN: Code restructure failed: missing block: B:43:0x00da, code lost:
        
            r0 = r0.append(r1);
            r1 = r7;
         */
        /* JADX WARN: Code restructure failed: missing block: B:44:0x00e2, code lost:
        
            if (r11 == false) goto L46;
         */
        /* JADX WARN: Code restructure failed: missing block: B:45:0x00e5, code lost:
        
            r3 = 3;
         */
        /* JADX WARN: Code restructure failed: missing block: B:46:0x00e9, code lost:
        
            r3 = 2;
         */
        /* JADX WARN: Code restructure failed: missing block: B:47:0x00ea, code lost:
        
            r7 = r0.append(r1.substring(r0 + r3)).toString();
            r0 = r0 + 1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private java.lang.String resolveDotDotSteps(java.lang.String r7, java.lang.String r8) throws freemarker.template.MalformedTemplateNameException {
            /*
                Method dump skipped, instructions count: 253
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: freemarker.cache.TemplateNameFormat.Default020400.resolveDotDotSteps(java.lang.String, java.lang.String):java.lang.String");
        }

        private String removeRedundantStarSteps(String path) {
            String prevName;
            do {
                int supiciousIdx = path.indexOf("*/*");
                if (supiciousIdx == -1) {
                    break;
                }
                prevName = path;
                if ((supiciousIdx == 0 || path.charAt(supiciousIdx - 1) == '/') && (supiciousIdx + 3 == path.length() || path.charAt(supiciousIdx + 3) == '/')) {
                    path = path.substring(0, supiciousIdx) + path.substring(supiciousIdx + 2);
                }
            } while (prevName != path);
            if (path.startsWith("*")) {
                if (path.length() == 1) {
                    path = "";
                } else if (path.charAt(1) == '/') {
                    path = path.substring(2);
                }
            }
            return path;
        }

        @Override // freemarker.cache.TemplateNameFormat
        String rootBasedNameToAbsoluteName(String name) throws MalformedTemplateNameException {
            if (findSchemeSectionEnd(name) != 0) {
                return name;
            }
            if (!name.startsWith("/")) {
                return "/" + name;
            }
            return name;
        }

        public String toString() {
            return "TemplateNameFormat.DEFAULT_2_4_0";
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkNameHasNoNullCharacter(String name) throws MalformedTemplateNameException {
        if (name.indexOf(0) != -1) {
            throw new MalformedTemplateNameException(name, "Null character (\\u0000) in the name; possible attack attempt");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static MalformedTemplateNameException newRootLeavingException(String name) {
        return new MalformedTemplateNameException(name, "Backing out from the root directory is not allowed");
    }
}
