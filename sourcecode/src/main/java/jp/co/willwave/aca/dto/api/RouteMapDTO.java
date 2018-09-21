package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.dto.api.map.LocationDTO;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouteMapDTO {
    private Long startVisitOrder;
    private Long endVisitOrder;
    private Long distance;
    private List<LocationDTO> line = new ArrayList<>();
}
