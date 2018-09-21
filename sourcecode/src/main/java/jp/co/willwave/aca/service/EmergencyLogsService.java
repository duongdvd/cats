package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.EmergencyDTO;
import jp.co.willwave.aca.model.entity.EmergencyLogsEntity;

import java.util.List;

public interface EmergencyLogsService {
    List<EmergencyLogsEntity> findByRoutesDetailIds(List<Long> routesDetailIds);

    void sendEmergency(EmergencyDTO emergencyDTO) throws Exception;
}
