package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.LocationTempDTO;

import java.util.List;

public interface LocationLogsService {
    void sendLocation(LocationTempDTO locationTemp) throws Exception;

    void sendLocationList(List<LocationTempDTO> locationTempDTOs) throws Exception;
}
