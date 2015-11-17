package org.webbuilder.web.core.bean;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import org.webbuilder.utils.base.MapUtils;
import org.webbuilder.utils.base.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 分页 工具
 *
 * @author ZhouHao
 */
public class PageUtil {
    public static final String PAGE_INDEX = "pageIndex";
    public static final String MAX_RESULTS = "maxResults";
    public static final String FIRST_RESULT = "firstResult";
    /**
     * 第几页 从0开始
     */
    private int pageIndex = 0;

    /**
     * 每页显示记录条数
     */
    private int pageSize = 25;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序方式 DESC 反序 ASC 正序
     */
    private String sortOrder;

    /**
     * 搜索关键字 格式为json :{key:value,key2:value2}
     */
    private String key;

    /**
     * 解析后的查询条件集合，根据属性key进行解析（key是一个json字符串）
     */
    private Map<String, Object> queryMap;

    /**
     * 获取的数据类型（0=表格，1=combobox）
     */
    private int type;

    private boolean paging = true;

    private Set<String> includes = new LinkedHashSet<>();

    private Set<String> excludes = new LinkedHashSet<>();

    public Set<String> getIncludes() {
        return includes;
    }

    public String[] getIncludesArray() {
        return includes.toArray(new String[includes.size()]);
    }

    public String[] getExcludesArray() {
        return excludes.toArray(new String[excludes.size()]);
    }

    public Set<String> getExcludes() {
        return excludes;
    }

    public PageUtil includes(String... fields) {
        includes.addAll(Arrays.asList(fields));
        return this;
    }

    public PageUtil excludes(String... fields) {
        excludes.addAll(Arrays.asList(fields));
        includes.removeAll(Arrays.asList(fields));
        return this;
    }

    public int getStart() {
        return pageIndex * pageSize;
    }

    public String getKey() {
        if (key == null)
            key = "";
        return key;
    }

    public boolean isPaging() {
        return paging;
    }

    public void setPaging(boolean paging) {
        this.paging = paging;
    }

    public void setKey(String key) {
        this.key = key;
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

    /**
     * 获取提交的查询条件，将提交的json转为map并进行去空等处理
     *
     * @return 查询条件集合
     */
    public Map<String, Object> getQueryMap() {
        if (StringUtil.isNullOrEmpty(getKey())) {
            return new HashMap<>();
        }
        if (queryMap == null)
            try {
                queryMap = JSON.parseObject(getKey(), Map.class);
                if (queryMap == null) {
                    return new HashMap<>();
                }
                for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    //进行IN相关的操作的时候，如果传入的是字符串类型的，如:1,2,3。将其转换为List后重新填充。
                    if (key.endsWith("$IN") || key.endsWith("$NOTIN")) {
                        if (StringUtil.isNullOrEmpty(value)) continue;
                        //如果不为Iterable实现则进行转换
                        if (!(value instanceof Iterable)) {
                            List<Object> list = new LinkedList<>();
                            String[] val_strs = String.valueOf(value).split("[,]");
                            if (val_strs.length == 0) {
                                entry.setValue("");
                                continue;
                            }
                            for (String val_str : val_strs) {
                                if (StringUtil.isDouble(val_str))
                                    list.add(StringUtil.toDouble(val_str));
                                else if (StringUtil.isInt(val_str))
                                    list.add(StringUtil.toInt(val_str));
                                else
                                    list.add(val_str);
                            }
                            entry.setValue(list);
                        }
                    }
                }
            } catch (Exception e) {
                queryMap = new HashMap<>();
            }
        return MapUtils.removeEmptyValue(queryMap);//去除无效的查询条件
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取查询条件（未分页）
     *
     * @return 查询条件
     */
    public Map<String, Object> params() {
        Map<String, Object> params = this.getQueryMap();
        // 字段排序
        String sortField = this.getSortField();
        String sortOrder = this.getSortOrder();
        // 排序
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);
        if (includes.size() > 0)
            params.put("$includes", includes);//指定了查询字段
        if (excludes.size() > 0)
            params.put("$excludes", excludes);//指定排除字段
        return params;
    }

    /**
     * 获取分页的查询条件
     *
     * @param total 记录总数
     * @return 包含分页查询的条件集合
     */
    public Map<String, Object> params(int total) {
        Map<String, Object> params = params();
        int firstResult = this.getStart();
        int pageIndex = getPageIndex();
        // 当前页没有数据后跳转到最后一页
        if (this.getPageIndex() != 0 && this.getStart() >= total) {
            int tmp = total / this.getPageSize();
            pageIndex = total % this.getPageSize() == 0 ? tmp - 1 : tmp;
            firstResult = pageIndex * this.getPageSize();
        }
        // 设置分页参数
        params.put("maxResults", this.getPageSize());
        params.put("firstResult", firstResult);
        params.put("pageIndex", pageIndex);
        return params;
    }

    public int pageIndex(int total) {
        int pageIndex = getPageIndex();
        // 当前页没有数据后跳转到最后一页
        if (this.getPageIndex() != 0 && this.getStart() >= total) {
            int tmp = total / this.getPageSize();
            pageIndex = total % this.getPageSize() == 0 ? tmp - 1 : tmp;
        }
        return pageIndex;
    }


    @Override
    public int hashCode() {
        return new StringBuilder(getKey())
                .append(getPageIndex())
                .append(getPageSize())
                .append(getSortField())
                .append(getSortOrder())
                .toString().hashCode();
    }
}
