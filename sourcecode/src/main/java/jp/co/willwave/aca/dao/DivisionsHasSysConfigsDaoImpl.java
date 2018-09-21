package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class DivisionsHasSysConfigsDaoImpl extends BaseDaoImpl<DivisionsHasSysConfigsEntity>
        implements DivisionsHasSysConfigsDao {
    public DivisionsHasSysConfigsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<DivisionsHasSysConfigsEntity> findByDivisionId(Long divisionsId) throws CommonException {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(DivisionsHasSysConfigsEntity.class);
        Root<DivisionsHasSysConfigsEntity> root = criteriaQuery.from(DivisionsHasSysConfigsEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("divisionsId"), divisionsId));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<DivisionsHasSysConfigsEntity> findConfigDivisionAndKey(List<Long> divisionIds, ConfigEnum key) {
        StringBuilder sql = new StringBuilder("FROM DivisionsHasSysConfigsEntity WHERE key = :key ");
        sql.append("AND deleteFlg = false AND divisionsId IN :divisionIds ");
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("key", key)
                .setParameter("divisionIds", divisionIds)
                .getResultList();
    }

    @Override
    public DivisionsHasSysConfigsEntity findConfigDivisionAndKey(Long divisionId, ConfigEnum key) {
        StringBuilder sql = new StringBuilder("FROM DivisionsHasSysConfigsEntity WHERE key = :key ");
        sql.append("AND deleteFlg = false AND divisionsId = :divisionId ");
        List<DivisionsHasSysConfigsEntity> result = sessionFactory.getCurrentSession()
                .createQuery(sql.toString())
                .setParameter("key", key)
                .setParameter("divisionId", divisionId)
                .getResultList();
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }
        return result.get(0);
    }
}
