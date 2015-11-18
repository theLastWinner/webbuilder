package org.webbuilder.sql.exception;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class CreateException extends SqlExeException {
    public CreateException(String message) {
        super(message);
    }

    public CreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreateException(Throwable cause) {
        super(cause);
    }

    public CreateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
