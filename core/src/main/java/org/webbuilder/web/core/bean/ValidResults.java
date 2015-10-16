package org.webbuilder.web.core.bean;

import com.alibaba.fastjson.JSON;
import org.webbuilder.web.core.FastJsonHttpMessageConverter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by 浩 on 2015-10-10 0010.
 */
public class ValidResults extends ArrayList<ValidResults.ValidResult> implements Serializable {

    private boolean success = true;

    @Override
    public boolean addAll(Collection<? extends ValidResult> c) {
        success = false;
        return super.addAll(c);
    }

    @Override
    public boolean add(ValidResult result) {
        success = false;
        return super.add(result);
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    public void addResult(String field, String message) {
        this.add(new ValidResult(field, message));
    }

    public class ValidResult {
        public ValidResult() {
        }

        public ValidResult(String field, String message) {
            this.field = field;
            this.message = message;
        }

        private String field;
        private String message;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return String.format("{\"%s\":\"%s\"}", getField(), getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
