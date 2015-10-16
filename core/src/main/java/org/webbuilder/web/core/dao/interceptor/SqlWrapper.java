package org.webbuilder.web.core.dao.interceptor;

import org.webbuilder.utils.base.StringUtil;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by 浩 on 2015-09-30 0030.
 */
public interface SqlWrapper {
    String wrapper(WrapperConf conf);

    class WrapperConf {
        public String sql;
        public int maxResults = 20;
        public int firstResult = 0;
        public int pageIndex = 1;
        public String sortField;
        public String sortOrder;

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }

        public int getMaxResults() {
            return maxResults;
        }

        public void setMaxResults(int maxResults) {
            if (maxResults == 0)
                maxResults = 25;
            this.maxResults = maxResults;
        }

        public int getFirstResult() {
            return firstResult;
        }

        public void setFirstResult(int firstResult) {
            this.firstResult = firstResult;
        }

        public String getSortField() {
            return sortField;
        }

        public void setSortField(String sortField) {
            this.sortField = sortField;
        }

        public String getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(String sortOrder) {
            this.sortOrder = sortOrder;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public static WrapperConf fromMap(Map<String, Object> param) {
            WrapperConf wrapperConf = new WrapperConf();
            int maxResults = StringUtil.toInt(param.get("maxResults"));
            int firstResult = StringUtil.toInt(param.get("firstResult"));
            int pageIndex = StringUtil.toInt(param.get("pageIndex"));
            if (maxResults == 0 && firstResult == 0 && pageIndex == 0)
                return null;
            try {
                String sortField = (String) param.get("sortField");
                String sortOrder = (String) param.get("sortOrder");
                wrapperConf.setSortField(sortField);
                wrapperConf.setSortOrder(sortOrder);
            } catch (Exception e) {
            }
            wrapperConf.setFirstResult(firstResult);
            wrapperConf.setMaxResults(maxResults);
            wrapperConf.setPageIndex(pageIndex);
            return wrapperConf;
        }
    }
}
