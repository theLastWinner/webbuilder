package org.webbuilder.utils.db.def.parser;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.DataBaseStorage;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.def.parser.annotation.FieldMeta;
import org.webbuilder.utils.db.def.parser.annotation.TableMeta;
import org.webbuilder.utils.db.def.valid.ValidatorFactory;
import org.webbuilder.utils.db.exception.TableParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-06-23 0023.
 */
public abstract class TableParser {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void parse(TableMetaData metaData) throws TableParseException {
        if (metaData.getDefineContentType() == TableMetaData.DefineContentType.HTML)
            fromHtml(metaData);
        if (metaData.getDefineContentType() == TableMetaData.DefineContentType.CLASS)
            fromClass(metaData);
    }

    private static final Map<String, Class<?>> typeMapper = new LinkedHashMap<String, Class<?>>() {{
        put("int", Integer.class);
        put("double", Double.class);
        put("float", Double.class);
        put("string", String.class);
        put("date", Date.class);
    }};

    private void fromClass(TableMetaData metaData) throws TableParseException {
        Class type = (Class) metaData.getDefineContent();
        if (type == null)
            throw new TableParseException("type is null!");
        TableMeta meta = (TableMeta) type.getAnnotation(TableMeta.class);
        metaData.setName(meta.name());
        metaData.setDataBase(DataBaseStorage.getDataBase(meta.databaseName()));
        for (String foreign : meta.foreign()) {
            metaData.getForeigns().add(new TableMetaData.Foreign(foreign));
        }
        Field fields[] = type.getDeclaredFields();
        for (Field field : fields) {
            FieldMeta f_meta = field.getAnnotation(FieldMeta.class);
            if (f_meta == null)
                continue;
            Class javaType = field.getType();
            if (typeMapper.containsKey(javaType.getSimpleName()))
                javaType = typeMapper.get(javaType.getSimpleName());
            String name = f_meta.name();
            if (StringUtil.isNullOrEmpty(name))
                name = field.getName();

            FieldMetaData field_meta = new FieldMetaData(name, javaType);
            field_meta.setLength(f_meta.length());
            field_meta.setDefaultValue(f_meta.defaultValue());
            field_meta.setNotNull(f_meta.notNull());
            field_meta.setPrimaryKey(f_meta.primaryKey());
            field_meta.setIndex(f_meta.index());
            field_meta.setAlias(f_meta.alias());
            if (StringUtil.isNullOrEmpty(f_meta.remark())) {
                field_meta.setRemark(f_meta.alias());
            } else {
                field_meta.setAlias(f_meta.remark());
            }
            field_meta.setCanUpdate(f_meta.canUpdate());
            if (!StringUtil.isNullOrEmpty(f_meta.dataType()))
                field_meta.setDataType(f_meta.dataType());
            if (!StringUtil.isNullOrEmpty(f_meta.vtype()))
                field_meta.setVtype(f_meta.vtype());

            if (field_meta.getVtype() != null) {
                field_meta.addValidator(ValidatorFactory.validator(field_meta.getVtype()));
            }
            try {
                metaData.addField(field_meta);
                //添加基本校验器
                if (field_meta.isNotNull()) {
                    if (field_meta.getJavaType() == Integer.class) {
                        field_meta.addValidator(ValidatorFactory.IS_INT);
                    } else if (field_meta.getJavaType() == Date.class) {
                        field_meta.addValidator(ValidatorFactory.IS_DATE);
                    } else if (field_meta.getJavaType() == String.class) {
                        field_meta.addValidator(ValidatorFactory.MAX_LENGTH(field_meta.getLength()));
                    } else {
                        field_meta.addValidator(ValidatorFactory.NOT_EMPTY);
                    }
                }
            } catch (Exception e) {
                throw new TableParseException(e.getMessage(), e);
            }
        }
    }


    private void fromHtml(TableMetaData metaData) throws TableParseException {
        String html = metaData.getDefineContent().toString();
        Document document = Jsoup.parse(html);
        Elements elements = document.getElementsByAttribute("javaType");
        for (Element element : elements) {
            String javaType = element.attr("javaType");
            String dataType = element.attr("dataType");
            String remark = element.attr("remark");
            String alias = element.attr("alias");
            String name = element.attr("name");
            String length = element.attr("field-length");
            String isPk = element.attr("primary-key");
            String isNotNull = element.attr("not-null");
            String canUpdate = element.attr("can-update");
            String isIndex = element.attr("index");
            String defaultValue = element.attr("default-value");
            String vtype = element.attr("vtype");

            if (StringUtil.isNullOrEmpty(name) || StringUtil.isNullOrEmpty(javaType)) {
                throw new TableParseException("name or javaType can't is null or empty!");
            }
            if (logger.isDebugEnabled()) {
                if (StringUtil.isNullOrEmpty(remark)) {
                    logger.debug(name + ".remark isNullOrEmpty!");
                }
            }
            Class<?> type;
            if (javaType.contains(".")) {
                try {
                    type = Class.forName(javaType);
                } catch (ClassNotFoundException e) {
                    throw new TableParseException(javaType + " Class Not Found!", e);
                }
            } else {
                type = typeMapper.get(javaType.toLowerCase());
                if (type == null)
                    throw new TableParseException(javaType + " Class Not Found!");
            }
            try {
                FieldMetaData fieldMetaData = new FieldMetaData(name, type);
                if (!StringUtil.isNullOrEmpty(dataType)) {
                    fieldMetaData.setDataType(dataType);
                }
                fieldMetaData.setPrimaryKey("true".equals(isPk));
                fieldMetaData.setNotNull("true".equals(isNotNull));
                fieldMetaData.setIndex("true".equals(isIndex));
                fieldMetaData.setCanUpdate(!"false".equals(canUpdate));
                fieldMetaData.setVtype(vtype);
                fieldMetaData.setDefaultValue(defaultValue);
                fieldMetaData.setRemark(remark);
                fieldMetaData.setAlias(alias);
                if (StringUtil.isInt(length))
                    fieldMetaData.setLength(Integer.parseInt(length));
                metaData.addField(fieldMetaData);

                //添加基本校验器
                if (fieldMetaData.isNotNull()) {
                    if (fieldMetaData.getJavaType() == Integer.class) {
                        fieldMetaData.addValidator(ValidatorFactory.IS_INT);
                    } else if (fieldMetaData.getJavaType() == Date.class) {
                        fieldMetaData.addValidator(ValidatorFactory.IS_DATE);
                    } else if (fieldMetaData.getJavaType() == String.class) {
                        fieldMetaData.addValidator(ValidatorFactory.MAX_LENGTH(fieldMetaData.getLength()));
                    } else {
                        fieldMetaData.addValidator(ValidatorFactory.NOT_EMPTY);
                    }
                }
            } catch (Exception e) {
                throw new TableParseException(e.getMessage(), e);
            }
        }
    }

    public abstract TableMetaData parse(Object session, String tableName) throws TableParseException;
}
