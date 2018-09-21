package jp.co.willwave.aca.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.DeviceType;
import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.dto.api.*;
import jp.co.willwave.aca.dto.api.quickblox.QuickBloxUserResponse;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.*;
import jp.co.willwave.aca.web.form.*;
import jp.co.willwave.aca.web.form.route.DeviceCarDTO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import static jp.co.willwave.aca.constants.BusinessConstants.PASS_DEFAULT;
import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class DevicesServiceImpl implements DevicesService {
    private final Logger logger = Logger.getLogger(DevicesServiceImpl.class);

    private final DevicesDao devicesDao;

    private final DivisionsDao divisionsDao;

    private final CatsMessageResource messageResource;

    private final CarsDao carsDao;

    private final DivisionsHasDevicesDao divisionsHasDevicesDao;

    private final ObjectMapper objectMapper;

    private final UsersManageDevicesDao usersManageDevicesDao;

    private final RoutesDao routesDao;

    private final UsersDao usersDao;

    private final QuickBloxService quickBloxService;

    private final FileUtil fileUtil;

    @Autowired
    public DevicesServiceImpl(DevicesDao devicesDao, DivisionsDao divisionsDao, CatsMessageResource messageResource, CarsDao carsDao, DivisionsHasDevicesDao divisionsHasDevicesDao, ObjectMapper objectMapper, UsersManageDevicesDao usersManageDevicesDao, RoutesDao routesDao, UsersDao usersDao, QuickBloxService quickBloxService, FileUtil fileUtil) {
        this.devicesDao = devicesDao;
        this.divisionsDao = divisionsDao;
        this.messageResource = messageResource;
        this.carsDao = carsDao;
        this.divisionsHasDevicesDao = divisionsHasDevicesDao;
        this.objectMapper = objectMapper;
        this.usersManageDevicesDao = usersManageDevicesDao;
        this.routesDao = routesDao;
        this.quickBloxService = quickBloxService;
        this.fileUtil = fileUtil;
        this.usersDao = usersDao;
    }

    @Override
    public DevicesDTO login(DevicesDTO devicesDTO) throws Exception {
        logger.info("UserLogic.login");
        if (StringUtils.isEmpty(devicesDTO.getLoginId())
                || StringUtils.isEmpty(devicesDTO.getPassword())
                || StringUtils.isEmpty(devicesDTO.getUuid())) {
            throw new LogicException(ErrorCodeConfig.LOGIN_FAIL, messageResource.get(Constant.ErrorCode.LOGIN_FAIL,new String[]{""}));
        }
        DevicesEntity devicesEntity = devicesDao.findByLoginId(devicesDTO.getLoginId());
        if (devicesEntity == null) {
            throw new LogicException(ErrorCodeConfig.LOGIN_FAIL, messageResource.get(Constant.ErrorCode.NOT_FOUND_LOGINID,new String[]{""}));
        }
        if (!PasswordUtils.verifyUserPassword(devicesDTO.getPassword(),
                devicesEntity.getPassword(), devicesEntity.getSalt())) {
            throw new LogicException(ErrorCodeConfig.LOGIN_FAIL, messageResource.get(Constant.ErrorCode.LOGIN_FAIL,new String[]{""}));
        }
        String token = PasswordUtils.generateToken();
        if (StringUtils.isEmpty(devicesEntity.getUuid())) {
            devicesEntity.setUuid(devicesDTO.getUuid());
        } else {
            if (!devicesEntity.getUuid().equals(devicesDTO.getUuid())) {
                throw new LogicException(ErrorCodeConfig.LOGIN_FAIL, messageResource.get(Constant.ErrorCode.UUID_INVALID,new String[]{""}));
            }
        }
        devicesEntity.setLoginToken(token);
        RoutesEntity routesActual = routesDao.getRouteActualRunningByDevicesId(devicesEntity.getId());
        if (routesActual != null) {
            devicesEntity.setCarStatus(CarStatus.ONLINE);
        }
        devicesDao.update(devicesEntity);
        devicesDTO.setLoginToken(devicesEntity.getLoginToken());
        devicesDTO.setCarStatus(devicesEntity.getCarStatus().getValue());
        return devicesDTO;
    }

    @Override
    public void logout() throws CommonException {
        logger.info("UserLogic.logout");
        Long id = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getDevicesEntity().getId();
        DevicesEntity devicesEntity = devicesDao.findById(id, DevicesEntity.class);
        devicesEntity.setLoginToken(null);
        devicesEntity.setCarStatus(CarStatus.OFFLINE);
        devicesEntity.setUpdateBy(id);
        devicesDao.update(devicesEntity);
    }

    @Override
    public DevicesEntity findByToken(String token) {
        return devicesDao.findByToken(token);
    }

    @Override
    public void changeStatusCar(ChangeStatusCarDTO changeStatusCarDTO) throws Exception {
        logger.info("DevicesServiceImpl.changeStatusCar");
        DevicesEntity currentDevice = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getDevicesEntity();
        if (changeStatusCarDTO == null
                || StringUtils.isEmpty(changeStatusCarDTO.getCarStatus())) {
            throw new LogicException(ErrorCodeConfig.INPUT_NOT_CORRECT, messageResource.get(Constant.ErrorCode.INPUT_NOT_CORRECT,new String[]{""}));
        }
        if (changeStatusCarDTO.getCarStatus().equals(currentDevice.getCarStatus().getValue())) {
            return;
        }
        DevicesEntity devicesEntity = devicesDao.findById(currentDevice.getId(), DevicesEntity.class);
        if (devicesEntity == null) {
            throw new LogicException(ErrorCodeConfig.DEVICES_NOT_FOUND, messageResource.get(Constant.ErrorCode.DEVICES_NOT_FOUND,new String[]{""}));
        }
        CarStatus carStatus = CarStatus.init(changeStatusCarDTO.getCarStatus());
        if (carStatus == null) {
            throw new LogicException(ErrorCodeConfig.INPUT_NOT_CORRECT, messageResource.get(Constant.ErrorCode.INPUT_NOT_CORRECT,new String[]{""}));
        }
        if (CarStatus.OFFLINE.equals(devicesEntity.getCarStatus())) {
            throw new LogicException(ErrorCodeConfig.INPUT_NOT_CORRECT, messageResource.get(Constant.ErrorCode.INPUT_NOT_CORRECT,new String[]{""}));
        }
        if (carStatus.equals(CarStatus.ONLINE)
                || carStatus.equals(CarStatus.IDLE)) {
            devicesEntity.setCarStatus(carStatus);
            devicesEntity.setUpdateBy(currentDevice.getId());
            devicesEntity.setUpdateDate(new Timestamp((new Date()).getTime()));
            devicesDao.update(devicesEntity);
        }
    }

    @Override
    public List<DeviceDTO> getDeviceList(Long dvivisionId, Integer offset, Integer maxResults, Boolean noPaging, Boolean isAssignNewDevice) {
        logger.info("DevicesServiceImpl.getDeviceList");
        List<DivisionsEntity> divisionTotalList = new ArrayList<>();
        List<DivisionsEntity> fatherList = divisionsDao.findDivisionFatherListByDivisionId(dvivisionId);
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(dvivisionId);
        if (!CollectionUtils.isEmpty(fatherList)) {
            divisionTotalList.addAll(fatherList);
        }
        if (!CollectionUtils.isEmpty(childList)) {
            divisionTotalList.addAll(childList);
        }


        if (!CollectionUtils.isEmpty(divisionTotalList)) {
            List<Long> divisionIdList = new ArrayList<>();
            for (DivisionsEntity divisionsEntity : divisionTotalList
                    ) {
                divisionIdList.add(divisionsEntity.getId());
            }
            List<DeviceDTO> devicesEntityList = devicesDao.findByDisivions(divisionIdList, offset, maxResults, noPaging, isAssignNewDevice);

            if (!CollectionUtils.isEmpty(childList)) {
                for (DeviceDTO deviceDTO : devicesEntityList
                        ) {
                    if (childList.stream().anyMatch(m -> m.getId().equals(deviceDTO.getDivisionId())) || (deviceDTO.getDivisionId().equals(dvivisionId))) {
                        deviceDTO.setEditPermission(true);
                    } else {
                        deviceDTO.setEditPermission(false);
                    }
                }
            } else {
                for (DeviceDTO deviceDTO : devicesEntityList
                        ) {
                    if (deviceDTO.getDivisionId().equals(dvivisionId)) {
                        deviceDTO.setEditPermission(true);
                    } else {
                        deviceDTO.setEditPermission(false);
                    }
                }
            }

            return devicesEntityList;
        }
        return null;
    }

    @Override
    public List<DeviceExportForm> exportDeviceList(Long dvivisionId) {
        logger.info("DevicesServiceImpl.exportDeviceList");
        List<DivisionsEntity> divisionTotalList = new ArrayList<>();
        List<DivisionsEntity> fatherList = divisionsDao.findDivisionFatherListByDivisionId(dvivisionId);
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(dvivisionId);
        if (!CollectionUtils.isEmpty(fatherList)) {
            divisionTotalList.addAll(fatherList);
        }
        if (!CollectionUtils.isEmpty(childList)) {
            divisionTotalList.addAll(childList);
        }

        if (!CollectionUtils.isEmpty(divisionTotalList)) {
            List<Long> divisionIdList = new ArrayList<>();
            for (DivisionsEntity divisionsEntity : divisionTotalList
                    ) {
                divisionIdList.add(divisionsEntity.getId());
            }

            List<DeviceExportForm> deviceExportFormList = devicesDao.exportByDisivions(divisionIdList);

            return deviceExportFormList;


        }

        return null;
    }

    @Override
    public List<DeviceExportForm> exportDeviceListByCondition(SearchDeviceForm searchDeviceForm) {
        logger.info("DevicesServiceImpl.exportDeviceListByCondition");
        List<Long> divisionTotalList = new ArrayList<>();
        if (searchDeviceForm.getDivisionId() == null) {
            List<DivisionsEntity> divisionAll = new ArrayList<>();
            Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
            Long divisionId = 0L;
            DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
            if (divisionsEntityRoot != null) {
                divisionId = divisionsEntityRoot.getId();
            }
            List<DivisionsEntity> fatherList = divisionsDao.findDivisionFatherListByDivisionId(divisionId);
            List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
            if (!CollectionUtils.isEmpty(fatherList)) {
                divisionAll.addAll(fatherList);
            }
            if (!CollectionUtils.isEmpty(childList)) {
                divisionAll.addAll(childList);
            }
            if (!CollectionUtils.isEmpty(divisionAll)) {
                for (DivisionsEntity divisionsEntity : divisionAll
                        ) {
                    divisionTotalList.add(divisionsEntity.getId());
                }
            }
        }

        List<DeviceExportForm> deviceExportFormList = devicesDao.exportByMultiCondition(searchDeviceForm, divisionTotalList);

        return deviceExportFormList;
    }

    @Override
    public List<DeviceDTO> searchDeviceList(SearchDeviceForm searchDeviceForm, Integer offset, Integer maxResults, Boolean noPaging, Boolean isAssignNewDevice) {
        logger.info("DevicesServiceImpl.searchDeviceList");
        List<Long> divisionTotalList = new ArrayList<>();
        if (searchDeviceForm.getDivisionId() == null) {
            List<DivisionsEntity> divisionAll = new ArrayList<>();
            //TODO add default division
            Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
            Long divisionId = 0L;
            DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
            if (divisionsEntityRoot != null) {
                divisionId = divisionsEntityRoot.getId();
            }
            List<DivisionsEntity> fatherList = divisionsDao.findDivisionFatherListByDivisionId(divisionId);
            List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
            if (!CollectionUtils.isEmpty(fatherList)) {
                divisionAll.addAll(fatherList);
            }
            if (!CollectionUtils.isEmpty(childList)) {
                divisionAll.addAll(childList);
            }
            if (!CollectionUtils.isEmpty(divisionAll)) {
                for (DivisionsEntity divisionsEntity : divisionAll
                        ) {
                    divisionTotalList.add(divisionsEntity.getId());
                }
            }
        }
        List<DeviceDTO> devicesEntityList = devicesDao.findByMultiCondition(searchDeviceForm, offset, maxResults, noPaging, divisionTotalList, isAssignNewDevice);
        if (!CollectionUtils.isEmpty(devicesEntityList)) {
            //TODO Add division id
            Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
            Long dividionId = 0L;
            DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
            if (divisionsEntityRoot != null) {
                dividionId = divisionsEntityRoot.getId();
            }
            List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(dividionId);
            if (!CollectionUtils.isEmpty(childList)) {
                for (DeviceDTO deviceDTO : devicesEntityList
                        ) {
                    if ((deviceDTO.getDivisionId().equals(dividionId))) {
                        deviceDTO.setEditPermission(true);
                    } else if (childList.stream().anyMatch(m -> m.getId().equals(deviceDTO.getDivisionId()))) {
                        deviceDTO.setEditPermission(true);
                    } else {
                        deviceDTO.setEditPermission(false);
                    }
                }
            } else {
                for (DeviceDTO deviceDTO : devicesEntityList
                        ) {
                    if (deviceDTO.getDivisionId().equals(dividionId)) {
                        deviceDTO.setEditPermission(true);
                    } else {
                        deviceDTO.setEditPermission(false);
                    }
                }
            }
        }
        return devicesEntityList;
    }

    @Override
    public List<Message> deleteDevice(Long id) throws CommonException {
        logger.info("DevicesServiceImpl.deleteDevice");
        List<Message> messageList = new ArrayList<>();
        if (id == null) {
            messageList.add(messageResource.get(Constant.ErrorCode.NOT_EMPTY));
            return messageList;
        }
        DevicesEntity devicesEntity = devicesDao.findById(id, DevicesEntity.class);
        if (devicesEntity == null) {
            messageList.add(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
            return messageList;
        }

        Long divisionIdDevice = devicesDao.findDivisionIdBasedOnDevice(id);
        if (divisionIdDevice == null) {
            messageList.add(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
            return messageList;
        }
        Boolean permission = false;
        //TODO get current user
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionId = 0L;
        DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionsEntityRoot != null) {
            divisionId = divisionsEntityRoot.getId();
        }
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
        if (!CollectionUtils.isEmpty(childList)) {
            if (childList.stream().anyMatch(m -> m.getId().equals(divisionIdDevice))) {
                permission = true;
            } else if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        } else {
            if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        }

        if (!permission) {
            messageList.add(messageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
            return messageList;
        }

        List<UsersManageDevicesEntity> usersManageDevicesEntityList = usersManageDevicesDao.findByDeviceId(id);

        if (!CollectionUtils.isEmpty(usersManageDevicesEntityList)) {
            messageList.add(messageResource.get(Constant.ErrorCode.DEVICE_ALREADY_USED));
            return messageList;
        }

        List<RoutesEntity> routesEntityList = routesDao.findByDeviceId(id);

        if (!CollectionUtils.isEmpty(routesEntityList)) {
            messageList.add(messageResource.get(Constant.ErrorCode.DEVICE_ALREADY_USED));
            return messageList;
        }

        Long deviceId = devicesEntity.getId();
        devicesEntity.setUpdateBy(userLoginId);
        devicesDao.delete(devicesEntity);

        CarsEntity carsEntity = carsDao.findByDeviceId(id);
        if (carsEntity != null) {
            carsEntity.setUpdateBy(userLoginId);
            carsDao.delete(carsEntity);
        }

        List<DivisionsHasDevicesEntity> divisionsHasDevicesEntityList = divisionsHasDevicesDao.findByDeviceIdAndDivisionID(deviceId, divisionIdDevice);

        if (!CollectionUtils.isEmpty(divisionsHasDevicesEntityList)) {
            for (DivisionsHasDevicesEntity divisionsHasDevicesEntity : divisionsHasDevicesEntityList
                    ) {
                divisionsHasDevicesEntity.setUpdateBy(userLoginId);
                divisionsHasDevicesDao.delete(divisionsHasDevicesEntity);
            }
        }
        return messageList;
    }

    @Override
    public List<Message> addNewDevice(DeviceForm deviceForm, Boolean isImport) throws CommonException {
        logger.info("DevicesServiceImpl.addNewDevice");
        List<Message> messages = new ArrayList<>();

        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionId = 0L;
        DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionsEntityRoot != null) {
            divisionId = divisionsEntityRoot.getId();
        }
        DivisionsEntity divisionsEntity = divisionsDao.findById(divisionId, DivisionsEntity.class);

        if (divisionsEntity == null) {
            messages.add(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
            return messages;
        }

        DevicesEntity findByLoginId = devicesDao.findByLoginId(deviceForm.getLoginId().trim());
        if (findByLoginId != null) {
            messages.add(messageResource.get(Constant.ErrorCode.DEVICE_LOGIN_ID_EXIST));
            return messages;
        }

        Long numberCarHasPlateNumber = devicesDao.countCarWithPlateNumber(deviceForm.getPlateNumber().trim());
        if (numberCarHasPlateNumber != null && numberCarHasPlateNumber > 0) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.DEVICE_NUMBER, new String[]{deviceForm.getPlateNumber().trim()}));
            return messages;
        }

        DevicesEntity devicesEntity = new DevicesEntity();
        devicesEntity.setStatus(deviceForm.getStatus());
        devicesEntity.setLoginId(deviceForm.getLoginId().trim());
        devicesEntity.setCarStatus(CarStatus.OFFLINE);
        devicesEntity.setSalt(PasswordUtils.getSalt());
        devicesEntity.setPassword(PasswordUtils.generateSecurePassword(PASS_DEFAULT, devicesEntity.getSalt()));
        devicesEntity.setDeviceType(DeviceType.REGULAR.name());
        devicesEntity.setCreateBy(userLoginId);

        if(!isImport) {
            if (!deviceForm.getIconPath().isEmpty()) {
                devicesEntity.setIconPath(fileUtil.uploadIcon(deviceForm.getIconPath()));
            }
        } else {
            if (!deviceForm.getCurrentImage().isEmpty()) {
                devicesEntity.setIconPath(deviceForm.getCurrentImage());
            }
        }

        // Call quickblox server to generate Quickblox informations and save them into devices entity.
        messages.addAll(generateQuickbloxInfo(divisionsEntity, devicesEntity));

        if (CollectionUtils.isEmpty(messages)) {
            devicesDao.insert(devicesEntity);

            CarsEntity carsEntity = new CarsEntity();
            carsEntity.setDevicesId(devicesEntity.getId());
            carsEntity.setCarMaker(deviceForm.getCarMaker());
            carsEntity.setCarType(deviceForm.getCarType());
            carsEntity.setDriverName(deviceForm.getDriverName());
            carsEntity.setPlateNumber(deviceForm.getPlateNumber());
            carsEntity.setCreateBy(userLoginId);
            carsDao.insert(carsEntity);

            DivisionsHasDevicesEntity divisionsHasDevicesEntity = new DivisionsHasDevicesEntity();
            divisionsHasDevicesEntity.setDevicesId(devicesEntity.getId());
            divisionsHasDevicesEntity.setDivisionsId(divisionsEntity.getId());
            divisionsHasDevicesEntity.setCreateBy(userLoginId);
            divisionsHasDevicesDao.insert(divisionsHasDevicesEntity);
            devicesDao.update(devicesEntity);
        }

        return messages;
    }

    private List<Message> generateQuickbloxInfo(DivisionsEntity divisionsEntity, DevicesEntity devicesEntity) {
        List<Message> messages = new ArrayList<>();
        String companyRootCode = CommonUtil.getCompanyID(divisionsEntity);
        String companyCode = quickBloxService.generateQuickBloxTagByCompanyId(Long.valueOf(companyRootCode));

        QuickBloxUserResponse quickBloxUserResponse = quickBloxService.createQuickBloxUser(companyCode, devicesEntity.getLoginId());

        if (quickBloxUserResponse.getErrorMessage() != null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.QUICKBLOX_ERROR, new String[]{quickBloxUserResponse.getErrorMessage()}));
            return messages;
        }

        devicesEntity.setCallUserName(quickBloxUserResponse.getLogin());
        devicesEntity.setCallPassword(quickBloxUserResponse.getPassword());
        devicesEntity.setUserTags(quickBloxUserResponse.getUser_tags());
        devicesEntity.setCallId(quickBloxUserResponse.getId());

        return messages;
    }


    @Override
    public List<Message> editDevice(DeviceForm deviceForm) throws CommonException {
        List<Message> messages = new ArrayList<>();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        EditDeviceDTO oldObject = getEditDevice(deviceForm.getId());
        if (oldObject == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_DEVICE, new String[]{deviceForm.getId().toString()}));
            return messages;
        }else {
            if (!deviceForm.getPlateNumber().equalsIgnoreCase(oldObject.getPlateNumber())){
                Long numberCarHasPlateNumber = devicesDao.countCarWithPlateNumber(deviceForm.getPlateNumber().trim());
                if (numberCarHasPlateNumber != null && numberCarHasPlateNumber > 0) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.DEVICE_NUMBER, new String[]{deviceForm.getPlateNumber().trim()}));
                    return messages;
                }
            }
        }

        EditDeviceDTO newObject = ConversionUtil.mapper(deviceForm, EditDeviceDTO.class);

        DevicesEntity findByLoginId = devicesDao.findByLoginId(newObject.getLoginId().trim());

        if (findByLoginId != null && !findByLoginId.getId().equals(oldObject.getId())) {
            messages.add(messageResource.get(Constant.ErrorCode.DEVICE_LOGIN_ID_EXIST));
            return messages;
        }

        if (!oldObject.getLoginId().equals(newObject.getLoginId()) || !oldObject.getStatus().equals(newObject.getStatus()) || deviceForm.getIconPath() != null) {
            DevicesEntity devicesEntity = devicesDao.findById(oldObject.getId(), DevicesEntity.class);
            devicesEntity.setStatus(newObject.getStatus());
            devicesEntity.setLoginId(newObject.getLoginId());
            devicesEntity.setUpdateBy(userLoginId);
            if (!deviceForm.getIconPath().isEmpty()) {
                devicesEntity.setIconPath(fileUtil.uploadIcon(deviceForm.getIconPath()));
            }
            devicesDao.update(devicesEntity);
        }

        Boolean carChange = false;
        if (oldObject.getCarMaker() != null && newObject.getCarMaker() != null) {
            if (!oldObject.getCarMaker().equals(newObject.getCarMaker())) {
                carChange = true;
            }
        }
        if (oldObject.getCarType() != null && newObject.getCarType() != null) {
            if (!oldObject.getCarType().equals(newObject.getCarType())) {
                carChange = true;
            }
        }
        if (oldObject.getDriverName() != null && newObject.getDriverName() != null) {
            if (!oldObject.getDriverName().equals(newObject.getDriverName())) {
                carChange = true;
            }
        }
        if (oldObject.getPlateNumber() != null && newObject.getPlateNumber() != null) {
            if (!oldObject.getPlateNumber().equals(newObject.getPlateNumber())) {
                carChange = true;
            }
        }
        if (carChange) {
            CarsEntity carsEntity = carsDao.findByDeviceId(newObject.getId());
            if (carsEntity == null) {
                messages.add(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
                messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_CAR_OF_DEVICE, new String[]{newObject.getId().toString()}));
                return messages;
            }
            carsEntity.setLatestFlg(0);
            carsEntity.setUpdateBy(userLoginId);
            carsDao.update(carsEntity);
            String json;
            CarsEntity carsNew = new CarsEntity();
            try {
                json = objectMapper.writeValueAsString(carsEntity);
                carsNew = objectMapper.readValue(json, CarsEntity.class);
            } catch (JsonProcessingException e) {
                messages.add(messageResource.get(Constant.ErrorCode.EDIT_ERROR));
                e.printStackTrace();
                return messages;
            } catch (IOException e) {
                messages.add(messageResource.get(Constant.ErrorCode.EDIT_ERROR));
                return messages;
            }


            if (newObject.getCarType() == null) {
                carsNew.setCarType(null);
            } else {
                carsNew.setCarType(newObject.getCarType());
            }

            if (newObject.getCarMaker() == null) {
                carsNew.setCarMaker(null);
            } else {
                carsNew.setCarMaker(newObject.getCarMaker());
            }

            if (newObject.getDriverName() == null) {
                carsNew.setDriverName(null);
            } else {
                carsNew.setDriverName(newObject.getDriverName());
            }

            if (newObject.getPlateNumber() == null) {
                carsNew.setPlateNumber(null);
            } else {
                carsNew.setPlateNumber(newObject.getPlateNumber());
            }
            carsNew.setId(null);
            carsNew.setLatestFlg(1);
            carsNew.setCreateBy(userLoginId);
            carsDao.insert(carsNew);
        }

        return messages;
    }

    @Override
    public EditDeviceDTO getEditDevice(Long deviceId) {
        EditDeviceDTO editDeviceDTO = devicesDao.findEditDevice(deviceId);
        return editDeviceDTO;
    }

    @Override
    public List<Message> validateEditPermission(Long id) {
        List<Message> messages = new ArrayList<>();
        Long divisionIdDevice = devicesDao.findDivisionIdBasedOnDevice(id);
        if (divisionIdDevice == null) {
            messages.add(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.DIVISION_PERMISSION, new String[]{id.toString()}));
            return messages;
        }
        Boolean permission = false;
        //TODO get current user
        //Long userLoginId = ((UsersEntity) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionId = 0L;
        DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionsEntityRoot != null) {
            divisionId = divisionsEntityRoot.getId();
        }
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
        if (!CollectionUtils.isEmpty(childList)) {
            if (childList.stream().anyMatch(m -> m.getId().equals(divisionIdDevice))) {
                permission = true;
            } else if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        } else {
            if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        }

        if (!permission) {
            messages.add(messageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));

            return messages;
        }

        return messages;
    }

    @Override
    public List<Message> validatePermissionForAssign(Long id) throws CommonException {

        List<Message> messages = new ArrayList<>();

        UsersEntity selectedOperator = usersDao.findById(id, UsersEntity.class);

        if (selectedOperator == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_USERID, new String[]{id.toString()}));
            return messages;
        }
        if (selectedOperator.getRoleId() != UserRole.OPERATOR.getRole()) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_OPERATOR, new String[]{id.toString()}));
            return messages;
        }

        // get selected operator's belonged division
        DivisionsEntity operatorDivision = divisionsDao.findById(selectedOperator.getDivisionsId(), DivisionsEntity.class);

        // get current logged in user's managed division
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionRootId = 0L;
        DivisionsEntity adminDivision = divisionsDao.findManagedDivisionByUserID(userLoginId);

        if (adminDivision != null) {
            // check if current user has role to assigned devices for selected operator using division code
            if(!operatorDivision.getDivisionCode().contains(adminDivision.getDivisionCode())) {
                messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.DIVISON_PERMISION, new String[]{divisionRootId.toString()}));
                return messages;
            }
        } else {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.USER_ID, new String[]{userLoginId.toString()}));
            return messages;
        }

        return messages;
    }

    @Override
    public List<AssignDeviceDTO> getAssignDeviceList(Long userId, Integer offset, Integer maxResults, Boolean noPaging) {
        return devicesDao.getAssignDeviceUser(userId, offset, maxResults, noPaging);
    }

    @Override
    public List<DeviceCarDTO> getAllAssignDeviceList(Long userId) {
        return devicesDao.findCarsManaged(userId);
    }

    @Override
    public List<DeviceCarDTO> getAllActiveAssignDeviceList(Long userId) {
        return devicesDao.findCarsManaged(userId).stream().filter(device -> device.getStatus()).collect(Collectors.toList());
    }

    @Override
    public List<AssignDeviceDTO> searchAssignDeviceList(Long userId, SearchAssignDeviceForm searchAssignDeviceForm, Integer offset, Integer maxResults, Boolean noPaging) {

        return devicesDao.getAssignDeviceUserByMultiCondition(userId, searchAssignDeviceForm, offset, maxResults, noPaging);
    }

    @Override
    public List<Message> removeAssign(Long userId, Long deviceId) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Message> messages = new ArrayList<>();

        if (userId == null || deviceId == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"user.device"}));
            return messages;
        }

        UsersManageDevicesEntity usersManageDevicesEntity = usersManageDevicesDao.findByUserIdAndDeviceId(userId, deviceId);

        if (usersManageDevicesEntity == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_DATA, new String[]{deviceId.toString()}));
            return messages;
        }

        messages = validatePermissionForAssign(userId);
        if (!CollectionUtils.isEmpty(messages)) {
            return messages;
        }
        usersManageDevicesEntity.setUpdateBy(userLoginId);
        usersManageDevicesDao.delete(usersManageDevicesEntity);


        return messages;
    }

    @Override
    public List<Message> assignDevice(Long userId, Long deviceId) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Message> messages = new ArrayList<>();
        UsersManageDevicesEntity usersManageDevicesEntity = usersManageDevicesDao.findByUserIdAndDeviceId(userId, deviceId);

        if (usersManageDevicesEntity != null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.ALREADY_EXISTS, new String[]{deviceId.toString()}));
            return messages;
        }

        DevicesEntity devicesEntity = devicesDao.findById(deviceId, DevicesEntity.class);

        if (devicesEntity == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_DATA, new String[]{deviceId.toString()}));
            return messages;
        }

        messages = validatePermissionForAssign(userId);
        if (!CollectionUtils.isEmpty(messages)) {
            return messages;
        }

        usersManageDevicesEntity = new UsersManageDevicesEntity();
        usersManageDevicesEntity.setDevicesId(deviceId);
        usersManageDevicesEntity.setUsersId(userId);
        usersManageDevicesEntity.setCreateBy(userLoginId);
        usersManageDevicesDao.insert(usersManageDevicesEntity);
        return messages;
    }

    @Override
    public List<Message> removeAssignAll(List<Long> deviceIdList, Long userId) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Message> messages = new ArrayList<>();

        List<UsersManageDevicesEntity> usersManageDevicesEntityList = usersManageDevicesDao.findByUserIdAndDeviceIdList(userId, deviceIdList);

        if (CollectionUtils.isEmpty(usersManageDevicesEntityList)) {
            List<String> stringList = new ArrayList<>();
            for (Long deviceId : deviceIdList
                    ) {
                stringList.add(String.valueOf(deviceId));
            }
            String deviceList = String.join(", ", stringList);
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_DATA, new String[]{deviceList}));
            return messages;
        }

        for (UsersManageDevicesEntity usersManageDevicesEntity : usersManageDevicesEntityList
                ) {
            usersManageDevicesEntity.setUpdateBy(userLoginId);
            usersManageDevicesDao.delete(usersManageDevicesEntity);
        }

        return messages;
    }

    @Override
    public List<Message> assignAllDevice(List<Long> deviceIdList, Long userId) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<Message> messages = new ArrayList<>();
        List<UsersManageDevicesEntity> usersManageDevicesEntityList = usersManageDevicesDao.findByUserIdAndDeviceIdList(userId, deviceIdList);

        if (!CollectionUtils.isEmpty(usersManageDevicesEntityList)) {
            List<String> stringList = new ArrayList<>();
            for (Long deviceId : deviceIdList
                    ) {
                stringList.add(String.valueOf(deviceId));
            }
            String deviceList = String.join(", ", stringList);
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.ALREADY_EXISTS, new String[]{deviceList}));
            return messages;
        }

        Long count = devicesDao.countAllValidDevice(deviceIdList);

        if (count != deviceIdList.size()) {
            List<String> stringList = new ArrayList<>();
            for (Long deviceId : deviceIdList
                    ) {
                stringList.add(String.valueOf(deviceId));
            }
            String deviceList = String.join(", ", stringList);
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_DATA, new String[]{deviceList}));
            return messages;
        }

        messages = validatePermissionForAssign(userId);
        if (!CollectionUtils.isEmpty(messages)) {
            return messages;
        }

        for (Long deviceId : deviceIdList
                ) {
            UsersManageDevicesEntity usersManageDevicesEntity = new UsersManageDevicesEntity();
            usersManageDevicesEntity.setDevicesId(deviceId);
            usersManageDevicesEntity.setUsersId(userId);
            usersManageDevicesEntity.setCreateBy(userLoginId);
            usersManageDevicesDao.insert(usersManageDevicesEntity);
        }

        return messages;
    }

    @Override
    public List<jp.co.willwave.aca.web.form.message.DeviceForm> findDevicesManaged(Long usersId) {
        List<DeviceCarDTO> devicesList = devicesDao.findCarsManaged(usersId);
        List<jp.co.willwave.aca.web.form.message.DeviceForm> deviceForms = new ArrayList<>();
        if (!CollectionUtils.isEmpty(devicesList)) {
            devicesList.forEach(devicesEntity -> {
                jp.co.willwave.aca.web.form.message.DeviceForm deviceForm = new jp.co.willwave.aca.web.form.message
                        .DeviceForm();
                deviceForm.setId(devicesEntity.getDeviceId());
                deviceForm.setName(devicesEntity.getLoginId());
                deviceForm.setPlateNumber(devicesEntity.getPlateNumber());
                deviceForms.add(deviceForm);
            });
        }
        return deviceForms;
    }

    @Override
    public DevicesEntity findById(Long devicesId) {
        try {
            return devicesDao.findById(devicesId, DevicesEntity.class);
        } catch (CommonException e) {
            return null;
        }
    }

    @Override
    public List<Message> checkImportFile(MultipartFile file) {
        String line = "";
        String cvsSplitBy = ",";
        List<Message> messages = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (i == 1) {
                    i++;
                    continue;
                }

                // use comma as separator
                String[] deviceExportForm = line.split(cvsSplitBy);

                if (deviceExportForm.length != 7 && deviceExportForm.length != 6) {
                    messages.add(messageResource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(i)}));
                    i++;
                    continue;
                }

                if (deviceExportForm[0] == null || org.apache.commons.lang3.StringUtils.isBlank(deviceExportForm[0])) {
                    messages.add(messageResource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(i)}));
                    i++;
                    continue;
                }
                if (deviceExportForm[1] == null || !org.apache.commons.lang3.StringUtils.isNumeric(deviceExportForm[1])) {
                    messages.add(messageResource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(i)}));
                    i++;
                    continue;
                }
                if (Integer.valueOf(deviceExportForm[1]) != StatusEnum.ACTIVE.getValue() && Integer.valueOf(deviceExportForm[1]) != StatusEnum.INACTIVE.getValue()) {
                    messages.add(messageResource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(i)}));
                    i++;
                    continue;
                }
                if (deviceExportForm[5] == null || org.apache.commons.lang3.StringUtils.isBlank(deviceExportForm[5])) {
                    messages.add(messageResource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(i)}));
                    i++;
                    continue;
                }
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return messages;
    }

    @Override
    public List<DeviceExportForm> parseCsvForImport(String filePath) {
        String line = "";
        String cvsSplitBy = ",";
        List<DeviceExportForm> deviceExportFormList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            int i = 1;
            while ((line = br.readLine()) != null) {
                if (i == 1) {
                    i++;
                    continue;
                }
                DeviceExportForm importDevice = new DeviceExportForm();
                // use comma as separator
                String[] deviceExportForm = line.split(cvsSplitBy);


                if (deviceExportForm[0] != null) {
                    importDevice.setLoginId(deviceExportForm[0].trim());
                }
                if (deviceExportForm[1] != null && org.apache.commons.lang3.StringUtils.isNumeric(deviceExportForm[1].trim())) {
                    importDevice.setStatus(Integer.valueOf(deviceExportForm[1].trim()));
                }
                if (deviceExportForm[2] != null) {
                    importDevice.setIconPath(deviceExportForm[2].trim());
                }
                if (deviceExportForm[3] != null) {
                    importDevice.setCarMaker(deviceExportForm[3].trim());
                }
                if (deviceExportForm[4] != null) {
                    importDevice.setCarType(deviceExportForm[4].trim());
                }
                if (deviceExportForm[5] != null) {
                    importDevice.setDriverName(deviceExportForm[5].trim());
                }
                if (deviceExportForm.length == 7) {
                    if (deviceExportForm[6] != null) {
                        importDevice.setPlateNumber(deviceExportForm[6].trim());
                    }
                }
                deviceExportFormList.add(importDevice);
                i++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return deviceExportFormList;
    }

    @Override
    public List<Message> importDevice(List<DeviceExportForm> deviceExportFormList) throws CommonException {

        List<Message> messages = new ArrayList<>();

        List<String> loginIdList = new ArrayList<>();

        for (DeviceExportForm deviceExportForm : deviceExportFormList
                ) {
            loginIdList.add(deviceExportForm.getLoginId());
        }
        String[] duplicateArray = loginIdList.stream().toArray(String[]::new);

        Set<String> resultDuplicate = new HashSet<>();
        for (int j=0;j<duplicateArray.length;j++) {
            for (int k = j + 1; k < duplicateArray.length; k++) {
                if (k != j && duplicateArray[k].equals(duplicateArray[j])) {
                    resultDuplicate.add(duplicateArray[j]);
                }
            }
        }

        if(!CollectionUtils.isEmpty(resultDuplicate)) {
            for (String loginId : resultDuplicate) {
                messages.add(messageResource.getMessage(Constant.ErrorCode.DUPLICATE_LOGIN_ID_CSV, new String[]{loginId}));
            }
            return messages;
        }

        List<DevicesEntity> devicesEntityList = devicesDao.findDevicesByLoginIdList(loginIdList);

        if (!CollectionUtils.isEmpty(devicesEntityList)) {
            for (DevicesEntity devicesEntity : devicesEntityList
                    ) {
                messages.add(messageResource.getMessage(Constant.ErrorCode.DEVICE_LOGIN_ID_EXIST, new String[]{devicesEntity.getLoginId()}));
            }
            return messages;
        }



        for (DeviceExportForm deviceExportForm : deviceExportFormList) {
            DeviceForm deviceForm = new DeviceForm();

            deviceForm.setLoginId(deviceExportForm.getLoginId());
            deviceForm.setStatus(deviceExportForm.getStatus());

            if (deviceExportForm.getIconPath() != null) {
                deviceForm.setCurrentImage(deviceExportForm.getIconPath());
            }
            if (deviceExportForm.getCarMaker() != null) {
                deviceForm.setCarMaker(deviceExportForm.getCarMaker());
            }
            if (deviceExportForm.getCarType() != null) {
                deviceForm.setCarType(deviceExportForm.getCarType());
            }
            if (deviceExportForm.getDriverName() != null) {
                deviceForm.setDriverName(deviceExportForm.getDriverName());
            }
            if (deviceExportForm.getPlateNumber() != null) {
                deviceForm.setPlateNumber(deviceExportForm.getPlateNumber());
            }

            messages = addNewDevice(deviceForm, true);
            if (!CollectionUtils.isEmpty(messages)) {
                throw new CommonException();
            }
        }
        return messages;
    }

    @Override
    public DevicesEntity findByRouteActualId(Long routeId) {
        return devicesDao.findByRouteActualId(routeId);
    }

    @Override
    public List<Message> changePassword(DevicesResetPasswordDTO resetPasswordDTO) throws CommonException {
        DevicesEntity devicesEntity = devicesDao.findById(resetPasswordDTO.getDeviceId(), DevicesEntity.class);
        //Current user managed device
        List<Message> messages = validateEditPermission(devicesEntity.getId());
        if (!CollectionUtils.isEmpty(messages)) {
            return messages;
        }
        String salt = PasswordUtils.getSalt();
        String newPassword = PasswordUtils.generateSecurePassword(resetPasswordDTO.getPassword(), salt);
        devicesEntity.setSalt(salt);
        devicesEntity.setPassword(newPassword);
        devicesEntity.setLoginToken(null);
        devicesEntity.setCarStatus(CarStatus.OFFLINE);
        devicesEntity.setUuid(null);
        devicesDao.update(devicesEntity);
        return new ArrayList<>();
    }

    @Override
    public void changeStatusDevice(Long id) throws Exception {
        if (id == null) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.NOT_EMPTY));

        }
        DevicesEntity devicesEntity = devicesDao.findById(id, DevicesEntity.class);
        if (devicesEntity == null) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
        }

        Long divisionIdDevice = devicesDao.findDivisionIdBasedOnDevice(id);
        if (divisionIdDevice == null) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.NOT_FOUND_DATA));
        }
        Boolean permission = false;
        //TODO get current user
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionId = 0L;
        DivisionsEntity divisionsEntityRoot = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionsEntityRoot != null) {
            divisionId = divisionsEntityRoot.getId();
        }
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
        if (!CollectionUtils.isEmpty(childList)) {
            if (childList.contains(divisionIdDevice)) {
                permission = true;
            } else if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        } else {
            if (divisionIdDevice.equals(divisionId)) {
                permission = true;
            }
        }

        if (!permission) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.HAVE_NO_PERMISSION));
        }

        List<UsersManageDevicesEntity> usersManageDevicesEntityList = usersManageDevicesDao.findByDeviceId(id);

        if (!CollectionUtils.isEmpty(usersManageDevicesEntityList)) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.DEVICE_ALREADY_USED));
        }

        List<RoutesEntity> routesEntityList = routesDao.findByDeviceId(id);

        if (!CollectionUtils.isEmpty(routesEntityList)) {
            throw new LogicWebException(messageResource.get(Constant.ErrorCode.DEVICE_ALREADY_USED));
        }

        devicesEntity.setStatus(StatusEnum.oppositeStatus(devicesEntity.getStatus()).getValue());
        devicesDao.update(devicesEntity);
    }

    @Override
    public boolean checkManageDevice(Long userId, Long deviceId) {
        return usersManageDevicesDao.findByUserIdAndDeviceId(userId, deviceId) != null;
    }
}
