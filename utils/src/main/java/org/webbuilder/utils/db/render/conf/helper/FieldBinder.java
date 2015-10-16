package org.webbuilder.utils.db.render.conf.helper;


import org.webbuilder.utils.db.render.conf.SqlRenderHelper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class FieldBinder implements SqlRenderHelper {
    private Map<String, FieldHelper> fieldHelperMap = new ConcurrentHashMap<String, FieldHelper>();

    public FieldBinder bind(String field, FieldHelper helper) {
        fieldHelperMap.put(field, helper);
        return this;
    }

    public Object helper(String field, Object val) {
        if (!fieldHelperMap.containsKey(field))
            return val;
        FieldHelper helper = fieldHelperMap.get(field);
        if (helper != null) {
            return helper.helper(field, val);
        }
        return val;
    }
}
