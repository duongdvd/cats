package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.MasterArrivalNotesEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class MasterArrivalNotesDaoImpl extends BaseDaoImpl<MasterArrivalNotesEntity>
        implements MasterArrivalNotesDao {

    public MasterArrivalNotesDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public List<MasterArrivalNotesEntity> findByCompanyId(Long companyId) {
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<MasterArrivalNotesEntity> criteriaQuery
                = criteriaBuilder.createQuery(MasterArrivalNotesEntity.class);
        Root<MasterArrivalNotesEntity> root = criteriaQuery.from(MasterArrivalNotesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("companiesId"), companyId));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }
}
