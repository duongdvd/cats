package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.AccessDao;
import jp.co.willwave.aca.model.entity.AccessEntity;
import jp.co.willwave.aca.model.enums.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class AccessServiceImpl extends BaseService implements AccessService {

    private final AccessDao accessDao;

    @Autowired
    public AccessServiceImpl(AccessDao accessDao) {
        this.accessDao = accessDao;
    }

    @Override
    public List<AccessEntity> getByRoleId(Integer roleId) {
        return accessDao.getByRoleId(roleId);
    }

    @Override
    public boolean hasAccess(List<AccessEntity> accessEntities, String path, HttpMethod method) {
        if (accessEntities == null) return false;
        return accessEntities.stream().anyMatch(a -> a.checkAccess(path, method.getMethod()));
    }
}
