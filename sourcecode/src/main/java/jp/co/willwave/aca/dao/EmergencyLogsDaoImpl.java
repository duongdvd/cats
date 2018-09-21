package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.EmergencyLogsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EmergencyLogsDaoImpl extends BaseDaoImpl<EmergencyLogsEntity> implements EmergencyLogsDao {

    public EmergencyLogsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<EmergencyLogsEntity> findByRoutesDetailIds(List<Long> routesDetailIds) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM EmergencyLogsEntity WHERE routeDetailId IN :routeDetailIds ORDER BY notificationTime ")
                .setParameter("routeDetailIds", routesDetailIds)
                .getResultList();
    }

    @Override
    public List<EmergencyLogsEntity> getEmergencyLogs(Long usersId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM EmergencyLogsEntity WHERE usersId = :usersId AND isRead = FALSE AND DATE(notificationTime) = CURDATE()")
                .setParameter("usersId", usersId)
                .getResultList();
    }
}
