package org.webbuilder.utils.db.exception;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public class TableParseException extends Exception {
    public TableParseException(String message) {
        super(message);
    }

    public TableParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
