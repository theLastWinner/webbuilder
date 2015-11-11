package org.webbuilder.sql.param;

/**
 * Created by 浩 on 2015-11-06 0006.
 */
public class QueryParam extends SqlRenderConfig {

    private boolean paging = true;

    private int pageIndex = 0;

    private int pageSize = 50;

    public QueryParam() {
    }

    public void copy(QueryParam queryParam) {
        super.copy(queryParam);
        queryParam.setPaging(this.paging);
        queryParam.setPageIndex(this.getPageIndex());
        queryParam.setPageSize(this.getPageSize());

    }

    public QueryParam(boolean paging) {
        this.paging = paging;
    }

    public QueryParam(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        paging = true;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void doPaging(int pageIndex, int pageSize) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        if (pageIndex < 0) this.pageIndex = 0;
        if (pageSize <= 0) this.pageSize = 1;
        setPaging(true);
    }
}
