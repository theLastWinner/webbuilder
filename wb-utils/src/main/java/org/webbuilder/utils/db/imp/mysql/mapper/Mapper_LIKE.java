package org.webbuilder.utils.db.imp.mysql.mapper;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

/**
 * Created by 浩 on 2015-07-30 0030.
 */
public class Mapper_LIKE  extends OracleKeyWordsMapper.Mapper {

    private boolean takeBack = false;

    public Mapper_LIKE(boolean takeBack) {
        super(takeBack ? "NOTLIKE" : "LIKE");
        this.takeBack = takeBack;
    }

    public String template(FieldMetaData<?> field, String t_name) {
        String key = field.getName();
        StringBuilder stringBuilder=   new StringBuilder();
        if (StringUtil.isNullOrEmpty(t_name)&&!key.contains("."))
            stringBuilder.append("u.");
        return  stringBuilder.append(key).append(takeBack?" NOT":"").append(" LIKE CONCAT('%',")
                .append(OracleKeyWordsMapper.preStart).append(field.getName()).append(getKw()).append(OracleKeyWordsMapper.preEnd)
                .append(",'%')").toString();
    }

}
