package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.RouteDetailDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportScreenDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.web.form.SearchExportDailyForm;

import java.text.ParseException;
import java.util.List;

public interface RouteDetailDao extends BaseDao<RouteDetailEntity> {
    List<RouteDetailDTO> getRouteDetailInfoByRouteId(Long planedRoutesId) throws CommonException;

    List<RouteDetailEntity> getRouteDetailByRouteId(Long routeId) throws CommonException;

    RouteDetailEntity findRouteDetailActualLastFinishByRouteId(Long routeActualId) throws CommonException;

    RouteDetailEntity findRouteDetailByRoutesIdAndVisitOrder(Long planedRoutesId, Long visitOrder) throws CommonException;

    List<Long> getRouteIdsByIds(List<Long> routeDetailIds) throws CommonException;

    List<RouteDetailEntity> findByRouteId(Long routeId);

    List<Object[]> getActualRouteDetail(Long deviceId, RunningStatus runningStatus);

    List<Object[]> getPlanRouteDetail(Long routeId, RunningStatus runningStatus);

    Long getLastRouteDetailIdByDevice(Long deviceId, RunningStatus runningStatus);

    /**
     * search data of daily report for displayed on the screen<br/>
     * if login user is operator, then get data relate to routes which create by login user
     * @param searchExportDailyForm search form
     * @param offset
     * @param maxResults
     * @param noPaging if true then get all
     * @param divisionIdList division manager by login user
     * @return
     * @throws ParseException
     */
    List<RouteForDailyReportScreenDTO> searchDailyReportForScreen(SearchExportDailyForm searchExportDailyForm, Integer offset, Integer maxResults, Boolean noPaging, List<Long> divisionIdList) throws ParseException;

    /**
     * search data of daily report for download
     * @param searchExportDailyForm
     * @param divisionIdList
     * @return
     * @throws ParseException
     */
    List<RouteForDailyReportDTO> searchDailyReportForDownload(SearchExportDailyForm searchExportDailyForm, List<Long> divisionIdList) throws ParseException;

    Long getMaxVisitedOrderByRouteId(Long routeId);

    void deleteByRouteId(Long routeId);
    /**
     * count active route detail by customer id
     * @param customerId
     * @return
     */
    Long countByCustomerId(Long customerId);
}
