package org.webbuilder.utils.db.imp.oracle.render.freemarker;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.*;
import org.webbuilder.utils.db.render.KeyWordsMapper;
import org.webbuilder.utils.db.render.SqlRender;
import org.webbuilder.utils.db.render.SqlRenderType;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-07-05 0005.
 */
public class SelectRender extends FreemarkerSqlRender {

    public SelectRender(TableMetaData metaData) {
        super(metaData);
    }

    protected Pattern pat = Pattern.compile("(?<=\\()(.+?)(?=\\))");

    /**
     * 构建表关联查询条件模板，如：user,{id:1} -->and user.id=@{1}
     *
     * @param key        表名
     * @param conditions 关联集合
     * @return 查询条件模板
     */
    public String buildForeign(String key, Map<String, Object> conditions) {
        StringBuilder foreignSql = new StringBuilder();
        for (Map.Entry<String, Object> e : conditions.entrySet()) {
            //关键字映射器
            KeyWordsMapper.Mapper mapper = getKeyWordsMapper().getMapper(e.getKey());
            if (e.getValue() instanceof Map) {
                foreignSql.append(buildForeign(e.getKey(), (Map) e.getValue()));
            } else {
                //根据字段生成查询模板
                foreignSql.append(mapper.template(new FieldMetaData<>(key + "." + mapper.fieldName(e.getKey()), Object.class), ""));
            }
        }
        return foreignSql.toString();
    }

    /**
     * 根据配置获取需要查询的字段，支持关联表字段查询；
     *
     * @param config 配置实例
     * @return 需要查询的字段
     */
    public Set<String> getSelectField(final SqlRenderConfig config) {
        Set<String> fields = new LinkedHashSet<String>();
        if (config.getIncludes().size() == 0) {
            config.getIncludes().add("*");
        }
        Set<FieldMetaData> metas = new HashSet<FieldMetaData>(getMetaData().getFields());
        //是否查询所有字段
        boolean all = config.getIncludes().contains("*");
        if (all) {
            for (FieldMetaData meta : metas) {
                fields.add(meta.getName());
            }
        }
        for (String s : config.getIncludes()) {
            String field = asName(s)[0];
            if (getMetaData().getField(field) != null || s.contains(".")) {
                fields.add(s);
            }
        }
        fields.removeAll(config.getExcludes());
        return fields;
    }

    public String buildGroupBy(String groupBy) {
        StringBuilder builder = new StringBuilder();
        String[] strs = groupBy.split(",");
        int i = 0;
        for (String str : strs) {
            if (i++ != 0) {
                builder.append(",");
            }
            if (!str.contains(".")) {
                builder.append("u.").append(str);
            } else {
                builder.append(str);
            }
        }
        return builder.toString();
    }

