package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RouterDetailMapsEntity;

public interface RouterDetailMapsDao extends BaseDao<RouterDetailMapsEntity> {
    RouterDetailMapsEntity getListRouteDetailMap(Long routesId) throws CommonException;
}
