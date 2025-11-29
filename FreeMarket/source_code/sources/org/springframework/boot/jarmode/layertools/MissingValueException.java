package org.springframework.boot.jarmode.layertools;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-jarmode-layertools-2.7.12.jar:org/springframework/boot/jarmode/layertools/MissingValueException.class */
class MissingValueException extends RuntimeException {
    private final String optionName;

    MissingValueException(String optionName) {
        this.optionName = optionName;
    }

    @Override // java.lang.Throwable
    public String getMessage() {
        return "--" + this.optionName;
    }
}
