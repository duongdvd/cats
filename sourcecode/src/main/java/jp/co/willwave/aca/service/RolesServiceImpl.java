package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.RolesDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class)
public class RolesServiceImpl extends BaseService implements RolesService {
    private final RolesDao rolesDao;

    public RolesServiceImpl(RolesDao rolesDao) {
        this.rolesDao = rolesDao;
    }
}
