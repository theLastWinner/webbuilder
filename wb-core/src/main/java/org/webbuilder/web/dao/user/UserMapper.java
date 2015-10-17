package org.webbuilder.web.dao.user;

import org.webbuilder.web.core.dao.GenericMapper;
import org.webbuilder.web.po.user.User;

/**
 * 后台管理用户数据映射接口
 * Created by generator
 */
public interface UserMapper extends GenericMapper<User, String> {
    User selectByUserName(String userName) throws Exception;
}
