package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.RolesEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RolesDaoImpl extends BaseDaoImpl<RolesEntity> implements RolesDao {
    public RolesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
