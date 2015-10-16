package org.webbuilder.web.service.form;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.DataBaseStorage;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.exec.ExecutorConfig;
import org.webbuilder.utils.db.exec.helper.AttrHelper;
import org.webbuilder.utils.db.exec.helper.MapResultHelper;
import org.webbuilder.utils.db.exec.helper.ResultHelper;
import org.webbuilder.utils.db.render.conf.helper.FieldHelper;
import org.webbuilder.utils.storage.Storage;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.dao.GenericMapper;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.dao.form.FormMapper;
import org.webbuilder.web.po.form.CustomFormData;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.service.storage.StorageService;
import org.apache.ibatis.transaction.managed.ManagedTransaction;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-08-01 0001.
 */
@Service
public class CustomFormService extends GenericService<CustomFormData, String> {

    public final static String CACHE_KEY = "custom_form_data";

    @Resource
    protected FormMapper formMapper;

    @Resource
    protected FormService formService;

    @Autowired
    protected ApplicationContext context;

    /**
     * 获取默认的数据映射接口
     *
     * @return 数据映射接口
     */
    @Override
    protected <T extends GenericMapper<CustomFormData, String>> T getMapper() {
        return null;
    }

    @Resource
    protected StorageService storageService;

    public Form getForm(String id) throws Exception {
        Form form = formService.selectByPk(id);
        if (form == null) {
            throw new BusinessException(id + "表单不存在!");
        }
        return form;
    }

    public Connection getCustomConnection(String name) {
        //获取指定的session
        SqlSessionTemplate session;
        Connection connection;
        try {
            session = ContextLoader.getCurrentWebApplicationContext().getBean(name, SqlSessionTemplate.class);
            connection = getConnection(session);
        } catch (Exception e) {
            try {
                session = context.getBean(name, SqlSessionTemplate.class);
                connection = getConnection(session);
            } catch (Exception e2) {
                throw new BusinessException("获取sqlSession失败:" + name, e);
            }
        }
        return connection;
    }

    public String insert(String form_id, Map<String, Object> data) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        String uid = (String) data.get("u_id");
        if (uid == null) {
            uid = GenericPo.createUID();
            data.put("u_id", uid);
        }
        data.put("create_date", new Date());
        config.setData(data);
        table.insert(config);
        return uid;
    }

    @CacheEvict(value = CACHE_KEY, key = "#form_id+'.'+#u_id")
    public int update(String form_id, String u_id, Map<String, Object> data) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        data.put("update_date", new Date());
        config.setData(data);
        config.addParam("u_id", u_id);
        //清空自定义的缓存策略  custom_form_data_id_#form_id#.#u_id#
        Storage<String, Map> storage = storageService.getStorage(CACHE_KEY.concat("_id_").concat(form_id).concat(".").concat(u_id), Map.class);
        storage.clear();
        return table.update(config);
    }

    @Cacheable(value = CACHE_KEY, key = "#form_id+'.'+#u_id")
    public Map selectByPk(String form_id, String u_id) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig<Map> config = new TableMetaData.ExeSqlConfig(session);
        config.addParam("u_id", u_id);
        return table.selectOne(config);
    }

    /**
     * 根据主键查询并指定查询字段和排除字段
     *
     * @param form_id  表单id
     * @param u_id     主键
     * @param includes 要进行查询的字段
     * @param excludes 不查询的字段
     * @return 查询结果
     * @throws Exception
     */
    public Map selectByPk(String form_id, String u_id, List<String> includes, List<String> excludes) throws Exception {
        if (includes.size() == 0 && excludes.size() == 0)
            return this.selectByPk(form_id, u_id);
        if (StringUtil.isNullOrEmpty(u_id))
            return null;
        //获取缓存 custom_form_data_id_#form_id#.#u_id#
        Storage<String, Map> storage = storageService.getStorage(CACHE_KEY.concat("_id_").concat(form_id).concat(".").concat(u_id), Map.class);
        String key = "_includes_".concat(String.valueOf(includes.hashCode())).concat("_excludes_".concat(String.valueOf(excludes.hashCode())));
        Map data = storage.get(key);
        if (data == null) {
            Form form = getForm(form_id);
            TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
            Connection session = getCustomConnection(form.getSession_name());
            TableMetaData.ExeSqlConfig<Map> config = new TableMetaData.ExeSqlConfig(session);
            config.include(includes.toArray(new String[includes.size()]))
                    .exclude(excludes.toArray(new String[excludes.size()]));
            config.addParam("u_id", u_id);
            data = table.selectOne(config);
            if (data != null) {
                storage.put(key, data);
            }
        }
        return data;
    }

    public List<Map> select(String form_id, Map<String, Object> params) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig<Map> config = new TableMetaData.ExeSqlConfig(session);
        config.addParam(params);
        return table.select(config);
    }

    public List<Map> select(String form_id, Map<String, Object> params, String[] includes, String[] excludes) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig<Map> config = new TableMetaData.ExeSqlConfig(session);
        config.include(includes).exclude(excludes);
        config.addParam(params);
        return table.select(config);
    }

    @Override
    @Deprecated
    public int total(Map<String, Object> conditions) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    public int total(String form_id, Map<String, Object> params) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        config.addParam(params);
        return table.total(config);
    }

    public Map<String, Object> selectPager(String form_id, PageUtil pageUtil) throws Exception {
        Form form = getForm(form_id);
        TableMetaData table = DataBaseStorage.getDataBase(form.getDb_name()).getTable(form.getTable_name());
        Connection session = getCustomConnection(form.getSession_name());
        TableMetaData.ExeSqlConfig config = new TableMetaData.ExeSqlConfig(session);
        config.getRenderConfig().setExcludes(pageUtil.getExcludes());
        config.getRenderConfig().setIncludes(pageUtil.getIncludes());
        config.addParam(pageUtil.params());
        // 数据总数
        int total = table.total(config);
        config.addParam(pageUtil.params(total));
        List<Map> data = table.select(config);
        // 将数据json化
        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        result.put("total", total);
        return result;
    }


    @Override
    @Deprecated
    public int insert(CustomFormData data) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public int update(CustomFormData data) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public CustomFormData selectByPk(String pk) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public List<CustomFormData> select(Map<String, Object> conditions) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

    @Override
    @Deprecated
    public Map<String, Object> selectPager(PageUtil pageUtil) throws Exception {
        throw new BusinessException("此服务已关闭访问!");
    }

}
