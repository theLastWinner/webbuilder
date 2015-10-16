package org.webbuilder.utils.db.def;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.exec.ExecutorConfig;
import org.webbuilder.utils.db.exec.SqlExecutor;
import org.webbuilder.utils.db.exec.helper.AttrHelper;
import org.webbuilder.utils.db.exec.helper.ResultHelper;
import org.webbuilder.utils.db.exec.jdbc.JdbcSqlExecutor;
import org.webbuilder.utils.db.render.DataTypeMapper;
import org.webbuilder.utils.db.render.SqlRender;
import org.webbuilder.utils.db.render.SqlRenderFactory;
import org.webbuilder.utils.db.render.SqlRenderType;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数据库表定义
 * Created by 浩 on 2015-06-23 0023.
 */
public class TableMetaData implements Serializable {

    /**
     * 所属数据库
     */
    private DataBase dataBase;

    /**
     * render工厂
     */
    private SqlRenderFactory renderFactory;

    /**
     * 可用的render集合
     */
    private Map<SqlRenderType, SqlRender> renders = new ConcurrentHashMap<SqlRenderType, SqlRender>();

    /**
     * 数据类型映射器
     */
    private DataTypeMapper dataTypeMapper;

    /**
     * 表名
     */
    private String name;

    /**
     * 备注
     */
    private String remark;

    /**
     * 定义主体,用于初始化前的定义，可使用xml，html，json定义
     */
    private Object defineContent;

    /**
     * 定义主体的类型,默认html定义
     */
    private DefineContentType defineContentType = DefineContentType.HTML;

    /**
     * 表字段
     */
    private final FieldList fields = new FieldList();

    /**
     * 表关联
     */
    private final Set<Foreign> foreigns = new LinkedHashSet<Foreign>();

    private boolean dataUpdated;

    public TableMetaData() {
    }

    public TableMetaData(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }

    protected void setRenders(Map<SqlRenderType, SqlRender> renders) {
        this.renders = renders;
    }

    @Override
    public TableMetaData clone() {
        TableMetaData metaData = new TableMetaData(this.getName());
        metaData.setDataBase(this.getDataBase());
        metaData.setRenderFactory(this.getRenderFactory());
        metaData.setDataTypeMapper(this.getDataTypeMapper());
        for (FieldMetaData fieldMetaData : getFields()) {
            metaData.getFields().add(fieldMetaData.clone());
        }
        metaData.setRenders(this.renders);
        metaData.getForeigns().addAll(new LinkedHashSet<>(this.getForeigns()));
        metaData.setDefineContent(this.getDefineContent());
        metaData.setDefineContentType(this.getDefineContentType());
        return metaData;
    }

    public TableMetaData(String name) {
        this(name, null);
    }

    public DataBase getDataBase() {
        return dataBase;
    }

    public void setDataBase(DataBase dataBase) {
        this.dataBase = dataBase;
    }

    public FieldMetaData getField(String fieldName) {
        return fields.get(fieldName);
    }


    public SqlRenderFactory getRenderFactory() {
        return renderFactory;
    }

    public void setRenderFactory(SqlRenderFactory renderFactory) {
        this.renderFactory = renderFactory;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Object getDefineContent() {
        return defineContent;
    }

    public void setDefineContent(Object defineContent) {
        this.defineContent = defineContent;
    }

    public Set<FieldMetaData> getFields() {
        return fields;
    }

    public TableMetaData addField(FieldMetaData metaData) throws Exception {
        getFields().add(metaData);
        return this;
    }

    public Set<Foreign> getForeigns() {
        return foreigns;
    }

    public DefineContentType getDefineContentType() {
        return defineContentType;
    }

    public void setDefineContentType(DefineContentType defineContentType) {
        this.defineContentType = defineContentType;
    }

    /**
     * 获取关联表信息，如果未获取到返回null
     *
     * @param name 关联的表名
     * @return 关联表信息
     */
    public Foreign getforeign(String name) {
        for (Foreign foreign : getForeigns()) {
            if (foreign.getTargetTable().equals(name))
                return foreign;
        }
        return null;
    }

    public long tableHashCode() {
        String key = String.valueOf(getFields().hashCode()) + String.valueOf(getForeigns().hashCode());
        return key.hashCode();
    }

    public DataTypeMapper getDataTypeMapper() {
        return dataTypeMapper;
    }

    public void setDataTypeMapper(DataTypeMapper dataTypeMapper) {
        this.dataTypeMapper = dataTypeMapper;
    }

    /**
     * 根据renderType获取SqlRender
     *
     * @param renderType 生成器类型
     * @return 对应的render
     * @throws Exception 获取失败
     */
    public SqlRender render(SqlRenderType renderType) throws Exception {
        if (renders.size() == 0)
            initRender();
        return renders.get(renderType);
    }

    /**
     * 初始化render
     *
     * @throws Exception
     */
    public void initRender() throws Exception {
        if (getRenderFactory() == null) {
            throw new ClassNotFoundException("SqlRenderFactory not found!");
        }
        List<SqlRenderType> renderTypes = getRenderFactory().supportRenders();
        for (SqlRenderType renderType : renderTypes) {
            SqlRender render = getRenderFactory().getRender(renderType, this);
            render.init();
            renders.put(renderType, render);
        }
    }

    /**
     * 执行select操作，返回list，根据配置，自动生成sql语句后执行
     *
     * @param config 配置
     * @param <T>    select操作返回的对象类型
     * @return 查询结果集
     * @throws Exception 执行异常
     */
    public <T> List<T> select(ExeSqlConfig<T> config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.SELECT);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig<T> executorConfig = config.getExecutorConfig();
        return config.getSqlExecutor().select(executorConfig);
    }

