package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.LocationLogsEntity;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class LocationLogsDaoImpl extends BaseDaoImpl<LocationLogsEntity> implements LocationLogsDao {

    public LocationLogsDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
}
