package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AllRouteDTO {
    private List<RouteDetailDTOMap> routeActualDetail = new ArrayList<>();
    private List<RouteDetailDTOMap> routePlanDetail = new ArrayList<>();
}