    @Override
    public SqlInfo render(final SqlRenderConfig configPo) throws Exception {
        Map<String, SqlInfo> bindSql = new LinkedHashMap<String, SqlInfo>();
        //使用克隆的配置，不干预原始配置
        final SqlRenderConfig config = configPo.clone();
        Object groupBy = config.getParams().get(SqlRender.GROUP_BY_KEY);
        if (groupBy != null)
            config.getParams().put(SqlRender.GROUP_BY_KEY, buildGroupBy(groupBy.toString()));
        checkTableChange();//检测表信息是否已发生改变
        Map<String, Object> params = config.getParams();
        List<String> conditions = new LinkedList();
        if (config.getParams().size() > 0) {//如果传递了参数，则进行基本参数(表字段查询)拼接
            String condition = conditionTemplate(config);
            if (!StringUtil.isNullOrEmpty(condition))
                conditions.add(condition);
        }
        TableMetaData tableMeta = getMetaData();
        //获取表关联查询信息  <表名，关联条件>
        Map<String, Map<String, Object>> tables = foreignTables(config);
        if (tables.size() > 0) {
            //拼接表关联条件
            StringBuilder builder = new StringBuilder();
            //遍历出所有需要链接查询的信息
            for (Map.Entry<String, Map<String, Object>> foreigns : tables.entrySet()) {
                TableMetaData.Foreign foreign = tableMeta.getforeign(foreigns.getKey());
                //表定义是否有此表的关联信息
                if (foreign != null && foreign.getType() == TableMetaData.Foreign.TYPE.ONE2ONE) {
                    TableMetaData.Foreign.MODE mode = config.getForeignMode();
                    //使用JOIN
                    if (mode == null)
                        mode = foreign.getMode();
                    builder.append(" ").append(mode).append(" ").append(foreigns.getKey()).append(" ").append(foreigns.getKey()).append(" ON ");
                    for (Map.Entry<String, String> e : foreign.getCondition().entrySet()) {
                        String tc = e.getValue();
                        if (!tc.contains("."))
                            builder.append("u.");
                        builder.append(e.getValue()).append("=").append(foreign.getTargetTable()).append(".").append(e.getKey());
                    }
                    conditions.add(buildForeign(foreigns.getKey(), foreigns.getValue()));
                }
            }
            params.put("_tableNames", builder.toString());
        }
        //构建where条件
        if (conditions.size() > 0) {
            StringBuilder builder = new StringBuilder();
            int index = 0;
            for (String condition : conditions) {
                if (condition == null || condition.trim().equals(""))
                    continue;
                if (index == 0) {
                    builder.append(" WHERE ");
                }
                if (index++ != 0)
                    builder.append(" AND ");
                builder.append(condition);
            }
            params.put("_conditions", builder.toString());
        }
        //指定查询字段
        Set<String> selectField = getSelectField(config);
        if (selectField.size() > 0) {
            StringBuilder SELECT_FIELDS = new StringBuilder();
            for (String field : selectField) {
                String[] asEs = asName(field);
                field = asEs[0];
                if (field.contains(".")) {
                    String str = field;
                    if (field.contains("(")) {
                        Matcher matcher = pat.matcher(field);
                        if (matcher.find()) {
                            str = matcher.group();
                        }
                    }
                    //其他表的字段
                    String[] info = str.split("[.]");
                    String tableName = info[0];
                    //如果表关联中有此表信息才进行查询
                    if (tables.containsKey(tableName)) {
                        TableMetaData.Foreign foreign = getMetaData().getforeign(tableName);
                        //关联的tableMeta
                        TableMetaData conTable = getMetaData().getDataBase().getTable(tableName);
                        if (conTable != null) {
                            boolean one2one = foreign == null || foreign.getType() == TableMetaData.Foreign.TYPE.ONE2ONE;
                            StringBuilder foreignSql = new StringBuilder();
                            SqlRender render = conTable.render(SqlRenderType.SELECT);
                            if (render instanceof SelectRender) {
                                SelectRender selectRender = (SelectRender) conTable.render(SqlRenderType.SELECT);
                                if (one2one) {
                                    //查询*
                                    if(info[1].equals("*")) {
                                        //只有1对1的关联，才合成到一个sql语句查询
                                        for (String connFieldName : selectRender.getSelectField(new SqlRenderConfig())) {
                                            if (config.getExcludes().contains(tableName + "." + connFieldName))
                                                continue;
                                            foreignSql.append(",").append(tableName).append(".").append(connFieldName)
                                                    .append(" AS ").append(getQuotesStart()).append(tableName).append(".").append(connFieldName).append(getQuotesEnd());
                                        }
                                    }else{
                                        foreignSql.append(",").append(tableName).append(".").append(info[1])
                                                .append(" AS ").append(getQuotesStart()).append(tableName).append(".").append(info[1]).append(getQuotesEnd());
                                    }
                                    SELECT_FIELDS.append(foreignSql);
                                } else {
                                    //1对多，生成关联的sql语句进行查询。
                                    SqlRenderConfig renderConfig = new SqlRenderConfig();
                                    final List<String> keys = new LinkedList<String>();
                                    for (Map.Entry<String, String> entry : foreign.getCondition().entrySet()) {
                                        keys.add(entry.getValue());
                                        renderConfig.getParams().put(entry.getKey(), params.get(entry.getValue()));
                                    }
                                    renderConfig.getIncludes().add("*");
                                    SqlInfo info1 = selectRender.render(renderConfig);
                                    SqlInfo newSql = new SqlInfo(info1.getSql()) {
                                        @Override
                                        public Object[] initForBind(Object object) {
                                            if (object instanceof Map) {
                                                Map<String, Object> cdt = (Map) object;
                                                Object[] params = new Object[keys.size()];
                                                int index = 0;
                                                for (String key : keys) {
                                                    params[index++] = cdt.get(key);
                                                }
                                                setParams(params);
                                                return params;
                                            }
                                            return super.initForBind(object);
                                        }
                                    };
                                    bindSql.put(tableName, newSql);
                                }
                            } else {
                                foreignSql.append(",").append(field);
                            }
                        } else {
                            if (foreign != null) {
                                if (field.contains("*")) {
                                    //单独进行查询
                                    final Map<String, String> objectMap = foreign.getCondition();
                                    StringBuilder c_builder = new StringBuilder("SELECT * FROM ");
                                    c_builder.append(tableName).append(" WHERE ");
                                    int index = 0;
                                    final List<String> keys = new LinkedList<String>();
                                    for (Map.Entry<String, String> entry : objectMap.entrySet()) {
                                        if (index++ != 0)
                                            c_builder.append(" AND ");
                                        c_builder.append(entry.getKey()).append("=").append("?");
                                        keys.add(entry.getValue());
                                    }
                                    bindSql.put(tableName, new SqlInfo(c_builder.toString()) {
                                        @Override
                                        public Object[] initForBind(Object object) {
                                            if (object instanceof Map) {
                                                Map<String, Object> cdt = (Map) object;
                                                Object[] params = new Object[keys.size()];
                                                int index = 0;
                                                for (String key : keys) {
                                                    params[index++] = cdt.get(key);
                                                }
                                                setParams(params);
                                                return params;
                                            }
                                            return super.initForBind(object);
                                        }
                                    });
                                } else {
                                    StringBuilder foreignSql = new StringBuilder();
                                    foreignSql.append(",").append(field)
                                            .append(" AS ").append(getQuotesStart()).append(field).append(getQuotesEnd());
                                    SELECT_FIELDS.append(foreignSql);
                                }
                            }
                        }
                    }
                } else {
                    if (!field.equals("*")) {
                        if (getMetaData().getField(field) != null)
                            SELECT_FIELDS.append(",").append("u.").append(field).append(asEs[1]);
                    } else
                        SELECT_FIELDS.append(",").append("u.").append(field);
                }
            }
            //定义了查询字段
            params.put("__SELECTFIELDS", SELECT_FIELDS.substring(1).toString());
            if (config.isSelectForUpdate())
                params.put("__for_update", config.isSelectForUpdate());

        }
        SqlInfo info = build(params);
        info.getBindSql().putAll(bindSql);
        return info;
    }


