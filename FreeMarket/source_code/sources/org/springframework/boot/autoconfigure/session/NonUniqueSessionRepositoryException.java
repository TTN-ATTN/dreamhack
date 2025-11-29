package org.springframework.boot.autoconfigure.session;

import java.util.Collections;
import java.util.List;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/session/NonUniqueSessionRepositoryException.class */
public class NonUniqueSessionRepositoryException extends RuntimeException {
    private final List<Class<?>> availableCandidates;

    public NonUniqueSessionRepositoryException(List<Class<?>> availableCandidates) {
        super("Multiple session repository candidates are available, set the 'spring.session.store-type' property accordingly");
        this.availableCandidates = !ObjectUtils.isEmpty(availableCandidates) ? availableCandidates : Collections.emptyList();
    }

    public List<Class<?>> getAvailableCandidates() {
        return this.availableCandidates;
    }
}
