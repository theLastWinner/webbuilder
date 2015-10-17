package org.webbuilder.web.controller.role;

import org.webbuilder.web.core.aop.logger.AccessLogger;
import org.webbuilder.web.core.authorize.annotation.Authorize;
import org.webbuilder.web.po.role.Role;
import org.webbuilder.web.service.role.RoleService;
import org.webbuilder.web.core.controller.GenericController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 后台管理角色控制器，继承自GenericController,使用rest+json
 * Created by generator 2015-8-26 10:57:38
 */
@Controller
@RequestMapping(value = "/role")
@Authorize(role = Role.SYS_ROLE_ADMIN)
@AccessLogger("角色管理")
public class RoleController extends GenericController<Role, String> {

    //默认服务类
    @Resource
    private RoleService roleService;

    @Override
    public RoleService getService() {
        return this.roleService;
    }


}
