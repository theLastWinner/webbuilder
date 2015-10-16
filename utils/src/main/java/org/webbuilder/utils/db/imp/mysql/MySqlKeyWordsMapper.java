package org.webbuilder.utils.db.imp.mysql;

import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.mysql.mapper.*;
import org.webbuilder.utils.db.imp.oracle.OracleKeyWordsMapper;

import java.util.Date;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class MySqlKeyWordsMapper extends OracleKeyWordsMapper {
    public MySqlKeyWordsMapper() {
        super();
        registerMapper(new Mapper_LIKE(true));
        registerMapper(new Mapper_LIKE(false));
        registerMapper(new Mapper_END(true));
        registerMapper(new Mapper_END(false));
        registerMapper(new Mapper_START(true));
        registerMapper(new Mapper_START(false));
        registerMapper(new Mapper("GT") {
            {
                setTypes(Number.class, Date.class);
            }

            @Override
            public String template(FieldMetaData<?> field, String name) {
                String key = field.getName();
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtil.isNullOrEmpty(name) && !key.contains("."))
                    stringBuilder.append("u.");
                stringBuilder.append(key).append(" >=")
                        .append(preStart).append(field.getName()).append(getKw()).append(preEnd);
                return stringBuilder.toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                if (value instanceof Date) {
                    value = DateTimeUtils.format((Date) value, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
                }
                return value;
            }
        });
        registerMapper(new Mapper("LT") {
            {
                setTypes(Number.class, Date.class);
            }

            @Override
            public String template(FieldMetaData<?> field, String name) {
                String key = field.getName();
                StringBuilder stringBuilder = new StringBuilder();
                if (StringUtil.isNullOrEmpty(name) && !key.contains("."))
                    stringBuilder.append("u.");
                stringBuilder.append(key).append(" <=")
                        .append(preStart).append(field.getName()).append(getKw()).append(preEnd);
                return stringBuilder.toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                if (value instanceof Date) {
                    value = DateTimeUtils.format((Date) value, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
                }
                return value;
            }
        });
    }
}
