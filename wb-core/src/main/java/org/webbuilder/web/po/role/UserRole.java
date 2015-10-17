package org.webbuilder.web.po.role;

import org.webbuilder.web.core.bean.GenericPo;

/**
 * 后台管理用户角色绑定
 * Created by generator
 */
public class UserRole extends GenericPo<String> {

    //主键
    private String u_id;

    //用户主键
    private String user_id;

    //角色主键
    private String role_id;

    //角色实例
    private transient Role role;

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
     * 获取 用户主键
     *
     * @return String 用户主键
     */
    public String getUser_id() {
        if (this.user_id == null)
            return "";
        return this.user_id;
    }

    /**
     * 设置 用户主键
     */
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /**
     * 获取 角色主键
     *
     * @return String 角色主键
     */
    public String getRole_id() {
        if (this.role_id == null)
            return "";
        return this.role_id;
    }

    /**
     * 设置 角色主键
     */
    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
