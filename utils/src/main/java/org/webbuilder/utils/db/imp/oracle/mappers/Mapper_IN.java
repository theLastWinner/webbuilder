package org.webbuilder.utils.db.imp.oracle.mappers;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class Mapper_IN extends OracleKeyWordsMapper.Mapper {

    private boolean takeBack = false;

    public Mapper_IN(boolean takeBack) {
        super(takeBack ? "NOTIN" : "IN");
        this.takeBack = takeBack;
        this.setTypes(String.class, Number.class);
    }

    public Object value(FieldMetaData<?> field, Object value) {
        String[] valStr;
        if (value instanceof Iterable) {
            return value;
        }
        if (value instanceof String) {
            valStr = ((String) value).split("[,]");
            return Arrays.asList(valStr);
        } else if (value instanceof String[]) {
            return Arrays.asList((String[]) value);
        } else if (value instanceof Number[]) {
            return Arrays.asList((Number[]) value);
        } else {
            return value;
        }
    }

    public String template(FieldMetaData<?> field, String name) {
        String key = field.getName();
        StringBuilder stringBuilder=   new StringBuilder();
        if (StringUtil.isNullOrEmpty(name)&&!key.contains("."))
            stringBuilder.append("u.");
        return stringBuilder.append(field.getName()).append(takeBack ? " NOT" : "").append(" IN(").append(OracleKeyWordsMapper.preStart).append(field.getName()).append(getKw()).append("...").append(OracleKeyWordsMapper.preEnd).append(")").toString();
    }


}
