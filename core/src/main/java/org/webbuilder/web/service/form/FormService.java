package org.webbuilder.web.service.form;

import org.webbuilder.utils.base.StringUtil;
import org.webbuilder.utils.db.def.DataBase;
import org.webbuilder.utils.db.def.DataBaseStorage;
import org.webbuilder.utils.db.def.FieldMetaData;
import org.webbuilder.utils.db.def.TableMetaData;
import org.webbuilder.utils.db.def.parser.annotation.FieldMeta;
import org.webbuilder.utils.db.def.parser.annotation.TableMeta;
import org.webbuilder.utils.db.imp.mysql.MySqlDataBase;
import org.webbuilder.utils.db.imp.oracle.OracleDataBase;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.exception.BusinessException;
import org.webbuilder.web.core.service.GenericService;
import org.webbuilder.web.po.form.Form;
import org.webbuilder.web.dao.form.FormMapper;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import java.sql.Connection;
import java.util.*;

/**
 * 自定义表单服务类
 * Created by generator
 *
 * @Copyright 2015 www.cqtaihong.com Inc. All rights reserved.
 * 注意：本内容仅限于重庆泰虹医药网络发展有限公司内部传阅，禁止外泄以及用于其他的商业目的
 */
@Service
public class FormService extends GenericService<Form, String> {

    public static final Set<FieldMetaData> DEFAULT_FIELD = new LinkedHashSet<>();


    private static final String CACHE_KEY = "form";
    static {
        FieldMetaData u_id = new FieldMetaData("u_id", String.class);
        u_id.setPrimaryKey(true);
        u_id.setRemark("id主键");
        u_id.setAlias("主键");
        u_id.setCanUpdate(false);
        u_id.setLength(64);

        FieldMetaData flow_instance_id = new FieldMetaData("flow_instance_id", String.class);
        flow_instance_id.setPrimaryKey(false);
        flow_instance_id.setRemark("流程实例id");
        flow_instance_id.setAlias("流程实例id");
        flow_instance_id.setCanUpdate(false);
        flow_instance_id.setLength(256);

        FieldMetaData now_step_id = new FieldMetaData("now_step_id", String.class);
        now_step_id.setPrimaryKey(false);
        now_step_id.setRemark("当前环节id");
        now_step_id.setAlias("当前环节id");
        now_step_id.setCanUpdate(true);
        now_step_id.setLength(256);

        FieldMetaData now_step_name = new FieldMetaData("now_step_name", String.class);
        now_step_name.setPrimaryKey(false);
        now_step_name.setRemark("当前环节名称");
        now_step_name.setAlias("当前环节名称");
        now_step_name.setCanUpdate(true);
        now_step_name.setLength(256);

        FieldMetaData creator_id = new FieldMetaData("creator_id", String.class);
        creator_id.setPrimaryKey(false);
        flow_instance_id.setRemark("创建者id");
        flow_instance_id.setAlias("创建者id");
        flow_instance_id.setCanUpdate(false);
        flow_instance_id.setLength(256);

        FieldMetaData create_date = new FieldMetaData("create_date", Date.class);
        create_date.setNotNull(true);
        create_date.setCanUpdate(false);
        create_date.setRemark("创建时间");

        FieldMetaData update_date = new FieldMetaData("update_date", Date.class);
        update_date.setNotNull(false);
        update_date.setRemark("最后一次修改时间");
        DEFAULT_FIELD.add(u_id);
        DEFAULT_FIELD.add(create_date);
        DEFAULT_FIELD.add(update_date);
        DEFAULT_FIELD.add(creator_id);
//        DEFAULT_FIELD.add(now_step_id);
//        DEFAULT_FIELD.add(now_step_name);
//        DEFAULT_FIELD.add(flow_instance_id);
    }

    //默认数据映射接口
    @Resource
    protected FormMapper formMapper;

    @Override
    protected FormMapper getMapper() {
        return this.formMapper;
    }

    @Override
    @Cacheable(value = CACHE_KEY,key = "#id")
    public Form selectByPk(String id) throws Exception {
        return super.selectByPk(id);
    }

    @Override
    public int insert(Form data) throws Exception {
        Form old = this.selectByPk(data.getU_id());
        if (old != null)
            throw new BusinessException("该表单已存在!");
        saveTable(data);
        data.setCreate_date(new Date());
        return super.insert(data);
    }

    @Override
    @CacheEvict(value = CACHE_KEY,key = "#data.u_id")
    public int update(Form data) throws Exception {
        Form old = this.selectByPk(data.getU_id());
        if (old == null)
            throw new BusinessException("该表单不存在!");
        saveTable(data);
        data.setUpdate_date(new Date());
        return super.update(data);
    }

    protected void saveTable(Form data) throws Exception {
        //获取数据库，如果未获取到，则新建
        DataBase dataBase = DataBaseStorage.getDataBase(data.getDb_name());
        if (dataBase == null) {
            if ("oracle".equals(data.getDb_type())) {
                dataBase = DataBaseStorage.register(new OracleDataBase(data.getDb_name()));
            } else if ("mysql".equals(data.getDb_type())) {
                dataBase = DataBaseStorage.register(new MySqlDataBase(data.getDb_name()));
            } else {
                throw new BusinessException("不支持的数据库类型:" + data.getDb_type());
            }
        }
        //获取指定的session
        SqlSessionTemplate session;
        Connection connection;
        try {
            session = ContextLoader.getCurrentWebApplicationContext().getBean(data.getSession_name(), SqlSessionTemplate.class);
            connection = getConnection(session);
        } catch (Exception e) {
            throw new BusinessException("获取sqlSession失败:" + data.getSession_name());
        }

        TableMetaData metaData = dataBase.getTable(data.getTable_name());
        if (metaData == null) {
            metaData = new TableMetaData(data.getTable_name());
            metaData.setDefineContent(data.getContent());
            metaData.setDefineContentType(TableMetaData.DefineContentType.HTML);
            dataBase.getParser().parse(metaData);
            //设置表关联  user_info[userId=id]
            initForeign(metaData,data.getForeigns());
            metaData.getFields().addAll(DEFAULT_FIELD);
            dataBase.putTable(metaData);
            dataBase.createTable(metaData, connection);
        } else {
            TableMetaData metaData_new = new TableMetaData(data.getTable_name());
            metaData_new.setDefineContent(data.getContent());
            metaData_new.setDefineContentType(TableMetaData.DefineContentType.HTML);
            dataBase.getParser().parse(metaData_new);
            //设置表关联  user_info[userId=id]
            initForeign(metaData_new,data.getForeigns());

            metaData_new.getFields().addAll(DEFAULT_FIELD);
            try {
                dataBase.updateTable(metaData_new, connection);
                dataBase.putTable(metaData_new);
            } catch (Exception e) {
                if (e.getMessage().contains("表或视图不存在")) {
                    dataBase.putTable(metaData_new);
                    dataBase.createTable(metaData_new, connection);
                } else {
                    throw e;
                }
            }
        }
    }

    public void initForeign(TableMetaData tableMetaData,String foregin_conf){
        if (!StringUtil.isNullOrEmpty(foregin_conf)) {
            String foregins_arr[] = foregin_conf.trim().split("[\n]");
            for (String foregin : foregins_arr) {
                tableMetaData.getForeigns().add(new TableMetaData.Foreign(foregin));
            }
        }
    }

    @Override
    public int delete(String s) throws Exception {
        throw new BusinessException("此服务已关闭!");
    }


}
