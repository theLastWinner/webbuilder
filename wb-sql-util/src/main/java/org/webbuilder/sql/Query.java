package org.webbuilder.sql;

import org.webbuilder.sql.param.QueryParam;

import java.util.List;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public interface Query {
    <T> List<T> list(QueryParam param) throws Exception;

    <T> T single(QueryParam param) throws Exception;

    int total(QueryParam param) throws Exception;
}
