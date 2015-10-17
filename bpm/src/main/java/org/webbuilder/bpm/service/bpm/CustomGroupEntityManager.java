package org.webbuilder.bpm.service.bpm;

import org.webbuilder.web.dao.role.UserRoleMapper;
import org.webbuilder.web.po.role.UserRole;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupEntityManager;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by 浩 on 2015-09-16 0016.
 */
@Service
public class CustomGroupEntityManager extends GroupEntityManager {
    @Resource
    private UserRoleMapper userRoleMapper;
    @Override
    public List<Group> findGroupsByUser(String userId) {
        List<Group> groups = new LinkedList<>();
        try {
            List<UserRole> roles = userRoleMapper.selectByUserId(userId);
            for (UserRole role : roles) {
                Group group =new GroupEntity();
                group.setName(role.getRole().getName());
                group.setId(role.getRole().getU_id());
                group.setType(role.getRole().getType());
                groups.add(group);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return groups;
    }
}
