package org.webbuilder.web.po.module;

import org.webbuilder.web.core.bean.GenericPo;
import org.hibernate.validator.constraints.NotEmpty;


/**
 * 系统模块
 * Created by generator
 */
public class Module extends GenericPo<String> implements Comparable<Module> {

    //主键
    @NotEmpty(message = "id不能为空")
    private String u_id;

    //模块名称
    @NotEmpty(message = "名称不能为空")
    private String name;

    //模块路径
    private String uri;

    //模块图标
    private String icon;

    //父级模块主键

    private String p_id = "-1";

    //备注
    private String remark;

    //状态
    private int status = 1;

    //模块操作选项
    private String m_option;

    //排序
    private int sort_index;

    private String old_id;

    /**
     * 获取 主键
     *
     * @return String 主键
     */
    public String getU_id() {
        if (this.u_id == null)
            return "";
        return this.u_id;
    }

    /**
     * 设置 主键
     */
    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    /**
     * 获取 模块名称
     *
     * @return String 模块名称
     */
    public String getName() {
        if (this.name == null)
            return "";
        return this.name;
    }

    /**
     * 设置 模块名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 模块路径
     *
     * @return String 模块路径
     */
    public String getUri() {
        if (this.uri == null)
            return "";
        return this.uri;
    }

    /**
     * 设置 模块路径
     */
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * 获取 模块图标
     *
     * @return String 模块图标
     */
    public String getIcon() {
        if (this.icon == null)
            return "";
        return this.icon;
    }

    /**
     * 设置 模块图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * 获取 父级模块主键
     *
     * @return String 父级模块主键
     */
    public String getP_id() {
        if (this.p_id == null)
            return "-1";
        return this.p_id;
    }

    /**
     * 设置 父级模块主键
     */
    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    /**
     * 获取 备注
     *
     * @return String 备注
     */
    public String getRemark() {
        if (this.remark == null)
            return "";
        return this.remark;
    }

    /**
     * 设置 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取 状态
     *
     * @return int 状态
     */
    public int getStatus() {
        return this.status;
    }

    /**
     * 设置 状态
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public int getSort_index() {
        return sort_index;
    }

    public void setSort_index(int sort_index) {
        this.sort_index = sort_index;
    }

    /**
     * 获取 模块操作选项
     *
     * @return String 模块操作选项
     */
    public String getM_option() {
        if (this.m_option == null)
            return "\"[{\"C\":\"新增\"},{\"R\":\"查询\"},{\"U\":\"修改\"},{\"D\":\"删除\"}]\"";
        return this.m_option;
    }

    /**
     * 设置 模块操作选项
     */
    public void setM_option(String m_option) {
        this.m_option = m_option;
    }

    @Override
    public int hashCode() {
        return getU_id().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    @Override
    public int compareTo(Module o) {
        return getSort_index() > o.getSort_index() ? 1 : -1;
    }

    public String getOld_id() {
        if (old_id == null)
            old_id = getU_id();
        return old_id;
    }

    public void setOld_id(String old_id) {
        this.old_id = old_id;
    }

}
