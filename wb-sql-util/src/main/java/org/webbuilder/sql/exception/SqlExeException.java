package org.webbuilder.sql.exception;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class SqlExeException extends RuntimeException {
    public SqlExeException(String message) {
        super(message);
    }

    public SqlExeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlExeException(Throwable cause) {
        super(cause);
    }

    public SqlExeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
