package org.webbuilder.web.core.utils.http.session.impl;

import org.webbuilder.utils.storage.Storage;
import org.webbuilder.utils.storage.driver.StorageDriver;
import org.webbuilder.utils.storage.event.KeyFilter;
import org.webbuilder.web.core.utils.http.session.HttpSessionManager;
import org.webbuilder.web.core.utils.http.session.HttpSessionManagerContainer;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;


/**
 * Created by 浩 on 2015-08-27 0027.
 */
public class StorageHttpSessionManager implements HttpSessionManager {

    /**
     * 用于存储httpSession的存储驱动
     */
    private static StorageDriver driver;

    private static StorageDriver userListDriver;

    /**
     * userId 进行前缀追加
     */
    private static final String userIdPrefix = "login_userId$";


    public StorageHttpSessionManager() {
        HttpSessionManagerContainer.setSessionManager(this);
    }


    public <V> Storage<String, V> sessionStorage() throws Exception {
        return driver.getStorage("http_sessions");
    }

    public <V> Storage<String, V> userStorage() throws Exception {
        if (userListDriver == null)
            userListDriver = driver;
        return userListDriver.getStorage("user_list");
    }

    public void removeSession(String id) throws Exception {
        sessionStorage().remove(id);
    }


    /**
     * 根据userId获取sessionId
     *
     * @param userId userId
     * @return sessionId
     * @throws Exception
     */
    public String getSessionIdByUserId(String userId) throws Exception {
        String sessionId = (String) userStorage().get(encodeUserId(userId));
        if (sessionId != null) {
            //校验是否已经session已到期
            if (sessionStorage().containsKey(sessionId)) {
                return sessionId;
            } else {
                removeUser(encodeUserId(userId));
                return null;
            }
        }
        return sessionId;
    }

    /**
     * 移除一个user
     *
     * @param userId
     * @throws Exception
     */
    @CacheEvict(value = "user.login.list", allEntries = true)
    public void removeUser(String userId) throws Exception {
        userStorage().remove(encodeUserId(userId));
    }

    /**
     * 加入一个user（用户登录）
     *
     * @param userId  userId
     * @param session HttpSession
     * @throws Exception
     */
    @CacheEvict(value = "user.login.list", allEntries = true)
    public void addUser(String userId, HttpSession session) throws Exception {
        userStorage().put(encodeUserId(userId), session.getId());
    }

    /**
     * 获取当前登录用户id列表
     *
     * @return 当前已登陆用户的 id集合
     * @throws Exception
     */
    @Cacheable(value = "user.login.list", key = "'list'")
    public Set<String> getUserIdList() throws Exception {
        final Set<String> ids = new LinkedHashSet<>();
        //扫描缓存中的key
        userStorage().keySet(new KeyFilter<String>() {
            @Override
            public boolean each(String key) {
                //所有以userIdPrefix前缀的则认为是代表userId
                if (key.startsWith(userIdPrefix)) {
                    String realKey = decodeUserId(key);
                    try {
                        //判断是否已经退出了
                        if (getSessionIdByUserId(realKey) != null)
                            ids.add(realKey);
                    } catch (Exception e) {
                    }
                }
                //所有key返回false，不进行默认方式的填充
                return false;
            }
        });
        return ids;
    }

    protected String encodeUserId(String userId) {
        return userIdPrefix.concat(userId);
    }

    protected String decodeUserId(String userId) {
        String realUserId = null;
        if (userId.startsWith(userIdPrefix))
            realUserId = userId.substring(userId.indexOf(userIdPrefix) + userIdPrefix.length());
        return realUserId;
    }

    /**
     * 获取当前在线总人数
     *
     * @return 总人数
     * @throws Exception 异常信息
     */
    @Cacheable(value = "user.login.list", key = "'total'")
    public int getUserTotal() throws Exception {
        final int total[] = new int[1];
        total[0] = 0;
        userStorage().keySet(new KeyFilter<String>() {
            @Override
            public boolean each(String key) {
                //所有以userIdPrefix前缀的则认为是代表userId
                if (key.startsWith(userIdPrefix)) {
                    String realKey = decodeUserId(key);
                    try {
                        //判断是否已经退出了
                        if (getSessionIdByUserId(realKey) != null)
                            total[0]++;
                    } catch (Exception e) {
                    }
                }
                //所有key返回false，不进行默认方式的填充
                return false;
            }
        });
        return total[0];
    }

    public StorageDriver getDriver() {
        return driver;
    }

    public void setDriver(StorageDriver driver) {
        this.driver = driver;
    }

    protected StorageDriver getUserListDriver() {
        return userListDriver;
    }

    public void setUserListDriver(StorageDriver userListDriver) {
        StorageHttpSessionManager.userListDriver = userListDriver;
    }

    //sessionId匹配，32位长度，由A-F和数字组成的key则认为是sessionId
    private static final Pattern sessionIdPattern = Pattern.compile("[A-F0-9]{32}");

    /**
     * 获取sessionId列表
     *
     * @return sessionId列表
     * @throws Exception
     */
    public Set<String> getSessionIdList() throws Exception {
        //获取缓存中，所有session的key
        return sessionStorage().keySet(new KeyFilter<String>() {
            @Override
            public boolean each(String key) {
                //长度不为32位，直接false
                if (key.length() != 32) return false;
                return sessionIdPattern.matcher(key).matches();
            }
        });
    }

    @Override
    public boolean isLogin(String userId) {
        try {
            return getSessionIdByUserId(userId) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
