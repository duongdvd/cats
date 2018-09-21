package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.SystemAdminEntity;

public interface SystemAdminDao extends BaseDao<SystemAdminEntity> {
    SystemAdminEntity getByLoginId(String loginId) throws CommonException;

    long updatePassword(String userName, String newPassword);

    SystemAdminEntity getSystemAdminByEmail(String email) throws CommonException;
}
