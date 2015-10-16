package org.webbuilder.web.dao.basic;


import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public interface BasicMapper {

    List<Map<String, Object>> selectSql(Map<String, Object> condition);

    List<Map<String, Object>> select(Map<String, Object> condition);

    int total(Map<String, Object> condition);
}
