package jp.co.willwave.aca.dto.report;

import lombok.Data;

import java.util.Date;

@Data
public class RouteForDailyReportScreenDTO {
    private Long routeId;
    private Long divisionId;
    private String divisionName;
    private Date actualDate;
    private Date arrivalTime;
    private Date reDepartTime;
    private Date createDate;
    private String customerName;
    private String driverName;
    private String loginId;
    private Boolean endPoint;
    private String endName;
    private String plateNumber;
}
