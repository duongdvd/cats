package jp.co.willwave.aca.service;

import jp.co.willwave.aca.model.entity.SafetyConfirmLogsEntity;

import java.util.List;

public interface SafetyConfirmService {
    void checkSafetyConfirmDevices();

    List<SafetyConfirmLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds);
}
