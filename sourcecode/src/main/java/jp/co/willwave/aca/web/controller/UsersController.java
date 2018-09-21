package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.SystemAdminEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SystemAdminProfileForm;
import jp.co.willwave.aca.web.form.UserProfilesForm;
import jp.co.willwave.aca.web.form.UsersForm;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class UsersController extends AbstractController {
    private final UserService userService;
    private final DivisionService divisionService;

    @Autowired
    public UsersController(UserService userService, CatsMessageResource messageResource, DivisionService divisionService) {
        this.userService = userService;
        this.divisionService = divisionService;
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public String getUser(@PathVariable("id") Long id) throws CommonException {
        logger.info("users");
        UsersEntity user = userService.getUser(id);
        return Constant.UserProfile.USER_FORM;
    }

    @RequestMapping(value = "/editProfilesUsersView", method = RequestMethod.GET)
    public String editUsersView(Model model) throws CommonException {
        logger.info(Constant.UserProfile.USER_PROFILE_FORM_UPDATE);
        Long id = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        UsersEntity usersEntity = userService.getUserProfile(id);
        UserProfilesForm form;
        if (usersEntity != null) {
            form = ConversionUtil.mapper(usersEntity, UserProfilesForm.class);

            // Get belonged division info
            DivisionsEntity divisionsEntity = divisionService.findById(usersEntity.getDivisionsId());
            form.mappingForm(divisionsEntity);

            model.addAttribute(Constant.UserProfile.USER_PROFILE_FORM, form);
        } else {
            List<Message> messages = new ArrayList<>();
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]{"User"}));
            model.addAttribute(Constant.Session.MESSAGES, messages);
        }
        return "profiles/editUserProfile";
    }

    @RequestMapping(value = "/editUserProfile", method = RequestMethod.POST)
    public String editUserProfile(@Valid @ModelAttribute UserProfilesForm form, BindingResult bindingResult
            , Model model) throws Exception {
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.updateLoginUserProfile(form));
            if (CollectionUtils.isEmpty(messages)) {
                model.addAttribute(Constant.Session.MESSAGES, Arrays.asList(messageSource.info("I0001")));
            }
        } else {
            model.addAttribute(Constant.Session.MESSAGES, messages);
        }

        model.addAttribute(Constant.UserProfile.USER_PROFILE_FORM, form);
        return "profiles/editUserProfile";
    }

    @RequestMapping(value = "/editSystemAdminProfileView", method = RequestMethod.GET)
    public String editSysAdminView(Model model) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        UserInfo systemAdminSession
                = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);

        boolean checkUserExists = false;
        if (systemAdminSession != null) {
            SystemAdminEntity systemAdminDb = userService.getSystemAdmin(userLoginId);
            if (systemAdminDb != null) {
                checkUserExists = true;
                UsersForm form = ConversionUtil.mapper(systemAdminDb, UsersForm.class);
                model.addAttribute(Constant.UserProfile.USER_SYSAMIN_FORM, form);
            }
        } else {
            return "profiles/editSystemAdminProfile";
        }
        if (!checkUserExists) {
            List<Message> messages = new ArrayList<>();
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]{"User"}));
            model.addAttribute(Constant.Session.MESSAGES, messages);
        }
        return "profiles/editSystemAdminProfile";
    }

    @RequestMapping(value = "/editSystemAdminProfile", method = RequestMethod.POST)
    public String editSystemAdminProfile(@Valid @ModelAttribute("systemAdminProfileForm") SystemAdminProfileForm form, BindingResult bindingResult, Model model)
            throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages.addAll(userService.updateSystemAdminProfile(form));
            if (CollectionUtils.isEmpty(messages)) {
                model.addAttribute(Constant.Session.MESSAGES, Arrays.asList(messageSource.info("I0001")));
            }
        } else {
            model.addAttribute(Constant.Session.MESSAGES, messages);
        }

        return "profiles/editSystemAdminProfile";
    }
}
