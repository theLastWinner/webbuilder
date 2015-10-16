package org.webbuilder.utils.db.imp.mysql.mapper;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class Mapper_START extends OracleKeyWordsMapper.Mapper {

    private boolean takeBack = false;

    public Mapper_START(boolean takeBack) {
        super(takeBack ? "NOTSTART" : "START");
        this.takeBack = takeBack;
    }

    public String template(FieldMetaData<?> field, String t_name) {
        String key = field.getName();
        StringBuilder stringBuilder=   new StringBuilder();
        if (StringUtil.isNullOrEmpty(t_name)&&!key.contains("."))
            stringBuilder.append("u.");
        return  stringBuilder.append(key).append(takeBack?" NOT":"").append(" LIKE CONCAT(")
                .append(OracleKeyWordsMapper.preStart).append(field.getName()).append(getKw()).append(OracleKeyWordsMapper.preEnd)
                .append(",'%')").toString();
    }
}
