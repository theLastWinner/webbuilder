package org.webbuilder.utils.db.imp.oracle.mappers;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class Mapper_LIKE extends OracleKeyWordsMapper.Mapper {

    private boolean takeBack = false;

    public Mapper_LIKE(boolean takeBack) {
        super(takeBack ? "NOTLIKE" : "LIKE");
        this.takeBack = takeBack;
    }

    public String template(FieldMetaData<?> field, String t_name) {
        StringBuilder template = new StringBuilder(" INSTR(");
        String key = field.getName();
        if (StringUtil.isNullOrEmpty(t_name)&&!key.contains("."))
            template.append("u.");

        return template.append(field.getName()).append(",")
                .append(OracleKeyWordsMapper.preStart).append(field.getName()).append(getKw()).append(OracleKeyWordsMapper.preEnd)
                .append(")").append(takeBack ? "<=0" : ">0").toString();
    }

}
