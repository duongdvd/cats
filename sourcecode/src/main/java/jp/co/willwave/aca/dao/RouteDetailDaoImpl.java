package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.RouteDetailDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportScreenDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.*;
import jp.co.willwave.aca.web.form.SearchExportDailyForm;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.internal.OrderImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;

@Repository
public class RouteDetailDaoImpl extends BaseDaoImpl<RouteDetailEntity> implements RouteDetailDao {
    private static Logger logger = Logger.getLogger(RouteDetailDao.class);

    public RouteDetailDaoImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Autowired
    private WebUtil webUtil;

    @Override
    public List<RouteDetailDTO> getRouteDetailInfoByRouteId(Long planedRoutesId) throws CommonException {
        logger.info("RouteDetailDao.getRouteDetailInfoByRouteId");
        CriteriaBuilder builder = sessionFactory.getCurrentSession().getCriteriaBuilder();
        CriteriaQuery<RouteDetailEntity> criteria = builder.createQuery(RouteDetailEntity.class);
        Root<RouteDetailEntity> root = criteria.from(RouteDetailEntity.class);
        criteria.select(root)
                .where(builder.equal(root.get("routesId"), planedRoutesId))
                .orderBy(new OrderImpl(root.get("visitOrder")));
        List<RouteDetailEntity> routeDetails = sessionFactory.getCurrentSession().createQuery(criteria).getResultList();
        List<RouteDetailDTO> routeDetailDTOs = new ArrayList<>();
        if (!CollectionUtils.isEmpty(routeDetails)) {
            routeDetails.forEach(routeDetail -> {
                RouteDetailDTO routeDetailDTO = new RouteDetailDTO();
                routeDetailDTO.setId(routeDetail.getId());
                routeDetailDTO.setRoutesId(routeDetail.getRoutesId());
                routeDetailDTO.setVisitOrder(routeDetail.getVisitOrder());
                routeDetailDTO.setArrivalTime(routeDetail.getArrivalTime());
                routeDetailDTO.setArrivalNote(routeDetail.getArrivalNote());
                routeDetailDTO.setReDepartTime(routeDetail.getReDepartTime());
                routeDetailDTO.setCustomersId(routeDetail.getCustomers().getId());
                routeDetailDTO.setName(routeDetail.getCustomers().getName());
                routeDetailDTO.setAddress(routeDetail.getCustomers().getAddress());
                routeDetailDTO.setLongtitude(routeDetail.getCustomers().getLongitude());
                routeDetailDTO.setLatitude(routeDetail.getCustomers().getLatitude());
                routeDetailDTO.setDescription(routeDetail.getCustomers().getDescription());
                routeDetailDTO.setBuildingName(routeDetail.getCustomers().getBuildingName());
                routeDetailDTO.setIconMarker(webUtil.getServerBaseUrl(), routeDetail.getCustomers().getIconMarker());
                routeDetailDTO.setCustomerType(routeDetail.getCustomers().getCustomerType());
                routeDetailDTOs.add(routeDetailDTO);
            });
        }
        return routeDetailDTOs;
    }


