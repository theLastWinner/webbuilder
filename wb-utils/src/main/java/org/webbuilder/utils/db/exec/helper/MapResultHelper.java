package org.webbuilder.utils.db.exec.helper;


import org.webbuilder.utils.db.def.DataFormater;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-08 0008.
 */
public class MapResultHelper extends ResultHelper<Map<String, Object>> {

    @Override
    public Map<String, Object> newInstance() {
        return new LinkedHashMap<>();
    }

    public MapResultHelper() {
        //添加默认格式化
        addDataformater(new DataFormater<Timestamp>() {
            @Override
            public Object format(Timestamp value) {
                return new Date(value.getTime());
            }
        });
        addDataformater(new DataFormater<BigDecimal>() {
            @Override
            public Object format(BigDecimal value) {
                return value.longValue();
            }
        });
    }

    @Override
    public void initAttr(String name, Object value, Map<String, Object> instance) {
        AttrHelper attrCreator = getAttrHelper(name);
        if (attrCreator != null) {
           value = attrCreator.value(value, instance);
        }
        if (name.contains(".")) {//类似 user_info.name
            String[] info = name.split("[.]");
            String attrName = info[0];
            String field = info[1];
            Object object = instance.get(attrName);
            //构建一个新的Map
            if (object == null)
                object = newInstance();
            if (object instanceof Map) {
                ((Map<String, Object>) object).put(field, value);
                instance.put(attrName, object);
                return;
            }
        } else {
            instance.put(name, value);
        }
    }
}
