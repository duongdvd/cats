package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RouterDetailMapsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class RouterDetailMapsDaoImpl extends BaseDaoImpl<RouterDetailMapsEntity> implements RouterDetailMapsDao {

    public RouterDetailMapsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public RouterDetailMapsEntity getListRouteDetailMap(Long routesId) throws CommonException {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery();
        Root<RouterDetailMapsEntity> root = criteriaQuery.from(RouterDetailMapsEntity.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("routesId"), routesId));
        List<RouterDetailMapsEntity> routeDetailMapsEntities =
                sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
        if (CollectionUtils.isEmpty(routeDetailMapsEntities)) {
            return null;
        }
        return routeDetailMapsEntities.get(0);
    }
}