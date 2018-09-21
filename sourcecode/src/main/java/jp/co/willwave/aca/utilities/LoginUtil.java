package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.model.constant.Constant;

import javax.servlet.http.HttpSession;

/**
 * @author p-khanhnv
 */
public class LoginUtil {

    public static boolean isLogin(HttpSession session) {
        return ((session != null) && (session.getAttribute(Constant.Session.USER_LOGIN_INFO) != null));
    }
}
