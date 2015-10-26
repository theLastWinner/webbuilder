package org.webbuilder.web.core.service;

import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.core.bean.PageUtil;
import org.webbuilder.web.core.bean.ValidResults;
import org.webbuilder.web.core.dao.GenericMapper;

import javax.annotation.Resource;
import javax.validation.ValidationException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局服务类，所有Service都应该实现此接口，此接口继承了GenericDao的方法以实现基本的增删改查
 * Created by 浩 on 2015-07-20 0020.
 *
 * @version 1.0
 */
public abstract class GenericService<Po extends GenericPo<Pk>, Pk> implements Serializable {

    protected transient Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 获取默认的数据映射接口
     *
     * @param <T> 数据映射接口类型必须是GenericDao的子类
     * @return 数据映射接口
     */
    protected abstract <T extends GenericMapper<Po, Pk>> T getMapper();

    /**
     * 获取指定类型的数据映射接口
     *
     * @param <T>  数据映射接口类型必须是GenericDao的子类
     * @param type 接口类型
     * @return 数据映射接口
     */
    protected <T extends GenericMapper<Po, Pk>> T getMapper(Class<T> type) {
        return getSqlSession().getMapper(type);
    }

    /**
     * 默认的sqlSession实例
     */
    @Resource
    private SqlSessionTemplate sqlSession;

    /**
     * 获取默认的sqlSession实例
     */
    protected SqlSessionTemplate getSqlSession() {
        return sqlSession;
    }

    /**
     * 获取 sqlSession的Connection实例（事务控制范围）
     *
     * @param sqlSession sqlSession 实例
     * @return Connection实例
     */
    protected Connection getConnection(SqlSessionTemplate sqlSession) {
        return SqlSessionUtils.getSqlSession(
                sqlSession.getSqlSessionFactory(), sqlSession.getExecutorType(),
                sqlSession.getPersistenceExceptionTranslator()).getConnection();
    }

    /**
     * 获取默认sqlSession的Connection实例（事务控制范围）
     *
     * @return sqlSession sqlSession 实例
     */
    protected Connection getConnection() {
        return getConnection(getSqlSession());
    }

    /**
     * 添加一条数据
     *
     * @param data 要添加的数据
     * @return 影响记录数
     * @throws Exception 异常信息
     */
    @Transactional
    public int insert(Po data) throws Exception {
        tryValidPo(data);

        return getMapper().insert(data);
    }

    /**
     * 尝试验证对象属性
     *
     * @param data 需要验证的对象
     */
    protected void tryValidPo(Po data) {
        ValidResults results = data.valid();
        if (!results.isSuccess())
            throw new ValidationException(results.toString());
    }

    /**
     * 根据主键删除记录
     *
     * @param pk 主键
     * @return 影响记录数
     * @throws Exception 异常信息
     */
    @Transactional
    public int delete(Pk pk) throws Exception {
        return getMapper().delete(pk);
    }

    /**
     * 修改记录信息
     *
     * @param data 要修改的对象
     * @return 影响记录数
     * @throws Exception 异常信息
     */
    @Transactional
    public int update(Po data) throws Exception {
        tryValidPo(data);
        return getMapper().update(data);
    }


    /**
     * 批量修改记录信息
     *
     * @param datas 要修改的对象
     * @return 影响记录数
     * @throws Exception 异常信息
     */
    @Transactional
    public int update(List<Po> datas) throws Exception {
        int i = 0;
        for (Po data : datas) {
            i += this.update(data);
        }
        return i;
    }

    /**
     * 根据条件集合查询记录，支持分页，排序。
     * <br/>查询条件支持 类似$LIKE,$IN 表达式查询，如传入 name$LIKE 则进行name字段模糊查询
     * <br/>$LIKE -->模糊查询 (只支持字符)
     * <br/>$START -->以?开始 (只支持字符 和数字)
     * <br/>$END -->以?结尾 (只支持字符 和数字)
     * <br/>$IN -->in查询，参数必须为List实现，传入类似 1,2,3 是非法的
     * <br/>$GT -->大于 (只支持 数字和日期)
     * <br/>$LT -->小于 (只支持 数字和日期)
     * <br/>$NOT -->不等于
     * <br/>$NOTNULL -->值不为空
     * <br/>$ISNULL -->值为空
     * <br/>$NOTIN -->$IN 反向
     *
     * @param conditions 查询条件集合
     * @return 查询结果
     * @throws Exception 异常信息
     */
    @Transactional(readOnly = true)
    public List<Po> select(Map<String, Object> conditions) throws Exception {
        return getMapper().select(conditions);
    }

    /**
     * 分页查询列表,并返回查询结果，支持查询条件见 {@link GenericService#select(Map)}
     *
     * @param pageUtil 分页工具实体，由SpringMvc自动填充其属性，具体参数见{@link PageUtil}
     * @return 查询结果: {total:1,data:[{....},...]}
     */
    @Transactional(readOnly = true)
    public Map<String, Object> selectPager(PageUtil pageUtil) throws Exception {
        // 数据总数
        int total = getMapper().total(pageUtil.params());
        List<Po> data = getMapper().select(pageUtil.params(total));
        Map<String, Object> result = new HashMap<>();
        result.put("data", data); //数据
        result.put("total", total);//总数
        return result;
    }

    /**
     * 查询记录总数，用于分页等操作。查询条件同 {@link GenericMapper#select}
     *
     * @param conditions 询条件集合
     * @return 查询结果，实现mapper中的sql应指定默认值，否则可能抛出异常
     * @throws Exception 异常信息
     */
    @Transactional(readOnly = true)
    public int total(Map<String, Object> conditions) throws Exception {
        return getMapper().total(conditions);
    }

    /**
     * 根据主键查询记录
     *
     * @param pk 主键
     * @return 查询结果
     * @throws Exception 异常信息
     */
    @Transactional(readOnly = true)
    public Po selectByPk(Pk pk) throws Exception {
        return getMapper().selectByPk(pk);
    }


}
