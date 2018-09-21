package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.web.form.*;
import jp.co.willwave.aca.web.form.route.DeviceCarDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DevicesService {

    DevicesDTO login(DevicesDTO devicesDTO) throws Exception;

    void logout() throws CommonException;

    DevicesEntity findByToken(String token);

    void changeStatusCar(ChangeStatusCarDTO changeStatusCarDTO) throws Exception;

    List<DeviceDTO> getDeviceList(Long dvivisionId, Integer offset, Integer maxResults, Boolean noPaging, Boolean isAssignNewDevice);

    List<DeviceExportForm> exportDeviceList(Long dvivisionId);

    List<DeviceExportForm> exportDeviceListByCondition(SearchDeviceForm searchDeviceForm);

    List<DeviceDTO> searchDeviceList(SearchDeviceForm searchDeviceForm, Integer offset, Integer maxResults, Boolean noPaging, Boolean isAssignNewDevice);

    List<Message> deleteDevice(Long id) throws CommonException;

    List<Message> addNewDevice(DeviceForm deviceForm, Boolean isImport) throws CommonException;

    List<Message> editDevice(DeviceForm deviceForm) throws CommonException;

    EditDeviceDTO getEditDevice(Long deviceId);

    List<Message> validateEditPermission(Long id);

    List<Message> validatePermissionForAssign(Long userId) throws CommonException;

    List<AssignDeviceDTO> getAssignDeviceList(Long userId, Integer offset, Integer maxResults, Boolean noPaging);

    List<DeviceCarDTO> getAllAssignDeviceList(Long userId);

    /**
     * get all devices assignment is active by user id
     * @param userId user id
     * @return list of {@link DeviceCarDTO}
     */
    List<DeviceCarDTO> getAllActiveAssignDeviceList(Long userId);

    List<AssignDeviceDTO> searchAssignDeviceList(Long userId, SearchAssignDeviceForm searchAssignDeviceForm, Integer offset, Integer maxResults, Boolean noPaging);

    List<Message> removeAssign(Long userId, Long deviceId) throws CommonException;

    List<Message> assignDevice(Long userId, Long deviceId) throws CommonException;

    List<Message> removeAssignAll(List<Long> deviceIdList, Long userId) throws CommonException;

    List<Message> assignAllDevice(List<Long> deviceIdList, Long userId) throws CommonException;

    List<jp.co.willwave.aca.web.form.message.DeviceForm> findDevicesManaged(Long usersId);

    DevicesEntity findById(Long devicesId);

    List<Message> checkImportFile(MultipartFile file);

    List<DeviceExportForm> parseCsvForImport(String filePath);

    List<Message> importDevice(List<DeviceExportForm> deviceExportFormList) throws CommonException;

    DevicesEntity findByRouteActualId(Long routeId) throws CommonException;

    List<Message> changePassword(DevicesResetPasswordDTO resetPasswordDTO) throws CommonException;

    void changeStatusDevice(Long id) throws Exception;

    /**
     * does the user manage the device
     * @param userId
     * @param deviceId
     * @return <i>true</i> if the user manage the device
     */
    boolean checkManageDevice(Long userId, Long deviceId);
}
