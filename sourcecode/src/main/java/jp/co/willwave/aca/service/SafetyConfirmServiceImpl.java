package jp.co.willwave.aca.service;

import jp.co.willwave.aca.config.SafetyConfirmContentMail;
import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.utilities.CommonUtil;
import jp.co.willwave.aca.utilities.WebUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class SafetyConfirmServiceImpl implements SafetyConfirmService {

    private final Logger logger = Logger.getLogger(SafetyConfirmServiceImpl.class);

    private final DevicesDao devicesDao;
    private final UsersDao usersDao;
    private final CarsDao carsDao;
    private final SafetyConfirmContentMail safetyConfirmContentMail;
    private final DivisionsDao divisionsDao;
    private final DivisionsHasSysConfigsDao divisionsHasSysConfigsDao;
    private final EmailService emailService;
    private final SafetyConfirmLogsDao safetyConfirmLogsDao;
    private final RoutesDao routesDao;
    private final DivisionService divisionService;

    public SafetyConfirmServiceImpl(DevicesDao devicesDao, UsersDao usersDao,
                                    CarsDao carsDao, SafetyConfirmContentMail safetyConfirmContentMail, DivisionsDao divisionsDao, DivisionsHasSysConfigsDao divisionsHasSysConfigsDao,
                                    EmailService emailService, SafetyConfirmLogsDao safetyConfirmLogsDao,
                                    RoutesDao routesDao, DivisionService divisionService) {
        this.devicesDao = devicesDao;
        this.usersDao = usersDao;
        this.carsDao = carsDao;
        this.safetyConfirmContentMail = safetyConfirmContentMail;
        this.divisionsDao = divisionsDao;
        this.divisionsHasSysConfigsDao = divisionsHasSysConfigsDao;
        this.emailService = emailService;
        this.safetyConfirmLogsDao = safetyConfirmLogsDao;
        this.routesDao = routesDao;
        this.divisionService = divisionService;
    }

    @Override
    public void checkSafetyConfirmDevices() {
        List<DevicesEntity> devicesCheck = devicesDao.findRunningDevicesAndActive();
        if (!CollectionUtils.isEmpty(devicesCheck)) {
            devicesCheck.forEach(this::checkSafetyConfirmDevice);
        }
    }

    @Override
    public List<SafetyConfirmLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds) {
        return safetyConfirmLogsDao.findByRoutesDetailId(routeDetailIds);
    }

    private String formatContentSafetyConfirmMail(String companyName, String divisionName, String loginId, String plateNumber,
                                                  Date updateDate, String longitude, String latitude, Long stopTime) throws IOException, CommonException {
        File mailContentFile = ResourceUtils.getFile(safetyConfirmContentMail.getSafetyConfirmContentTemplateFilePath());

        InputStream in = new FileInputStream(mailContentFile);
        Map<String, Object> map = new HashMap<>();
        map.put("companyName", companyName);
        map.put("divisionName", divisionName);
        map.put("loginId", loginId);
        map.put("plateNumber", plateNumber);
        map.put("updateDate", updateDate);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("stopTime", stopTime);
        return CommonUtil.format(in, map);
    }

    private void checkSafetyConfirmDevice(DevicesEntity device) {
        try {
            Date date = new Date();
            List<String> divisionIdSendEmail = StringUtils.isEmpty(device.getListDivisionSend()) ? new ArrayList<>() :
                    new ArrayList<>(Arrays.asList(device.getListDivisionSend().split(",")));
            List<UsersEntity> userManageDevices = usersDao.getInfoUserManageDevice(device.getId());
            CarsEntity carsEntity = carsDao.findByDeviceId(device.getId());
            if (CollectionUtils.isEmpty(userManageDevices)) {
                return;
            }
            List<Long> divisionUses = userManageDevices.stream().map(UsersEntity::getDivisionsId).collect(Collectors
                    .toList());
            List<DivisionsEntity> divisionsParentAndCurrent = divisionsDao.getDivisionParentAndCurrent
                    (divisionUses);
            Map<Long, DivisionsEntity> mapDivision = divisionsParentAndCurrent.stream().collect(Collectors.toMap
                    (DivisionsEntity::getId, x -> x));
            List<Long> divisionIds = divisionsParentAndCurrent.stream()
                    .map(DivisionsEntity::getId).collect(Collectors.toList());
            List<DivisionsHasSysConfigsEntity> divisionsConfigs
                    = divisionsHasSysConfigsDao.findConfigDivisionAndKey(divisionIds, ConfigEnum.NOTIFICATION_TIME);
            boolean isUpdate = false;
            for (DivisionsHasSysConfigsEntity divisionsConfig : divisionsConfigs) {
                if (!divisionIdSendEmail.contains(String.valueOf(divisionsConfig.getDivisionsId()))) {
                    DivisionsEntity divisionsEntity = divisionsDao.findById(divisionsConfig.getDivisionsId(), DivisionsEntity.class);
                    List<DivisionsEntity> divisionsParentEntity = divisionsDao.findDivisionFatherListByDivisionId(divisionsConfig.getDivisionsId());
                    Long stopTime = diff(date, device.getTimeLocation());

                    if (stopTime >= Long.valueOf(divisionsConfig.getValue())) {
                        String notificationEmail = this.getNotificationEmail(divisionsConfig.getDivisionsId());
                        if (!StringUtils.isEmpty(notificationEmail)) {
                            try {
                                String content = formatContentSafetyConfirmMail(divisionsParentEntity.get(divisionsParentEntity.size()-1).getDivisionName(), divisionsEntity.getDivisionName(),
                                        device.getLoginId(), carsEntity.getPlateNumber(), device.getUpdateDate(), device.getLongitude(), device.getLatitude(), stopTime);
                                emailService.sendMessageHtml(notificationEmail,
                                        safetyConfirmContentMail.getSafetyConfirmContentSubject(), "" +
                                                content);
                                logger.info("Send email to " + notificationEmail);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } else {
                            logger.warn("notification email is not existed");
                        }

                        RoutesEntity routesActual =
                                routesDao.getRouteActualRunningByDevicesId(device.getId());
                        RoutesEntity routesPlan = routesDao.findById(routesActual.getPlanedRoutesId(), RoutesEntity
                                .class);
                        divisionIdSendEmail.add(String.valueOf(divisionsConfig.getDivisionsId()));
                        SafetyConfirmLogsEntity safetyConfirmLogsEntity = new SafetyConfirmLogsEntity();
                        safetyConfirmLogsEntity.setNotificationTime(date);
                        safetyConfirmLogsEntity.setDivision(mapDivision.get(divisionsConfig.getDivisionsId()));
                        safetyConfirmLogsEntity.setNotificationMail(notificationEmail);
                        safetyConfirmLogsEntity.setLatitude(device.getLatitude());
                        safetyConfirmLogsEntity.setLongitude(device.getLongitude());
                        safetyConfirmLogsEntity.setRouteDetailId(device.getRouteDetailFinished());
                        safetyConfirmLogsEntity.setStopTime(stopTime);
                        safetyConfirmLogsEntity.setCreateBy(0L);
                        safetyConfirmLogsEntity.setUpdateBy(0L);
                        safetyConfirmLogsEntity.setCreateDate(date);
                        safetyConfirmLogsEntity.setUpdateDate(date);
                        safetyConfirmLogsEntity.setUsersId(routesPlan.getCreateBy());
                        safetyConfirmLogsEntity.setDevicesId(device.getId());
                        safetyConfirmLogsDao.insert(safetyConfirmLogsEntity);
                        isUpdate = true;
                    }
                }
            }
            if (isUpdate) {
                device.setListDivisionSend(String.join(",", divisionIdSendEmail));
                devicesDao.update(device);
            }
        } catch (CommonException e) {
            logger.error("Error when send mail or update...", e);
        }
    }

    private Long diff(Date date, Timestamp timeLocation) {
        Long fromDate = timeLocation.getTime();
        Long toDate = date.getTime();
        return (toDate - fromDate) / 60000;
    }

    private String getNotificationEmail(Long divisionId) throws CommonException {
        DivisionsHasSysConfigsEntity mailConfigEntity
                = divisionsHasSysConfigsDao.findConfigDivisionAndKey(divisionId, ConfigEnum.NOTIFICATION_EMAIL);
        String safetyMail;
        if (mailConfigEntity != null && !StringUtils.isEmpty(mailConfigEntity.getValue())) {
            safetyMail = mailConfigEntity.getValue();
        } else {
            DivisionsEntity division = divisionsDao.findById(divisionId, DivisionsEntity.class);
            UsersEntity divisionAdmin = division != null ? usersDao.findById(division.getUsersId(), UsersEntity.class) : null;
            safetyMail = divisionAdmin != null ? divisionAdmin.getUserEmail() : null;
        }
        return safetyMail;
    }
}
