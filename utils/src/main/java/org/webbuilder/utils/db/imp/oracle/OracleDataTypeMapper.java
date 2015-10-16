package org.webbuilder.utils.db.imp.oracle;


import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.render.DataTypeMapper;

import java.util.Date;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-07 0007.
 */
public class OracleDataTypeMapper extends DataTypeMapper {
    public static final String TYPE_NUMBER = "number";
    public static final String TYPE_VARCHAR2 = "varchar2";
    public static final String TYPE_DATE = "date";
    public static final String TYPE_BYTES = "blob";


    public OracleDataTypeMapper() {
        this.registerMapper(Integer.class, "number(32,0)").
                registerMapper(Number.class, TYPE_NUMBER).
                registerMapper(String.class, TYPE_VARCHAR2 + "(${length})").
                registerMapper(Date.class, TYPE_DATE).
                registerMapper(Byte[].class, TYPE_BYTES).
                registerMapper(byte[].class, TYPE_BYTES);
    }

    public String dataType(FieldMetaData metaData) throws Exception {
        if (metaData.getDataType() != null && metaData.getDataType().contains("("))
            return metaData.getDataType();
        if (metaData.getDataType() != null && metaData.getDataType().equals("clob"))
            return "clob";
        //未定义，根据mapper进行映射
        if (metaData.getJavaType() == String.class && metaData.getLength() >= 4000)
            return "clob";//大文本
        for (Map.Entry<Class, String> entry : getMappers().entrySet()) {
            //未定义长度
            if (entry.getValue().startsWith(metaData.getDataType() + "(")) {
                return metaData.getDataType() + "(" + metaData.getLength() + ")";
            }
            try {
                metaData.getJavaType().asSubclass(entry.getKey());
                return entry.getValue().replace("${length}", String.valueOf(metaData.getLength()));
            } catch (Exception e) {
            }
        }
        throw new ClassNotFoundException(metaData.getJavaType() + " dataTypeMapper is not found!");
    }

    public Class javaType(String dataType) throws Exception {
        dataType = dataType.toLowerCase();
        if (dataType.equals("clob"))
            return String.class;
        if (dataType.contains("("))
            dataType = dataType.split("[(]")[0];
        for (Map.Entry<Class, String> entry : getMappers().entrySet()) {
            String val = entry.getValue();
            if (val.contains("("))
                val = val.split("[(]")[0];
            if (val.equals(dataType))
                return entry.getKey();
        }
        throw new ClassNotFoundException("DataTypeMapper is not found :" + dataType + ".");
    }

}
