package org.webbuilder.web.core.utils.http.session;

import javax.servlet.http.HttpSession;
import java.util.Set;

/**
 * httpSession管理
 * Created by 浩 on 2015-09-28 0028.
 */
public interface HttpSessionManager {
    String getSessionIdByUserId(String userId) throws Exception;

    void removeUser(String userId) throws Exception;

    void removeSession(String sessionId) throws Exception;

    void addUser(String userId, HttpSession session) throws Exception;

    Set<String> getUserIdList() throws Exception;

    int getUserTotal() throws Exception;

    Set<String> getSessionIdList() throws Exception;

    boolean isLogin(String userId);
}
