package org.webbuilder.utils.db.render;


import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.exception.DataValidException;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-07 0007.
 */
public abstract class DataTypeMapper implements Serializable {
    public Map<Class, String> mappers = new LinkedHashMap<>();

    public DataTypeMapper registerMapper(Class javaType, String dataType) {
        mappers.put(javaType, dataType);
        return this;
    }

    public Map<Class, String> getMappers() {
        return mappers;
    }

    public abstract String dataType(FieldMetaData metaData) throws Exception;

    public abstract Class javaType(String dataType) throws Exception;

    public boolean valid(FieldMetaData metaData) throws Exception {
        if (mappers.containsKey(this.javaType(dataType(metaData))))
            return true;
        throw new DataValidException(metaData + " valid failed! because the dataType illegal!");
    }

}
