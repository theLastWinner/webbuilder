package org.webbuilder.sql.support.executor;

/**
 * Created by 浩 on 2015-11-09 0009.
 */
public class SqlExecutorException extends RuntimeException {
    public SqlExecutorException() {
    }

    public SqlExecutorException(String message) {
        super(message);
    }

    public SqlExecutorException(String message, Throwable cause) {
        super(message, cause);
    }

    public SqlExecutorException(Throwable cause) {
        super(cause);
    }

    public SqlExecutorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
