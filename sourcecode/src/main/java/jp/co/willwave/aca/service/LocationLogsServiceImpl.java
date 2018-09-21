package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.dao.DevicesDao;
import jp.co.willwave.aca.dao.LocationLogsDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dao.RoutesDao;
import jp.co.willwave.aca.dto.api.LocationTempDTO;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.LocationLogsEntity;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class LocationLogsServiceImpl implements LocationLogsService {
    private final Logger logger = Logger.getLogger(LocationLogsServiceImpl.class);

    private final LocationLogsDao locationLogsDao;
    private final RoutesDao routesDao;
    private final RouteDetailDao routeDetailDao;
    private final DevicesDao devicesDao;
    private final CatsMessageResource messageResource;

    @Autowired
    public LocationLogsServiceImpl(LocationLogsDao locationLogsDao,
                                   RoutesDao routesDao,
                                   RouteDetailDao routeDetailDao,
                                   DevicesDao devicesDao, CatsMessageResource messageResource) {
        this.locationLogsDao = locationLogsDao;
        this.routesDao = routesDao;
        this.routeDetailDao = routeDetailDao;
        this.devicesDao = devicesDao;
        this.messageResource = messageResource;
    }

    @Override
    public void sendLocation(LocationTempDTO location) throws Exception {
        logger.debug("LocationLogsLogic.sendLocation");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        RoutesEntity routesEntity = routesDao.getRouteActualRunningByDevicesId(devicesId);
        if (routesEntity == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        RouteDetailEntity routeDetailLastFinish
                = routeDetailDao.findRouteDetailActualLastFinishByRouteId(routesEntity.getId());
        if (routeDetailLastFinish == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        Date today = new Date();
        LocationLogsEntity locationLogsEntity = new LocationLogsEntity();
        locationLogsEntity.setLogTime(today);
        locationLogsEntity.setLongitude(location.getLongtitude());
        locationLogsEntity.setLatitude(location.getLatitude());
        locationLogsEntity.setSpeed(location.getSpeed());
        locationLogsEntity.setRouteDetailId(routeDetailLastFinish.getId());
        locationLogsEntity.setCreateBy(null);
        locationLogsEntity.setUpdateBy(null);
        locationLogsEntity.setCreateDate(new Timestamp(today.getTime()));
        locationLogsEntity.setUpdateDate(new Timestamp(today.getTime()));
        locationLogsDao.insert(locationLogsEntity);

        DevicesEntity devicesEntity = devicesDao.findById(devicesId, DevicesEntity.class);
        devicesEntity.setTimeLocation(new Timestamp(today.getTime()));
        devicesEntity.setListDivisionSend(null);
        devicesEntity.setLongitude(location.getLongtitude());
        devicesEntity.setLatitude(location.getLatitude());
        devicesEntity.setRouteDetailFinished(routeDetailLastFinish.getId());
        devicesEntity.setSpeed(location.getSpeed());
        devicesDao.update(devicesEntity);
    }

    @Override
    public void sendLocationList(List<LocationTempDTO> locationTempDTOs) throws Exception {
        if (!CollectionUtils.isEmpty(locationTempDTOs)) {
            Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                    .getAuthentication().getPrincipal()).getDevicesEntity().getId();
            RoutesEntity routesEntity = routesDao.getRouteActualRunningByDevicesId(devicesId);
            if (routesEntity == null) {
                throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
            }
            RouteDetailEntity routeDetailLastFinish
                    = routeDetailDao.findRouteDetailActualLastFinishByRouteId(routesEntity.getId());
            if (routeDetailLastFinish == null) {
                throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
            }
            locationTempDTOs.sort((o1, o2) -> {
                return o1.getTime().compareTo(o2.getTime());
            });
            for (LocationTempDTO location : locationTempDTOs) {
                LocationLogsEntity locationLogsEntity = convertLocationLogs(location, routeDetailLastFinish
                        .getId(), devicesId);
                locationLogsDao.insert(locationLogsEntity);
            }
            LocationTempDTO locationLast = locationTempDTOs.get(locationTempDTOs.size() - 1);
            DevicesEntity devicesEntity = devicesDao.findById(devicesId, DevicesEntity.class);
            devicesEntity.setTimeLocation(new Timestamp(locationLast.getTime()));
            devicesEntity.setListDivisionSend(null);
            devicesEntity.setLongitude(locationLast.getLongtitude());
            devicesEntity.setLatitude(locationLast.getLatitude());
            devicesEntity.setRouteDetailFinished(routeDetailLastFinish.getId());
            devicesDao.update(devicesEntity);
        }
    }

    private LocationLogsEntity convertLocationLogs(LocationTempDTO location, Long routeDetailId, Long devicesId) {
        LocationLogsEntity locationLogsEntity = new LocationLogsEntity();
        locationLogsEntity.setLogTime(new Date(location.getTime()));
        locationLogsEntity.setLongitude(location.getLongtitude());
        locationLogsEntity.setLatitude(location.getLatitude());
        locationLogsEntity.setSpeed(location.getSpeed());
        locationLogsEntity.setRouteDetailId(routeDetailId);
        locationLogsEntity.setCreateBy(null);
        locationLogsEntity.setUpdateBy(null);
        locationLogsEntity.setCreateDate(new Timestamp(location.getTime()));
        locationLogsEntity.setUpdateDate(new Timestamp(location.getTime()));
        return locationLogsEntity;
    }
}
