package org.webbuilder.search.engine.solr;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.webbuilder.search.engine.SearchEngine;
import org.webbuilder.search.engine.SearchResult;
import org.webbuilder.utils.base.StringUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by 浩 on 2015-11-04 0004.
 */
public class SolrSearchEngine implements SearchEngine {

    protected String name;

    protected String host;

    protected SolrClient client;

    public void SolrSearchEngine() {
        client = new HttpSolrClient(host);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void createDb(String name, Map<String, Object> config) throws Exception {
        Object instanceDir = config.get("instanceDir");
        if (instanceDir == null) {
            throw new NullPointerException("config property 'instanceDir' is null.");
        }
        CoreAdminRequest.createCore(name, String.valueOf(instanceDir), client);
    }

    @Override
    public SearchResult select(String db, Map<String, Object> condition) throws Exception {
        return null;
    }

    @Override
    public boolean insert(String db, Map<String, Object> data) throws Exception {
        return false;
    }

    @Override
    public boolean update(String db, Map<String, Object> data) throws Exception {
        return false;
    }

    @Override
    public boolean delete(String db, Map<String, Object> data) throws Exception {
        return false;
    }

    @Override
    public boolean insert(String db, List<Map<String, Object>> data) throws Exception {
        return false;
    }

    @Override
    public boolean update(String db, List<Map<String, Object>> data) throws Exception {
        return false;
    }

    @Override
    public boolean delete(String db, List<Map<String, Object>> data) throws Exception {
        return false;
    }

    @Override
    public int total(String db, List<Map<String, Object>> data) throws Exception {
        return 0;
    }

    protected SolrQuery buildeQueryByMap(Map<String, Object> map) {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setStart(StringUtil.toInt(map.get("pageIndex"), 0));
        solrQuery.setRows(StringUtil.toInt(map.get("pageSize"), 0));
        return solrQuery;
    }
}