    @Override
    public List<RouteDetailEntity> getRouteDetailByRouteId(Long routeId) throws CommonException {
        logger.info("RouteDetailDao.getRouteDetailIdsByRouteId");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RouteDetailEntity> criteriaQuery = criteriaBuilder.createQuery(RouteDetailEntity.class);
        Root<RouteDetailEntity> root = criteriaQuery.from(RouteDetailEntity.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("routesId"), routeId))
                .orderBy(criteriaBuilder.asc(root.get("visitOrder")));
        return sessionFactory.getCurrentSession()
                .createQuery(criteriaQuery)
                .getResultList();
    }

    @Override
    public RouteDetailEntity findRouteDetailActualLastFinishByRouteId(Long routeActualId) throws CommonException {
        logger.info("RouteDetailDao.findRouteDetailActualLastFinishByRouteId");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RouteDetailEntity> criteriaQuery = criteriaBuilder.createQuery(RouteDetailEntity.class);
        Root<RouteDetailEntity> root = criteriaQuery.from(RouteDetailEntity.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.equal(root.get("routesId"), routeActualId))
                .orderBy(new OrderImpl(root.get("visitOrder"), false));
        List<RouteDetailEntity> routeDetailEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .setMaxResults(1)
                .getResultList();
        if (CollectionUtils.isEmpty(routeDetailEntities)) {
            return null;
        }
        return routeDetailEntities.get(0);
    }

    @Override
    public RouteDetailEntity findRouteDetailByRoutesIdAndVisitOrder(Long routesId, Long visitOrder) throws
            CommonException {
        logger.info("RouteDetailDao.findRouteDetailByRoutesIdAndVisitOrder");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RouteDetailEntity> criteriaQuery = criteriaBuilder.createQuery(RouteDetailEntity.class);
        Root<RouteDetailEntity> root = criteriaQuery.from(RouteDetailEntity.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.and(criteriaBuilder.equal(root.get("routesId"), routesId),
                        criteriaBuilder.equal(root.get("visitOrder"), visitOrder)));
        List<RouteDetailEntity> routeDetailEntities = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(routeDetailEntities)) {
            return null;
        }
        return routeDetailEntities.get(0);
    }

    @Override
    public List<Long> getRouteIdsByIds(List<Long> routeDetailIds) throws CommonException {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT(routesId) FROM RouteDetailEntity ");
        sql.append("WHERE id IN :routeDetailIds ");
        return sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), Long.class)
                .setParameter("routeDetailIds", routeDetailIds)
                .getResultList();
    }


    @Override
    public List<RouteDetailEntity> findByRouteId(Long routeId) {
        return sessionFactory.getCurrentSession()
                .createQuery("FROM RouteDetailEntity WHERE routesId = :routesId ORDER BY visitOrder",
                        RouteDetailEntity.class)
                .setParameter("routesId", routeId)
                .getResultList();
    }

    @Override
    public List<Object[]> getActualRouteDetail(Long deviceId, RunningStatus runningStatus) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT ract, rd, c ");
        sql.append(" FROM RoutesEntity ract");
        sql.append("     INNER JOIN RoutesEntity rplan");
        sql.append("         ON ract.planedRoutesId = rplan.id");
        sql.append("         AND rplan.runningStatus = :runningStatus");
        sql.append("     INNER JOIN RouteDetailEntity rd");
        sql.append("        ON ract.id = rd.routesId");
        sql.append("     INNER JOIN CustomersEntity c");
        sql.append("        ON rd.customers.id = c.id");
        sql.append(" WHERE rplan.devicesId = :deviceId");

        Query query = sessionFactory.getCurrentSession().createQuery(sql.toString())
            .setParameter("deviceId", deviceId)
            .setParameter("runningStatus", runningStatus);

        return query.getResultList();
    }

    @Override
    public List<Object[]> getPlanRouteDetail(Long routeId, RunningStatus runningStatus) {
        String getColumns = "r, rd, c";

        StringBuilder sql = new StringBuilder("SELECT ")
            .append(getColumns)
            .append(" FROM RoutesEntity r, RouteDetailEntity rd , CustomersEntity c")
            .append(" WHERE")
            .append(" r.id = :routeId");
        if (runningStatus != null) {
            sql.append(" AND r.runningStatus = :runningStatus");
        }

        sql.append(" AND rd.routesId = r.id ")
            .append(" AND c.id = rd.customers.id " )
            .append(" ORDER BY rd.visitOrder ASC");
        Query query = sessionFactory.getCurrentSession()
            .createQuery(sql.toString()).setParameter("routeId", routeId);

        if (runningStatus != null) {
            query = query.setParameter("runningStatus", runningStatus);
        }

        return query.getResultList();
    }

    @Override
    public Long getLastRouteDetailIdByDevice(Long deviceId, RunningStatus runningStatus) {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT max(rd.id) ");
        sql.append(" FROM RoutesEntity ract");
        sql.append("     INNER JOIN RoutesEntity rplan");
        sql.append("         ON ract.planedRoutesId = rplan.id");
        sql.append("         AND rplan.runningStatus = :runningStatus");
        sql.append("     INNER JOIN RouteDetailEntity rd");
        sql.append("        ON ract.id = rd.routesId");
        sql.append(" WHERE rplan.devicesId = :deviceId");

        Query<Long> query = sessionFactory.getCurrentSession().createQuery(sql.toString(), Long.class)
            .setParameter("deviceId", deviceId)
            .setParameter("runningStatus", runningStatus);

        return query.getSingleResult();
    }

    public List<RouteForDailyReportScreenDTO> searchDailyReportForScreen(SearchExportDailyForm searchExportDailyForm, Integer offset, Integer maxResults, Boolean noPaging, List<Long> divisionIdList) throws ParseException {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT d.id divisionId, d.name divisionName, routeActual.actual_date," +
                    // start garage name
                    "(SELECT cus.name FROM customers cus INNER JOIN route_detail rd ON(cus.id = rd.customers_id) WHERE rd.routes_id = routePlan.id AND rd.visit_order = 0) startGarageName," +
                    // end garage nanme
                    "(SELECT cus.name FROM customers cus INNER JOIN route_detail rd ON(cus.id = rd.customers_id) WHERE rd.routes_id = routePlan.id AND rd.visit_order = (SELECT MAX(visit_order) FROM route_detail WHERE routes_id = routePlan.id)) endGarageName," +
                    "c.driver_name , de.login_id, c.plate_number, routeActual.id routeId\n" +
                "FROM divisions d, users u, cars c, routes routeActual, routes routePlan, devices de\n" +
                "WHERE\n" +
                    "d.id = u.division_id\n" +
                    "AND\n" +
                    "u.role_id = :roleId\n" +
                    "AND\n" +
                    "routePlan.create_by = u.id\n" +
                    "AND\n" +
                    "routeActual.planed_routes_id = routePlan.id\n" +
                    "AND\n" +
                    "c.id = routeActual.cars_id\n" +
                    "AND\n" +
                    "de.id = c.device_id\n" +
                    "AND\n" +
                    "routeActual.actual_date IS NOT NULL\n" +
                    "AND\n");

        String orderBy = "ORDER BY d.id, routeActual.actual_date, de.login_id\n";
        Query query = this.buildExportQueryBySearchForm(sb, orderBy, searchExportDailyForm, offset, maxResults, noPaging, divisionIdList);
        List<Object[]> resultList = query.getResultList();

        List<RouteForDailyReportScreenDTO> dtoList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(resultList)) {
            for (Object[] result : resultList) {
                RouteForDailyReportScreenDTO dto = new RouteForDailyReportScreenDTO();
                dto.setDivisionId(CommonUtil.bigIntegerToLong(result[0]));
                dto.setDivisionName(CatStringUtil.stringValue(result[1]));
                dto.setActualDate(DateUtil.castDate(result[2]));
                dto.setCustomerName(CatStringUtil.stringValue(result[3]));
                dto.setEndName(CatStringUtil.stringValue(result[4]));
                dto.setDriverName(CatStringUtil.stringValue(result[5]));
                dto.setLoginId(CatStringUtil.stringValue(result[6]));
                dto.setPlateNumber(CatStringUtil.stringValue(result[7]));
                dto.setRouteId(CommonUtil.bigIntegerToLong(result[8]));

                dtoList.add(dto);
            }
        }

        return dtoList;
    }


    public List<RouteForDailyReportDTO> searchDailyReportForDownload(SearchExportDailyForm searchForm, List<Long> divisionIdList) throws ParseException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT routeActual.id, d.name divisionName, routeActual.name routeName, c.driver_name, c.plate_number," +
                    // start garage name
                    "(SELECT cus.name FROM customers cus INNER JOIN route_detail rd ON(cus.id = rd.customers_id) WHERE rd.routes_id = routePlan.id AND rd.visit_order = 0) startGarageName," +
                    // end garage nanme
                    "(SELECT cus.name FROM customers cus INNER JOIN route_detail rd ON(cus.id = rd.customers_id) WHERE rd.routes_id = routePlan.id AND rd.visit_order = (SELECT MAX(visit_order) FROM route_detail WHERE routes_id = routePlan.id)) endGarageName," +

                    "routeActual.actual_date, routeActual.finished_time, " +

                    "cu.name, rd.`re-depart_time`, rd.arrival_time\n" +
                "FROM divisions d, users u, cars c, routes routeActual, " +
                    "routes routePlan, route_detail rd, customers cu, devices de\n\n" +
                "WHERE\n" +
                    "d.id = u.division_id\n" +
                    "AND\n" +
                    "u.role_id = :roleId\n" +
                    "AND\n" +
                    "routePlan.create_by = u.id\n" +
                    "AND\n" +
                    "routeActual.planed_routes_id = routePlan.id\n" +
                    "AND\n" +
                    "routeActual.actual_date IS NOT NULL\n" +
                    "AND\n" +
                    "c.id = routeActual.cars_id\n" +
                    "AND\n" +
                    "de.id = c.device_id\n" +
                    "AND\n" +
                    "routeActual.id = rd.routes_id\n" +
                    "AND\n" +
                    "cu.id = rd.customers_id\n" +
                    "AND\n");
        // add conditions only export the selected route on the screen
        if (!CollectionUtils.isEmpty(searchForm.getSelectedRouteIds())) {
            sql.append("routeActual.id in :selectRoute AND ");
        }
        String orderBy = "ORDER BY d.id, routeActual.actual_date, routeActual.id, rd.visit_order ASC\n";

        Query query = this.buildExportQueryBySearchForm(sql, orderBy, searchForm, null, null, true, divisionIdList);
        // add conditions only export the selected route on the screen
        if (!CollectionUtils.isEmpty(searchForm.getSelectedRouteIds())) {
            query.setParameter("selectRoute", searchForm.getSelectedRouteIds());
        }

        List<Object[]> resultList = query.getResultList();
        List<RouteForDailyReportDTO> exportDailyDTOList = new ArrayList<>();

        // use for group by route plan id
        Long routePlanId = -1L;
        RouteForDailyReportDTO dto = null;
        RouteForDailyReportDTO.RouteDetailRowDTO prevRouteDetail = null;
        if (!CollectionUtils.isEmpty(resultList)) {
            for (Object[] objects : resultList) {
                // when route id is changed, create a new route DTO
                if (!routePlanId.equals(CommonUtil.bigIntegerToLong(objects[0]))) {
                    routePlanId           = CommonUtil.bigIntegerToLong(objects[0]);
                    String divisionName   = CatStringUtil.stringValue(objects[1]);
                    String routePlanName  = CatStringUtil.stringValue(objects[2]);
                    String driverName     = CatStringUtil.stringValue(objects[3]);
                    String plateNumber    = CatStringUtil.stringValue(objects[4]);
                    String planStartPlace = CatStringUtil.stringValue(objects[5]);
                    String planEndPlace   = CatStringUtil.stringValue(objects[6]);
                    Date actualDate       = DateUtil.castDate(objects[7]);
                    Date finishedDate     = DateUtil.castDate(objects[8]);

                    // create a new route DTO
                    dto = new RouteForDailyReportDTO();
                    dto.setRouteId(routePlanId);
                    dto.setDivisionName(divisionName);
                    dto.setRouteName(routePlanName);
                    dto.setActualDriverName(driverName);
                    dto.setActualPlateNumber(plateNumber);
                    dto.setPlanStartPlace(planStartPlace);
                    dto.setPlanEndPlace(planEndPlace);
                    dto.setActualStartDateTime(actualDate);
                    dto.setActualEndDateTime(finishedDate);
                    dto.setRouteDetails(new ArrayList<>());

                    exportDailyDTOList.add(dto);
                    // reset first line, when route id is changed
                    prevRouteDetail = null;
                }
                // get customer info (place info)
                String place = CatStringUtil.stringValue(objects[9]);
                Date startDate = DateUtil.castDate(objects[10]);
                Date endDate = DateUtil.castDate(objects[11]);

                // create first RouteDetailRowDTO
                if (prevRouteDetail == null) {
                    prevRouteDetail = new RouteForDailyReportDTO.RouteDetailRowDTO();
                    prevRouteDetail.setStartPlace(place);
                    prevRouteDetail.setStartDateTime(startDate);
                    dto.getRouteDetails().add(prevRouteDetail);
                    continue;
                }
                // set end info for prev RouteDetailRowDTO
                prevRouteDetail.setEndPlace(place);
                prevRouteDetail.setEndDateTime(endDate);

                // when re-depart date is not null, then create next RouteDetailRowDTO with start date is the re-depart date.
                // IMPORTANT: be careful when route is finished, re-depart date of the last RouteDetail was not null
                // so it still create a RouteDetailRowDTO with startDate is re-depart date of the last RouteDetail,
                // so you need to remove that RouteDetailRowDTO
                if (startDate != null) {
                    RouteForDailyReportDTO.RouteDetailRowDTO nextRouteDetail = new RouteForDailyReportDTO.RouteDetailRowDTO();
                    nextRouteDetail.setStartPlace(place);
                    nextRouteDetail.setStartDateTime(startDate);
                    //
                    prevRouteDetail = nextRouteDetail;
                    dto.getRouteDetails().add(prevRouteDetail);
                }
            }

            // IMPORTANT: if route is finished, remove the last RouteDetailRowDTO
            exportDailyDTOList.forEach(dtoTemp -> {
                if (dtoTemp != null && dtoTemp.getActualEndDateTime() != null) {
                    if (!CollectionUtils.isEmpty(dtoTemp.getRouteDetails())) {
                        dtoTemp.getRouteDetails().remove(dtoTemp.getRouteDetails().size() - 1);
                    }
                }
            });
        }

        return exportDailyDTOList;
    }

    public Query buildExportQueryBySearchForm(StringBuilder sql, String orderBy,
                                              SearchExportDailyForm searchExportDailyForm,
                                              Integer offset, Integer maxResults,
                                              Boolean noPaging, List<Long> divisionIdList) throws ParseException {
        UserInfo loginUser = SessionUtil.getLoginUser();
        // if login user is operator then get routes create by login user
        if (loginUser.isOperator()) {
            sql.append("routePlan.create_by = :loginUserId\n");
        } else {
            sql.append("d.id in :divisionId\n");
        }

        List<Long> divisionId = new ArrayList<>();
        Date actualDate = null;
        String driverName = null;
        String plateNumber = null;
        String startPoint = null;
        String endPoint = null;
        String loginId = null;

        if (searchExportDailyForm == null) {
            divisionId = divisionIdList;
        } else {
            if (searchExportDailyForm.getDivisionId() == null) {
                divisionId = divisionIdList;
            } else {
                divisionId.add(searchExportDailyForm.getDivisionId());
            }

            if (searchExportDailyForm.getActualDate() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getActualDate().trim())) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    Date date = dateFormat.parse(searchExportDailyForm.getActualDate().trim());
                    actualDate = DateUtils.truncate(date, java.util.Calendar.DAY_OF_MONTH);
                    sql.append("AND\n" +
                            "DATE(routeActual.actual_date) = :actualDate\n");
                }
            }
            if (searchExportDailyForm.getDriverName() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getDriverName().trim().trim())) {
                    driverName = searchExportDailyForm.getDriverName().trim();
                    sql.append("AND\n" +
                            "c.driver_name LIKE :driverName \n");
                }
            }
            if (searchExportDailyForm.getPlateNumber() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getPlateNumber().trim())) {
                    plateNumber = searchExportDailyForm.getPlateNumber().trim();
                    sql.append("AND\n" +
                            "c.plate_number LIKE :plateNumber \n");
                }
            }
            if (searchExportDailyForm.getLoginId() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getLoginId().trim())) {
                    loginId = searchExportDailyForm.getLoginId().trim();
                    sql.append("AND\n" +
                            "de.login_id LIKE :loginId \n");
                }
            }

            if (searchExportDailyForm.getStartPoint() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getStartPoint().trim())) {
                    startPoint = searchExportDailyForm.getStartPoint().trim();
                    StringBuilder queryStartpoint = new StringBuilder();
                    queryStartpoint.append("AND\n" +
                            "EXISTS (SELECT 1 FROM route_detail rdStart, customers cStart\n" +
                            "WHERE rdStart.customers_id = cStart.id\n" +
                            "AND routePlan.id = rdStart.routes_id\n" +
                            "AND cStart.name LIKE :startPoint\n" +
                            "AND rdStart.visit_order = 0\n)");
                    sql.append(queryStartpoint.toString());
                }
            }

            if (searchExportDailyForm.getEndPoint() != null) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(searchExportDailyForm.getEndPoint().trim())) {
                    endPoint = searchExportDailyForm.getEndPoint().trim();
                    StringBuilder queryEndPoint = new StringBuilder();
                    queryEndPoint.append("AND\n" +
                            "EXISTS (SELECT 1 FROM route_detail rdEnd, customers cEnd\n" +
                            "WHERE rdEnd.customers_id = cEnd.id\n" +
                            "AND routePlan.id = rdEnd.routes_id\n" +
                            "AND cEnd.name LIKE :endPoint\n" +
                            "AND rdEnd.visit_order = (SELECT MAX(visit_order) FROM route_detail WHERE routes_id = rdEnd.routes_id))\n");
                    sql.append(queryEndPoint.toString());
                }
            }
        }
        sql.append(orderBy);
        Query query = sessionFactory.getCurrentSession()
                .createNativeQuery(sql.toString()).setParameter("roleId", UserRole.OPERATOR.getRole());

        // check if login user is operator
        if (loginUser.isOperator()) {
            query.setParameter("loginUserId", loginUser.getId());
        } else {
            query.setParameter("divisionId", divisionId);
        }

        if (actualDate != null) {
            query.setParameter("actualDate", actualDate);
        }
        if (driverName != null) {
            query.setParameter("driverName", "%" + driverName + "%");
        }
        if (plateNumber != null) {
            query.setParameter("plateNumber", "%" + plateNumber + "%");
        }
        if (startPoint != null) {
            query.setParameter("startPoint", "%" + startPoint + "%");

        }
        if (endPoint != null) {
            query.setParameter("endPoint", "%" + endPoint + "%");
        }

        if (loginId != null) {
            query.setParameter("loginId", loginId);
        }

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }
        return query;
    }

    /**
     *  This method to get max visited order of route.
     *  In case route not yet started => result is NULL.
     *  In case route is running => result is max visit order in all the visited customers.
     *  In case route is already finished => result equal to visit order of end garage.
     *
     * @param routePlanId is route plan
     * @return max visit order.
     */
    @Override
    public Long getMaxVisitedOrderByRouteId(Long routePlanId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT MAX(rd.visitOrder)" +
                        " FROM RouteDetailEntity rd INNER JOIN RoutesEntity r ON" +
                        " rd.routesId = r.id " +
                        " WHERE r.planedRoutesId = :routeId ", Long.class)
                .setParameter("routeId", routePlanId).getSingleResult();
    }

    @Override
    public void deleteByRouteId(Long routeId) {
        sessionFactory.getCurrentSession()
                .createQuery("UPDATE RouteDetailEntity set deleteFlg = true WHERE routesId = :routeId")
                .setParameter("routeId", routeId)
                .executeUpdate();
    }

    @Override
    public Long countByCustomerId(Long customerId) {
        return sessionFactory.getCurrentSession()
                .createQuery("SELECT COUNT(*) FROM RouteDetailEntity rd WHERE deleteFlg = false AND customers.id = :customerId", Long.class)
                .setParameter("customerId", customerId)
                .getSingleResult();
    }
}
