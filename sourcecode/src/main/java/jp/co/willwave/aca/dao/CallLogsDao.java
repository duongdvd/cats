package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.CallLogsEntity;

import java.util.List;

public interface CallLogsDao extends BaseDao<CallLogsEntity> {
    List<CallLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds);
}
