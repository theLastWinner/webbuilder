package org.webbuilder.utils.db.imp.oracle.mappers;


import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class Mapper_END extends OracleKeyWordsMapper.Mapper {

    private boolean takeBack = false;

    public Mapper_END(boolean takeBack) {
        super(takeBack ? "NOTEND" : "END");
        this.takeBack = takeBack;
    }

    public String template(FieldMetaData<?> field, String name) {
        StringBuilder template = new StringBuilder();
        String key = field.getName();
        if (StringUtil.isNullOrEmpty(name)&&!key.contains("."))
            template.append("u.");
        return template.append(key).append(takeBack ? " NOT" : "").append(" LIKE ").append("'%'||")
                .append(OracleKeyWordsMapper.preStart).append(field.getName()).append(getKw()).append(OracleKeyWordsMapper.preEnd).toString();
    }
}
