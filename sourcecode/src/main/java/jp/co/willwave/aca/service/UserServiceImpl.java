package jp.co.willwave.aca.service;

import jp.co.willwave.aca.config.ClientConfig;
import jp.co.willwave.aca.config.ContentMail;
import jp.co.willwave.aca.constants.BusinessConstants;
import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.model.enums.Channel;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.*;
import jp.co.willwave.aca.web.form.*;
import jp.co.willwave.aca.web.form.division.UserForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    private final CatsMessageResource messageResource;

    private final UsersDao userDao;

    private final DivisionsDao divisionsDao;

    private final SystemAdminDao systemAdminDao;
    private final UsersManageDevicesDao usersManageDevicesDao;
    private final MessagesDao messagesDao;
    private final AccessDao accessDao;
    private final EmailService emailService;
    private final ContentMail contentMail;
    private final ClientConfig clientConfig;
    private final DivisionService divisionService;
    private final WebUtil webUtil;

    @Autowired
    public UserServiceImpl(CatsMessageResource messageResource, UsersDao userDao,
                           DivisionsDao divisionsDao, SystemAdminDao systemAdminDao,
                           UsersManageDevicesDao usersManageDevicesDao, MessagesDao messagesDao,
                           EmailService emailService, ContentMail contentMail, AccessDao accessDao,
                           ClientConfig clientConfig, DivisionService divisionService, WebUtil webUtil) {
        this.messageResource = messageResource;
        this.userDao = userDao;
        this.divisionsDao = divisionsDao;
        this.systemAdminDao = systemAdminDao;
        this.usersManageDevicesDao = usersManageDevicesDao;
        this.messagesDao = messagesDao;
        this.emailService = emailService;
        this.contentMail = contentMail;
        this.accessDao = accessDao;
        this.clientConfig = clientConfig;
        this.divisionService = divisionService;
        this.webUtil = webUtil;
    }

    @Override
    public UsersEntity getUser(Long id) throws CommonException {
        return userDao.findById(id, UsersEntity.class);
    }

    @Override
    public SystemAdminEntity getSystemAdmin(Long id) throws CommonException {
        return systemAdminDao.findById(id, SystemAdminEntity.class);
    }

    @Override
    public List<UsersEntity> searchUser(SearchCondition searchCondition) throws CommonException {
        return userDao.search(searchCondition, UsersEntity.class);
    }

    @Override
    public List<Message> createUser(UsersEntity newUser) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<Message> messages = new ArrayList<>(checkExistsUser(newUser));
        if (CollectionUtils.isEmpty(messages)) {
            if (StringUtils.isEmpty(newUser.getPasswd())) {
                newUser.setPasswd(BusinessConstants.PASS_DEFAULT);
            }
            newUser.setPasswd(PasswordUtils.generateSecurePassword(newUser.getPasswd(), Constant.SHA_512_PASSWORD_KEY));
            newUser.setCreateBy(userLoginId);
            userDao.insert(newUser);
        }
        return messages;
    }

    @Override
    public List<Message> deleteUser(UsersEntity user) throws CommonException {
        userDao.delete(user);
        return new ArrayList<>();
    }

    @Override
    public List<Message> updateUser(UsersEntity usersEntity) throws CommonException {
        userDao.update(usersEntity);
        return new ArrayList<>();
    }

    @Override
    public List<Message> login(String loginId, String password, Channel channel) throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (channel.isSystemAdmin()) {
            messages.addAll(loginSystemAdmin(loginId.trim(), password.trim()));
        } else {
            messages.addAll(loginUser(loginId.trim(), password.trim()));
        }

        return messages;
    }

    private List<Message> loginUser(String loginId, String password) throws CommonException {
        List<Message> messages = new ArrayList<>();
        UsersEntity user = userDao.getByLoginId(loginId);
        // In case user name or password input is incorrect.
        if (user == null || !PasswordUtils.verifyUserPassword(
                password, user.getPasswd(), Constant.SHA_512_PASSWORD_KEY)) {
            messages.add(messageResource.get(Constant.ErrorCode.USER_OR_PASS_INCORRECT));
        } else if (!user.getStatus()) {
            messages.add(messageResource.get(Constant.ErrorCode.USER_INACTIVE));
        } else {
            // Saving User Information into Session.
            UserInfo userInfo = ConversionUtil.mapper(user, UserInfo.class);
            userInfo.setConfigNotification(clientConfig.getNotification());

            // Saving division info into UserInfo.
            DivisionsEntity divisionsEntity = divisionsDao.findById(userInfo.getDivisionsId(), DivisionsEntity.class);
            userInfo.setDivisionAddress(divisionsEntity.getDivisionAddress());

            SessionUtil.setAttribute(Constant.Session.USER_LOGIN_INFO, userInfo);
            SessionUtil.setAttribute(Constant.Session.ACCESS_PATHS, accessDao.getByRoleId(userInfo.getRoleId()));
        }

        return messages;
    }

    private List<Message> loginSystemAdmin(String userName, String password) throws CommonException {
        List<Message> messages = new ArrayList<>();
        SystemAdminEntity systemAdmin = systemAdminDao.getByLoginId(userName);
        // In case user name or password input is incorrect.
        if (systemAdmin == null || !PasswordUtils.verifyUserPassword(password, systemAdmin.getPasswd(), Constant.SHA_512_PASSWORD_KEY)) {
            messages.add(messageResource.get(Constant.ErrorCode.USER_OR_PASS_INCORRECT));
        } else {
            // Saving User Information into Session.
            UserInfo userInfo = ConversionUtil.mapper(systemAdmin, UserInfo.class);
            userInfo.setRoleId(UserRole.SYSTEM_ADMIN.getRole());
            SessionUtil.setAttribute(Constant.Session.USER_LOGIN_INFO, userInfo);
            SessionUtil.setAttribute(Constant.Session.ACCESS_PATHS, accessDao.getByRoleId(userInfo.getRoleId()));
        }

        return messages;
    }

    @Override
    public void logout() {
        SessionUtil.invalidate();
    }

    @Override
    public boolean existUserId(Long id) throws CommonException {
        return userDao.findById(id, UsersEntity.class) != null;
    }

    @Override
    public boolean existsLoginId(String loginId) throws CommonException {
        return userDao.find("loginId", loginId, UsersEntity.class) != null;
    }

    @Override
    public boolean existEmail(String email) throws CommonException {
        return userDao.find("userEmail", email, UsersEntity.class) != null;
    }

    @Override
    public UsersEntity getUserProfile(Long id) throws CommonException {
        return userDao.findById(id, UsersEntity.class);
    }

    @Override
    public List<Message> delete(Long id) throws CommonException {
        UserInfo currentUser = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        if (currentUser.getId().equals(id)) {
            throw new CommonException();
        }
        if (checkConstrainUser(id)) {
            throw new CommonException();
        }
        UsersEntity usersEntity = userDao.findById(id, UsersEntity.class);
        if (usersEntity == null) {
            return new ArrayList<>();
        }
        return deleteUser(usersEntity);
    }

    @Override
    public List<EmployeeForm> searchEmployees(SearchEmployeeForm searchEmployeeForm, Integer offset, Integer maxResults) {
        List<UsersEntity> usersEntities = userDao.searchEmployee(searchEmployeeForm, offset, maxResults);
        return ConversionUtil.mapperAsList(usersEntities, EmployeeForm.class);
    }

    @Override
    public Long countSearchEmployees(SearchEmployeeForm searchEmployeeForm) {
        List<UsersEntity> usersEntities = userDao.searchAll(searchEmployeeForm);
        return (long) usersEntities.size();
    }

    @Override
    public List<UserForm> getUserByDivisionExceptViewer(Long divisionId) {
        List<UsersEntity> usersEntities = userDao.findByDivision(divisionId);
        if (CollectionUtils.isEmpty(usersEntities)) {
            return new ArrayList<>();
        }
        for (int i=0;i<usersEntities.size();i++){
            if(usersEntities.get(i).getRoleId() == UserRole.VIEWER.getRole()){
                usersEntities.remove(i);
                i--;
            }
        }
        return ConversionUtil.mapperAsList(usersEntities, UserForm.class);
    }

    /**
     * format content of the reset password mail
     * @param password password
     * @return content has been formatted
     * @throws IOException
     */
    private String formatContentResetPasswordMail(String fullName, String password, boolean systemAdmin) throws IOException, CommonException {
        File mailContentFile = ResourceUtils.getFile(contentMail.getResetPasswordTemplateFilePath());

        InputStream in = new FileInputStream(mailContentFile);
        Map<String, Object> map = new HashMap<>();
        map.put("fullname", fullName);
        map.put("password", password);
        String loginPortal;
        if (systemAdmin) {
            loginPortal = WebUtil.combineUrl(webUtil.getBaseUrl(), "systemAdminLoginView"); //TODO constant
        } else {
            loginPortal = WebUtil.combineUrl(webUtil.getBaseUrl(), "userLoginView"); //TODO constant
        }
        map.put("loginPortal", loginPortal);

        return CommonUtil.format(in, map);
    }

    @Override
    public List<Message> resetUserPassword(String email) throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (email == null || org.apache.commons.lang3.StringUtils.isBlank(email.trim())) {
            messages.add(messageResource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"email.notEmpty"}));
            return messages;
        }

        UsersEntity usersEntity = userDao.getUserByEmail(email.trim());
        if (usersEntity != null) {
            String newPassword = PasswordUtils.generateValidPassword();
            usersEntity.setPasswd(PasswordUtils.generateSecurePassword(newPassword, Constant.SHA_512_PASSWORD_KEY));
            userDao.update(usersEntity);
            try {
                String fullName = String.join(" ", usersEntity.getFirstName(), usersEntity.getLastName());
                String content = this.formatContentResetPasswordMail(fullName, newPassword, false);
                emailService.sendMessageHtml(email.trim(), contentMail.getResetPasswordSubject(), content);
            } catch (Exception e) {
                messages.add(messageResource.getMessage(Constant.ErrorCode.SEND_MAIL_FAILED, new String[]{email}));
            }
        } else {
            return messages;
        }
        return messages;
    }

    @Override
    public List<Message> resetSystemAdminPassword(String email) throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (email == null || org.apache.commons.lang3.StringUtils.isBlank(email.trim())) {
            messages.add(messageResource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"email.notEmpty"}));
            return messages;
        }

        SystemAdminEntity systemAdminEntity = systemAdminDao.getSystemAdminByEmail(email.trim());
        if (systemAdminEntity != null) {
            String newPassword = PasswordUtils.generateValidPassword();
            systemAdminEntity.setPasswd(PasswordUtils.generateSecurePassword(newPassword, Constant.SHA_512_PASSWORD_KEY));
            systemAdminDao.update(systemAdminEntity);
            try {
                String fullName = String.join(" ", systemAdminEntity.getFirstName(), systemAdminEntity.getLastName());
                String content = this.formatContentResetPasswordMail(fullName, newPassword, true);
                emailService.sendMessageHtml(email.trim(), contentMail.getResetPasswordSubject(), content);
            } catch (Exception e) {
                messages.add(messageResource.getMessage(Constant.ErrorCode.NOT_FOUND_USER, new String[]{email}));
            }
        } else {
            messages.add(messageResource.getMessage(Constant.ErrorCode.NOT_FOUND_SYSADMIN, new String[]{email}));
            return messages;
        }
        return messages;
    }

    private List<Message> checkExistsUser(UsersEntity user) throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (existsLoginId(user.getLoginId())) {
            messages.add(new Message(Constant.ErrorCode.ALREADY_EXISTS,
                    messageResource.get(Constant.ErrorCode.ALREADY_EXISTS, new String[]{user.getLoginId()})));
        } else if (existEmail(user.getUserEmail())) {
            messages.add(new Message(Constant.ErrorCode.ALREADY_EXISTS,
                    messageResource.get(Constant.ErrorCode.ALREADY_EXISTS, new String[]{user.getUserEmail()})));
        }

        return messages;
    }

    @Override
    public List<Message> updateUserProfile(UserProfilesForm userProfilesForm, Long userId) throws CommonException {
        List<Message> messages = new ArrayList<>();
        messages.addAll(this.validateProfilesForm(userProfilesForm));
        UsersEntity oldUser = this.getUserProfile(userId);
        if (oldUser == null) messages.add(messageResource.getMessage(Constant.ErrorCode.USER_NOT_EXIST, new Object[]{userId}));

        if (CollectionUtils.isEmpty(messages)) {
            oldUser.setFirstName(userProfilesForm.getFirstName());
            oldUser.setLastName(userProfilesForm.getLastName());
            oldUser.setUserAddress(userProfilesForm.getUserAddress());
            oldUser.setUserEmail(userProfilesForm.getUserEmail());
            oldUser.setFixedPhone(userProfilesForm.getFixedPhone());
            oldUser.setMobilePhone(userProfilesForm.getMobilePhone());
            oldUser.setFaxNumber(userProfilesForm.getFaxNumber());
            if (!StringUtils.isEmpty(userProfilesForm.getNewPassword())) {
                if (!PasswordUtils.verifyUserPassword(userProfilesForm.getPasswd(), oldUser.getPasswd(), Constant.SHA_512_PASSWORD_KEY)) {
                    messages.add(messageResource.get(Constant.ErrorCode.CUR_PASSWORD_INCORRECT));
                    return messages;
                }
                oldUser.setPasswd(PasswordUtils.generateSecurePassword(userProfilesForm.getNewPassword(), Constant.SHA_512_PASSWORD_KEY));
            }
            userDao.update(oldUser);
        }

        return messages;
    }
    public List<Message> updateLoginUserProfile(UserProfilesForm userProfilesForm) throws CommonException {
        Long userId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<Message> messages = this.updateUserProfile(userProfilesForm, userId);
        UserInfo userInfo = ConversionUtil.mapper(this.getUser(userId), UserInfo.class);
        userInfo.setConfigNotification(clientConfig.getNotification());
        if (CollectionUtils.isEmpty(messages)) {
            // save division address into User session
            DivisionsEntity divisionsLoginUser = divisionService.findById(userInfo.getDivisionsId());
            userInfo.setDivisionAddress(divisionsLoginUser.getDivisionAddress());
            SessionUtil.setAttribute(Constant.Session.USER_LOGIN_INFO, userInfo);
        }
        return messages;
    }

    @Override
    public List<Message> updateSystemAdminProfile(SystemAdminProfileForm systemAdminProfileForm) throws CommonException {
        List<Message> messages = new ArrayList<>();
        messages.addAll(this.validateProfilesForm(systemAdminProfileForm));
        Long userId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        SystemAdminEntity oldAdmin = this.getSystemAdmin(userId);

        if (CollectionUtils.isEmpty(messages)) {
            oldAdmin.setFirstName(systemAdminProfileForm.getFirstName());
            oldAdmin.setLastName(systemAdminProfileForm.getLastName());
            oldAdmin.setUserAddress(systemAdminProfileForm.getUserAddress());
            oldAdmin.setUserEmail(systemAdminProfileForm.getUserEmail());
            oldAdmin.setFixedPhone(systemAdminProfileForm.getFixedPhone());
            oldAdmin.setMobilePhone(systemAdminProfileForm.getMobilePhone());
            oldAdmin.setFaxNumber(systemAdminProfileForm.getFaxNumber());
            if (!StringUtils.isEmpty(systemAdminProfileForm.getNewPassword())) {
                if (!PasswordUtils.verifyUserPassword(systemAdminProfileForm.getPasswd(), oldAdmin.getPasswd(), Constant.SHA_512_PASSWORD_KEY)) {
                    messages.add(messageResource.get(Constant.ErrorCode.CUR_PASSWORD_INCORRECT));
                    return messages;
                }
                oldAdmin.setPasswd(PasswordUtils.generateSecurePassword(systemAdminProfileForm.getNewPassword(), Constant.SHA_512_PASSWORD_KEY));
            }
            systemAdminDao.update(oldAdmin);

            UserInfo userInfo = ConversionUtil.mapper(oldAdmin, UserInfo.class);
            userInfo.setRoleId(UserRole.SYSTEM_ADMIN.getRole());
            if (CollectionUtils.isEmpty(messages)) SessionUtil.setAttribute(Constant.Session.USER_LOGIN_INFO, userInfo);
        }

        return messages;
    }

    public boolean checkConstrainUser(Long userId) {
        DivisionsEntity divisionsEntity = divisionsDao.findManagedDivisionByUserID(userId);
        if (divisionsEntity != null) {
            return true;
        }
        List<UsersManageDevicesEntity> devicesManaged = usersManageDevicesDao.findByUserId(userId);
        if (!CollectionUtils.isEmpty(devicesManaged)) {
            return true;
        }
        List<MessagesEntity> messagesEntities = messagesDao.findByUserId(userId);
        if (!CollectionUtils.isEmpty(messagesEntities)) {
            return true;
        }
        return false;
    }

    @Override
    public UsersEntity getUserCreateRoute(Long routePlanedId) {
        return userDao.getUserCreateRoute(routePlanedId);
    }

    @Override
    public List<Message> validateProfilesForm(UsersForm userForm) {
        List<Message> messages = new ArrayList<>();

        if (!StringUtils.isEmpty(userForm.getNewPassword()) || !StringUtils.isEmpty(userForm.getConfirmPassword())) {
            if (StringUtils.isEmpty(userForm.getPasswd())) {
                messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"profiles.title.Password"}));
            }
            if (StringUtils.isEmpty(userForm.getNewPassword())) {
                messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"profiles.title.NewPassword"}));
            }
            if (StringUtils.isEmpty(userForm.getConfirmPassword())) {
                messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"profiles.title.ConfirmPassword"}));
            }
            if (!StringUtils.isEmpty(userForm.getNewPassword())
                && !StringUtils.isEmpty(userForm.getConfirmPassword())
                && !Objects.equals(userForm.getNewPassword(), userForm.getConfirmPassword())) {
                messages.add(messageResource.get(Constant.ErrorCode.PASS_CONFIRM_NOT_MATCH));
            }
        }
        return messages;
    }
}
