package jp.co.willwave.aca.dao;

import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.RouteCustomerDetailDTO;
import jp.co.willwave.aca.dto.api.RouteDTO;
import jp.co.willwave.aca.dto.report.DeviceForMonthlyReportDTO;
import jp.co.willwave.aca.dto.report.RouteForMonthlyReportScreenDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.CommonUtil;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.web.form.RouteCustomerDetailForm;
import jp.co.willwave.aca.web.form.SearchRouteForm;
import jp.co.willwave.aca.web.form.report.ReportFormSearch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;

@Repository
public class RoutesDaoImpl extends BaseDaoImpl<RoutesEntity> implements RoutesDao {

    private final Logger logger = Logger.getLogger(RoutesDaoImpl.class);
    private final RouteDetailDao routeDetailDao;

    public RoutesDaoImpl(SessionFactory sessionFactory, RouteDetailDao routeDetailDao) {
        super(sessionFactory);
        this.routeDetailDao = routeDetailDao;
    }
    @Override
    public RoutesEntity getRouteActualByDevicesIdAndTodayAndActive(Long devicesId, Date today, boolean active) {
        logger.info("RoutesDao.getRouteActualByDevicesIdAndTodayAndActive");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery("FROM RoutesEntity WHERE devicesId = :devicesId AND"
                        + " DATE(actualDate) = DATE(:today) AND active = :active ", RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("today", today)
                .setParameter("active", active)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public RoutesEntity getRoutePlanRunningByDevicesId(Long devicesId) throws CommonException {
        logger.info("RoutesDao.getRoutePlanRunningByDevicesId");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery(new StringBuilder("FROM RoutesEntity ")
                        .append("WHERE devicesId = :devicesId ")
                        .append("AND runningStatus = :runningStatus ")
                        .append("AND planedRoutesId IS NULL ")
                        .append("AND active = true ")
                        .append("AND deleteFlg = false").toString(), RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("runningStatus", RunningStatus.RUNNING)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public RoutesEntity getRouteActualRunningByDevicesId(Long devicesId) throws CommonException {
        logger.info("RoutesDao.getRouteActualRunningByDevicesId");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createNativeQuery(new StringBuilder("SELECT rActual.* FROM routes rPlan, routes rActual ")
                        .append("WHERE rActual.planed_routes_id = rPlan.id ")
                        .append("AND rPlan.devices_id = :devicesId ")
                        .append("AND rPlan.running_status = :runningStatus ")
                        .append("AND rPlan.delete_flg = 0 ")
                        .append("AND rPlan.active = 1 ")
                        .append("AND rActual.delete_flg = 0").toString(), RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("runningStatus", 1) //TODO use enum RunningStatus
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public RoutesEntity getRoutePlanedWillBeRunningByDevicesId(Long devicesId) throws CommonException {
        logger.info("RoutesDao.getRoutePlanedWillBeRunningByDevicesId");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery(new StringBuilder("FROM RoutesEntity ")
                        .append("WHERE devicesId = :devicesId ")
                        .append("AND runningStatus = :runningStatus ")
                        .append("AND planedRoutesId IS NULL ")
                        .append("AND active = true ")
                        .append("AND deleteFlg = false ")
                        .append("AND DATE(startDate) = DATE(CURDATE()) ")
                        .append("ORDER BY startDate ASC ").toString(), RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("runningStatus", RunningStatus.NOT_YET_STARTED)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public RoutesEntity getRoutePlanedByDevicesIdAndTodayAndActive(Long devicesId, Date today, boolean active) {
        logger.info("RoutesDao.getRoutePlanedByDevicesIdAndTodayAndActive");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery("FROM RoutesEntity WHERE devicesId = :devicesId "
                        + "AND DATE(startDate) <= DATE(:today) AND DATE(endDate) >= DATE(:today) AND actualDate "
                        + "IS NULL AND active = :active  ORDER BY updateDate DESC ", RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("today", today)
                .setParameter("active", active)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public List<RoutesEntity> getRouteActualByDevicesIdAndActive(Long devicesId, boolean active) throws CommonException {
        logger.info("RoutesDao.getRouteActualByDevicesIdAndActive");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RoutesEntity> criteriaQuery = criteriaBuilder.createQuery(RoutesEntity.class);
        Root<RoutesEntity> root = criteriaQuery.from(RoutesEntity.class);
        criteriaQuery.select(root)
                .where(criteriaBuilder.and(criteriaBuilder.equal(root.get("devicesId"), devicesId)),
                        criteriaBuilder
                                .isNotNull(root.get("actualDate")), criteriaBuilder.equal(root.get("active"), active));
        return sessionFactory.getCurrentSession().createQuery(criteriaQuery).getResultList();
    }

    @Override
    public RoutesEntity getActualByPlan(Long planRouteId) throws CommonException {
       return find("planedRoutesId", String.valueOf(planRouteId), RoutesEntity.class);
    }

    @Override
    public List<RoutesEntity> findByRouteActualByDevicesId(Long devicesId, Long page, Long numberRecord) throws
            CommonException {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM routes WHERE devices_id = :devicesId ");
        sqlBuilder.append("AND actual_date IS NOT NULL AND active = true ");
        sqlBuilder.append("ORDER BY actual_date DESC ");
        sqlBuilder.append("LIMIT :fromRecord OFFSET  :toRecord ");
        return sessionFactory.getCurrentSession()
                .createNativeQuery(sqlBuilder.toString(), RoutesEntity.class)
                .setParameter("devicesId", devicesId)
                .setParameter("fromRecord", page * numberRecord - 1)
                .setParameter("toRecord", numberRecord)
                .getResultList();
    }

    @Override
    public List<RoutesEntity> getRouteByIds(List<Long> routeIds) throws CommonException {
        StringBuilder sqlBuilder = new StringBuilder("FROM RoutesEntity WHERE id IN :ids ");
        return sessionFactory.getCurrentSession()
                .createQuery(sqlBuilder.toString(), RoutesEntity.class)
                .setParameter("ids", routeIds)
                .getResultList();
    }

    @Override
    public List<RoutesEntity> findByDeviceId(Long devicesId) {
        logger.info("RoutesDao.findByDeviceId");
        CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<RoutesEntity> criteriaQuery = criteriaBuilder.createQuery(RoutesEntity.class);
        Root<RoutesEntity> root = criteriaQuery.from(RoutesEntity.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("devicesId"), devicesId), criteriaBuilder.equal(root.get("deleteFlg"), 0));
        List<RoutesEntity> routesEntityList = sessionFactory.getCurrentSession().createQuery(criteriaQuery)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntityList)) {
            return null;
        }
        return routesEntityList;
    }

    @Override
    public List<RouteDTO> getRouteList(Long userId, Integer offset, Integer maxResults, Boolean noPaging) {
        logger.info("RoutesDao.getRouteList");
        StringBuilder sb = new StringBuilder();
        sb.append("Select r.id, r.name, c.plateNumber, r.startDate, r.endDate, r.active , r.routeMemo\n" +
                "from RoutesEntity r, CarsEntity c \n" +
                "where r.devicesId = c.devicesId\n" +
                "and c.latestFlg = 1\n" +
                "and c.deleteFlg = 0\n" +
                "and r.deleteFlg = 0\n" +
                "and r.actualDate is null\n" +
                "and r.createBy = :userId\n");
        Date specificDate = new Date();
        Date preSpecificDate = DateUtils.truncate(specificDate, java.util.Calendar.DAY_OF_MONTH);
        sb.append("and DATE(r.startDate) <= DATE(:preSpecificDate)\n");

        Date afterSpecificDate = DateUtils.truncate(specificDate, java.util.Calendar.DAY_OF_MONTH);
        sb.append("and DATE(r.endDate) >= DATE(:afterSpecificDate)\n");
        sb.append("ORDER BY r.id ASC\n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("userId", userId).setParameter("preSpecificDate", preSpecificDate).setParameter("afterSpecificDate", afterSpecificDate);

        if (!noPaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        List<Object[]> routeEntities = query.getResultList();

        List<RouteDTO> routeDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(routeEntities)) {
            for (Object[] object : routeEntities
                    ) {
                RouteDTO routeDTO = new RouteDTO();
                if (object[0] != null) {
                    routeDTO.setRouteId(Long.valueOf(object[0].toString()));
                }
                if (object[1] != null) {
                    routeDTO.setRouteName(String.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    routeDTO.setPlateNumber(String.valueOf(object[2].toString()));
                }
                if (object[3] != null) {
                    routeDTO.setStartDate((Date) object[3]);
                }
                if (object[4] != null) {
                    routeDTO.setEndDate((Date) object[4]);
                }
                if (object[5] != null) {
                    Boolean status = (Boolean) object[5];
                    if (status) {
                        routeDTO.setStatus(1);
                    } else {
                        routeDTO.setStatus(0);
                    }
                }
                if (object[6] != null) {
                    routeDTO.setRouteMemo(object[6].toString());
                }
                routeDTOList.add(routeDTO);
            }
        }
        return routeDTOList;
    }

    @Override
    public List<RouteDTO> searchPlanRouteList(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {
        logger.info("RoutesDao.searchPlanRouteList");

        String columnSelect = "Select rplan.id, rplan.name, c.plateNumber, rplan.startDate, rplan.endDate, rplan.active , rplan.routeMemo, rplan.runningStatus\n";
        Query<Object[]> query = buildSearchPlanRouteListQuery(columnSelect, userId, searchRouteForm, offset, maxResults, havePaging, Object[].class);
        List<Object[]> routeEntities = query.getResultList();

        List<RouteDTO> routeDTOList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(routeEntities)) {
            for (Object[] object : routeEntities) {
                RouteDTO routeDTO = new RouteDTO();
                if (object[0] != null) {
                    routeDTO.setRouteId(Long.valueOf(object[0].toString()));
                }
                if (object[1] != null) {
                    routeDTO.setRouteName(String.valueOf(object[1].toString()));
                }
                if (object[2] != null) {
                    routeDTO.setPlateNumber(String.valueOf(object[2].toString()));
                }
                if (object[3] != null) {
                    routeDTO.setStartDate((Date) object[3]);
                }
                if (object[4] != null) {
                    routeDTO.setEndDate((Date) object[4]);
                }
                if (object[5] != null) {
                    Boolean status = (Boolean) object[5];
                    if (status) {
                        routeDTO.setStatus(1);
                    } else {
                        routeDTO.setStatus(0);
                    }
                }
                if (object[6] != null) {
                    routeDTO.setRouteMemo(object[6].toString());
                }

                if (object[7] != null) {
                    routeDTO.setRunningStatus((RunningStatus) object[7]);
                }

                routeDTOList.add(routeDTO);
            }
        }
        return routeDTOList;
    }

    @Override
    public Long countPlanRoutes(Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging) throws ParseException {
        String columnSelect = "SELECT COUNT(rplan.id) ";
        Query<Long> query = buildSearchPlanRouteListQuery(columnSelect, userId, searchRouteForm, offset, maxResults, havePaging, Long.class);
        return query.getSingleResult();
    }

    private <E> Query<E> buildSearchPlanRouteListQuery(String columnSelect, Long userId, SearchRouteForm searchRouteForm, Integer offset, Integer maxResults, Boolean havePaging, Class<E> tClass) throws ParseException {
        StringBuilder sb = new StringBuilder();
        sb.append(columnSelect);
        sb.append(" FROM RoutesEntity rplan");
        sb.append("   INNER JOIN CarsEntity c");
        sb.append("       ON rplan.devicesId = c.devicesId");
        sb.append("       AND c.latestFlg = 1");
        sb.append("       AND c.deleteFlg = 0");

        Date inputDate = null;
        String strInputDate = searchRouteForm.getSpecificDate();

        // In case have search by operation date.
        if (StringUtils.isNotBlank(strInputDate)) {
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
            inputDate = sdf.parse(strInputDate);

            // Get current date.
            Date currentDate = sdf.parse(sdf.format(new Date()));

            // In case date input is future date.
            if (currentDate.before(inputDate)) {
                sb.append("   AND DATE(rplan.startDate) <= DATE(:inputDate)");
                sb.append("   AND DATE(rplan.endDate) >= DATE(:inputDate)");

            } else if (currentDate.equals(inputDate)) {
                // In case date input is current date.
                // If route not yet started.
                sb.append("   AND (");
                sb.append("         (");
                sb.append("             DATE(rplan.startDate) = DATE(:inputDate)");
                sb.append("             AND DATE(rplan.runningStatus) = ").append(RunningStatus.NOT_YET_STARTED.ordinal());
                sb.append("         )");
                sb.append("         OR");

                // In case route is still running.
                sb.append("         (");
                sb.append("             DATE(rplan.startDate) <= DATE(:inputDate)");
                sb.append("             AND rplan.runningStatus = ").append(RunningStatus.RUNNING.ordinal());
                sb.append("          )");

                // If route is already finished.
                sb.append("         OR");
                sb.append("         (");
                sb.append("             rplan.id IN ");
                sb.append("                 (SELECT DISTINCT ractual.planedRoutesId");
                sb.append("                     FROM RoutesEntity ractual");
                sb.append("                     WHERE DATE(ractual.startDate) <= DATE(:inputDate)");
                sb.append("                         AND DATE(ractual.finishedTime) >= DATE(:inputDate)");
                sb.append("                         AND ractual.planedRoutesId = rplan.id)");
                sb.append("             AND rplan.runningStatus = ").append(RunningStatus.FINISHED.ordinal());
                sb.append("          )");
                sb.append("   )");

            } else {
                // In case date input is past date.
                sb.append("   INNER JOIN RoutesEntity ractual");
                sb.append("       ON rplan.id = ractual.planedRoutesId");
                sb.append("       AND (");

                // In case route is already finished.
                sb.append("             (");
                sb.append("                 DATE(ractual.startDate) <= DATE(:inputDate)");
                sb.append("                 AND DATE(ractual.finishedTime) >= DATE(:inputDate)");
                sb.append("                 AND rplan.runningStatus = ").append(RunningStatus.FINISHED.ordinal());
                sb.append("             )");
                sb.append("             OR");

                // In case route is still running.
                sb.append("             (");
                sb.append("                 DATE(ractual.startDate) <= DATE(:inputDate)");
                sb.append("                 AND rplan.runningStatus = ").append(RunningStatus.RUNNING.ordinal());
                sb.append("             )");
                sb.append("       )");
            }

        }

        sb.append(" WHERE ");
        sb.append("   rplan.deleteFlg = 0");
        sb.append("   AND rplan.actualDate is null");
        sb.append("   AND rplan.runningStatus <> :changeStatus");
        sb.append("   AND rplan.createBy = :userId");

        Long routeId = null;
        if (searchRouteForm.getRouteId() != null) {
            routeId = searchRouteForm.getRouteId();
            sb.append(" AND rplan.id = :routeId");
        }

        String routeName = null;
        if (searchRouteForm.getRouteName() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getRouteName())) {
                routeName = searchRouteForm.getRouteName();
                sb.append(" AND UPPER(rplan.name) LIKE CONCAT('%', CONCAT(:routeName, '%'))");
            }
        }

        String plateNumber = null;
        if (searchRouteForm.getPlateNumber() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getPlateNumber())) {
                plateNumber = searchRouteForm.getPlateNumber();
                sb.append(" AND UPPER(c.plateNumber) LIKE CONCAT('%',CONCAT(:plateNumber, '%'))");
            }
        }

        Date fromStartDate = null;
        if (searchRouteForm.getFromStartDate() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getFromStartDate())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = dateFormat.parse(searchRouteForm.getFromStartDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                fromStartDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                sb.append(" AND DATE(rplan.startDate) > DATE(:fromStartDate)\n");
            }
        }

        Date toStartDate = null;
        if (searchRouteForm.getToStartDate() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getToStartDate())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = dateFormat.parse(searchRouteForm.getToStartDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, +1);
                toStartDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                sb.append(" AND DATE(rplan.startDate) < DATE(:toStartDate)");
            }
        }

        Date fromEndDate = null;
        if (searchRouteForm.getFromEndDate() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getFromEndDate())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = dateFormat.parse(searchRouteForm.getFromEndDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, -1);
                fromEndDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                sb.append(" AND DATE(rplan.endDate) > DATE(:fromEndDate)");
            }
        }

        Date toEndDate = null;
        if (searchRouteForm.getToEndDate() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getToEndDate())) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                Date date = dateFormat.parse(searchRouteForm.getToEndDate());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DAY_OF_YEAR, +1);
                toEndDate = DateUtils.truncate(cal.getTime(), java.util.Calendar.DAY_OF_MONTH);
                sb.append(" AND DATE(rplan.endDate) < DATE(:toEndDate)\n");
            }
        }

        Boolean routeStatus = null;
        if (searchRouteForm.getStatus() != null) {
            routeStatus = (searchRouteForm.getStatus() == 1);
            sb.append(" AND rplan.active = :routeStatus");
        }

        String routeMemo = null;
        if (searchRouteForm.getRouteMemo() != null) {
            if (StringUtils.isNotBlank(searchRouteForm.getRouteMemo())) {
                routeMemo = searchRouteForm.getRouteMemo();
                sb.append(" AND UPPER(rplan.routeMemo) LIKE CONCAT('%',CONCAT(:routeMemo, '%'))");
            }
        }


        sb.append(" ORDER BY rplan.id ASC");

        Query<E> query = sessionFactory.getCurrentSession()
            .createQuery(sb.toString(), tClass).setParameter("userId", userId);

        query.setParameter("changeStatus", RunningStatus.CHANGED);
        if (inputDate != null) {
            query.setParameter("inputDate", inputDate);
        }

        if (routeId != null) {
            query.setParameter("routeId", routeId);
        }

        if (routeName != null) {
            query.setParameter("routeName", routeName.toUpperCase());
        }

        if (plateNumber != null) {
            query.setParameter("plateNumber", plateNumber.toUpperCase());
        }

        if (fromStartDate != null) {
            query.setParameter("fromStartDate", fromStartDate);
        }

        if (toStartDate != null) {
            query.setParameter("toStartDate", toStartDate);
        }

        if (fromEndDate != null) {
            query.setParameter("fromEndDate", fromEndDate);
        }

        if (toEndDate != null) {
            query.setParameter("toEndDate", toEndDate);
        }

        if (routeStatus != null) {
            query.setParameter("routeStatus", routeStatus);
        }

        if (routeMemo != null) {
            query.setParameter("routeMemo", "%" + routeMemo + "%");
        }

        if (havePaging) {
            query.setFirstResult(offset != null ? offset : 0);
            query.setMaxResults(maxResults != null ? maxResults : 10);
        }

        return query;
    }


    @Override
    public RoutesEntity findByUserIdAndRouteId(Long routeId, Long userId) {
        logger.info("RoutesDao.findByUserIdAndRouteId");
        StringBuilder sb = new StringBuilder();
        sb.append("Select r \n" +
                "from RoutesEntity r, UsersManageDevicesEntity u, DevicesEntity de\n" +
                "where u.usersId = :userId\n" +
                "and u.devicesId =  de.id\n" +
                "and r.devicesId = u.devicesId\n" +
                "and r.deleteFlg = 0\n" +
                "and de.deleteFlg = 0\n" +
                "and u.deleteFlg = 0\n" +
                "and r.id = :routeId\n" +
                "ORDER BY r.id ASC\n");
        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString(), RoutesEntity.class).setParameter("userId", userId).setParameter("routeId", routeId);
        List<RoutesEntity> routesEntities = query.getResultList();
        if (!CollectionUtils.isEmpty(routesEntities)) {
            return routesEntities.get(0);
        }
        return null;
    }

    @Override
    public List<RouteForMonthlyReportScreenDTO> searchReportMonthForScreen(ReportFormSearch formSearch, Integer offset, Integer maxResult, Boolean noPaging) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT routeActual.id routeId, c.driver_name, c.plate_number,")
            .append("de.login_id, routeActual.distance, routeActual.name routeName ")
            .append("FROM divisions d, users u, users_manage_devices ud, cars c, routes routeActual, devices de ")
            .append("WHERE d.id = u.division_id ")
                .append("AND u.id = ud.user_id ")
                .append("AND ud.devices_id = de.id ")
                .append("AND de.id = c.device_id ")
                .append("AND c.id = routeActual.cars_id ");

        String orderBy = "ORDER BY de.id, routeActual.actual_date, routeActual.id";
        Query query = buildQuerySearch(sql, orderBy, formSearch);
        if (!noPaging) {
            query.setFirstResult(offset == null ? 0 : offset);
            query.setMaxResults(maxResult == null ? 10 : maxResult);
        }

        List<Object[]> resultList = query.getResultList();
        List<RouteForMonthlyReportScreenDTO> dtoList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(resultList)) {
            for (Object[] result: resultList) {
                Long routeId = CommonUtil.bigIntegerToLong(result[0]);

                RouteForMonthlyReportScreenDTO dto = new RouteForMonthlyReportScreenDTO();
                dto.setRouteId(routeId);
                dto.setDriverName(CatStringUtil.stringValue(result[1]));
                dto.setPlateNumber(CatStringUtil.stringValue(result[2]));
                dto.setLoginId(CatStringUtil.stringValue(result[3]));
                dto.setDistance((Float) result[4]);
                dto.setActualRouteName(CatStringUtil.stringValue(result[5]));

                List<RouteDetailEntity> routeDetailEntities = routeDetailDao.findByRouteId(routeId);
                List<String> customer = routeDetailEntities.stream().map(x -> x.getCustomers().getName()).collect(Collectors.toList());
                dto.setVisitedPlaces(customer);

                dtoList.add(dto);
            }
        }
        return dtoList;
    }

    @Override
    public Long countSearchReportMonth(ReportFormSearch formSearch) {
        StringBuilder countSql = new StringBuilder("SELECT count(DISTINCT routeActual.id)")
                .append("FROM divisions d, users u, users_manage_devices ud, cars c, routes routeActual, devices de ")
                .append("WHERE d.id = u.division_id ")
                .append("AND u.id = ud.user_id ")
                .append("AND ud.devices_id = de.id ")
                .append("AND de.id = c.device_id ")
                .append("AND c.id = routeActual.cars_id ");

        Query query = buildQuerySearch(countSql, "", formSearch);
        return CommonUtil.bigIntegerToLong(query.getSingleResult());
    }

    @Override
    public List<DeviceForMonthlyReportDTO> searchReportMonthForDownload(ReportFormSearch formSearch) {
        StringBuilder sql = new StringBuilder("SELECT DISTINCT de.id deviceId, dOwnDevice.name divisionName, de.login_id, ")
                .append("cLatest.car_maker, cLatest.car_type, cLatest.plate_number, routeActual.id routeId, routeActual.actual_date, ")
                .append("c.driver_name, routeActual.distance, routeActual.name routeName ")
            .append("FROM divisions d, divisions dOwnDevice, divisions_has_devices dhd, users u, users_manage_devices ud, cars c, cars cLatest, routes routeActual, devices de ")
            .append("WHERE d.id = u.division_id ")
                .append("AND u.id = ud.user_id ")
                .append("AND ud.devices_id = de.id ")
                // latest car
                .append("AND de.id = cLatest.device_id AND cLatest.latest_flg = 1 ")
                .append("AND de.id = c.device_id ")
                .append("AND c.id = routeActual.cars_id ")
                // division has device
                .append("AND dOwnDevice.id = dhd.divisions_id ")
                .append("AND dhd.devices_id = de.id ");

        // add conditions only export the selected route on the screen
        if (!CollectionUtils.isEmpty(formSearch.getSelectedRouteIds())) {
            sql.append("AND routeActual.id IN :selectedRoute ");
        }
        String orderBy = "ORDER BY de.id, routeActual.actual_date, routeActual.id";

        Query query = buildQuerySearch(sql, orderBy, formSearch);
        // add conditions only export the selected route on the screen
        if (!CollectionUtils.isEmpty(formSearch.getSelectedRouteIds())) {
            query.setParameter("selectedRoute", formSearch.getSelectedRouteIds());
        }
        List<Object[]> resultList = query.getResultList();

        List<DeviceForMonthlyReportDTO> dtoList = new ArrayList<>();
        DeviceForMonthlyReportDTO dtoTemp = null;
        Long deviceIdTemp = -1L;
        if (!CollectionUtils.isEmpty(resultList)) {
            for (Object[] result : resultList) {
                if (!deviceIdTemp.equals(CommonUtil.bigIntegerToLong(result[0]))) {
                    deviceIdTemp = CommonUtil.bigIntegerToLong(result[0]);

                    dtoTemp = new DeviceForMonthlyReportDTO();
                    dtoTemp.setDeviceId(deviceIdTemp);
                    dtoTemp.setDivisionName(CatStringUtil.stringValue(result[1]));
                    dtoTemp.setLoginId(CatStringUtil.stringValue(result[2]));
                    dtoTemp.setCarMaker(CatStringUtil.stringValue(result[3]));
                    dtoTemp.setCarType(CatStringUtil.stringValue(result[4]));
                    dtoTemp.setPlateNumber(CatStringUtil.stringValue(result[5]));
                    dtoTemp.setRoutes(new ArrayList<>());

                    dtoList.add(dtoTemp);
                }
                // set route info
                DeviceForMonthlyReportDTO.RouteRowDTO route = new DeviceForMonthlyReportDTO.RouteRowDTO();
                Long routeId = CommonUtil.bigIntegerToLong(result[6]);
                route.setActualStartDate(DateUtil.castDate(result[7]));
                route.setActualDriverName(CatStringUtil.stringValue(result[8]));
                route.setTotalDistance((Float) result[9]);
                // get route detail
                List<RouteDetailEntity> routeDetailEntities = routeDetailDao.findByRouteId(routeId);
                List<String> customer = routeDetailEntities.stream().map(x -> x.getCustomers().getName()).collect(Collectors.toList());
                route.setVisitedPlaces(customer);

                dtoTemp.getRoutes().add(route);
            }
        }
        return dtoList;
    }

    /**
     * build query search for monthly report
     * @param formSearch form search
     * @return
     */
    private Query buildQuerySearch(StringBuilder sql, String orderBy, ReportFormSearch formSearch) {
        sql.append("AND u.role_id = :roleId ")
            .append("AND routeActual.actual_date IS NOT NULL ");

        if (StringUtils.isNotBlank(formSearch.getDriverName())) {
            sql.append("AND UPPER(c.driver_name) LIKE CONCAT('%', CONCAT(:driverName,'%')) ");
        }
        if (StringUtils.isNotBlank(formSearch.getPlateNumber())) {
            sql.append("AND UPPER(c.plate_number) LIKE CONCAT('%', CONCAT(:plateNumber,'%')) ");
        }
        if (StringUtils.isNotBlank(formSearch.getMonth())) {
            sql.append("AND DATE_FORMAT(routeActual.actual_date,'%Y-%m') = :month ");
        }

        if (formSearch.getUserId() != null) {
            sql.append("AND ud.user_id = :loginUserId ");
        } else {
            sql.append("AND d.id IN :divisionIds ");
        }

        sql.append(orderBy);
        Query query = sessionFactory.getCurrentSession()
                .createNativeQuery(sql.toString())
                .setParameter("roleId", UserRole.OPERATOR.getRole());;
        if (StringUtils.isNotBlank(formSearch.getDriverName())) {
            query.setParameter("driverName", StringUtils.trim(formSearch.getDriverName()).toUpperCase());
        }
        if (StringUtils.isNotBlank(formSearch.getPlateNumber())) {
            query.setParameter("plateNumber", StringUtils.trim(formSearch.getPlateNumber()).toUpperCase());
        }
        if (StringUtils.isNotBlank(formSearch.getMonth())) {
            query.setParameter("month", formSearch.getMonth());
        }

        if (formSearch.getUserId() != null) {
            query.setParameter("loginUserId", formSearch.getUserId());
        } else {
            query.setParameter("divisionIds", formSearch.getManagedDivisionIds());
        }
        return query;
    }

    public RouteDTO getRouteInfoInDetail(Long routeId) {
        logger.info("RoutesDao.getRouteInfoInDetail");
        StringBuilder sb = new StringBuilder();
        sb.append("Select r.id, r.name, de.loginId, r.startDate, r.endDate, r.active , r.routeMemo\n" +
                "from RoutesEntity r, DevicesEntity de\n" +
                "where r.id = :routeId\n" +
                "and r.devicesId = de.id\n");
        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("routeId", routeId);

        List<Object[]> routeEntities = query.getResultList();

        if (!CollectionUtils.isEmpty(routeEntities)) {
            RouteDTO routeDTO = new RouteDTO();
            Object[] object = routeEntities.get(0);
            if (object[0] != null) {
                routeDTO.setRouteId(Long.valueOf(object[0].toString()));
            }
            if (object[1] != null) {
                routeDTO.setRouteName(String.valueOf(object[1].toString()));
            }
            if (object[2] != null) {
                routeDTO.setDeviceName(String.valueOf(object[2].toString()));
            }
            if (object[3] != null) {
                routeDTO.setStartDate((Date) object[3]);
            }
            if (object[4] != null) {
                routeDTO.setEndDate((Date) object[4]);
            }
            if (object[5] != null) {
                Boolean status = (Boolean) object[5];
                if (status) {
                    routeDTO.setStatus(1);
                } else {
                    routeDTO.setStatus(0);
                }
            }
            if (object[6] != null) {
                routeDTO.setRouteMemo(object[6].toString());
            }
            return routeDTO;
        }

        return null;
    }

    @Override
    public List<RouteCustomerDetailDTO> getRouteCustomerDetail(Long routeId, Long userId, RouteCustomerDetailForm routeCustomerDetailForm) {
        logger.info("RoutesDao.getRouteCustomerDetail");
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT c.name, c.address, c.buildingName, rd.visitOrder \n" +
                "FROM RouteDetailEntity rd, RoutesEntity r, CustomersEntity c, UsersManageDevicesEntity u , DevicesEntity de\n" +
                "WHERE\n" +
                "u.usersId = :userId\n" +
                "AND\n" +
                "rd.routesId = :routeId\n" +
                "AND\n" +
                "u.devicesId = de.id\n" +
                "AND\n" +
                "r.devicesId = de.id\n" +
                "AND\n" +
                "r.id = rd.routesId\n" +
                "AND\n" +
                "rd.customers.id = c.id\n" +
                "AND\n" +
                "rd.deleteFlg = 0\n" +
                "AND\n" +
                "r.deleteFlg = 0\n" +
                "AND\n" +
                "c.deleteFlg = 0\n" +
                "AND\n" +
                "u.deleteFlg = 0\n" +
                "AND\n" +
                "de.deleteFlg = 0\n");

        String customerName = null;
        String customerAddress = null;
        String customerBuildingName = null;
        Long visitOrder = null;

        if (routeCustomerDetailForm != null) {
            if (routeCustomerDetailForm.getCustomerName() != null) {
                if (StringUtils.isNotBlank(routeCustomerDetailForm.getCustomerName())) {
                    customerName = routeCustomerDetailForm.getCustomerName();
                    sb.append("AND\n" +
                            "c.name LIKE :customerName\n");
                }
            }

            if (routeCustomerDetailForm.getCustomerAddress() != null) {
                if (StringUtils.isNotBlank(routeCustomerDetailForm.getCustomerAddress())) {
                    customerAddress = routeCustomerDetailForm.getCustomerAddress();
                    sb.append("AND\n" +
                            "c.address LIKE :customerAddress\n");
                }
            }

            if (routeCustomerDetailForm.getCustomerBuildingName() != null) {
                if (StringUtils.isNotBlank(routeCustomerDetailForm.getCustomerBuildingName())) {
                    customerBuildingName = routeCustomerDetailForm.getCustomerBuildingName();
                    sb.append("AND\n" +
                            "c.buildingName LIKE :customerBuildingName\n");
                }
            }

            if (routeCustomerDetailForm.getVisitOrder() != null) {
                visitOrder = routeCustomerDetailForm.getVisitOrder();
                sb.append("AND\n" +
                        "rd.visitOrder = :visitOrder\n");
            }
        }
        sb.append("ORDER BY rd.visitOrder ASC\n");

        Query query = sessionFactory.getCurrentSession()
                .createQuery(sb.toString()).setParameter("userId", userId).setParameter("routeId", routeId);

        if (customerName != null) {
            query.setParameter("customerName", "%" + customerName + "%");
        }
        if (customerAddress != null) {
            query.setParameter("customerAddress", "%" + customerAddress + "%");
        }
        if (customerBuildingName != null) {
            query.setParameter("customerBuildingName", "%" + customerBuildingName + "%");
        }
        if (visitOrder != null) {
            query.setParameter("visitOrder", visitOrder);
        }

        List<Object[]> routeDetailEntities = query.getResultList();

        List<RouteCustomerDetailDTO> routeCustomerDetailDTOList = new ArrayList<>();

        if (!CollectionUtils.isEmpty(routeDetailEntities)) {
            for (Object[] object : routeDetailEntities
                    ) {
                RouteCustomerDetailDTO routeCustomerDetailDTO = new RouteCustomerDetailDTO();

                if (object[0] != null) {
                    routeCustomerDetailDTO.setCustomerName(object[0].toString());
                }
                if (object[1] != null) {
                    routeCustomerDetailDTO.setCustomerAddress(object[1].toString());
                }
                if (object[2] != null) {
                    routeCustomerDetailDTO.setCustomerBuildingName(object[2].toString());
                }
                if (object[3] != null) {
                    routeCustomerDetailDTO.setVisitOrder((Long) object[3]);
                }
                routeCustomerDetailDTOList.add(routeCustomerDetailDTO);
            }

        }

        return routeCustomerDetailDTOList;
    }

    @Override
    public RoutesEntity findByDeviceIDAndActualDate(Long deviceId, Date actualDate) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM routes WHERE devices_id = :deviceId ");
        sqlBuilder.append("AND actual_date IS NOT NULL AND delete_flg = 0 AND DATE(actual_date) = DATE(:actualDate)");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
            .createNativeQuery(sqlBuilder.toString(), RoutesEntity.class)
            .setParameter("deviceId", deviceId)
            .setParameter("actualDate", actualDate).getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    public RoutesEntity findByDeviceIdAndToday(Long devicesId, Date date) {
        String sql = "FROM RoutesEntity WHERE devicesId = :deviceId AND DATE(actualDate) = DATE(:date) ";
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery(sql)
                .setParameter("deviceId", devicesId)
                .setParameter("date", date)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public RoutesEntity getRouteActualRunningByDate(Long routeId, Date dateTime, boolean active) {
        logger.info("RoutesDao.getRouteActualByDevicesIdAndTodayAndActive");
        List<RoutesEntity> routesEntities = sessionFactory.getCurrentSession()
                .createQuery("FROM RoutesEntity WHERE planedRoutesId = :routeId AND"
                        + " DATE(actualDate) = DATE(:dateTime) AND active = :active ", RoutesEntity.class)
                .setParameter("routeId", routeId)
                .setParameter("dateTime", dateTime)
                .setParameter("active", active)
                .getResultList();
        if (CollectionUtils.isEmpty(routesEntities)) {
            return null;
        }
        return routesEntities.get(0);
    }

    @Override
    public Long countHistoryRouteActualByRoutePlanId(Long routeId, Boolean active) throws CommonException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(1) FROM RoutesEntity WHERE planedRoutesId = :routeId");
        if (active != null) {
            sql.append(" AND active = :active");
        }
        Query<Long> q = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), Long.class)
                .setParameter("routeId", routeId);
        if (active != null) {
            q.setParameter("active", active);
        }
        return q.getSingleResult();
    }

    @Override
    public int deleteRouteDetailByRouteId(Long routeId) throws CommonException {
        Query q = sessionFactory.getCurrentSession()
                .createQuery(" DELETE RouteDetailEntity WHERE routesId = :routeId")
                .setParameter("routeId", routeId);
        return q.executeUpdate();
    }

    @Override
    public List<RoutesEntity> searchMessageRoute(Long deviceId, String text) {
        StringBuilder sql = new StringBuilder("SELECT r FROM RoutesEntity r INNER JOIN RouteDetailEntity rd ON r.id = rd.routesId ");
        sql.append("LEFT JOIN MessagesEntity me ON me.routeDetailId = rd.id ");
        sql.append("WHERE r.actualDate IS NOT NULL ");
        sql.append("AND r.devicesId = :deviceId ");
        if (StringUtils.isNotBlank(text)) {
            sql.append("AND ( ");
            sql.append(" UPPER(r.name) LIKE CONCAT( CONCAT('%', :text), '%') ");
            sql.append(" OR UPPER(me.content) LIKE CONCAT( CONCAT('%', :text), '%') ");
            sql.append(") ");
        }
        sql.append("GROUP BY r.id ");
        Query<RoutesEntity> query = sessionFactory.getCurrentSession()
                .createQuery(sql.toString(), RoutesEntity.class)
                .setParameter("deviceId", deviceId);
        if (StringUtils.isNotBlank(text)) {
            query.setParameter("text", text.toUpperCase());
        }
        return query.getResultList();
    }

    @Override
    public List<RoutesEntity> getRoutePlanByCreator(Long userId) {
        StringBuilder sql = new StringBuilder("FROM RoutesEntity WHERE createBy = :userId ")
                .append("AND actualDate IS NULL ")
                .append("AND deleteFlg = false");
        return sessionFactory.getCurrentSession().createQuery(sql.toString())
                .setParameter("userId", userId)
                .getResultList();
    }
}
