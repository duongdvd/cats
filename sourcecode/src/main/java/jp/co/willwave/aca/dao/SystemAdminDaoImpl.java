package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.SystemAdminEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class SystemAdminDaoImpl extends BaseDaoImpl<SystemAdminEntity> implements SystemAdminDao {

    public SystemAdminDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public SystemAdminEntity getByLoginId(String loginId) throws CommonException {
        SystemAdminEntity systemAdminEntity = find("loginId", loginId, SystemAdminEntity.class);
        if (systemAdminEntity != null && systemAdminEntity.getLoginId().equals(loginId)) {
            return systemAdminEntity;
        }
        return null;
    }

    @Override
    public long updatePassword(String userName, String newPassword) {
        return 0;
    }

    @Override
    public SystemAdminEntity getSystemAdminByEmail(String email) throws CommonException {
        SystemAdminEntity systemAdminEntity = find("userEmail", email, SystemAdminEntity.class);
        if (systemAdminEntity != null) {
            return systemAdminEntity;
        }
        return null;
    }
}