    /**
     * 根据条件获取需要进行表关联的信息，如，参数中包含了.key为表定义中进行了关联的表名且value为一个Map
     * <p/>
     * 支持语法:user.id=1 ; user={id:1}；如果指定的语法为user.id，则会自动生成一个map:{id:1}填充到原始查询条件中.
     * 推荐使用user={id:1}模式
     *
     * @param config render配置实体
     * @return 表关联信息
     */
    public Map<String, Map<String, Object>> foreignTables(SqlRenderConfig config) {
        Map<String, Object> conditions = config.getParams();
        Map<String, Map<String, Object>> res = new HashMap<String, Map<String, Object>>();
        Map<String, Object> conditions_new = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : conditions.entrySet()) {
            //如果为简写模式,则根据规则，生成 一个对应的map以及值，如
            //key=user.id,value=1,将生产key为user,value={id:1}的新元素
            if (entry.getKey().contains(".")) {
                String field = entry.getKey();
                String[] asEs = asName(field);
                field = asEs[0];
                if (field.contains("(")) {
                    Matcher matcher = pat.matcher(field);
                    if (matcher.find()) {
                        field = matcher.group();
                    }
                }
                String kv[] = field.split("[.]");
                Object obj = conditions.get(kv[0]);
                if (obj == null)
                    obj = conditions_new.get(kv[0]);
                Map<String, Object> objectMap;
                if (obj == null || !(obj instanceof Map))
                    objectMap = new HashMap<String, Object>();
                else
                    objectMap = (Map) obj;
                //使用helper获取值，不使用KeyWordMapper默认获取方式
                Object value = parseValue(kv[1], entry.getValue());
                objectMap.put(kv[1], value);
                conditions_new.put(kv[0], objectMap);
                res.put(kv[0], objectMap);
            } else if (entry.getValue() instanceof Map) {
                //只要值为Map则代表表关联查询
                res.put(entry.getKey(), (Map) entry.getValue());
            }
        }
        //指定了关联表字段查询也进行表关联处理
        for (String field : config.getIncludes()) {
            String[] asEs = asName(field);
            field = asEs[0];
            if (field.contains("(")) {

                Matcher matcher = pat.matcher(field);
                if (matcher.find()) {
                    field = matcher.group();
                }
            }
            if (field.contains(".")) {//表关联字段查询
                String[] info = field.split("[.]");
                String tableName = info[0];
                if (!res.containsKey(tableName)) {
                    res.put(tableName, new HashMap<String, Object>());
                }
            }
        }
        if (conditions_new.size() > 0)
            conditions.putAll(conditions_new);
        return res;
    }

    public String conditionTemplate(final SqlRenderConfig config) {
        StringBuilder builder = new StringBuilder();
        KeyWordsMapper.Mapper mapper;
        int i = 0;
        for (Map.Entry<String, Object> e : config.getParams().entrySet()) {
            mapper = getKeyWordsMapper().getMapper(e.getKey());
            String fieldName = mapper.fieldName(e.getKey());
            String taName = "u";
            if (fieldName.contains(".")) {
                String infs[] = fieldName.split("[.]");
                taName = infs[0];
                fieldName = infs[1];
            }
            FieldMetaData metaData = getMetaData().getField(fieldName);
            if (metaData == null || !mapper.canUse(metaData))
                continue;
            if (i++ != 0)
                builder.append(" and ");
            builder.append(mapper.template(metaData, taName));
        }
        return builder.toString();
    }

    /**
     * 生成条件判断模板，基于freemarker条件判断。将所有支持的语法添加入条件，具体语法支持 由KeyWordsMapper定义
     *
     * @return 条件判断模板
     */
    public String conditionTemplate() {

        StringBuilder builder = new StringBuilder();
        //取消使用拼接条件，由conditionTemplate(config)方法根据具体查询参数进行拼接
        //       TableMetaData table = getMetaData();
        //        KeyWordsMapper.Mapper mapper;
        //        Set<FieldMetaData> fields = table.getFields();
        //        for (FieldMetaData field : fields) {
        //            Set<String> kws = KeyWordsMapper.getKws();//支持的语法
        //            mapper = KeyWordsMapper.getMapper("");//先拼接默认语法：key=value
        //            builder.append("<#if ").append(field.getName()).append("??>\n");
        //            builder.append("\t");
        //            builder.append(" and u.").append(mapper.template(field)).append("\n");
        //            builder.append("</#if>\n");
        //            //拼接所有支持的特殊关键字如：$IN,$LIKE...
        //            for (String kw : kws) {
        //                mapper = KeyWordsMapper.getMapper(kw);
        //                if (!mapper.canUse(field))//字段类型是否适用，入 string类型不适用于$GT,$LT 将不进行拼接
        //                    continue;
        //                String name_key = field.getName() + kw;
        //                builder.append("<#if ").append(name_key).append("??>\n");
        //                builder.append("\t");
        //                builder.append(" and u.").append(mapper.template(field)).append("\n");
        //                builder.append("</#if>\n");
        //            }
        //        }
        builder.append(" ${_conditions!''}");
        //拼接表关联定义条件
        builder.append(" ${_tableSql!''}");
        //拼接表关联查询条件
        builder.append(" ${_foreignSql!''}");
        //用户自定义查询条件
        // builder.append(" ${").append(customSqlKeyWord).append("!''}");

        return builder.toString();
    }

    /**
     * 获取表名条件
     *
     * @return 表名
     */
    public String tablesNames() {
        TableMetaData table = getMetaData();
        StringBuilder builder = new StringBuilder();
        builder.append(table.getName()).append(" u ${_tableNames!''}\n");
        return builder.toString();
    }

    @Override
    public void init() throws Exception {
        //初始化模板
        StringBuilder builder = new StringBuilder();
        builder.append("\n<#if !").append("__for_update").append("??>\n");
        builder.append("SELECT * FROM ( SELECT row_.*, rownum rownum_ FROM (");
        builder.append("\n</#if>");
        //查询字段
        builder.append(" SELECT ${__SELECTFIELDS!'u.*'} FROM ");
        //查询表
        builder.append(tablesNames());
        //查询条件模板
        builder.append(conditionTemplate());
        //分组判断模板
        builder.append("\n<#if ").append(SqlRender.GROUP_BY_KEY).append("??>\n");
        builder.append("\t GROUP BY ${").append(SqlRender.GROUP_BY_KEY).append("}");
        builder.append("\n</#if>");
        //排序判断模板
        builder.append("\n<#if ").append(SqlRender.SORT_FIELD_KEY).append("??>\n");
        builder.append("\t ORDER BY ${").append(SqlRender.SORT_FIELD_KEY).append("}");
        builder.append(" ${").append(SqlRender.SORT_ORDER_KEY).append("!''}");
        builder.append("\n</#if>");
        builder.append("\n<#if !").append("__for_update").append("??>\n");
        builder.append(") row_ )");
        builder.append("\n</#if>");
        //分页判断模板
        builder.append("\n<#if ").append(SqlRender.PAGE_FIRST_RESULT_KEY).append("??>\n");
        builder.append("\t WHERE rownum_ <= (@{")
                .append(SqlRender.PAGE_MAX_RESULTS_KEY).append("}*(@{")
                .append(SqlRender.PAGE_PAGE_INDEX_KEY).append("}+1)) and rownum_ > @{")
                .append(SqlRender.PAGE_FIRST_RESULT_KEY).append("}");
        builder.append("\n</#if>");
        builder.append(" ${__for_update???string(' FOR UPDATE','')}");
        setTemplate(builder.toString());
        initTableHash();
    }

    public String[] asName(String field) {
        String[] res = new String[2];
        if (field.contains("$AS")) {//自定义AS
            String[] info = field.split("\\$AS");
            res[0] = info[0].trim();
            res[1] = new StringBuilder(" AS ").append(getQuotesStart()).append(replaceKeyWord(info[1].trim())).append(getQuotesEnd()).toString();
        } else {
            res[0] = field;
            res[1] = new StringBuilder(" AS ").append(getQuotesStart()).append(field).append(getQuotesEnd()).toString();
        }
        return res;
    }

    public String replaceKeyWord(String str) {
        return str.replace("-", "").replace("\"", "").replace("'", "");
    }

}


