package org.webbuilder.utils.db.imp.mysql;


import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.render.DataTypeMapper;

import java.util.Date;
import java.util.Map;

/**
 * Created by 浩 on 2015-07-07 0007.
 */
public class MySqlDataTypeMapper extends DataTypeMapper {
    public static final String TYPE_NUMBER = "double";
    public static final String TYPE_VARCHAR = "varchar";
    public static final String TYPE_DATE = "datetime";
    public static final String TYPE_BYTES = "text";

    public MySqlDataTypeMapper() {
        this.registerMapper(Integer.class, "int").
                registerMapper(Number.class, MySqlDataTypeMapper.TYPE_NUMBER).
                registerMapper(String.class, MySqlDataTypeMapper.TYPE_VARCHAR + "(${length})").
                registerMapper(Date.class, MySqlDataTypeMapper.TYPE_DATE).
                registerMapper(Byte[].class, MySqlDataTypeMapper.TYPE_BYTES).
                registerMapper(byte[].class, MySqlDataTypeMapper.TYPE_BYTES);
    }

    public String dataType(FieldMetaData metaData) throws Exception {
        if (metaData.getDataType() != null && metaData.getDataType().contains("("))
            return metaData.getDataType();
        if (metaData.getDataType() != null && metaData.getDataType().equals("text"))
            return "text";
        //未定义，根据mapper进行映射
        if (metaData.getJavaType() == String.class && metaData.getLength() >= 4000)
            return "text";//大文本
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
        if (dataType.equals("text"))
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
        throw new ClassNotFoundException("DataTypeMapper is not found :" + dataType);
    }

}
