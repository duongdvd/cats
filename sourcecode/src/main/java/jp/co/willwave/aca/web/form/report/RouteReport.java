package jp.co.willwave.aca.web.form.report;

import lombok.Data;

import java.util.Date;

@Data
public class RouteReport {
    private Long id;
    private String name;
    private Float distance;
    private String loginId;
    private String driverName;
    private String plateNumber;
    private String detail;
    private Long divisionId;
    private String divisionName;
    private Date actualDate;
}
