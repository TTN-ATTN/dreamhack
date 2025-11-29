package org.springframework.web.servlet.mvc.condition;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-webmvc-5.3.27.jar:org/springframework/web/servlet/mvc/condition/AbstractMediaTypeExpression.class */
abstract class AbstractMediaTypeExpression implements MediaTypeExpression, Comparable<AbstractMediaTypeExpression> {
    private final MediaType mediaType;
    private final boolean isNegated;

    AbstractMediaTypeExpression(String expression) {
        if (expression.startsWith("!")) {
            this.isNegated = true;
            expression = expression.substring(1);
        } else {
            this.isNegated = false;
        }
        this.mediaType = MediaType.parseMediaType(expression);
    }

    AbstractMediaTypeExpression(MediaType mediaType, boolean negated) {
        this.mediaType = mediaType;
        this.isNegated = negated;
    }

    @Override // org.springframework.web.servlet.mvc.condition.MediaTypeExpression
    public MediaType getMediaType() {
        return this.mediaType;
    }

    @Override // org.springframework.web.servlet.mvc.condition.MediaTypeExpression
    public boolean isNegated() {
        return this.isNegated;
    }

    @Override // java.lang.Comparable
    public int compareTo(AbstractMediaTypeExpression other) {
        return MediaType.SPECIFICITY_COMPARATOR.compare(getMediaType(), other.getMediaType());
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        AbstractMediaTypeExpression otherExpr = (AbstractMediaTypeExpression) other;
        return this.mediaType.equals(otherExpr.mediaType) && this.isNegated == otherExpr.isNegated;
    }

    public int hashCode() {
        return this.mediaType.hashCode();
    }

    public String toString() {
        if (this.isNegated) {
            return '!' + this.mediaType.toString();
        }
        return this.mediaType.toString();
    }
}
