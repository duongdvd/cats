package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class SearchRouteForm {

    private Long routeId;
    private String routeName;
    private String deviceName;
    private String plateNumber;
    private String fromStartDate;
    private String fromEndDate;
    private String toEndDate;
    private String toStartDate;
    private Integer status;
    private String routeMemo;
    private String specificDate;

}
