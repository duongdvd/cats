package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CarsEntity;

public interface CarsDao extends BaseDao<CarsEntity> {
    CarsEntity findByDeviceId(Long deviceId);

}
