package org.webbuilder.web.po.form;

import org.webbuilder.web.core.bean.GenericPo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-01 0001.
 */
public class CustomFormData extends GenericPo<String> {
    private Map<String, Object> prototype = new LinkedHashMap<>();

    private String formId;

    private transient Map<String, Object> params;

    public CustomFormData value(String field, Object value) {
        prototype.put(field, value);
        return this;
    }

    public <T> T value(String field) {
        return (T) prototype.get(field);
    }

    public void value(Map<String, Object> value) {
        prototype.putAll(value);
    }

    public Map<String, Object> value() {
        prototype.put("u_id", getU_id());
        return prototype;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
