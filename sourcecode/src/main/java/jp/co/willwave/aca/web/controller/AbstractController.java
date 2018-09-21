package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.utilities.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

/**
 * Base Controller class.
 *
 * @author p-khanhnv.
 */
public abstract class AbstractController {
    protected Logger logger;

    @Autowired
    protected CatsMessageResource messageSource;

    @Autowired
    protected ValidatorUtil validatorUtil;

    public AbstractController() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    protected String redirect(String view) {
        return "redirect:/" + view;
    }

    protected Long getLoginUserId() {
        return ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
    }

    protected void setAddEditResultIntoModelMap(ModelMap model) {
        Boolean deleteResult = (Boolean) SessionUtil.getAttribute("deleteResult");
        if (deleteResult != null) {
            model.addAttribute("deleteResult", deleteResult);
        }

        Boolean addEditResult = (Boolean) SessionUtil.getAttribute("addEditResult");
        if (addEditResult != null) {
            model.addAttribute("addEditResult", addEditResult);
        }
    }

    protected String pagingRedirectToList(String view, Integer offset, Integer maxResults) {
        offset = (offset == null ? 0 : offset);
        maxResults = (maxResults == null ? Constant.MAX_SEARCH_RESULT : maxResults);
        return redirect(view) + "?offset=" + offset + "&maxResults=" + maxResults;
    }

    protected UserInfo getUserLoginInfo() {
        return ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO));
    }
}
