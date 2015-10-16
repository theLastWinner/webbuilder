package org.webbuilder.web.core.bean;

import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by 浩 on 2015-08-02 0002.
 */
public class ResponseData {
    private Object data;

    private String callBack;


    private Set<SerializeFilter> filters = new HashSet<>();

    public ResponseData() {

    }

    public ResponseData(Object data) {
        this.data = data;
    }

    public ResponseData addFilter(SerializeFilter serializeFilter) {
        filters.add(serializeFilter);
        return this;
    }

    public Object getData() {
        return data;
    }

    public ResponseData setData(Object data) {
        this.data = data;
        return this;
    }

    public Set<SerializeFilter> getFilters() {
        return filters;
    }

    public void setFilters(Set<SerializeFilter> filters) {
        this.filters = filters;
    }

    public ResponseData excludes(Class type, String... excludes) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(type);
        filter.getExcludes().addAll(Arrays.asList(excludes));
        filters.add(filter);
        return this;
    }

    public ResponseData includes(Class type, String... excludes) {
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(type);
        filter.getIncludes().addAll(Arrays.asList(excludes));
        filters.add(filter);
        return this;
    }

    public String getCallBack() {
        return callBack;
    }

    public ResponseData setCallBack(String callBack) {
        this.callBack = callBack;
        return this;
    }
}
