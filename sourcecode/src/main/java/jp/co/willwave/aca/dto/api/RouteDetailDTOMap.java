package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.Date;

@Data
public class RouteDetailDTOMap {
    private Long deviceId;
    private String customerName;
    private Date arrivalTime;
    private Date reDepartTime;
    private Long visitOrder;
    private Long routeId;
    private Long routeDetailId;
    private String description;
    private String address;
    private Long routePlanId;
    private String arrivalNote;
    private String latitude;
    private String longitude;
    private String iconMaker;
    private String name;
}
