package org.springframework.scheduling.support;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/scheduling/support/CronTrigger.class */
public class CronTrigger implements Trigger {
    private final CronExpression expression;
    private final ZoneId zoneId;

    public CronTrigger(String expression) {
        this(expression, ZoneId.systemDefault());
    }

    public CronTrigger(String expression, TimeZone timeZone) {
        this(expression, timeZone.toZoneId());
    }

    public CronTrigger(String expression, ZoneId zoneId) {
        Assert.hasLength(expression, "Expression must not be empty");
        Assert.notNull(zoneId, "ZoneId must not be null");
        this.expression = CronExpression.parse(expression);
        this.zoneId = zoneId;
    }

    public String getExpression() {
        return this.expression.toString();
    }

    @Override // org.springframework.scheduling.Trigger
    public Date nextExecutionTime(TriggerContext triggerContext) {
        Date date = triggerContext.lastCompletionTime();
        if (date != null) {
            Date scheduled = triggerContext.lastScheduledExecutionTime();
            if (scheduled != null && date.before(scheduled)) {
                date = scheduled;
            }
        } else {
            date = new Date(triggerContext.getClock().millis());
        }
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(date.toInstant(), this.zoneId);
        ZonedDateTime next = (ZonedDateTime) this.expression.next(dateTime);
        if (next != null) {
            return Date.from(next.toInstant());
        }
        return null;
    }

    public boolean equals(@Nullable Object other) {
        return this == other || ((other instanceof CronTrigger) && this.expression.equals(((CronTrigger) other).expression));
    }

    public int hashCode() {
        return this.expression.hashCode();
    }

    public String toString() {
        return this.expression.toString();
    }
}
