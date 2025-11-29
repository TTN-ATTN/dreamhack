package org.springframework.boot.ansi;

import org.springframework.beans.propertyeditors.CustomBooleanEditor;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-2.7.12.jar:org/springframework/boot/ansi/AnsiStyle.class */
public enum AnsiStyle implements AnsiElement {
    NORMAL(CustomBooleanEditor.VALUE_0),
    BOLD(CustomBooleanEditor.VALUE_1),
    FAINT("2"),
    ITALIC("3"),
    UNDERLINE("4");

    private final String code;

    AnsiStyle(String code) {
        this.code = code;
    }

    @Override // java.lang.Enum, org.springframework.boot.ansi.AnsiElement
    public String toString() {
        return this.code;
    }
}
