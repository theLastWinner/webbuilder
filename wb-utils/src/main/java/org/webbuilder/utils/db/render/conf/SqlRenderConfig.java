package org.webbuilder.utils.db.render.conf;

import org.webbuilder.utils.db.def.TableMetaData;

import java.util.*;

/**
 * Sql构建器配置
 * Created by 浩 on 2015-07-04 0004.
 */
public class SqlRenderConfig {

    public SqlRenderConfig() {
        this(null, new HashMap<String, Object>());
    }

    public SqlRenderConfig(Object data, Map<String, Object> params) {
        this.data = data;
        this.params = params;
    }

    public SqlRenderConfig(Object datas) {
        this(datas, new HashMap<String, Object>());
    }

    public SqlRenderConfig(Map<String, Object> params) {
        this(null, params);
    }

    private TableMetaData.Foreign.MODE foreignMode = TableMetaData.Foreign.MODE.LEFT_JOIN;

    //要操作的数据，一般用于insert，update
    private Object data;

    //执行条件参数，用于进行where语句生成
    private Map<String, Object> params;

    //进行操作的字段
    private Set<String> includes = new LinkedHashSet<>();

    //例外字段（主要用于select，包含在此中的字段将不进行查询）
    private Set<String> excludes = new LinkedHashSet<>();

    //forUpdate
    private boolean selectForUpdate = false;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, Object> getParams() {
        if (params == null)
            params = new HashMap<String, Object>();
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public SqlRenderConfig addParam(String key, Object value) {
        getParams().put(key, value);
        return this;
    }

    public Set<String> getIncludes() {
        return includes;
    }

    public void setIncludes(Set<String> includes) {
        this.includes = includes;
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }

    @Override
    public int hashCode() {
        return params.hashCode();
    }

    public TableMetaData.Foreign.MODE getForeignMode() {
        return foreignMode;
    }

    public void setForeignMode(TableMetaData.Foreign.MODE foreignMode) {
        this.foreignMode = foreignMode;
    }

    /**
     * 克隆配置，在sqlRender实现类进行操作的时使用的配置应该是克隆后的数据
     *
     * @return 克隆后的配置实例
     */
    public SqlRenderConfig clone() {
        SqlRenderConfig config = new SqlRenderConfig();
        config.setData(this.getData());
        config.setParams(new HashMap<>(getParams()));
        config.setIncludes(new LinkedHashSet<>(getIncludes()));
        config.setExcludes(new LinkedHashSet<>(getExcludes()));
        config.setSelectForUpdate(this.isSelectForUpdate());
        config.setForeignMode(this.getForeignMode());
        return config;
    }


    public boolean isSelectForUpdate() {
        return selectForUpdate;
    }

    public void setSelectForUpdate(boolean selectForUpdate) {
        this.selectForUpdate = selectForUpdate;
    }
}
