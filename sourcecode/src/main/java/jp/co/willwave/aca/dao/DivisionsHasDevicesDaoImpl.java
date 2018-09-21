package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasDevicesEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class DivisionsHasDevicesDaoImpl extends BaseDaoImpl<DivisionsHasDevicesEntity>
        implements DivisionsHasDevicesDao {

    public DivisionsHasDevicesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Long getCompaniesIdByDevicesId(Long devicesId) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT DISTINCT(companiesId) FROM DivisionsEntity ");
        sqlBuilder.append("WHERE id IN (SELECT divisionsId FROM DivisionsHasDevicesEntity WHERE devicesId = "
                + ":devicesId)");
        List<Long> companiesIds = sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), Long.class)
                .setParameter("devicesId", devicesId)
                .getResultList();
        if (CollectionUtils.isEmpty(companiesIds)) {
            return null;
        }
        return companiesIds.get(0);
    }

    @Override
    public List<DivisionsHasDevicesEntity> findByDeviceIdAndDivisionID(Long deviceId, Long divisionId) {
        logger.info("DevicesDao.findByToken");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DivisionsHasDevicesEntity> criteriaQuery = criteriaBuilder.createQuery(DivisionsHasDevicesEntity.class);
        Root<DivisionsHasDevicesEntity> root = criteriaQuery.from(DivisionsHasDevicesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("devicesId"), deviceId), criteriaBuilder.equal(root.get("divisionsId"), divisionId));
        List<DivisionsHasDevicesEntity> divisionsHasDevicesEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(divisionsHasDevicesEntities)) {
            return null;
        }
        return divisionsHasDevicesEntities;
    }
}
