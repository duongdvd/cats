package jp.co.willwave.aca.dto.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Each dto corresponds to the data of a sheet of the excel file
 */
@Data
public class RouteForDailyReportDTO {
    private Long routeId;
    private String divisionName;
    private String routeName;
    private String actualDriverName;
    private String actualPlateNumber;
    private String planStartPlace;
    private String planEndPlace;
    private Date actualStartDateTime;
    private Date actualEndDateTime;
    private List<RouteDetailRowDTO> routeDetails;

    /**
     * Each object corresponds to the data of a row in a sheet of the excel file
     */
    @Data
    public static class RouteDetailRowDTO {
        private String startPlace;
        private String endPlace;
        private Date startDateTime;
        private Date endDateTime;
    }
}
