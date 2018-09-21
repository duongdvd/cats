package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.dto.api.RouteCustomerDetailDTO;
import jp.co.willwave.aca.dto.api.RouteDTO;
import jp.co.willwave.aca.dto.report.DeviceForMonthlyReportDTO;
import jp.co.willwave.aca.dto.report.RouteForMonthlyReportScreenDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.web.form.RouteCustomerDetailForm;
import jp.co.willwave.aca.web.form.SearchRouteForm;
import jp.co.willwave.aca.web.form.report.ReportFormSearch;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface RoutesDao extends BaseDao<RoutesEntity> {
    @Deprecated
    RoutesEntity getRouteActualByDevicesIdAndTodayAndActive(Long devicesId, Date today, boolean active) throws CommonException;

    /**
     * get route plan by device (running)
     * @param devicesId device id
     * @return Route entity
     * @throws CommonException
     */
    RoutesEntity getRoutePlanRunningByDevicesId(Long devicesId) throws CommonException;

    /**
     * get route plan by device (running)
     * @param devicesId device id
     * @return Route entity
     * @throws CommonException
     */
    RoutesEntity getRouteActualRunningByDevicesId(Long devicesId) throws CommonException;

    @Deprecated
    RoutesEntity getRoutePlanedByDevicesIdAndTodayAndActive(Long devicesId, Date today, boolean active) throws CommonException;

    /**
     * get
     * @param devicesId
     * @param today
     * @param active
     * @return
     * @throws CommonException
     */
    RoutesEntity getRoutePlanedWillBeRunningByDevicesId(Long devicesId) throws CommonException;

    List<RoutesEntity> getRouteActualByDevicesIdAndActive(Long devicesId, boolean active) throws CommonException;

    List<RoutesEntity> findByRouteActualByDevicesId(Long devicesId, Long page, Long numberRecord) throws CommonException;

    List<RoutesEntity> getRouteByIds(List<Long> routeIds) throws CommonException;

    List<RoutesEntity> findByDeviceId(Long devicesId);

    /**
     * get routes create by the param userId
     * @param userId user id
     * @param offset
     * @param maxResults
     * @param count
     * @return routes create by the param userId
     */
    List<RouteDTO> getRouteList(Long userId, Integer offset, Integer maxResults, Boolean noPaging);

    /**
     * search routes using {@link SearchRouteForm} condition and create by the param userId
     * @param userId user id
     * @param offset
     * @param maxResults
     * @param havePaging
     * @return result search
     */
    List<RouteDTO> searchPlanRouteList(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;

    Long countPlanRoutes(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException;

    RoutesEntity findByUserIdAndRouteId(Long routeId, Long userId);

    List<RouteForMonthlyReportScreenDTO> searchReportMonthForScreen(ReportFormSearch formSearch, Integer offset, Integer maxResult, Boolean noPaging);
    List<DeviceForMonthlyReportDTO> searchReportMonthForDownload(ReportFormSearch formSearch);

    Long countSearchReportMonth(ReportFormSearch formSearch);



    RouteDTO getRouteInfoInDetail(Long routeId);

    List<RouteCustomerDetailDTO> getRouteCustomerDetail(Long routeId, Long userId, RouteCustomerDetailForm routeCustomerDetailForm);

    RoutesEntity findByDeviceIDAndActualDate(Long deviceId, Date actualDate);

    RoutesEntity findByDeviceIdAndToday(Long devicesId, Date date);

    RoutesEntity getRouteActualRunningByDate(Long routeId, Date today, boolean active) throws CommonException;

    Long countHistoryRouteActualByRoutePlanId(Long routeId, Boolean active) throws CommonException;

    int deleteRouteDetailByRouteId(Long routeId) throws CommonException;

    List<RoutesEntity> searchMessageRoute(Long deviceId, String text);

    /**
     * get list of route plan by creator
     * @param userId user id (the creator)
     * @return list route plan
     */
    List<RoutesEntity> getRoutePlanByCreator(Long userId);

    public RoutesEntity getActualByPlan(Long planRouteId) throws CommonException;
}
