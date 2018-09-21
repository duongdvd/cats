package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.FinishRouteDetailMessageDTO;
import jp.co.willwave.aca.exception.CommonException;

import java.util.List;

public interface MasterArrivalNotesService {
    List<FinishRouteDetailMessageDTO> getFinishedMessages(Long devicesId) throws CommonException;
}
