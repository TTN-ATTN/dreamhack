package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-context-5.3.27.jar:org/springframework/context/support/MessageSourceSupport.class */
public abstract class MessageSourceSupport {
    private static final MessageFormat INVALID_MESSAGE_FORMAT = new MessageFormat("");
    protected final Log logger = LogFactory.getLog(getClass());
    private boolean alwaysUseMessageFormat = false;
    private final Map<String, Map<Locale, MessageFormat>> messageFormatsPerMessage = new HashMap();

    public void setAlwaysUseMessageFormat(boolean alwaysUseMessageFormat) {
        this.alwaysUseMessageFormat = alwaysUseMessageFormat;
    }

    protected boolean isAlwaysUseMessageFormat() {
        return this.alwaysUseMessageFormat;
    }

    protected String renderDefaultMessage(String defaultMessage, @Nullable Object[] args, Locale locale) {
        return formatMessage(defaultMessage, args, locale);
    }

    protected String formatMessage(String msg, @Nullable Object[] args, Locale locale) {
        String str;
        if (!isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
            return msg;
        }
        MessageFormat messageFormat = null;
        synchronized (this.messageFormatsPerMessage) {
            Map<Locale, MessageFormat> messageFormatsPerLocale = this.messageFormatsPerMessage.get(msg);
            if (messageFormatsPerLocale != null) {
                messageFormat = messageFormatsPerLocale.get(locale);
            } else {
                messageFormatsPerLocale = new HashMap();
                this.messageFormatsPerMessage.put(msg, messageFormatsPerLocale);
            }
            if (messageFormat == null) {
                try {
                    messageFormat = createMessageFormat(msg, locale);
                } catch (IllegalArgumentException ex) {
                    if (isAlwaysUseMessageFormat()) {
                        throw ex;
                    }
                    messageFormat = INVALID_MESSAGE_FORMAT;
                }
                messageFormatsPerLocale.put(locale, messageFormat);
            }
        }
        if (messageFormat == INVALID_MESSAGE_FORMAT) {
            return msg;
        }
        synchronized (messageFormat) {
            str = messageFormat.format(resolveArguments(args, locale));
        }
        return str;
    }

    protected MessageFormat createMessageFormat(String msg, Locale locale) {
        return new MessageFormat(msg, locale);
    }

    protected Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        return args != null ? args : new Object[0];
    }
}
