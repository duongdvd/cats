package jp.co.willwave.aca.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.willwave.aca.common.ErrorCodeConfig;
import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.constants.BusinessConstants;
import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dao.*;
import jp.co.willwave.aca.dto.api.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.*;
import jp.co.willwave.aca.utilities.CatsMessageResource;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.RouteCustomerDetailForm;
import jp.co.willwave.aca.web.form.SearchRouteForm;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoutesServiceImpl implements RoutesService {
    private final Logger logger = Logger.getLogger(RoutesServiceImpl.class);

    private final RoutesDao routesDao;
    private final RouteDetailDao routeDetailDao;
    private final RouteChangeHistoryDao routeChangeHistoryDao;
    private final ObjectMapper objectMapper;
    private final RouterDetailMapsDao routerDetailMapsDao;
    private final CatsMessageResource messageResource;
    private final DevicesDao devicesDao;
    private final UsersDao usersDao;
    private final CarsDao carsDao;
    private final RouteDetailService routeDetailService;
    private final CustomersService customersService;

    public RoutesServiceImpl(RoutesDao routesDao, RouteDetailDao routeDetailDao,
                             RouteChangeHistoryDao routeChangeHistoryDao, ObjectMapper objectMapper,
                             RouterDetailMapsDao routerDetailMapsDao, CatsMessageResource messageResource,
                             DevicesDao devicesDao, UsersDao usersDao, CarsDao carsDao,
                             RouteDetailService routeDetailService, CustomersService customersService) {
        this.routesDao = routesDao;
        this.routeDetailDao = routeDetailDao;
        this.routeChangeHistoryDao = routeChangeHistoryDao;
        this.objectMapper = objectMapper;
        this.routerDetailMapsDao = routerDetailMapsDao;
        this.messageResource = messageResource;
        this.devicesDao = devicesDao;
        this.usersDao = usersDao;
        this.carsDao = carsDao;
        this.routeDetailService = routeDetailService;
        this.customersService = customersService;
    }

    @Override
    public RoutesDTO getRouteByTime(Long devicesId) throws Exception {
        logger.info("RoutesLogic.getRouteByTime");
        RoutesEntity routePlanedRunning = routesDao.getRoutePlanRunningByDevicesId(devicesId);
        RoutesEntity routeActual = null;
        if (routePlanedRunning == null) {
            routePlanedRunning = routesDao.getRoutePlanedWillBeRunningByDevicesId(devicesId);
        } else {
            routeActual = routesDao.getActualByPlan(routePlanedRunning.getId());
        }
        if (routePlanedRunning == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        RoutesDTO routesDTO = ConversionUtil.strictMapper(routePlanedRunning, RoutesDTO.class);
        //result tra ve cho mobile is route memo
        routesDTO.setDescription(routePlanedRunning.getRouteMemo());
        UsersEntity currentUser = usersDao.findById(routePlanedRunning.getCreateBy(), UsersEntity.class);
        if (currentUser == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE, new String[]{""}));
        }

        routesDTO.setCallUserId(currentUser.getCallId());
        if (routeActual == null) {
            routesDTO.setRouteDetails(routeDetailDao.getRouteDetailInfoByRouteId(routePlanedRunning.getId()));
            routesDTO.setFinished(false);
            return routesDTO;
        } else {
            routesDTO.setFinished(routeActual.getFinishedTime() != null);
        }

        List<RouteDetailDTO> routeDetailsPlaned = routeDetailDao.getRouteDetailInfoByRouteId(routePlanedRunning.getId());
        if (CollectionUtils.isEmpty(routeDetailsPlaned)) {
            return routesDTO;
        }

        //Set route detail finished
        RouteDetailEntity routeDetailActualLastFinish =
                routeDetailDao.findRouteDetailActualLastFinishByRouteId(routeActual.getId());
        if (routeDetailActualLastFinish == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        Long maxVisitOrderFinish = routeDetailActualLastFinish.getVisitOrder();
        for (int i = 0; i <= maxVisitOrderFinish; i++) {
            routeDetailsPlaned.get(i).setFinished(true);
        }
        routesDTO.setRouteDetails(routeDetailsPlaned);
        return routesDTO;
    }

    @Override
    public List<RouteMapDTO> getListRouteDetailMap(Long devicesId) throws IOException, CommonException, LogicException {
        RoutesEntity routePlanedRunning = routesDao.getRoutePlanRunningByDevicesId(devicesId);
        if (routePlanedRunning == null) {
            routePlanedRunning = routesDao.getRoutePlanedWillBeRunningByDevicesId(devicesId);
        }
        if (routePlanedRunning == null) {
            return new ArrayList<>();
        }

        RouterDetailMapsEntity routerDetailMapsEntity = routerDetailMapsDao.getListRouteDetailMap(routePlanedRunning.getId());
        if (routerDetailMapsEntity == null) {
            return new ArrayList<>();
        }
        return objectMapper.readValue(routerDetailMapsEntity.getDetailMaps(), List.class);
    }

    @Override
    public Boolean startRoute(Long routeUpdateTime) throws Exception {
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        Date sysDate = new Date();
        RoutesEntity routePlaned = routesDao.getRoutePlanedWillBeRunningByDevicesId(devicesId);
        if (routePlaned == null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        if (routePlaned.getUpdateDate().getTime() != routeUpdateTime) {
            return false;
        }
        if (routesDao.getRoutePlanRunningByDevicesId(devicesId) != null) {
            throw new LogicException(ErrorCodeConfig.ROUTE_ACTUAL_CREATED, messageResource.get(Constant.ErrorCode.ROUTE_ACTUAL_CREATE,new String[]{""}));
        }
        RoutesEntity routesActual = ConversionUtil.clone(routePlaned, RoutesEntity.class);

        //update running flag
        routePlaned.setRunningStatus(RunningStatus.RUNNING);
        routesDao.update(routePlaned);

        routesActual.setId(null);
        routesActual.setPlanedRoutesId(routePlaned.getId());
        routesActual.setActualDate(sysDate);
        routesActual.setName(routePlaned.getName() + "-" + new Timestamp(sysDate.getTime()));
        routesActual.setDistance(0F);
        CarsEntity carsEntity = carsDao.findByDeviceId(devicesId);
        routesActual.setCarsId(carsEntity.getId());
        routesDao.insert(routesActual);
        RouteDetailEntity routeDetailPlanedFirst
                = routeDetailDao.findRouteDetailByRoutesIdAndVisitOrder(routePlaned.getId(), 0L);
        if (routeDetailPlanedFirst == null) {
            return true;
        }
        Long routesActual1Id = routesActual.getId();
        RouteDetailEntity routeDetailActual = new RouteDetailEntity();
        routeDetailActual.setRoutesId(routesActual1Id);
        routeDetailActual.setCustomers(routeDetailPlanedFirst.getCustomers());
        routeDetailActual.setArrivalTime(sysDate);
        routeDetailActual.setArrivalNote(BusinessConstants.ARRIVAL_NOTE_START);
        routeDetailActual.setVisitOrder(routeDetailPlanedFirst.getVisitOrder());
        routeDetailActual.setReDepartTime(sysDate);
        routeDetailActual.setCreateDate(new Timestamp(sysDate.getTime()));
        routeDetailActual.setUpdateDate(new Timestamp(sysDate.getTime()));
        routeDetailActual.setCreateBy(null);
        routeDetailActual.setUpdateBy(null);
        routeDetailDao.insert(routeDetailActual);
        DevicesEntity currentDevices = devicesDao.findById(devicesId, DevicesEntity.class);
        currentDevices.setCarStatus(CarStatus.ONLINE);
        currentDevices.setUpdateDate(sysDate);
        devicesDao.update(currentDevices);
        return true;
    }

    @Override
    public List<RoutesDTO> getAllRouteActual(Long devicesId) throws CommonException {
        List<RoutesEntity> routesEntities = routesDao.getRouteActualByDevicesIdAndActive(devicesId, true);
        if (CollectionUtils.isEmpty(routesEntities)) {
            return new ArrayList<>();
        }
        return routesEntities.stream()
                .map(routesEntity -> ConversionUtil.strictMapper(routesEntity, RoutesDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<RouteActualDTO> getRouteActualByPage(Long devicesId, Long page, Long numberRecord) throws CommonException {
        List<RoutesEntity> routesEntities = routesDao.findByRouteActualByDevicesId(devicesId, page, numberRecord);
        List<RouteActualDTO> routeActualDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(routesEntities)) {
            routesEntities.forEach(routesEntity -> {
                RouteActualDTO routeActualDTO = new RouteActualDTO();
                routeActualDTO.setId(routesEntity.getId());
                routeActualDTO.setName(routesEntity.getName());
                routeActualDTO.setDescription(routesEntity.getRouteMemo());
                routeActualDTO.setActualDate(routesEntity.getActualDate());
                routeActualDTOs.add(routeActualDTO);
            });
        }
        return routeActualDTOs;
    }

    @Override
    public List<RouteActualDTO> getRouteActualByMessage(String keyword) throws CommonException {
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getDevicesEntity().getId();
        List<RoutesEntity> routesEntities = routesDao.searchMessageRoute(devicesId, StringUtils.trimWhitespace(keyword));
        List<RouteActualDTO> routeActualDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(routesEntities)) {
            routesEntities.forEach(routesEntity -> {
                RouteActualDTO routeActualDTO = new RouteActualDTO();
                routeActualDTO.setId(routesEntity.getId());
                routeActualDTO.setName(routesEntity.getName());
                routeActualDTO.setDescription(routesEntity.getRouteMemo());
                routeActualDTO.setActualDate(routesEntity.getActualDate());
                routeActualDTOs.add(routeActualDTO);
            });
        }
        return routeActualDTOs;
    }

    @Override
    public RoutesDTO getRouteDetailByRouteId(Long routeId) throws Exception {
        logger.info("RoutesLogic.getRouteByTime");
        Long devicesId = ((ExpandUserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getDevicesEntity().getId();
        RoutesDTO routesDTO;
        RoutesEntity routesActual = routesDao.findById(routeId, RoutesEntity.class);
        if (routesActual == null || !routesActual.getDevicesId().equals(devicesId)) {
            throw new LogicException(ErrorCodeConfig.ROUTE_NOT_FOUND, messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND,new String[]{""}));
        }
        routesDTO = ConversionUtil.strictMapper(routesActual, RoutesDTO.class);
        routesDTO.setDescription(routesActual.getRouteMemo());
        routesDTO.setRouteDetails(routeDetailDao.getRouteDetailInfoByRouteId(routesActual.getId()));
        return routesDTO;
    }

    @Override
    public RoutesDTO findRouteDetailByRouteId(Long routeId) throws Exception {
        RoutesEntity route = routesDao.findById(routeId, RoutesEntity.class);

        if (route != null) {
            RoutesDTO routesDTO = ConversionUtil.strictMapper(route, RoutesDTO.class);
            routesDTO.setRouteDetails(routeDetailDao.getRouteDetailInfoByRouteId(route.getId()));
            return routesDTO;
        }

        return new RoutesDTO();
    }

    @Override
    public List<RouteDTO> getRouteList(Long userId, Integer offset, Integer maxResults, Boolean noPaging) {
        logger.info("RoutesService.getDeviceList");
        return routesDao.getRouteList(userId, offset, maxResults, noPaging);
    }

    @Override
    public List<RouteDTO> searchPlanRouteList(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {
        logger.info("RoutesService.searchPlanRouteList");
        return routesDao.searchPlanRouteList(userId, searchRouteForm, offset, maxResults, havePaging);
    }

    @Override
    public Long countPlanRoutes(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {
        return routesDao.countPlanRoutes(userId, searchRouteForm, offset, maxResults, havePaging);
    }

    @Override
    public List<Message> validateDateTimeForm(SearchRouteForm searchRouteForm) {
        List<Message> messages = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        if (searchRouteForm.getFromStartDate() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchRouteForm.getFromStartDate())) {
                try {
                    Date date = dateFormat.parse(searchRouteForm.getFromStartDate());
                } catch (ParseException e) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FORMAT, new String[]{"route.fromStartDate"}));
                }
            }
        }
        if (searchRouteForm.getToStartDate() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchRouteForm.getToStartDate())) {
                try {
                    Date date = dateFormat.parse(searchRouteForm.getToStartDate());
                } catch (ParseException e) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FORMAT, new String[]{"route.toStartDate"}));
                }
            }
        }
        if (searchRouteForm.getFromEndDate() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchRouteForm.getFromEndDate())) {
                try {
                    Date date = dateFormat.parse(searchRouteForm.getFromEndDate());
                } catch (ParseException e) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FORMAT, new String[]{"route.fromEndDate"}));
                }
            }
        }
        if (searchRouteForm.getToEndDate() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchRouteForm.getToEndDate())) {
                try {
                    Date date = dateFormat.parse(searchRouteForm.getToEndDate());
                } catch (ParseException e) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FORMAT, new String[]{"route.toEndDate"}));
                }
            }
        }

        if (searchRouteForm.getSpecificDate() != null) {
            if (org.apache.commons.lang3.StringUtils.isNotBlank(searchRouteForm.getSpecificDate())) {
                try {
                    Date date = dateFormat.parse(searchRouteForm.getSpecificDate());
                } catch (ParseException e) {
                    messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FORMAT, new String[]{"route.specificDate"}));
                }
            }
        }
        return messages;
    }

    @Override
    public List<Message> deleteRoute(Long routeId, Long userLoginId) throws CommonException {

        List<Message> messages = new ArrayList<>();
        if (routeId == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"route.id"}));
            return messages;
        }

        if (userLoginId == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"division.user.LoginId"}));
            return messages;
        }

        RoutesEntity routesEntity = routesDao.findByUserIdAndRouteId(routeId, userLoginId);
        if (routesEntity == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_ROUTE, new String[]{routeId.toString()}));
            return messages;
        }

        // check route has been used
        if (routesDao.countHistoryRouteActualByRoutePlanId(routeId, null) > 0) {
            throw new LogicWebException(messageResource.getWithParamKeys(Constant.ErrorCode.ROUTE_HAS_BEEN_USED, new String[]{routeId.toString()}));
        }

        routesEntity.setUpdateBy(userLoginId);
        routesDao.delete(routesEntity);
        routeDetailDao.deleteByRouteId(routeId);
        return messages;

    }

    @Override
    public List<Message> validateViewRouteDetail(Long routeId, Long userId) {

        List<Message> messages = new ArrayList<>();
        RoutesEntity routesEntity = routesDao.findByUserIdAndRouteId(routeId, userId);

        if (routesEntity == null) {
            messages.add(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_ROUTE, new String[]{routeId.toString()}));
        }
        return messages;
    }

    @Override
    public RouteDTO getRouteInfo(Long routeId) {
        return routesDao.getRouteInfoInDetail(routeId);
    }

    @Override
    public List<RouteCustomerDetailDTO> getRouteCustomerDetail(Long routeId, Long userId, RouteCustomerDetailForm routeCustomerDetailForm) {
        return routesDao.getRouteCustomerDetail(routeId, userId, routeCustomerDetailForm);
    }

    @Override
    public RoutesEntity findById(Long routeId) {
        try {
            return routesDao.findById(routeId, RoutesEntity.class);
        } catch (CommonException e) {
            return null;
        }
    }

    @Override
    public RoutesDTO getRouteActualDetail(Long devicesId) throws Exception {
        RoutesEntity currentRoutesEntity = routesDao.getRouteActualRunningByDevicesId(devicesId);
        if (currentRoutesEntity == null) {
            return new RoutesDTO();
        }
        RoutesDTO routesDTO = ConversionUtil.strictMapper(currentRoutesEntity, RoutesDTO.class);
        routesDTO.setDescription(currentRoutesEntity.getRouteMemo());
        routesDTO.setRouteDetails(routeDetailDao.getRouteDetailInfoByRouteId(currentRoutesEntity.getId()));
        return routesDTO;
    }

    @Override
    public void finishedRoute(Long devicesId) throws Exception {
        Date sysDate = new Date();
        RoutesEntity routePlanRunning = routesDao.getRoutePlanRunningByDevicesId(devicesId);
        if (routePlanRunning == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }

        RoutesEntity routesActual = routesDao.getActualByPlan(routePlanRunning.getId());
        if (routesActual == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        if (routesActual.getFinishedTime() != null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ROUTE_FINISH,new String[]{""}));
        }
        //update running status
        routePlanRunning.setRunningStatus(RunningStatus.FINISHED);
        routesDao.update(routePlanRunning);

        RouteDetailEntity routeLastActualDetail
                = routeDetailDao.findRouteDetailActualLastFinishByRouteId(routesActual.getId());
        if (routeLastActualDetail == null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.ERROR_DATABASE,new String[]{""}));
        }
        RouteDetailEntity routeDetailNext = routeDetailDao.findRouteDetailByRoutesIdAndVisitOrder(routesActual
                .getPlanedRoutesId(), routeLastActualDetail.getVisitOrder() + 1);
        if (routeDetailNext != null) {
            throw new LogicException(ErrorCodeConfig.ERROR_DATABASE, messageResource.get(Constant.ErrorCode.TAKE_OUT,new String[]{""}));
        }
        routesActual.setFinishedTime(routeLastActualDetail.getArrivalTime());
        routesActual.setUpdateDate(sysDate);
        routesDao.update(routesActual);

        DevicesEntity devicesEntity = devicesDao.findById(devicesId, DevicesEntity.class);
        devicesEntity.setUpdateDate(sysDate);
        devicesEntity.setCarStatus(CarStatus.OFFLINE);
        devicesDao.update(devicesEntity);
        routeLastActualDetail.setReDepartTime(routeLastActualDetail.getArrivalTime());
        routeLastActualDetail.setUpdateDate(sysDate);
        routeDetailDao.update(routeLastActualDetail);
    }

    @Override
    public List<Message> createRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException {
        route.setId(null);
        routesDao.insert(route);

        insertRouteDetail(route.getId(), routeDetails);


        RouterDetailMapsEntity routerDetailMap = new RouterDetailMapsEntity();
        routerDetailMap.setDetailMaps(detailMaps);
        routerDetailMap.setRoutesId(route.getId());

        routerDetailMapsDao.insert(routerDetailMap);
        return new ArrayList<>();
    }

    @Override
    public List<Message> updateRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException {
        List<Message> messages = new ArrayList<>();

        RoutesEntity routePlanDb = routesDao.findById(route.getId(), RoutesEntity.class);

        Message editPermissionMessage = this.validateEditPermission(routePlanDb);
        if (editPermissionMessage != null) {
            messages.add(editPermissionMessage);
            return messages;
        }

        // Update route plan.
        routePlanDb.setDevicesId(route.getDevicesId());
        routePlanDb.setName(route.getName());
        routePlanDb.setRouteMemo(route.getRouteMemo());
        routePlanDb.setStartDate(route.getStartDate());
        routePlanDb.setEndDate(route.getEndDate());
        routePlanDb.setActive(route.getActive());
        routesDao.update(routePlanDb);

        // Delete and insert new routes details.
        routesDao.deleteRouteDetailByRouteId(routePlanDb.getId());

        // Update route detail Map.
        RouterDetailMapsEntity routerDetailMap = routerDetailMapsDao.getListRouteDetailMap(routePlanDb.getId());
        routerDetailMap.setDetailMaps(detailMaps);
        routerDetailMapsDao.update(routerDetailMap);
        insertRouteDetail(route.getId(), routeDetails);

        return messages;
    }

    @Override
    public List<Message> updateRunningRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException {
        Long previousRoutePlanId = route.getId();

        RoutesEntity actualRoute = routesDao.getActualByPlan(previousRoutePlanId);

        List<Message> messages = new ArrayList<>();
        if (actualRoute != null) {
            // Create new RUNNING plan Route.
            route.setRunningStatus(RunningStatus.RUNNING);
            createRoute(route, routeDetails, detailMaps);

            // Update Route Plan for Running actual route.
            actualRoute.setPlanedRoutesId(route.getId());
            routesDao.update(actualRoute);

            // Create Route Change History.
            RouteChangeHistoryEntity routeChangeHistoryEntity = new RouteChangeHistoryEntity();
            routeChangeHistoryEntity.setActualRouteId(actualRoute.getId());
            routeChangeHistoryEntity.setCurrentPlanRouteId(route.getId());
            routeChangeHistoryEntity.setPreviousPlanRouteId(previousRoutePlanId);
            routeChangeHistoryEntity.setOperatorId(
                        ((UserInfo)SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO)).getId());
            routeChangeHistoryDao.insert(routeChangeHistoryEntity);

            // Update Previous Plan route status from RUNNING -> CHANGED.
            RoutesEntity previousPlanRoute = routesDao.findById(previousRoutePlanId, RoutesEntity.class);
            previousPlanRoute.setRunningStatus(RunningStatus.CHANGED);
            routesDao.update(previousPlanRoute);
        } else {
            messages.add(messageResource.get(Constant.ErrorCode.ROUTE_NOT_FOUND));
        }
        return messages;
    }

    @Override
    public RoutesEntity findRouteActualByDevices(Long devicesId) throws CommonException {
        return routesDao.getRouteActualRunningByDevicesId(devicesId);
    }

    @Override
    public void changeStatusRoute(Long routeId) throws Exception {
        UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
        Long userLoginId = userInfo.getId();
        if (routeId == null) {
            throw new LogicWebException(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"route.id"}));
        }

        if (userLoginId == null) {
            throw new LogicWebException(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"division.user.LoginId"}));
        }

        RoutesEntity routesEntity = routesDao.findByUserIdAndRouteId(routeId, userLoginId);
        if (routesEntity == null) {
            throw new LogicWebException(messageResource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_ROUTE, new String[]{routeId.toString()}));
        }

        Message editPermissionMessage = this.validateEditPermission(routesEntity);
        if (editPermissionMessage != null) {
            throw new LogicWebException(editPermissionMessage);
        }

        Boolean oldStatus = routesEntity.getActive();
        Boolean newStatus = !oldStatus;
        routesEntity.setActive(newStatus);
        routesDao.update(routesEntity);
    }

    @Override
    public Long getLastRouteDetailIdByDevice(Long deviceId, RunningStatus runningStatus) {
        return routeDetailDao.getLastRouteDetailIdByDevice(deviceId, runningStatus);
    }

    private void insertRouteDetail(Long routeId, String routeDetailsStr) throws CommonException {
        List<RouteDetailEntity> routeDetails = routeDetailService.createRouteDetailList(routeDetailsStr);
        //validate customer
        this.validateCustomerOfRouteDetail(routeDetails.stream().map(rd -> rd.getCustomers().getId()).collect(Collectors.toList()));

        for (RouteDetailEntity routeDetail : routeDetails) {
            routeDetail.setRoutesId(routeId);
            routeDetailDao.insert(routeDetail);
        }
    }

    @Override
    public Message validateEditPermission(RoutesEntity routesEntity) {
        // check route is deleted
        if (routesEntity.getDeleteFlg()) {
            return messageResource.getWithParamKeys(Constant.ErrorCode.HAS_BEEN_DELETED, new String[] {"route.route"});
        }

        // check route is running or not. (REMEMBER to check null before use route RUNNING STATUS, because of OLD Route Data)
        if (routesEntity.getRunningStatus() != null && routesEntity.getRunningStatus().isRunning()) {
            return messageResource.get(Constant.ErrorCode.ROUTE_IS_RUNNING);
        }

        // check route is finished or not. (REMEMBER to check null before use route RUNNING STATUS, because of OLD Route Data)
        if (routesEntity.getRunningStatus() != null && routesEntity.getRunningStatus().isFinished()) {
            return messageResource.get(Constant.ErrorCode.ROUTE_FINISH);
        }

        // check route is changed or not.
        if (routesEntity.getRunningStatus() != null && routesEntity.getRunningStatus().isChanged()) {
            return messageResource.get(Constant.ErrorCode.ROUTE_HAS_BEEN_CHANGED);
        }
        return null;
    }

    /**
     * validate customer of route detail (inactive...)
     * @param customersId list of customer id
     * @throws CommonException
     */
    private void validateCustomerOfRouteDetail(List<Long> customersId) throws CommonException {
        CustomersEntity inActiveCustomer = customersService.findInActive(customersId);
        if (inActiveCustomer != null) {
            throw new CommonException(messageResource.getMessage(Constant.ErrorCode.CUS_INACTIVE, new Object[]{inActiveCustomer.getName()}));
        }
    }

    @Override
    public void setDeleteAndChangeStatusPermission(RouteDTO routeDTO) throws CommonException {
        // if route is finished. (REMEMBER to check null before use route RUNNING STATUS, because of OLD Route Data)
        if (routeDTO.getRunningStatus() != null && routeDTO.getRunningStatus().isFinished()) {
            routeDTO.setChangeStatusPermission(false);
            return;
        }
        // if route is running
        if (routeDTO.getRunningStatus() != null && routeDTO.getRunningStatus().isRunning()) {
            routeDTO.setChangeStatusPermission(false);
            routeDTO.setDeletePermission(false);
            return;
        }

        // check route has been used
        if (routesDao.countHistoryRouteActualByRoutePlanId(routeDTO.getRouteId(), null) > 0) {
            routeDTO.setDeletePermission(false);
        }
    }

    @Override
    public void setDeleteAndChangeStatusPermission(List<RouteDTO> routeDTO) {
        routeDTO.forEach(route -> {
            try {
                this.setDeleteAndChangeStatusPermission(route);
            } catch (CommonException e) {
                logger.error("ERROR", e);
            }
        });
    }

    @Override
    public List<Long> getRoutePlanIdByCreator(Long userId) {
        return routesDao.getRoutePlanByCreator(userId).stream().map(route -> route.getId()).collect(Collectors.toList());
    }
}
