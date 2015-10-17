package org.webbuilder.utils.db.exception;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public class DataValidException extends Exception {
    public DataValidException(String message) {
        super(message);
    }

    public DataValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
