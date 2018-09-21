package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.TimeLineDTO;

import java.util.List;

public interface TimeLineService {
    List<TimeLineDTO> getListTimeLineByRouteActual(Long routeActualId, String loginUser, String loginDevice);

    List<TimeLineDTO> getListTimeLineByRouteActualAndUser(Long routeActualId, String loginUser, String loginDevice, Long userId);
}
