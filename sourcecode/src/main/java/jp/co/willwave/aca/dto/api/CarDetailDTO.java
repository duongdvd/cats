package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class CarDetailDTO {

    private Long carId;
    private String carName;
    private String driverName;
    private String plateNumber;
    private Long routeId;
    private String currentRouteName;
    private String routeMemo;
    private Long planRouteId;
    private String planRouteName;
    private String planRouteMemo;
    private String latitude;
    private String longitude;
    private Double speed;
    private Long deviceId;
    private String loginId;
    private Long callId;
    private String callUserName;
    private String callPassword;
    private String userTags;
}
