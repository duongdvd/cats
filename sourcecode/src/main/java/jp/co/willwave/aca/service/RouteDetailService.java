package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.api.FinishedRouteDetailDTO;
import jp.co.willwave.aca.dto.api.RouteDetailDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.entity.RouteDetailEntity;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Date;
import java.util.List;

public interface RouteDetailService {
    void finishedRouteDetail(FinishedRouteDetailDTO finishedRouteDetail) throws Exception;

    void reDepartRouteDetail() throws Exception;

    List<RouteDetailEntity> findByRouteId(Long routeId);

    RouteDetailEntity findById(Long routeDetailId) throws CommonException;

    Long getVisitedOrderByRouteId(Long routeId);

    /**
     * @param routeDetails
     * @return Triple<Start garage, route customers list, end garage>
     */
    Triple<RouteDetailDTO, List<RouteDetailDTO>, RouteDetailDTO> splitRoutesGarageAndCustomer(List<RouteDetailDTO> routeDetails);

    /**
     * create a list of RouteDetailEntity from routeDetailsStr
     * @param routeDetailsStr format "customerId1, order1, customerId2, order2..."
     * @return a list of RouteDetailEntity
     * @throws CommonException
     */
    public List<RouteDetailEntity> createRouteDetailList(String routeDetailsStr) throws CommonException;

    /**
     * remove the garages from RouterDetail list
     * (assure that the 1st route detail is start garage, the last route detail is end garage)
     * @param routeDetails
     * @return
     */
    public List<RouteDetailEntity> createCustomerListFromRouteDetailStr(List<RouteDetailEntity> routeDetails);

    /**
     * <p>create a list of create a list of RouteDetailEntity from routeDetailsStr
     * and remove the garages from RouterDetail list</p>
     * <i>(assure that the 1st route detail is start garage, the last route detail is end garage)</i>
     * @param routeDetailsStr format "customerId1, order1, customerId2, order2..."
     * @return
     */
    public List<RouteDetailEntity> createCustomerListFromRouteDetailStr(String routeDetailsStr) throws CommonException;
}
