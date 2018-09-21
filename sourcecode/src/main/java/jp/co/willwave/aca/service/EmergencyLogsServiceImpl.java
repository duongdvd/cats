package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dao.EmergencyLogsDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dao.RoutesDao;
import jp.co.willwave.aca.dto.api.EmergencyDTO;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.EmergencyLogsEntity;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class EmergencyLogsServiceImpl extends BaseService implements EmergencyLogsService {
    private final EmergencyLogsDao emergencyLogsDao;
    private final RoutesDao routesDao;
    private final RouteDetailDao routeDetailDao;
    private final CatsMessageResource catsMessageResource;

    public EmergencyLogsServiceImpl(EmergencyLogsDao emergencyLogsDao,
                                    RoutesDao routesDao,
                                    RouteDetailDao routeDetailDao, CatsMessageResource catsMessageResource) {
        this.emergencyLogsDao = emergencyLogsDao;
        this.routesDao = routesDao;
        this.routeDetailDao = routeDetailDao;
        this.catsMessageResource = catsMessageResource;
    }

    @Override
    public List<EmergencyLogsEntity> findByRoutesDetailIds(List<Long> routesDetailIds) {
        return emergencyLogsDao.findByRoutesDetailIds(routesDetailIds);
    }

    @Override
    public void sendEmergency(EmergencyDTO emergencyDTO) throws Exception {
        DevicesEntity devicesEntity = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal()).getDevicesEntity();
        if (StringUtils.isEmpty(emergencyDTO.getLatitude())) {
            throw new LogicException(ErrorCodeConfig.INPUT_NOT_CORRECT, catsMessageResource.get(Constant.ErrorCode.INPUT_NOT_CORRECT,new String[]{""}));
        }
        if (StringUtils.isEmpty(emergencyDTO.getLongitude())) {
            throw new LogicException(ErrorCodeConfig.INPUT_NOT_CORRECT, catsMessageResource.get(Constant.ErrorCode.INPUT_NOT_CORRECT,new String[]{""}));
        }
        RoutesEntity currentRouteActual = routesDao.getRouteActualRunningByDevicesId(devicesEntity.getId());
        if (currentRouteActual == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, catsMessageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        RoutesEntity routesPlan = routesDao.findById(currentRouteActual.getPlanedRoutesId(), RoutesEntity.class);
        RouteDetailEntity finishLastRouteDetail = routeDetailDao.findRouteDetailActualLastFinishByRouteId
                (currentRouteActual.getId());
        if (finishLastRouteDetail == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, catsMessageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        EmergencyLogsEntity emergencyLogsEntity = new EmergencyLogsEntity();
        emergencyLogsEntity.setLongitude(emergencyDTO.getLongitude().trim());
        emergencyLogsEntity.setLatitude(emergencyDTO.getLatitude().trim());
        emergencyLogsEntity.setMessage(StringUtils.trimWhitespace(emergencyDTO.getMessage()));
        emergencyLogsEntity.setNotificationTime(new Date());
        emergencyLogsEntity.setRouteDetailId(finishLastRouteDetail.getId());
        emergencyLogsEntity.setUsersId(routesPlan.getCreateBy());
        emergencyLogsEntity.setDevicesId(devicesEntity.getId());
        emergencyLogsEntity.setCreateBy(devicesEntity.getId());
        emergencyLogsEntity.setUpdateBy(devicesEntity.getId());
        emergencyLogsDao.insert(emergencyLogsEntity);

    }
}
