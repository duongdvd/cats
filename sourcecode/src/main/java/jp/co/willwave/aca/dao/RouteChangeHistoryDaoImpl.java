package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.RouteChangeHistoryEntity;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class RouteChangeHistoryDaoImpl extends BaseDaoImpl<RouteChangeHistoryEntity> implements RouteChangeHistoryDao {
    private static Logger logger = Logger.getLogger(RouteDetailDao.class);

    public RouteChangeHistoryDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}
