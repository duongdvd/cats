package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.EmergencyLogsEntity;

import java.util.List;

public interface EmergencyLogsDao extends BaseDao<EmergencyLogsEntity> {
    List<EmergencyLogsEntity> findByRoutesDetailIds(List<Long> routesDetailIds);

    List<EmergencyLogsEntity> getEmergencyLogs(Long usersId);
}
