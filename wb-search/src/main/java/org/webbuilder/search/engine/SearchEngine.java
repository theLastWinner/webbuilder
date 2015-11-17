package org.webbuilder.search.engine;

import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-10-23 0023.
 */
public interface SearchEngine {
    String getName();

    void createDb(String name, Map<String, Object> config) throws Exception;

    SearchResult select(String db, Map<String, Object> condition) throws Exception;

    boolean insert(String db, Map<String, Object> data) throws Exception;

    boolean update(String db, Map<String, Object> data) throws Exception;

    boolean delete(String db, Map<String, Object> data) throws Exception;

    boolean insert(String db, List<Map<String, Object>> data) throws Exception;

    boolean update(String db, List<Map<String, Object>> data) throws Exception;

    boolean delete(String db, List<Map<String, Object>> data) throws Exception;

    int total(String db, List<Map<String, Object>> data) throws Exception;
}
