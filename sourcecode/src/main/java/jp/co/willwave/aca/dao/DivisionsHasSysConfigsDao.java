package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;

import java.util.List;

public interface DivisionsHasSysConfigsDao extends BaseDao<DivisionsHasSysConfigsEntity> {
    List<DivisionsHasSysConfigsEntity> findByDivisionId(Long divisionsId) throws CommonException;

    List<DivisionsHasSysConfigsEntity> findConfigDivisionAndKey(List<Long> divisionIds, ConfigEnum key);

    DivisionsHasSysConfigsEntity findConfigDivisionAndKey(Long divisionId, ConfigEnum key);
}
