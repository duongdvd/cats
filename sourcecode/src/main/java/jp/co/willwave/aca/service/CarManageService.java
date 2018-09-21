package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.AllRouteDTO;
import jp.co.willwave.aca.dto.api.CarDetailDTO;
import jp.co.willwave.aca.dto.api.CarMapDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;

import java.util.List;
import java.util.Map;

public interface CarManageService {

    CarDetailDTO getCarDetailInfo(Long deviceId) throws CommonException;

    Map<List<DivisionsEntity>, List<CarMapDTO>> getCarListManagedByOperator(Long divisionId, Long userId, CarStatus carStatus, RunningStatus runningStatus) throws CommonException;

    Map<List<DivisionsEntity>, List<CarMapDTO>> getCarListOfViewer(Long divisionId, CarStatus carStatus, RunningStatus runningStatus) throws CommonException;

    AllRouteDTO getAllRouteDetailByDevice(Long deviceId, RunningStatus runningStatus);

}
