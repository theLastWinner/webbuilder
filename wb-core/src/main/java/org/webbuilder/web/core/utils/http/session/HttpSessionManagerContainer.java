package org.webbuilder.web.core.utils.http.session;

import org.webbuilder.web.core.utils.http.session.impl.LocalHttpSessionManager;

/**
 * Created by 浩 on 2015-09-28 0028.
 */
public class HttpSessionManagerContainer {
    public static HttpSessionManager sessionManager;

    public static HttpSessionManager getSessionManager() {
        if (sessionManager == null)
            sessionManager = new LocalHttpSessionManager();
        return sessionManager;
    }

    public static void setSessionManager(HttpSessionManager sessionManager) {
        HttpSessionManagerContainer.sessionManager = sessionManager;
    }
}
