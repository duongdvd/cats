package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsHasDevicesEntity;

import java.util.List;

public interface DivisionsHasDevicesDao extends BaseDao<DivisionsHasDevicesEntity> {
    Long getCompaniesIdByDevicesId(Long devicesId) throws CommonException;

    List<DivisionsHasDevicesEntity> findByDeviceIdAndDivisionID(Long deviceId, Long divisionId);
}
