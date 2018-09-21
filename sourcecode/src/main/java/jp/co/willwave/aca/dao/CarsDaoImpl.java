package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CarsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class CarsDaoImpl extends BaseDaoImpl<CarsEntity> implements CarsDao {
    public CarsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public CarsEntity findByDeviceId(Long deviceId) {
        logger.info("CarsDao.findByDeviceId");
        CriteriaBuilder builder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<CarsEntity> criteria = builder.createQuery(CarsEntity.class);
        Root<CarsEntity> root = criteria.from(CarsEntity.class);
        criteria.select(root).where(builder.equal(root.get("devicesId"), deviceId),builder.equal(root.get("latestFlg"), 1));
        List<CarsEntity> carsEntities = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        if (CollectionUtils.isEmpty(carsEntities)) {
            return null;
        }
        return carsEntities.get(0);
    }
}
