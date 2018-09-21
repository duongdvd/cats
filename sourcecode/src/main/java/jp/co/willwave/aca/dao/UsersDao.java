package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.web.form.SearchEmployeeForm;

import java.util.List;

public interface UsersDao extends BaseDao<UsersEntity> {
    UsersEntity getByLoginId(String loginId) throws CommonException;

    long updatePassword(String userName, String newPassword);

    List<UsersEntity> findUsersByDivisionId(List<Long> divisionIds) throws CommonException;

    List<UsersEntity> searchEmployee(SearchEmployeeForm searchEmployeeForm, Integer offset, Integer maxResults);

    List<UsersEntity> searchAll(SearchEmployeeForm searchEmployeeForm);

    boolean checkExistsLoginId(String loginId) throws CommonException;

    boolean checkExistsMail(String email) throws CommonException;

    UsersEntity findUserByDivisionId(Long divisionId);

    List<UsersEntity> getInfoUserManageDevice(Long devicesId);

    List<UsersEntity> findByDivision(Long divisionId);

    UsersEntity getUserByEmail(String email) throws CommonException;

    UsersEntity getUserCreateRoute(Long routePlanedId);
}
