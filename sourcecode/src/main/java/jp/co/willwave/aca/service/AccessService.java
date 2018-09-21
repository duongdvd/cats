package jp.co.willwave.aca.service;

import jp.co.willwave.aca.model.entity.AccessEntity;
import jp.co.willwave.aca.model.enums.HttpMethod;

import java.util.List;

public interface AccessService {
    List<AccessEntity> getByRoleId(Integer roleId);
    boolean hasAccess(List<AccessEntity> accessEntities, String path, HttpMethod method);
}
