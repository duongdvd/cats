package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;
import jp.co.willwave.aca.utilities.ConversionUtil;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class MasterSysConfigsDaoImpl extends BaseDaoImpl<MasterSysConfigsEntity> implements MasterSysConfigsDao {

    public MasterSysConfigsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<MasterSysConfigsEntity> findAll() throws CommonException {
        return ConversionUtil.castList(executeWithException(() ->{
            CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
            CriteriaQuery<MasterSysConfigsEntity> criteriaQuery = criteriaBuilder.createQuery(MasterSysConfigsEntity.class);
            Root<MasterSysConfigsEntity> root = criteriaQuery.from(MasterSysConfigsEntity.class);
            criteriaQuery.select(root);
            return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
        }), MasterSysConfigsEntity.class);

    }
}
