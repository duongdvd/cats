package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.enums.Channel;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.ForgotPasswordForm;
import jp.co.willwave.aca.web.form.LoginForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;

@Controller
public class LoginController extends AbstractController {
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(value = {"/userLoginView"}, method = RequestMethod.GET)
    public String userLoginView() {
        return "login/userLogin";
    }

    @RequestMapping(value = {""}, method = RequestMethod.GET)
    public String index() {
        return redirect("carStatusMapView");
    }

    @RequestMapping(value = {"/systemAdminLoginView"}, method = RequestMethod.GET)
    public String systemAdminLoginView() {
        return "login/systemAdminLogin";
    }

    @RequestMapping(value = {"/userLogin"}, method = RequestMethod.POST)
    public String login(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult, Model model)
            throws CommonException {
        return doLogin(form, bindingResult, model, Channel.NORMAL_USER);
    }

    @RequestMapping(value = {"/systemAdminLogin"}, method = RequestMethod.POST)
    public String systemAdminLogin(@Valid @ModelAttribute("loginForm") LoginForm form, BindingResult bindingResult,
                                   Model model) throws CommonException {
        return doLoginAdmin(form, bindingResult, model, Channel.SYSTEM_ADMIN);

    }

    @RequestMapping(value = {"/userForgotPassword"}, method = RequestMethod.GET)
    public String userForgotPassword() {
        return "login/userForgotPassword";
    }

    @RequestMapping(value = {"/systemAdminForgotPassword"}, method = RequestMethod.GET)
    public String systemAdminForgotPassword() {
        return "login/systemAdminForgotPassword";
    }

    @RequestMapping(value = {"/forgotPasswordRequest"}, method = RequestMethod.POST)
    public String forgotPasswordRequest(@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm forgotPasswordForm, BindingResult bindingResult,
                                        Model model) throws CommonException {

        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.resetUserPassword(forgotPasswordForm.getEmail()));
            if (CollectionUtils.isEmpty(messages)) {
                model.addAttribute("resetResult", true);
                return "login/userForgotPassword";
            } else {
                model.addAttribute("resetResult", false);
                model.addAttribute(Constant.Session.MESSAGES, messages);
                return "login/userForgotPassword";
            }
        }
        model.addAttribute(Constant.Session.MESSAGES, messages);
        return "login/userForgotPassword";
    }

    @RequestMapping(value = {"/forgotPasswordAdminRequest"}, method = RequestMethod.POST)
    public String forgotPasswordAdminRequest(@Valid @ModelAttribute("forgotPasswordForm") ForgotPasswordForm forgotPasswordForm, BindingResult bindingResult,
                                             Model model) throws CommonException {

        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.resetSystemAdminPassword(forgotPasswordForm.getEmail()));
            if (CollectionUtils.isEmpty(messages)) {
                model.addAttribute("resetResult", true);
                return "login/systemAdminForgotPassword";
            } else {
                model.addAttribute("resetResult", false);
                model.addAttribute(Constant.Session.MESSAGES, messages);
                return "login/systemAdminForgotPassword";
            }
        }
        model.addAttribute(Constant.Session.MESSAGES, messages);
        return "login/systemAdminForgotPassword";
    }

    private String doLoginAdmin(LoginForm form, BindingResult bindingResult, Model model, Channel channel)
            throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.login(form.getLoginId(), form.getPasswd(), channel));
            if (CollectionUtils.isEmpty(messages)) {
                UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
                model.addAttribute("userLoginRole", userInfo.getRoleId());
                return redirect("usageStatus");
            }
        }

        model.addAttribute(Constant.Session.MESSAGES, messages);
        if (channel.isSystemAdmin()) {
            return "login/systemAdminLogin";
        }

        return "login/userLogin";
    }

    private String doLogin(LoginForm form, BindingResult bindingResult, Model model, Channel channel)
            throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.login(form.getLoginId(), form.getPasswd(), channel));
            if (CollectionUtils.isEmpty(messages)) {
                UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
                model.addAttribute("userLoginRole", userInfo.getRoleId());
                if (channel.isSystemAdmin()) {
                    return redirect("usageStatus");
                } else {
                    return redirect("carStatusMapView");
                }

            }
        }
        model.addAttribute(Constant.Session.MESSAGES, messages);

        return "login/userLogin";
    }

    @RequestMapping(value = {"/logout"}, method = RequestMethod.GET)
    public String logout() {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        Integer roleId = userInfo.getRoleId();
        userService.logout();
        if (UserRole.SYSTEM_ADMIN.getRole().equals(roleId)) {
            return redirect("systemAdminLoginView");
        }

        return redirect("userLoginView");
    }

}
