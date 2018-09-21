package jp.co.willwave.aca.service;

import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.dto.api.AllRouteDTO;
import jp.co.willwave.aca.dto.api.CarDetailDTO;
import jp.co.willwave.aca.dto.api.CarMapDTO;
import jp.co.willwave.aca.dto.api.RouteDetailDTOMap;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
@Transactional(rollbackFor = Exception.class)
public class CarManageServiceImpl implements CarManageService {

    private final DevicesDao devicesDao;
    private final CarsDao carsDao;
    private final RoutesDao routesDao;
    private final DivisionsDao divisionsDao;
    private final UsersDao usersDao;
    private final RouteDetailDao routeDetailDao;

    @Autowired
    public CarManageServiceImpl(DevicesDao devicesDao, CarsDao carsDao, RoutesDao routesDao, DivisionsDao divisionsDao, UsersDao usersDao, RouteDetailDao routeDetailDao) {
        this.devicesDao = devicesDao;
        this.carsDao = carsDao;
        this.routesDao = routesDao;
        this.divisionsDao = divisionsDao;
        this.usersDao = usersDao;
        this.routeDetailDao = routeDetailDao;
    }

    @Override
    public CarDetailDTO getCarDetailInfo(Long deviceId) throws CommonException {
        CarDetailDTO carDetailDTO = null;
        if (deviceId != null) {
            DevicesEntity devicesEntity = devicesDao.findById(deviceId, DevicesEntity.class);
            if (devicesEntity != null) {
                carDetailDTO = new CarDetailDTO();
                carDetailDTO.setDeviceId(deviceId);
                carDetailDTO.setLoginId(devicesEntity.getLoginId());
                carDetailDTO.setCarName(devicesEntity.getLoginId());
                carDetailDTO.setLatitude(devicesEntity.getLatitude());
                carDetailDTO.setLongitude(devicesEntity.getLongitude());
                carDetailDTO.setSpeed(devicesEntity.getSpeed());
                carDetailDTO.setCallId(devicesEntity.getCallId());
                carDetailDTO.setCallPassword(devicesEntity.getCallPassword());
                carDetailDTO.setCallUserName(devicesEntity.getCallUserName());
                carDetailDTO.setUserTags(devicesEntity.getUserTags());

                RoutesEntity routeActual = routesDao.getRouteActualRunningByDevicesId(deviceId);
                if (routeActual != null) {
                    carDetailDTO.setCurrentRouteName(routeActual.getName());
                    carDetailDTO.setRouteId(routeActual.getId());
                    carDetailDTO.setRouteMemo(routeActual.getRouteMemo());

                    RoutesEntity routePlan = routesDao.findById(routeActual.getPlanedRoutesId(), RoutesEntity.class);
                    if (routePlan != null) {
                        carDetailDTO.setPlanRouteName(routePlan.getName());
                        carDetailDTO.setPlanRouteId(routePlan.getId());
                        carDetailDTO.setPlanRouteName(routePlan.getRouteMemo());
                    }

                    CarsEntity carsEntity = carsDao.findById(routeActual.getCarsId(), CarsEntity.class);
                    if (carsEntity != null) {
                        carDetailDTO.setDriverName(carsEntity.getDriverName());
                        carDetailDTO.setPlateNumber(carsEntity.getPlateNumber());
                        carDetailDTO.setCarId(routeActual.getCarsId());
                    }
                }
            }
        }

        return carDetailDTO;
    }

    @Override
    public Map<List<DivisionsEntity>, List<CarMapDTO>> getCarListManagedByOperator(Long divisionId, Long userId, CarStatus carStatus, RunningStatus runningStatus) throws CommonException {
        Map<List<DivisionsEntity>, List<CarMapDTO>> carMap = new HashMap<>();
        List<DivisionsEntity> divisionsEntities = new ArrayList<>();
        DivisionsEntity divisionsEntity = divisionsDao.findById(divisionId, DivisionsEntity.class);
        divisionsEntities.add(divisionsEntity);

        List<Object[]> objectList = devicesDao.getDevicesManageByOperator(userId, carStatus, runningStatus);
        carMap.put(divisionsEntities, buildCarMapDto(objectList));
	    return carMap;
    }

    @Override
    public Map<List<DivisionsEntity>, List<CarMapDTO>> getCarListOfViewer(Long divisionId, CarStatus carStatus, RunningStatus runningStatus) throws CommonException {
        Map<List<DivisionsEntity>, List<CarMapDTO>> carMap = new HashMap<>();

        List<DivisionsEntity> divisionsEntities = divisionsDao.findDivisionChildListByDivisionId(divisionId);
        divisionsEntities.add(divisionsDao.findById(divisionId, DivisionsEntity.class));

        List<Long> divisionIdList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            for (DivisionsEntity d : divisionsEntities) {
                divisionIdList.add(d.getId());
            }
        }

