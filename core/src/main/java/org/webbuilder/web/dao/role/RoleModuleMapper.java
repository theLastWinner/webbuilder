package org.webbuilder.web.dao.role;

import org.webbuilder.web.core.dao.GenericMapper;
import org.webbuilder.web.po.role.RoleModule;

import java.util.List;

/**
 * 系统模块角色绑定数据映射接口
 * Created by generator
 */
public interface RoleModuleMapper extends GenericMapper<RoleModule, String> {
    /**
     * 根据角色id查询
     *
     * @param roleId 角色id
     * @return
     * @throws Exception
     */
    List<RoleModule> selectByRoleId(String roleId) throws Exception;

    int deleteByRoleId(String roleId) throws Exception;

    int deleteByModuleId(String moduleId) throws Exception;
}
