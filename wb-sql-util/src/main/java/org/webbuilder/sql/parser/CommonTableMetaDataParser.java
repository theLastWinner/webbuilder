package org.webbuilder.sql.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.webbuilder.sql.FieldMetaData;
import org.webbuilder.sql.TableMetaData;
import org.webbuilder.sql.param.ExecuteCondition;
import org.webbuilder.sql.trigger.ScriptTriggerSupport;
import org.webbuilder.utils.base.StringUtil;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-13 0013.
 */
public class CommonTableMetaDataParser implements TableMetaDataParser {

    private static final Map<String, Class> typeMapper = new LinkedHashMap<>();

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    static {
        typeMapper.put("int", Integer.class);
        typeMapper.put("string", String.class);
        typeMapper.put("date", Date.class);
        typeMapper.put("double", Double.class);
        typeMapper.put("float", Double.class);
        typeMapper.put("boolean", Integer.class);
    }

    @Override
    public TableMetaData parse(String content, String type) throws Exception {
        if (type.equalsIgnoreCase("html")) {
            return parseHTML(content);
        }
        return null;
    }

    protected TableMetaData parseHTML(String content) throws Exception {
        TableMetaData tableMetaData = new TableMetaData();
        Document document = Jsoup.parse(content);
        Elements t_name = document.getElementsByTag("table-meta");
        if (t_name.size() > 0) {
            tableMetaData.setName(t_name.get(0).attr("name"));
            tableMetaData.setComment(t_name.get(0).attr("remark"));
            tableMetaData.setReadOnly("true".equals(t_name.get(0).attr("read-only")));
            Elements attrs = t_name.get(0).getElementsByTag("attr");
            for (Element attr : attrs) {
                for (Attribute attribute : attr.attributes().asList()) {
                    tableMetaData.attr(attribute.getKey(),attribute.getValue());
                }
            }
        }
        //加载自定义属性
        //
        Elements attrs;

        Elements elements = document.getElementsByAttribute("field-meta");
        for (Element element : elements) {
            FieldMetaData fieldMetaData = new FieldMetaData();
            String javaType = element.attr("java-type");
            if (StringUtil.isNullOrEmpty(javaType))
                javaType = "string";
            Class type = typeMapper.get(javaType);
            if (type == null) continue;
            String dataType = element.attr("data-type");
            String remark = element.attr("remark");
            String alias = element.attr("alias");
            String name = element.attr("name");
            String length = element.attr("field-length");
            String isPk = element.attr("primary-key");
            String isNotNull = element.attr("not-null");
            String canUpdate = element.attr("can-update");
            String defaultValue = element.attr("default-value");
            String validator = element.attr("validator");
            fieldMetaData.setName(name);
            fieldMetaData.setAlias(alias);
            fieldMetaData.setLength(StringUtil.toInt(length, 256));
            fieldMetaData.setPrimaryKey("true".equals(isPk));
            fieldMetaData.setCanUpdate("true".equals(canUpdate));
            fieldMetaData.setNotNull("true".equals(isNotNull));
            fieldMetaData.setDefaultValue(defaultValue);
            fieldMetaData.setValidator(validator);
            fieldMetaData.setDataType(dataType);
            fieldMetaData.setComment(remark);
            attrs = element.getElementsByTag("attr");
            for (Element attr : attrs) {
                for (Attribute attribute : attr.attributes().asList()) {
                    tableMetaData.attr(attribute.getKey(),attribute.getValue());
                }
            }
            tableMetaData.addField(fieldMetaData);
        }

        //解析表关联
        Elements correlations = document.getElementsByTag("correlation");
        for (Element correlation : correlations) {
            TableMetaData.Correlation cor = new TableMetaData.Correlation();

            String target = correlation.attr("target");
            cor.setTargetTable(target);
            Elements conditions = correlation.getElementsByTag("condition");
            for (Element condition : conditions) {
                String filed = condition.attr("filed");
                String value = condition.attr("value");
                String appendType = condition.attr("type");
                String sql = condition.attr("sql");
                String queryType = condition.attr("query-type");
                ExecuteCondition cdt = new ExecuteCondition();
                cdt.setAppendType(appendType);
                try {
                    cdt.setQueryType(ExecuteCondition.QueryType.valueOf(queryType.toUpperCase()));
                } catch (Exception e) {
                }
                cdt.setField(filed);
                cdt.setValue(value);
                cdt.setSql("true".equals(sql));
                cor.addCondition(cdt);
            }
            tableMetaData.addCorrelation(cor);
        }

        //解析触发器
        Elements triggers = document.getElementsByAttribute("trigger");
        for (Element trigger : triggers) {
            String name = trigger.attr("trigger");
            String language = trigger.attr("language");
            String text = trigger.html();
            ScriptTriggerSupport triggerSupport = new ScriptTriggerSupport();
            triggerSupport.setContent(text);
            triggerSupport.setLanguage(language);
            triggerSupport.setName(name);
            try {
                triggerSupport.init();
            } catch (Exception e) {
                logger.error(String.format("init trigger (%s.%s) error!", name, language), e);
                continue;
            }
            tableMetaData.on(triggerSupport);
        }

        return tableMetaData;
    }

}
