package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.dao.CustomersDao;
import jp.co.willwave.aca.dao.DevicesDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dao.RoutesDao;
import jp.co.willwave.aca.dto.api.FinishedRouteDetailDTO;
import jp.co.willwave.aca.dto.api.RouteDetailDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.DevicesEntity;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.ConversionUtil;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class RouteDetailServiceImpl implements RouteDetailService {
    private final Logger logger = Logger.getLogger(RouteDetailServiceImpl.class);

    private final RouteDetailDao routeDetailDao;
    private final RoutesDao routesDao;
    private final DevicesDao devicesDao;
    private final CustomersDao customersDao;
    private final CatsMessageResource messageResource;

    @Autowired
    public RouteDetailServiceImpl(RouteDetailDao routeDetailDao,
                                  RoutesDao routesDao, DevicesDao devicesDao, CustomersDao customersDao,
                                  CatsMessageResource messageResource) {
        this.routeDetailDao = routeDetailDao;
        this.routesDao = routesDao;
        this.devicesDao = devicesDao;
        this.customersDao = customersDao;
        this.messageResource = messageResource;
    }

    @Transactional(rollbackFor = Exception.class)
    public void finishedRouteDetail(FinishedRouteDetailDTO finishedRouteDetail) throws Exception {
        logger.info("RouteDetailLogic.finishedRouteDetail");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        Date sysDate = new Date();
        RoutesEntity actualRoutes = routesDao.getRouteActualRunningByDevicesId(devicesId);
        if (actualRoutes == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        if (actualRoutes.getFinishedTime() != null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_FINISH,new String[]{""}));
        }
        RouteDetailEntity routeDetailActualLastFinish
                = routeDetailDao.findRouteDetailActualLastFinishByRouteId(actualRoutes.getId());
        if (routeDetailActualLastFinish == null
                || routeDetailActualLastFinish.getReDepartTime() == null) {
            throw new LogicException(ErrorCodeConfig.MARKER_DEPARTED, messageResource.get(Constant.ErrorCode.MAKER_HAS_NOT,new String[]{""}));
        }
        RouteDetailEntity routeDetailPlanedNext
                = routeDetailDao.findRouteDetailByRoutesIdAndVisitOrder(actualRoutes.getPlanedRoutesId(),
                routeDetailActualLastFinish.getVisitOrder() + 1);
        if (routeDetailPlanedNext == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_PLAN,new String[]{""}));
        }
        RouteDetailEntity routeDetailFinish = ConversionUtil.clone(routeDetailPlanedNext, RouteDetailEntity.class);
        routeDetailFinish.setId(null);
        routeDetailFinish.setRoutesId(actualRoutes.getId());
        routeDetailFinish.setCustomers(routeDetailPlanedNext.getCustomers());
        routeDetailFinish.setVisitOrder(routeDetailPlanedNext.getVisitOrder());
        routeDetailFinish.setArrivalTime(sysDate);
        routeDetailFinish.setArrivalNote(finishedRouteDetail.getMessage());
        routeDetailFinish.setArrivalNotesId(finishedRouteDetail.getIdMessage());
        routeDetailFinish.setCreateBy(null);
        routeDetailFinish.setCreateDate(new Timestamp(sysDate.getTime()));
        routeDetailFinish.setUpdateBy(null);
        routeDetailFinish.setUpdateDate(new Timestamp(sysDate.getTime()));
        routeDetailDao.insert(routeDetailFinish);

        actualRoutes.setDistance(finishedRouteDetail.getDistance());
        actualRoutes.setUpdateDate(sysDate);
        routesDao.update(actualRoutes);

        DevicesEntity devicesEntity = devicesDao.findById(devicesId, DevicesEntity.class);
        devicesEntity.setCarStatus(CarStatus.VISITED);
        devicesEntity.setUpdateDate(sysDate);
        devicesDao.update(devicesEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void reDepartRouteDetail() throws Exception {
        logger.info("RouteDetailLogic.reDepartRouteDetail");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        Date sysDate = new Date();
        RoutesEntity routesActualEntity = routesDao.getRouteActualRunningByDevicesId(devicesId);
        if (routesActualEntity == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        RouteDetailEntity routeDetailActualLastFinish
                = routeDetailDao.findRouteDetailActualLastFinishByRouteId(routesActualEntity.getId());
        if (routeDetailActualLastFinish == null
                || routeDetailActualLastFinish.getReDepartTime() != null) {
            throw new LogicException(ErrorCodeConfig.MARKER_DEPARTED,  messageResource.get(Constant.ErrorCode.MAKER_HAS_NOT,new String[]{""}));
        }
        routeDetailActualLastFinish.setReDepartTime(sysDate);
        routeDetailActualLastFinish.setUpdateDate(new Timestamp(sysDate.getTime()));
        routeDetailDao.update(routeDetailActualLastFinish);

        DevicesEntity devicesEntity = devicesDao.findById(devicesId, DevicesEntity.class);
        devicesEntity.setCarStatus(CarStatus.ONLINE);
        devicesEntity.setUpdateDate(sysDate);
        devicesDao.update(devicesEntity);
    }

    @Override
    public List<RouteDetailEntity> findByRouteId(Long routeId) {
        return routeDetailDao.findByRouteId(routeId);
    }

    @Override
    public RouteDetailEntity findById(Long routeDetailId) throws CommonException {
        return routeDetailDao.findById(routeDetailId, RouteDetailEntity.class);
    }

    @Override
    public Long getVisitedOrderByRouteId(Long routeId) {
        return routeDetailDao.getMaxVisitedOrderByRouteId(routeId);
    }

    @Override
    public Triple<RouteDetailDTO, List<RouteDetailDTO>, RouteDetailDTO> splitRoutesGarageAndCustomer(List<RouteDetailDTO> routeDetails) {
        List<RouteDetailDTO> routeCustomerDetails = new ArrayList<>();
        RouteDetailDTO startGarage = new RouteDetailDTO();
        RouteDetailDTO endGarage = new RouteDetailDTO();
        for (RouteDetailDTO routeDetailDTO : routeDetails) {
            if (CustomerType.GARAGE.getType().equals(routeDetailDTO.getCustomerType().getType())) {
                if (routeDetailDTO.getVisitOrder() == 0) {
                    startGarage = routeDetailDTO;
                } else {
                    endGarage = routeDetailDTO;
                }
            } else {
                routeCustomerDetails.add(routeDetailDTO);
            }
        }
        return Triple.of(startGarage, routeCustomerDetails, endGarage);
    }

    public List<RouteDetailEntity> createRouteDetailList(String routeDetailsStr) throws CommonException {
        List<RouteDetailEntity> routeDetails = new ArrayList<>();
        if (!CatStringUtil.isEmpty(routeDetailsStr)) {
            String[] arrDetail = routeDetailsStr.split(",");
            if (arrDetail.length > 0) {
                for (int i = 0; i < arrDetail.length; i += 2) {
                    Long customersId = Long.valueOf(arrDetail[i]);
                    Long visitOrder = Long.valueOf(arrDetail[i + 1]);

                    CustomersEntity customer = customersDao.findById(customersId, CustomersEntity.class);
                    routeDetails.add(new RouteDetailEntity(customer, visitOrder));
                }
            }
        }
        return routeDetails;
    }

    @Override
    public List<RouteDetailEntity> createCustomerListFromRouteDetailStr(List<RouteDetailEntity> routeDetails) {
        if (routeDetails != null && routeDetails.size() > 2) {
            routeDetails.remove(0);
            routeDetails.remove(routeDetails.size() - 1);
        } else {
            routeDetails.clear();
        }
        return routeDetails;
    }

    @Override
    public List<RouteDetailEntity> createCustomerListFromRouteDetailStr(String routeDetailsStr) throws CommonException {
        List<RouteDetailEntity> routeDetails = this.createRouteDetailList(routeDetailsStr);
        routeDetails = routeDetails.stream()
                                   .sorted((r1, r2) -> Long.compare(r1.getVisitOrder(), r2.getVisitOrder()))
                                   .collect(Collectors.toList());
        routeDetails = this.createCustomerListFromRouteDetailStr(routeDetails);
        return routeDetails;
    }
}
