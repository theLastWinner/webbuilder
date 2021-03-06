package org.webbuilder.web.po.user;

import org.joda.time.DateTime;
import org.webbuilder.utils.base.DateTimeUtils;
import org.webbuilder.utils.base.MapUtils;
import org.webbuilder.web.core.bean.GenericPo;
import org.webbuilder.web.po.module.Module;
import org.webbuilder.web.po.role.Role;
import org.webbuilder.web.po.role.RoleModule;
import org.webbuilder.web.po.role.UserRole;

import java.util.*;

/**
 * 后台管理用户
 * Created by generator
 */
public class User extends GenericPo<String> {

    //主键
    private String u_id;

    //用户名
    private String username;

    //密码
    private String password;

    //姓名
    private String name;

    //邮箱
    private String email;

    //联系电话
    private String phone;

    //状态
    private int status;

    //创建日期
    private java.util.Date create_date;

    //修改日期
    private java.util.Date update_date;

    //用户角色绑定
    private List<UserRole> userRoles;

    private Map<Module, Set<String>> roleInfo;

    /**
     * 判断此用户是否拥有对指定模块的访问权限
     *
     * @param mId    模块id
     * @param levels 访问级别
     * @return 是否能够访问
     */
    public boolean hasAccessModuleLevel(String mId, String... levels) {
        if (!hasAccessModule(mId)) return false;
        Set<String> lv = roleInfo.get(getModule(mId));
        if (lv != null)
            for (String level : levels) {
                if (lv.contains(level)) return true;
            }
        return false;
    }

    public Module getModule(String mId) {
        for (Module module : getModules()) {
            if (module.getU_id().equals(mId)) return module;
        }
        return null;
    }

    public Set<Module> getModules() {
        if (roleInfo == null) initRoleInfo();
        return roleInfo.keySet();
    }

    public Set<Module> getModulesByPid(String pid) {
        Set<Module> modules = new LinkedHashSet<>();
        for (Module module : getModules()) {
            if (pid.equals(module.getP_id())) {
                modules.add(module);
            }
        }
        return modules;
    }

    public boolean hasAccessRole(String rId) {
        if (getUserRoles() != null)
            for (UserRole userRole : getUserRoles()) {
                if (rId.equals(userRole.getRole_id())) return true;
            }
        return false;
    }

    public boolean hasAccessModule(String mId) {
        if (roleInfo == null) initRoleInfo();
        for (Module module : roleInfo.keySet()) {
            if (module.getU_id().equals(mId)) return true;
        }
        return false;
    }

    /**
     * 初始化用户的权限角色信息
     */
    public void initRoleInfo() {
        Map<Module, Set<String>> roleInfo_tmp = new LinkedHashMap<>();
        for (UserRole userRole : getUserRoles()) {
            Role role = userRole.getRole();
            if (role == null) continue;
            //角色可访问的路径
            List<RoleModule> roleModules = role.getModules();
            for (RoleModule roleModule : roleModules) {
                Module module = roleModule.getModule();
                if (module.getStatus() != 1) continue;
                Set<String> levels = roleInfo_tmp.get(module);
                if (levels == null)
                    roleInfo_tmp.put(module, new HashSet<>(roleModule.getLevels()));
                else {
                    levels.addAll(roleModule.getLevels());
                }
            }
        }
        //排序
        roleInfo = MapUtils.sortMapByKey(roleInfo_tmp);
    }


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
     * 获取 用户名
     *
     * @return String 用户名
     */
    public String getUsername() {
        if (this.username == null)
            return "";
        return this.username;
    }

    /**
     * 设置 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取 密码
     *
     * @return String 密码
     */
    public String getPassword() {
        if (this.password == null)
            return "";
        return this.password;
    }

    /**
     * 设置 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取 姓名
     *
     * @return String 姓名
     */
    public String getName() {
        if (this.name == null)
            return "";
        return this.name;
    }

    /**
     * 设置 姓名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取 邮箱
     *
     * @return String 邮箱
     */
    public String getEmail() {
        if (this.email == null)
            return "";
        return this.email;
    }

    /**
     * 设置 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取 联系电话
     *
     * @return String 联系电话
     */
    public String getPhone() {
        if (this.phone == null)
            return "";
        return this.phone;
    }

    /**
     * 设置 联系电话
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取 状态
     *
     * @return boolean 状态
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

    /**
     * 获取 创建日期
     *
     * @return java.util.Date 创建日期
     */
    public java.util.Date getCreate_date() {
        return this.create_date;
    }

    /**
     * 设置 创建日期
     */
    public void setCreate_date(java.util.Date create_date) {
        this.create_date = create_date;
    }

    /**
     * 获取 修改日期
     *
     * @return java.util.Date 修改日期
     */
    public java.util.Date getUpdate_date() {
        return this.update_date;
    }

    /**
     * 设置 修改日期
     */
    public void setUpdate_date(java.util.Date update_date) {
        this.update_date = update_date;
    }

    public List<UserRole> getUserRoles() {
        if (userRoles == null)
            userRoles = new ArrayList<>();
        return userRoles;
    }

    public void setUserRoles(List<UserRole> userRoles) {
        this.userRoles = userRoles;
    }

    public Map<Module, Set<String>> getRoleInfo() {
        return roleInfo;
    }

    public void setRoleInfo(Map<Module, Set<String>> roleInfo) {
        this.roleInfo = roleInfo;
    }

    public static void main(String[] args) {
        System.out.println(DateTimeUtils.formatUnknownString2Date("2015-11-02").getTime());
        System.out.println(DateTimeUtils.formatUnknownString2Date("2015-11-03").getTime());
    }
}
