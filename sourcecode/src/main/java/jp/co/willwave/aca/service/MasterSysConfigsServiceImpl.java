package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.DivisionsHasSysConfigsDao;
import jp.co.willwave.aca.dao.MasterSysConfigsDao;
import jp.co.willwave.aca.dto.api.ColorConfig;
import jp.co.willwave.aca.dto.api.ConfigDeviceDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;
import jp.co.willwave.aca.utilities.ConversionUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class MasterSysConfigsServiceImpl implements MasterSysConfigsService {
    private Logger logger = Logger.getLogger(MasterSysConfigsServiceImpl.class);

    private final DivisionsDao divisionsDao;
    private final MasterSysConfigsDao masterSysConfigsDao;
    private final DivisionsHasSysConfigsDao divisionsHasSysConfigsDao;

    public MasterSysConfigsServiceImpl(DivisionsDao divisionsDao, MasterSysConfigsDao masterSysConfigsDao,
                                       DivisionsHasSysConfigsDao divisionsHasSysConfigsDao) {
        this.divisionsDao = divisionsDao;
        this.masterSysConfigsDao = masterSysConfigsDao;
        this.divisionsHasSysConfigsDao = divisionsHasSysConfigsDao;
    }


    public ConfigDeviceDTO getConfigsMobile(Long devicesId) throws CommonException {
        logger.info("MasterSysConfigsLogic.getConfigsMobile");
        ConfigDeviceDTO configDeviceDTO = new ConfigDeviceDTO();
        List<MasterSysConfigsEntity> masterSysConfigs = masterSysConfigsDao.findAll();
        if (CollectionUtils.isEmpty(masterSysConfigs)) {
            return configDeviceDTO;
        }
        DivisionsEntity divisionsEntity = divisionsDao.findDivisionManagedDevices(devicesId);
        if (divisionsEntity != null) {
            String[] divisionParentId = divisionsEntity.getDivisionCode().split("_");
            List<Long> divisionIds = new ArrayList<>();
            //ignore i = 0
            for (int i = 1; i < divisionParentId.length; i++) {
                divisionIds.add(Long.valueOf(divisionParentId[i]));
            }
            for (MasterSysConfigsEntity sysConfig : masterSysConfigs) {
                if (ConfigEnum.MAX_LENGTH_INPUT_TEXT.equals(sysConfig.getKey())) {
                    configDeviceDTO.setMaxLengthInputText(sysConfig.getValue());
                    continue;
                }
                List<DivisionsHasSysConfigsEntity> divisionsHasSysConfigsEntities = divisionsHasSysConfigsDao
                        .findConfigDivisionAndKey(divisionIds, sysConfig.getKey());
                if (CollectionUtils.isEmpty(divisionsHasSysConfigsEntities)) {
                    configDeviceDTO = setValueConfigs(sysConfig.getKey(), sysConfig.getValue(), configDeviceDTO);
                } else {
                    Map<Long, DivisionsHasSysConfigsEntity> mapDivisionConfig = divisionsHasSysConfigsEntities.stream
                            ().collect(Collectors.toMap(DivisionsHasSysConfigsEntity::getDivisionsId, x -> x));
                    for (int i = divisionParentId.length - 1; i > 0; i--) {
                        DivisionsHasSysConfigsEntity divisionsHasSysConfigs = mapDivisionConfig.get(Long.valueOf
                                (divisionParentId[i]));
                        if (divisionsHasSysConfigs != null) {
                            configDeviceDTO = setValueConfigs(divisionsHasSysConfigs.getKey(),
                                    divisionsHasSysConfigs
                                    .getValue(), configDeviceDTO);
                            break;
                        }
                    }
                }
            }
        }
        return configDeviceDTO;
    }

    private ConfigDeviceDTO setValueConfigs(ConfigEnum key, String value, ConfigDeviceDTO sourceConfig) {
        ConfigDeviceDTO configDeviceDTO = ConversionUtil.mapper(sourceConfig, ConfigDeviceDTO.class);
        ColorConfig colorConfig = configDeviceDTO.getColor();
        switch (key) {
            case DISTANCE_FINISHED:
                configDeviceDTO.setDistanceFinished(value);
                break;
            case START_END_POINT_COLOR:
                if (colorConfig == null) {
                    colorConfig = new ColorConfig();
                }
                colorConfig.setStartEnd(value);
                configDeviceDTO.setColor(colorConfig);
                break;
            case ARRIVED_POINT:
                if (colorConfig == null) {
                    colorConfig = new ColorConfig();
                }
                colorConfig.setFinish(value);
                configDeviceDTO.setColor(colorConfig);
                break;
            case NOT_ARRIVED_POINT:
                if (colorConfig == null) {
                    colorConfig = new ColorConfig();
                }
                colorConfig.setNotFinish(value);
                configDeviceDTO.setColor(colorConfig);
                break;
            case TIME_MESSAGE:
                configDeviceDTO.setTimeMessage(value);
                break;
            case TRAVEL_TIME_ALERT:
                configDeviceDTO.setTravelTimeAlert(value);
                break;
            default:
                break;

        }
        return configDeviceDTO;
    }

    @Override
    public MasterSysConfigsEntity findByKey(String key) throws CommonException {
        return masterSysConfigsDao.find("key", key, MasterSysConfigsEntity.class);
    }
}
