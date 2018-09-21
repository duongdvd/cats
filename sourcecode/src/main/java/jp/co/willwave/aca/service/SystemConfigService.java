package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.dto.api.SystemConfigDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;
import jp.co.willwave.aca.web.form.SystemConfigForm;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

public interface SystemConfigService {

    SystemConfigDTO findByDivisionId(Long divisionId) throws CommonException;

    HashSet<DivisionDTO> getTreeDivisions(Long divisionId);

    List<Message> updateDivisionConfig(Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionConfigMap, SystemConfigForm form)
        throws CommonException;

    Map<ConfigEnum, DivisionsHasSysConfigsEntity> findDivisionsConfig(Long divisionId) throws CommonException;
}
