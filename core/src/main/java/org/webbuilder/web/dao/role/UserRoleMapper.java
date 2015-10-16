package org.webbuilder.web.dao.role;

import org.webbuilder.web.core.dao.GenericMapper;
import org.webbuilder.web.po.role.UserRole;

import java.util.List;

/**
 * 后台管理用户角色绑定数据映射接口
 * Created by generator
 */
public interface UserRoleMapper extends GenericMapper<UserRole, String> {
    /**
     * 根据用户id查询用户的角色列表
     *
     * @param userId 用户id
     * @return 角色列表
     * @throws Exception 异常信息
     */
    List<UserRole> selectByUserId(String userId) throws Exception;
}
