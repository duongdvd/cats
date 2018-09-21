package jp.co.willwave.aca.service;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.CallLogsEntity;

import java.util.List;

public interface CallLogsService {
    List<CallLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds);

    void writeCallLog(CallLogsEntity callLog) throws CommonException;
}
