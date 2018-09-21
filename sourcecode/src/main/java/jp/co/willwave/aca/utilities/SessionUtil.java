package jp.co.willwave.aca.utilities;

import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

public class SessionUtil {
    /**
     * Get session from request
     *
     * @return HttpSession object that get from request
     * @see HttpServletRequest#getSession()
     */
    public static HttpSession getSession() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attr.getRequest().getSession();
    }

    /**
     * Get value of object from session by key
     *
     * @param key Key to store object on session
     * @return Object that need get from session
     * @see HttpSession#getAttribute(String)
     */
    public static Object getAttribute(String key) {
        try {
            return getSession().getAttribute(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Removes the object bound with the specified name from this session
     *
     * @param key the name of the object to remove from this session
     * @see HttpSession#removeAttribute(String)
     */
    public static void removeAttribute(String key) {
        getSession().removeAttribute(key);
    }

    /**
     * Store object to session with specific key
     *
     * @param key the name to which the object is bound; cannot be null
     * @param value the object to be bound
     * @see HttpSession#setAttribute(String, Object)
     */
    public static void setAttribute(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static void invalidate() {
        getSession().invalidate();
    }

    public static UserInfo getLoginUser() {
        return (UserInfo) getAttribute(Constant.Session.USER_LOGIN_INFO);
    }
    /**
     * get and remove messages
     * @return message
     */
    public static List<Message> getAndRemoveMessages() {
        List<Message> messages = (List<Message>) SessionUtil.getAttribute(Constant.Session.MESSAGES);
        SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
        return messages;
    }
}
