package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.AssignDeviceDTO;
import jp.co.willwave.aca.dto.api.DeviceDTO;
import jp.co.willwave.aca.dto.api.EditDeviceDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.web.form.DeviceExportForm;
import jp.co.willwave.aca.web.form.SearchAssignDeviceForm;
import jp.co.willwave.aca.web.form.SearchDeviceForm;
import jp.co.willwave.aca.web.form.route.DeviceCarDTO;

import java.util.Date;
import java.util.List;

public interface DevicesDao extends BaseDao<DevicesEntity> {
    DevicesEntity findByLoginId(String loginId) throws CommonException;

    DevicesEntity findByToken(String token);

    List<DeviceDTO> findByDisivions(List<Long> divisionIdList, Integer offset, Integer maxResult, Boolean noPaging, Boolean isAssignNewDevice);

    List<DeviceExportForm> exportByDisivions(List<Long> divisionIdList);

    List<DeviceExportForm> exportByMultiCondition(SearchDeviceForm searchDeviceForm, List<Long> divisionIdList);

    List<DeviceDTO> findByMultiCondition(SearchDeviceForm searchDeviceForm, Integer offset, Integer maxResults, Boolean noPaging, List<Long> divisionTotalList, Boolean isAssignNewDevice);

    Long findDivisionIdBasedOnDevice(Long deviceId);

    EditDeviceDTO findEditDevice(Long deviceId);

    List<AssignDeviceDTO> getAssignDeviceUser(Long userId, Integer offset, Integer maxResults, Boolean noPaging);

    List<AssignDeviceDTO> getAssignDeviceUserByMultiCondition(Long userId, SearchAssignDeviceForm searchAssignDeviceForm, Integer offset, Integer maxResults, Boolean noPaging);

    Long countAllValidDevice(List<Long> deviceIdList);

    Long countCarWithPlateNumber(String plateNumber);

    @Deprecated
    List<DevicesEntity> findDevicesActiveTodayAndOnline(Date date);

    List<DevicesEntity> findRunningDevicesAndActive();

    List<DevicesEntity> findDevicesManaged(Long usersId);

    List<DeviceCarDTO> findCarsManaged(Long usersId);

    List<Object[]> getDevicesManageByOperator(Long userId, CarStatus carStatus, RunningStatus runningStatus);

    List<Object[]> getDevicesByDivisions(List<Long> divisionIds, CarStatus carStatus, RunningStatus runningStatus);

    List<DevicesEntity> findDevicesByLoginIdList(List<String> loginIdList);

    DevicesEntity findByRouteActualId(Long routeId);
}
