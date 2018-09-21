package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.UsersManageDevicesEntity;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class UsersManageDevicesDaoImpl extends BaseDaoImpl<UsersManageDevicesEntity> implements UsersManageDevicesDao {

    public UsersManageDevicesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public UsersManageDevicesEntity findByUserIdAndDeviceId(Long userId, Long deviceId) {
        logger.info("UsersManageDevicesDao.findByUserIdAndDeviceId");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<UsersManageDevicesEntity> criteriaQuery = criteriaBuilder.createQuery(UsersManageDevicesEntity.class);
        Root<UsersManageDevicesEntity> root = criteriaQuery.from(UsersManageDevicesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("devicesId"), deviceId), criteriaBuilder.equal(root.get("usersId"), userId), criteriaBuilder.equal(root.get("deleteFlg"), 0));
        List<UsersManageDevicesEntity> usersManageDevicesEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(usersManageDevicesEntities)) {
            return null;
        }
        return usersManageDevicesEntities.get(0);
    }

    @Override
    public List<UsersManageDevicesEntity> findByUserIdAndDeviceIdList(Long userId, List<Long> deviceIdList) {
        logger.info("UsersManageDevicesDao.findByUserIdAndDeviceIdList");
        Query query = sessionFactory.getCurrentSession()
                .createQuery("FROM UsersManageDevicesEntity u where u.devicesId IN :deviceIdList AND u.usersId = :userId AND u.deleteFlg = 0").setParameter("deviceIdList", deviceIdList).setParameter("userId", userId);

        List<UsersManageDevicesEntity> usersManageDevicesEntityList = query.getResultList();

        if (CollectionUtils.isEmpty(usersManageDevicesEntityList)) {
            return null;
        }
        return usersManageDevicesEntityList;
    }

    @Override
    public List<UsersManageDevicesEntity> findByDeviceId(Long deviceId) {

        logger.info("UsersManageDevicesDao.findByDeviceId");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<UsersManageDevicesEntity> criteriaQuery = criteriaBuilder.createQuery(UsersManageDevicesEntity.class);
        Root<UsersManageDevicesEntity> root = criteriaQuery.from(UsersManageDevicesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("devicesId"), deviceId), criteriaBuilder.equal(root.get("deleteFlg"), 0));
        List<UsersManageDevicesEntity> usersManageDevicesEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(usersManageDevicesEntities)) {
            return null;
        }
        return usersManageDevicesEntities;
    }
    @Override
    public List<UsersManageDevicesEntity> findByUserId(Long userId) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<UsersManageDevicesEntity> criteriaQuery = criteriaBuilder.createQuery(UsersManageDevicesEntity.class);
        Root<UsersManageDevicesEntity> root = criteriaQuery.from(UsersManageDevicesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("usersId"), userId));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }
}