    public int total(ExeSqlConfig config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.TOTAL);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        try {
            Map<String, Object> data = (Map<String, Object>) config.getSqlExecutor().selectOne(executorConfig);
            return StringUtil.toInt(data.get("total"));
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 查询单个结果，返回对象
     *
     * @param config 配置
     * @param <T>    查询结果类型
     * @return 查询结果
     * @throws Exception 执行异常
     */
    public <T> T selectOne(ExeSqlConfig<T> config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.SELECT);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig<T> executorConfig = config.getExecutorConfig();
        return config.getSqlExecutor().selectOne(executorConfig);
    }

    public int update(ExeSqlConfig config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.UPDATE);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        setDataUpdated(true);//标记数据已更新
        return config.getSqlExecutor().update(executorConfig);
    }

    public int delete(ExeSqlConfig config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.DELETE);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        setDataUpdated(true);//标记数据已更新
        return config.getSqlExecutor().update(executorConfig);
    }

    public int insert(ExeSqlConfig config) throws Exception {
        config.getExecutorConfig().setMetaData(this);
        SqlRender render = this.render(SqlRenderType.INSERT);
        config.getExecutorConfig().setSqlInfo(render.render(config.getRenderConfig()));
        ExecutorConfig executorConfig = config.getExecutorConfig();
        setDataUpdated(true);//标记数据已更新
        return config.getSqlExecutor().update(executorConfig);
    }

    /**
     * 执行sql操作配置
     *
     * @param <T> 操作数据的类型
     */
    public static class ExeSqlConfig<T> {
        //默认的sql执行器
        public static SqlExecutor DEFAULT_SQL_EXECUTOR = new JdbcSqlExecutor();
        //sql执行器
        private SqlExecutor sqlExecutor = null;
        //sql生成器配置
        private SqlRenderConfig renderConfig = new SqlRenderConfig();
        //sql执行器配置
        private ExecutorConfig<T> executorConfig = new ExecutorConfig<T>();

        public ExeSqlConfig(List<T> datas, Object session) {
            executorConfig.setSession(session);
            renderConfig.setData(datas);
        }

        public ExeSqlConfig() {

        }

        public ExeSqlConfig(Object session) {
            executorConfig.setSession(session);
        }

        public ExeSqlConfig setData(T data) {
            renderConfig.setData(data);
            return this;
        }

        public ExeSqlConfig setDatas(List<T> datas) {
            renderConfig.setData(datas);
            return this;
        }

        public void setSqlExecutor(SqlExecutor sqlExecutor) {
            this.sqlExecutor = sqlExecutor;
        }

        public SqlExecutor getSqlExecutor() {
            if (sqlExecutor == null)
                sqlExecutor = DEFAULT_SQL_EXECUTOR;
            return sqlExecutor;
        }

        public ExeSqlConfig setResultHelper(ResultHelper<T> helper) {
            getExecutorConfig().setResultHelper(helper);
            return this;
        }

        public ExeSqlConfig addParam(String key, Object val) {
            getRenderConfig().getParams().put(key, val);
            return this;
        }

        public ExeSqlConfig addParam(Map<String, Object> params) {
            getRenderConfig().getParams().putAll(params);
            return this;
        }

        public ExeSqlConfig include(String... name) {
            getRenderConfig().getIncludes().addAll(Arrays.asList(name));
            return this;
        }

        public ExeSqlConfig exclude(String... name) {
            getRenderConfig().getExcludes().addAll(Arrays.asList(name));
            return this;
        }


        public ExeSqlConfig addFieldHelper(String attrName, AttrHelper helper) {
            getExecutorConfig().getResultHelper().getAttrHelpers().put(attrName, helper);
            return this;
        }

        public ExeSqlConfig selectForUpdate() {
            getRenderConfig().setSelectForUpdate(true);
            return this;
        }

        public SqlRenderConfig getRenderConfig() {
            return renderConfig;
        }

        public ExecutorConfig getExecutorConfig() {
            return executorConfig;
        }
    }

    /**
     * 表关联定义
     */
    public static class Foreign implements Serializable {
        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return this.hashCode() == obj.hashCode();
        }

        //关联信息正则解析:[ ]
        private static Pattern pat = Pattern.compile("(?<=\\[)(.+?)(?=])");
        // like  user.id=id or user[name=name,age=age]
        private String key;
        //关联条件
        private Map<String, String> condition = new LinkedHashMap<String, String>();
        //管理的表
        private String targetTable;

        //关联模式
        private MODE mode = MODE.LEFT_JOIN;

        //关联类型，尽量只使用ONE2ONE(1对1)，在1对多的情况下，可使用ResultHelper在查询结束后进行处理
        private TYPE type = TYPE.ONE2ONE;

        public enum TYPE {
            ONE2ONE, ONE2MANY
        }

        public Foreign(String key, TYPE type) {
            this(key);
            this.type = type;
        }
        public Foreign(){}
        public Foreign(String key) {
            this.key = key;
            //user.id=id格式语法
            if (key.contains(".") && !key.contains("[")) {
                String[] strs = key.split("[.]");
                targetTable = strs[0];
                String[] cdt = strs[1].split("[=]");
                condition.put(cdt[0], cdt[1]);
            }
            //user[user_id=id,user_profile_id=user_profile.id] 格式语法
            else if (key.contains("[")) {
                Matcher mat = pat.matcher(key);
                String cdts = "";
                if (mat.find())
                    cdts = mat.group();
                targetTable = key.split("[\\[]")[0];
                String[] cdts_ = cdts.split(",");
                for (String s : cdts_) {
                    String[] cdt = s.split("[=]");
                    condition.put(cdt[0], cdt[1]);
                }
            }
        }

        public MODE getMode() {
            return mode;
        }

        public void setMode(MODE mode) {
            this.mode = mode;
        }

        public Map<String, String> getCondition() {
            return condition;
        }

        public String getTargetTable() {
            return targetTable;
        }

        public TYPE getType() {
            return type;
        }

        public void setType(TYPE type) {
            this.type = type;
        }

        public enum MODE {
            JOIN,//: 如果表中有至少一个匹配，则返回行
            LEFT_JOIN,//即使右表中没有匹配，也从左表返回所有的行
            RIGHT_JOIN,// 即使左表中没有匹配，也从右表返回所有的行
            FULL_JOIN// 只要其中一个表中存在匹配，就返回
            ;

            @Override
            public String toString() {
                return super.toString().replace("_", " ");
            }
        }
    }

    @Override
    public String toString() {
        return new StringBuilder(getName()).append(" in [").append(dataBase).append("]").toString();
    }

    private class FieldList extends LinkedHashSet<FieldMetaData> {
        private Map<String, FieldMetaData> fieldMetaDataMap = new ConcurrentHashMap<String, FieldMetaData>();

        @Override
        public boolean add(FieldMetaData fieldMetaData) {
            try {
                fieldMetaData.setDataType(getDataTypeMapper().dataType(fieldMetaData));
            } catch (Exception e) {
            }
            fieldMetaDataMap.put(fieldMetaData.getName(), fieldMetaData);
            return super.add(fieldMetaData);
        }

        @Override
        public boolean remove(Object o) {
            fieldMetaDataMap.remove(o);
            return super.remove(o);
        }

        @Override
        public boolean addAll(Collection<? extends FieldMetaData> c) {
            for (FieldMetaData metaData : c) {
                try {
                    metaData.setDataType(getDataTypeMapper().dataType(metaData));
                } catch (Exception e) {
                }
                fieldMetaDataMap.put(metaData.getName(), metaData);
            }
            return super.addAll(c);
        }

        public FieldMetaData get(String name) {
            return fieldMetaDataMap.get(name);
        }
    }

    public boolean isDataUpdated() {
        return dataUpdated;
    }

    public void setDataUpdated(boolean dataUpdated) {
        this.dataUpdated = dataUpdated;
    }

    public enum DefineContentType implements Serializable {
        HTML, XML, JSON, DB, CLASS
    }

}
