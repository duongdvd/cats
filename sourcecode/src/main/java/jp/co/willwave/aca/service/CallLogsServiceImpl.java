package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.CallLogsDao;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.CallLogsEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class CallLogsServiceImpl implements CallLogsService {
    private final CallLogsDao callLogsDao;

    public CallLogsServiceImpl(CallLogsDao callLogsDao) {
        this.callLogsDao = callLogsDao;
    }

    @Override
    public List<CallLogsEntity> findByRoutesDetailIds(List<Long> routeDetailIds) {
        return callLogsDao.findByRoutesDetailIds(routeDetailIds);
    }

    @Override
    public void writeCallLog(CallLogsEntity callLog) throws CommonException {
        callLogsDao.insert(callLog);
    }
}