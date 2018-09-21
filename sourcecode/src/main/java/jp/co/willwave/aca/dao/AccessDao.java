package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.model.entity.AccessEntity;

import java.util.List;

public interface AccessDao extends BaseDao<AccessEntity> {
    List<AccessEntity> getByRoleId(Integer roleId);
}