	    List<Object[]> objectList = devicesDao.getDevicesByDivisions(divisionIdList, carStatus, runningStatus);
        carMap.put(divisionsEntities, buildCarMapDto(objectList));
        return carMap;

    }

    private List<CarMapDTO> buildCarMapDto(List<Object[]> objectList) {
        List<CarMapDTO> carMapDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(objectList)) {
            objectList.forEach(objects -> {
                CarMapDTO carMapDTO = new CarMapDTO();
                if (objects[0] instanceof DevicesEntity) {
                    DevicesEntity devicesEntity = (DevicesEntity) objects[0];
                    carMapDTO.setLongitude(devicesEntity.getLongitude());
                    carMapDTO.setLatitude(devicesEntity.getLatitude());
                    carMapDTO.setSpeed(devicesEntity.getSpeed());
                    carMapDTO.setCarName(devicesEntity.getLoginId());
                    carMapDTO.setDeviceId(devicesEntity.getId());
                    carMapDTO.setIconPath(devicesEntity.getIconPath());
                }
                if (objects[1] instanceof CarsEntity) {
                    CarsEntity carsEntity = (CarsEntity) objects[1];
                    carMapDTO.setPlateNumber(carsEntity.getPlateNumber());
                }

                carMapDTOList.add(carMapDTO);
            });
        }

        return carMapDTOList;
    }

    @Override
    public AllRouteDTO getAllRouteDetailByDevice(Long deviceId, RunningStatus runningStatus) {
        List<Object[]> actualList = routeDetailDao.getActualRouteDetail(deviceId, runningStatus);

        AllRouteDTO allRouteDTO = new AllRouteDTO();
        if (!CollectionUtils.isEmpty(actualList)) {
            List<RouteDetailDTOMap> actualRouteDetailList = convertData(actualList);
            allRouteDTO.setRouteActualDetail(actualRouteDetailList);
            if (!CollectionUtils.isEmpty(allRouteDTO.getRouteActualDetail())) {
                List<Object[]> planList = routeDetailDao.getPlanRouteDetail(
                    actualRouteDetailList.get(0).getRoutePlanId(), runningStatus);
                if (!CollectionUtils.isEmpty(planList))  {
                    allRouteDTO.setRoutePlanDetail(convertData(planList));
                }
            }

        }
        return allRouteDTO;
    }

    private List<RouteDetailDTOMap> convertData(List<Object[]> objectList) {
        List<RouteDetailDTOMap> routeDetailList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(objectList)) {
            objectList.forEach(objects -> {
                RouteDetailDTOMap routeDetailDTOMap = new RouteDetailDTOMap();
                if (objects[0] instanceof RoutesEntity) {
                    RoutesEntity routesEntity = (RoutesEntity) objects[0];

                    if (routesEntity.getDevicesId() != null) {
                        routeDetailDTOMap.setDeviceId(routesEntity.getDevicesId());
                    }
                    routeDetailDTOMap.setRouteId(routesEntity.getId());
                    if (routesEntity.getPlanedRoutesId() != null) {
                        routeDetailDTOMap.setRoutePlanId(routesEntity.getPlanedRoutesId());
                    }

                    routeDetailDTOMap.setName(routesEntity.getName());
                }
                if (objects[1] instanceof RouteDetailEntity) {
                    RouteDetailEntity routeDetailEntity = (RouteDetailEntity) objects[1];

                    routeDetailDTOMap.setRouteDetailId(routeDetailEntity.getId());
                    routeDetailDTOMap.setVisitOrder(routeDetailEntity.getVisitOrder());
                    if (routeDetailEntity.getArrivalTime() != null) {
                        routeDetailDTOMap.setArrivalTime(routeDetailEntity.getArrivalTime());
                    }
                    if (routeDetailEntity.getArrivalNote() != null) {
                        routeDetailDTOMap.setArrivalNote(routeDetailEntity.getArrivalNote());
                    }
                    if (routeDetailEntity.getReDepartTime() != null) {
                        routeDetailDTOMap.setReDepartTime(routeDetailEntity.getReDepartTime());
                    }

                }

                if (objects[2] instanceof CustomersEntity) {
                    CustomersEntity customersEntity = (CustomersEntity) objects[2];
                    if (customersEntity.getName() != null) {
                        routeDetailDTOMap.setCustomerName(customersEntity.getName());
                    }
                    if (customersEntity.getAddress() != null) {
                        routeDetailDTOMap.setAddress(customersEntity.getAddress());
                    }
                    if (customersEntity.getDescription() != null) {
                        routeDetailDTOMap.setDescription(customersEntity.getDescription());
                    }
                    if (customersEntity.getLatitude() != null) {
                        routeDetailDTOMap.setLatitude(customersEntity.getLatitude());
                    }
                    if (customersEntity.getLongitude() != null) {
                        routeDetailDTOMap.setLongitude(customersEntity.getLongitude());
                    }
                    if (customersEntity.getIconMarker() != null) {
                        routeDetailDTOMap.setIconMaker(customersEntity.getIconMarker());
                    }
                }
                routeDetailList.add(routeDetailDTOMap);
            });
        }
            return routeDetailList;
    }
}
