package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.ConfigDeviceDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;

public interface MasterSysConfigsService {
    ConfigDeviceDTO getConfigsMobile(Long devicesId) throws Exception;

    MasterSysConfigsEntity findByKey(String key) throws CommonException;
}
