package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.MasterArrivalNotesEntity;

import java.util.List;

public interface MasterArrivalNotesDao extends BaseDao<MasterArrivalNotesEntity> {

    List<MasterArrivalNotesEntity> findByCompanyId(Long companyId);
}
