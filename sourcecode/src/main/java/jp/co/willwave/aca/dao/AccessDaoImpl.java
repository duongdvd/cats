package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.AccessEntity;
import jp.co.willwave.aca.model.enums.DeleteFlg;
import jp.co.willwave.aca.model.enums.PathType;
import jp.co.willwave.aca.model.enums.UserRole;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Repository
public class AccessDaoImpl extends BaseDaoImpl<AccessEntity> implements AccessDao {
    public AccessDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<AccessEntity> getByRoleId(Integer roleId) {
        StringBuilder sql = new StringBuilder("SELECT a FROM AccessEntity as a ");
        sql.append("WHERE a.roleId = :roleId ");
        sql.append("AND a.deleteFlg = :deleteFlg");

        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), AccessEntity.class)
                .setParameter("roleId", roleId)
                .setParameter("deleteFlg", false)
                .getResultList();
    }
}
