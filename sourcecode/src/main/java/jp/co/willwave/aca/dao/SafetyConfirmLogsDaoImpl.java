package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.SafetyConfirmLogsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SafetyConfirmLogsDaoImpl extends BaseDaoImpl<SafetyConfirmLogsEntity> implements SafetyConfirmLogsDao {
    public SafetyConfirmLogsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<SafetyConfirmLogsEntity> findByRoutesDetailId(List<Long> routeDetailIds) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM SafetyConfirmLogsEntity WHERE routeDetailId IN :routeDetailIds ORDER BY " +
                        "notificationTime ")
                .setParameter("routeDetailIds", routeDetailIds)
                .getResultList();
    }

    @Override
    public List<SafetyConfirmLogsEntity> getSafetyConfirmLogsNotRead(Long usersId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM SafetyConfirmLogsEntity WHERE usersId = :usersId AND isRead = FALSE " +
                        " AND DATE(notificationTime) = CURDATE() ")
                .setParameter("usersId", usersId)
                .getResultList();
    }
}
