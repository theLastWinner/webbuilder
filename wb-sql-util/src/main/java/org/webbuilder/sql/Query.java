package org.webbuilder.sql;

import org.webbuilder.sql.param.QueryParam;

import java.util.List;

/**
 * 查询接口，通过此接口进行表数据查询
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Query {
    /**
     * 查询列表
     *
     * @param param 查询参数
     * @param <T>   查询结果泛型
     * @return 查询下结果
     * @throws Exception 查询异常
     */
    <T> List<T> list(QueryParam param) throws Exception;

    /**
     * 查询并返回单个结果，此方法将强制进行分页为0,1
     *
     * @param param 查询参数
     * @param <T>   查询结果泛型
     * @return 单个查询结果
     * @throws Exception 查询异常
     */
    <T> T single(QueryParam param) throws Exception;

    /**
     * 统计数量，等同于count查询
     *
     * @param param 查询参数
     * @return 数量
     * @throws Exception 查询异常
     */
    int total(QueryParam param) throws Exception;

}
