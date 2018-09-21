package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.DivisionsHasSysConfigsDao;
import jp.co.willwave.aca.dao.MasterSysConfigsDao;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.dto.api.SystemConfigDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.web.form.SystemConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class SystemConfigServiceImpl implements SystemConfigService {

    private final DivisionsHasSysConfigsDao divisionsHasSysConfigsDao;
    private final MasterSysConfigsDao masterSysConfigsDao;
    private final DivisionsDao divisionsDao;
    private final FileUtil<SystemConfigDTO> fileUtil;

    @Autowired
    public SystemConfigServiceImpl(DivisionsHasSysConfigsDao divisionsHasSysConfigsDao, MasterSysConfigsDao masterSysConfigsDao,
                                   DivisionsDao divisionsDao, FileUtil<SystemConfigDTO> fileUtil) {
        this.divisionsHasSysConfigsDao = divisionsHasSysConfigsDao;
        this.masterSysConfigsDao = masterSysConfigsDao;
        this.divisionsDao = divisionsDao;
        this.fileUtil = fileUtil;
    }

    @Override
    public SystemConfigDTO findByDivisionId(Long divisionId) throws CommonException {

        List<DivisionsHasSysConfigsEntity> divisionsHasSysConfigsEntityList = divisionsHasSysConfigsDao.findByDivisionId(divisionId);
        SystemConfigDTO systemConfigDTO = new SystemConfigDTO();
        if (!CollectionUtils.isEmpty(divisionsHasSysConfigsEntityList)) {
            List<MasterSysConfigsEntity> masterSysConfigsEntityList = masterSysConfigsDao.findAll();

            for (MasterSysConfigsEntity m : masterSysConfigsEntityList
                    ) {
                for (DivisionsHasSysConfigsEntity d : divisionsHasSysConfigsEntityList
                        ) {
                    if (d.getKey().equals(m.getKey())) {
                        if (m.getKey().equals(ConfigEnum.START_END_POINT_COLOR)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setStartEndPointColor(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.NOT_ARRIVED_POINT)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setNotArrivedPoint(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.ARRIVED_POINT)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setArrivedPoint(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.NOTIFICATION_TIME)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setNotificationTime(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.NOTIFICATION_EMAIL)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setNotificationEmail(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.MOBILE_ICON)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setMobileIcon(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.CUSTOMER_ICON)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setCustomerIcon(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.TIME_MESSAGE)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setTimeMessage(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.DISTANCE_FINISHED)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setDistanceFinished(d.getValue());
                            }
                        }
                        if (m.getKey().equals(ConfigEnum.TRAVEL_TIME_ALERT)) {
                            if (d.getValue() != null) {
                                systemConfigDTO.setTravelTimeAlert(d.getValue());
                            }
                        }
                    }

                }
            }
            DivisionsEntity divisionsEntity = divisionsDao.findById(divisionId, DivisionsEntity.class);

            systemConfigDTO.setDivisionId(divisionId);
            systemConfigDTO.setDivisionName(divisionsEntity.getDivisionName());
            if (divisionsEntity.getParentDivisionsId() != null) {
                Long parentId = divisionsEntity.getParentDivisionsId();
                divisionsEntity = divisionsDao.findById(parentId, DivisionsEntity.class);
                systemConfigDTO.setParentDivisionName(divisionsEntity.getDivisionName());
            }
            return systemConfigDTO;
        } else {
            return null;
        }
    }

    @Override
    public HashSet<DivisionDTO> getTreeDivisions(Long divisionId) {
        HashSet<DivisionDTO> divisionDTOS = new HashSet<>();
        List<DivisionsEntity> divisionTotalList = new ArrayList<>();
        List<DivisionsEntity> fatherList = divisionsDao.findDivisionFatherListByDivisionId(divisionId);
        List<DivisionsEntity> childList = divisionsDao.findDivisionChildListByDivisionId(divisionId);
        if (!CollectionUtils.isEmpty(fatherList)) {
            divisionTotalList.addAll(fatherList);
        }
        if (!CollectionUtils.isEmpty(childList)) {
            divisionTotalList.addAll(childList);
        }

        if (!CollectionUtils.isEmpty(divisionTotalList)) {
            for (DivisionsEntity d : divisionTotalList
                    ) {
                DivisionDTO divisionDTO = new DivisionDTO();
                divisionDTO.setDivisionName(d.getDivisionName());
                divisionDTO.setDivisionId(d.getId());
                divisionDTOS.add(divisionDTO);
            }
        }

        return divisionDTOS;
    }

    @Override
    public List<Message> updateDivisionConfig(Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionConfigMap,
                                              SystemConfigForm form)
            throws CommonException {
        List<Message> messages = new ArrayList<>();
        MultipartFile mobileIcon = form.getMobileIconFile();
        MultipartFile customerIcon = form.getCustomerIconFile();
        for (DivisionsHasSysConfigsEntity divisionConfig : divisionConfigMap.values()) {
            if (ConfigEnum.MOBILE_ICON.equals(divisionConfig.getKey())) {
                if (!divisionConfig.getDeleteFlg() && !mobileIcon.isEmpty()) {
                    divisionConfig.setValue(fileUtil.uploadIcon(mobileIcon));
                    form.setMobileIcon(divisionConfig.getValue());
                }
            } else if (ConfigEnum.CUSTOMER_ICON.equals(divisionConfig.getKey())) {
                if (!divisionConfig.getDeleteFlg() && !customerIcon.isEmpty()) {
                    divisionConfig.setValue(fileUtil.uploadIcon(customerIcon));
                    form.setCustomerIcon(divisionConfig.getValue());
                }
            }
            divisionsHasSysConfigsDao.saveOrUpdate(divisionConfig);
        }

        return messages;
    }

    @Override
    public Map<ConfigEnum, DivisionsHasSysConfigsEntity> findDivisionsConfig(Long divisionId) throws CommonException {
        List<DivisionsHasSysConfigsEntity> divisionsHasConfigs = divisionsHasSysConfigsDao.findByDivisionId(divisionId);

        Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionsHasConfigMap = new HashMap<>();
        for (DivisionsHasSysConfigsEntity divisionConfig : divisionsHasConfigs) {
            divisionsHasConfigMap.put(divisionConfig.getKey(), divisionConfig);
        }

        return divisionsHasConfigMap;
    }
}
