package org.webbuilder.sql.exception;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public class TriggerException extends RuntimeException {
    public TriggerException(String message) {
        super(message);
    }

    public TriggerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TriggerException(Throwable cause) {
        super(cause);
    }

    public TriggerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
