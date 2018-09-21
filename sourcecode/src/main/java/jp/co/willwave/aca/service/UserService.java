package jp.co.willwave.aca.service;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.SearchCondition;
import jp.co.willwave.aca.model.entity.SystemAdminEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.model.enums.Channel;
import jp.co.willwave.aca.web.form.*;
import jp.co.willwave.aca.web.form.division.UserForm;

import java.util.List;

public interface UserService {
    UsersEntity getUser(Long id) throws CommonException;

    SystemAdminEntity getSystemAdmin(Long id) throws CommonException;

    List<UsersEntity> searchUser(SearchCondition searchCondition) throws CommonException;

    List<Message> createUser(UsersEntity usersEntity) throws CommonException;

    List<Message> deleteUser(UsersEntity usersEntity) throws CommonException;

    List<Message> updateUser(UsersEntity usersEntity) throws CommonException;

    List<Message> login(String loginId, String password, Channel channel) throws CommonException;

    void logout();

    boolean existUserId(Long id) throws CommonException;

    boolean existsLoginId(String loginId) throws CommonException;

    boolean existEmail(String email) throws CommonException;

    UsersEntity getUserProfile(Long id) throws CommonException;

    List<Message> updateUserProfile(UserProfilesForm userProfilesForm, Long userId) throws CommonException;

    List<Message> updateLoginUserProfile(UserProfilesForm userProfilesForm) throws CommonException;

    List<Message> updateSystemAdminProfile(SystemAdminProfileForm systemAdmin) throws CommonException;

    List<Message> delete(Long id) throws CommonException;

    List<EmployeeForm> searchEmployees(SearchEmployeeForm searchEmployeeForm, Integer offset, Integer maxResults);

    Long countSearchEmployees(SearchEmployeeForm searchEmployeeForm);

    List<UserForm> getUserByDivisionExceptViewer(Long divisionId) throws CommonException;

    List<Message> resetUserPassword(String email) throws CommonException;

    List<Message> resetSystemAdminPassword(String email) throws CommonException;

    UsersEntity getUserCreateRoute(Long routePlanId);

    List<Message> validateProfilesForm(UsersForm userForm);
}
