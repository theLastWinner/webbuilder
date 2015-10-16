package org.webbuilder.utils.db.imp.oracle;

import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.imp.oracle.mappers.Mapper_END;
import org.webbuilder.utils.db.imp.oracle.mappers.Mapper_IN;
import org.webbuilder.utils.db.imp.oracle.mappers.Mapper_LIKE;
import org.webbuilder.utils.db.imp.oracle.mappers.Mapper_START;
import org.webbuilder.utils.db.render.KeyWordsMapper;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-07-04 0004.
 */
public class OracleKeyWordsMapper extends KeyWordsMapper {
    private static final String keyWordSpliter = "$";
    private static final Pattern pat = Pattern.compile("(?<=\\$)(.+?)(?=\\$)");
    //预编译
    public static final String preStart = "@{", preEnd = "}";

    //直接拼接
    public static final String appendStart = "${", appendEnd = "}";
    public static final Mapper common_mapper = new Mapper();

    public OracleKeyWordsMapper() {
        registerMapper(new Mapper_IN(true));
        registerMapper(new Mapper_IN(false));
        registerMapper(new Mapper_LIKE(true));
        registerMapper(new Mapper_LIKE(false));
        registerMapper(new Mapper_START(true));
        registerMapper(new Mapper_START(false));
        registerMapper(new Mapper_END(true));
        registerMapper(new Mapper_END(false));
        registerMapper(new Mapper("GT") {
            {
                setTypes(Number.class, Date.class);
            }

            @Override
            public String template(FieldMetaData<?> field, String name) {
                String key = field.getName();
                StringBuilder template = new StringBuilder();
                if (StringUtil.isNullOrEmpty(name) && !key.contains("."))
                    template.append("u.");
                template.append(key).append(" >=");
                //日期
                if (field.getJavaType() == Date.class) {
                    template.append("to_date(")
                            .append(preStart).append(field.getName()).append(getKw()).append(preEnd)
                            .append(",'YYYY-MM-DD HH24:MI:SS')");
                } else {
                    template.append(preStart).append(field.getName()).append(getKw()).append(preEnd);
                }
                return template.toString();
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
                StringBuilder template = new StringBuilder();
                if (StringUtil.isNullOrEmpty(name) && !key.contains("."))
                    template.append("u.");
                template.append(key).append(" <=");
                //日期
                if (field.getJavaType() == Date.class) {
                    template.append("to_date(")
                            .append(preStart).append(field.getName()).append(getKw()).append(preEnd)
                            .append(",'YYYY-MM-DD HH24:MI:SS')");
                } else {
                    template.append(preStart).append(field.getName()).append(getKw()).append(preEnd);
                }
                return template.toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                if (value instanceof Date) {
                    value = DateTimeUtils.format((Date) value, DateTimeUtils.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND);
                }
                return value;
            }
        });
        registerMapper(new Mapper("NOT") {
            @Override
            public String template(FieldMetaData<?> field, String name) {
                if (StringUtil.isNullOrEmpty(name))
                    name = "u";
                return new StringBuilder(name).append(".").append(field.getName()).append(" !=").append(preStart).append(field.getName()).append(getKw()).append(preEnd).toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                return value;
            }
        });
        registerMapper(new Mapper("NOTNULL") {
            @Override
            public String template(FieldMetaData<?> field, String name) {
                if (StringUtil.isNullOrEmpty(name))
                    name = "u";
                return new StringBuilder(name).append(".").append(field.getName()).append(" NOT NULL").toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                return value;
            }
        });
        registerMapper(new Mapper("ISNULL") {
            @Override
            public String template(FieldMetaData<?> field, String name) {
                if (StringUtil.isNullOrEmpty(name))
                    name = "u";
                return new StringBuilder(name).append(".").append(field.getName()).append(" IS NULL").toString();
            }

            @Override
            public Object value(FieldMetaData<?> field, Object value) {
                return value;
            }
        });
    }


    public KeyWordsMapper.Mapper getMapperByKey(String key) {
        key = key + "$";
        Matcher mat = pat.matcher(key);
        String kw = null;
        if (mat.find()) {
            kw = mat.group();
        }
        if (kw == null)
            return common_mapper;
        kw = keyWordSpliter + kw;
        KeyWordsMapper.Mapper mapper = key_mapper.get(kw);
        if (mapper == null)
            mapper = common_mapper;
        return mapper;
    }

    public KeyWordsMapper.Mapper getMapper(String key) {
        KeyWordsMapper.Mapper mapper = key_mapper.get(keyWordSpliter + key);
        if (mapper != null)
            return mapper;
        return getMapperByKey(key);
    }

    public static class Mapper extends KeyWordsMapper.Mapper {

        public Mapper() {
        }

        public Mapper(String kw) {
            this.kw = kw;
        }

        public String getKw() {
            if (kw == null)
                kw = "";
            return keyWordSpliter + kw;
        }

        private Class<?>[] types = {String.class, Number.class, Date.class};

        private String kw;

        public Class<?>[] getTypes() {
            return types;
        }

        public boolean canUse(FieldMetaData<?> field) {
            for (Class<?> aClass : getTypes()) {
                try {
                    field.getJavaType().asSubclass(aClass);
                    return true;
                } catch (Exception e) {
                }
            }
            return false;
        }

        public void setTypes(Class<?>... types) {
            this.types = types;
        }

        public String template(FieldMetaData<?> field, String t_name) {
            if (StringUtil.isNullOrEmpty(t_name))
                t_name = "u";
            return  new StringBuilder(t_name).append(".").append(field.getName()).append("=").append(preStart).append(field.getName()).append(preEnd).toString();
        }

        public String fieldName(String key) {
            if ("".equals(getKw()))
                return key;
            return key.replace(getKw(), "");
        }

        public Object value(FieldMetaData<?> field, Object value) {
            return value;
        }

    }
}
