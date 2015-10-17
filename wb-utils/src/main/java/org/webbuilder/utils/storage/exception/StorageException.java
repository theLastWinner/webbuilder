package org.webbuilder.utils.storage.exception;

/**
 * Created by 浩 on 2015-6-13.
 */
public class StorageException extends RuntimeException {
    public StorageException(String msg) {
        super(msg);
    }

    public StorageException(String msg,Throwable e) {
        super(msg,e);
    }
}
