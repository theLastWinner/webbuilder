package org.webbuilder.utils.db.render;

import org.webbuilder.utils.db.def.SqlInfo;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.render.conf.SqlRenderConfig;
import org.webbuilder.utils.db.render.conf.SqlRenderHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by 浩 on 2015-07-04 0004.
 */
public abstract class SqlRender {
    public Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    //关键字语法映射器
    private KeyWordsMapper keyWordsMapper;

    //最后一次初始化时，table的hash，用于检测table是否发生了改变
    private long lastInitMetaHashCode = 0;

    private Class<? extends SqlInfo> sqlInfoType;
    /**
     * 分页查询所需firstResult
     */
    public static final String PAGE_FIRST_RESULT_KEY = "firstResult";

    /**
     * 分页查询所需maxResults
     */
    public static final String PAGE_MAX_RESULTS_KEY = "maxResults";

    /**
     * 分页查询所需pageIndex
     */
    public static final String PAGE_PAGE_INDEX_KEY = "pageIndex";

    /**
     * 排序关键字
     */
    public static final String SORT_FIELD_KEY = "sortField";

    /**
     * 正反序关键字
     */
    public static final String SORT_ORDER_KEY = "sortOrder";

    /**
     * 分组查询
     */
    public static final String GROUP_BY_KEY = "groupBy";


    /**
     * 所属Table
     */
    private TableMetaData metaData;

    public SqlRender(TableMetaData metaData) {
        this.metaData = metaData;
    }

    public TableMetaData getMetaData() {
        return metaData;
    }

    /**
     * 传入配置，返回Sql信息
     *
     * @param config SqlRenderConfig配置实体
     * @return SqlInfo
     * @throws Exception
     */
    public abstract SqlInfo render(SqlRenderConfig config) throws Exception;

    /**
     * 注册各种工具实现
     *
     * @param helper SqlRenderHelper 由继承SqlRender的子类自由定义
     * @return 应该返回this实现类
     * @throws Exception
     */
    public abstract SqlRender helper(SqlRenderHelper helper) throws Exception;

    /**
     * 初始化工作，当此Render被注册到Factory时自动调用
     *
     * @throws Exception 初始化异常
     */
    public abstract void init() throws Exception;

    public boolean tableChanged() {
        return getMetaData().tableHashCode() != lastInitMetaHashCode;
    }

    public void initTableHash() {
        lastInitMetaHashCode = getMetaData().tableHashCode();
    }

    public void setMetaData(TableMetaData metaData) {
        this.metaData = metaData;
        initTableHash();
    }

    public String getQuotesStart(){
        return "";
    }

    public String getQuotesEnd(){
        return "";
    }

    public KeyWordsMapper getKeyWordsMapper() {
        return keyWordsMapper;
    }

    public void setKeyWordsMapper(KeyWordsMapper keyWordsMapper) {
        this.keyWordsMapper = keyWordsMapper;
    }

    public Class<? extends SqlInfo> getSqlInfoType() {
        if (sqlInfoType == null)
            sqlInfoType = SqlInfo.class;
        return sqlInfoType;
    }

    public SqlInfo createSqlInfo(String sql) {
        try {
            return getSqlInfoType().getConstructor(String.class).newInstance(sql);
        } catch (Exception e) {
            LOGGER.error("create createSqlInfo:" + getSqlInfoType() + " error,use Default!");
        }
        return new SqlInfo(sql);
    }

    public void setSqlInfoType(Class<? extends SqlInfo> sqlInfoType) {
        this.sqlInfoType = sqlInfoType;
    }
}
