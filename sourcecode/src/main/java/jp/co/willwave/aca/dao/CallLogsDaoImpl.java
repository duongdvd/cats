package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CallLogsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CallLogsDaoImpl extends BaseDaoImpl<CallLogsEntity> implements CallLogsDao {

    public CallLogsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<CallLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM CallLogsEntity WHERE routeDetailId IN :routeDetailIds ", CallLogsEntity.class)
                .setParameter("routeDetailIds", routeDetailIds)
                .getResultList();
    }
}
