package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;

import java.util.List;

public interface MasterSysConfigsDao extends BaseDao<MasterSysConfigsEntity> {
    List<MasterSysConfigsEntity> findAll() throws CommonException;
}
