package org.webbuilder.sql.exception;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class SqlRenderException extends RuntimeException {
    public SqlRenderException(String message) {
        super(message);
    }

    public SqlRenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlRenderException(Throwable cause) {
        super(cause);
    }

    public SqlRenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
