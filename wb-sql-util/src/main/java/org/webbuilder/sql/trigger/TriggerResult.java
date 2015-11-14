package org.webbuilder.sql.trigger;

import java.io.Serializable;

/**
 * Created by 浩 on 2015-11-14 0014.
 */
public class TriggerResult implements Serializable {
    private boolean success;

    private String message;

    private Object data;

    public TriggerResult() {
    }

    public TriggerResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public TriggerResult(boolean success, String message, Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
