package jp.co.willwave.aca.service;

import jp.co.willwave.aca.common.LogicException;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.*;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.web.form.RouteCustomerDetailForm;
import jp.co.willwave.aca.web.form.SearchRouteForm;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

public interface RoutesService {
    RoutesDTO getRouteByTime(Long devicesId) throws Exception;

    List<RouteMapDTO> getListRouteDetailMap(Long devicesId) throws IOException, CommonException, LogicException;

    Boolean startRoute(Long routeUpdateTime) throws Exception;

    List<RoutesDTO> getAllRouteActual(Long devicesId) throws CommonException;

    List<RouteActualDTO> getRouteActualByPage(Long devicesId, Long page, Long numberRecord) throws CommonException;

    List<RouteActualDTO> getRouteActualByMessage(String keyword) throws CommonException;

    RoutesDTO getRouteDetailByRouteId(Long routeId) throws Exception;

    RoutesDTO findRouteDetailByRouteId(Long routeId) throws Exception;

    List<RouteDTO> getRouteList(Long userId, Integer offset, Integer maxResults, Boolean noPaging);

    List<RouteDTO> searchPlanRouteList(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;
    Long countPlanRoutes(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;

    List<Message> validateDateTimeForm(SearchRouteForm searchRouteForm);

    List<Message> deleteRoute(Long routeId, Long userLoginId) throws CommonException;

    List<Message> validateViewRouteDetail(Long routeId, Long userId);

    RouteDTO getRouteInfo(Long routeId);

    List<RouteCustomerDetailDTO> getRouteCustomerDetail(Long routeId, Long userId, RouteCustomerDetailForm routeCustomerDetailForm);

    RoutesEntity findById(Long routeId);

    RoutesDTO getRouteActualDetail(Long devicesId) throws Exception;

    void finishedRoute(Long devicesId) throws Exception;

    List<Message> createRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException;

    List<Message> updateRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException;

    List<Message> updateRunningRoute(RoutesEntity route, String routeDetails, String detailMaps) throws CommonException;

    RoutesEntity findRouteActualByDevices(Long devicesId) throws CommonException;

    void changeStatusRoute(Long id) throws Exception;

    Long getLastRouteDetailIdByDevice(Long deviceId, RunningStatus runningStatus);

    /**
     * return error message if route is running, finished, changed or deleted
     * @param routesEntity route
     * @return message
     */
    Message validateEditPermission(RoutesEntity routesEntity);
    /**
     * set delete and change status permission, used to disable the buttons on the client
     * @param routeDTO route dto
     * @throws CommonException
     */
    void setDeleteAndChangeStatusPermission(RouteDTO routeDTO) throws CommonException;

    /**
     * set delete and change status permission, used to disable the buttons on the client
     * @param routeDTO routes dto
     * @throws CommonException
     */
    void setDeleteAndChangeStatusPermission(List<RouteDTO> routeDTO);

    /**
     * get list of route plan id by creator
     * @param userId user id (the creator)
     * @return list route plan id
     */
    List<Long> getRoutePlanIdByCreator(Long userId);
}
