package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasCustomersEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class DivisionsHasCustomersDaoImpl extends BaseDaoImpl<DivisionsHasCustomersEntity> implements DivisionsHasCustomersDao {
    public DivisionsHasCustomersDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public DivisionsHasCustomersEntity findByDivisionIdAndCustomerId(Long divisionsId, Long customerId) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<DivisionsHasCustomersEntity> criteriaQuery = criteriaBuilder.createQuery
                (DivisionsHasCustomersEntity.class);
        Root<DivisionsHasCustomersEntity> root = criteriaQuery.from(DivisionsHasCustomersEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.and(criteriaBuilder.equal(root.get("divisionsId"),
                divisionsId), criteriaBuilder.equal(root.get("customersId"), customerId)));
        List<DivisionsHasCustomersEntity> divisionsHasDevicesEntities = sessionFactory.getCurrentSession().createQuery
                (criteriaQuery).getResultList();
        if (CollectionUtils.isEmpty(divisionsHasDevicesEntities)) {
            return null;
        }
        return divisionsHasDevicesEntities.get(0);
    }

    @Override
    public List<CustomersEntity> getCustomers(List<Long> divisionIds) {
        return null;
    }
}
