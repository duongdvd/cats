package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.SafetyConfirmLogsEntity;

import java.util.List;

public interface SafetyConfirmLogsDao extends BaseDao<SafetyConfirmLogsEntity> {
    List<SafetyConfirmLogsEntity> findByRoutesDetailId(List<Long> routeDetailIds);

    List<SafetyConfirmLogsEntity> getSafetyConfirmLogsNotRead(Long usersId);
}
