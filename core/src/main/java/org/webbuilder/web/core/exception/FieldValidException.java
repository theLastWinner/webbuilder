package org.webbuilder.web.core.exception;

/**
 * Created by 浩 on 2015-07-20 0020.
 */
public class FieldValidException extends Exception {

    public FieldValidException(String message) {
        super(message);
    }

    public FieldValidException(String message, Throwable cause) {
        super(message, cause);
    }
}
