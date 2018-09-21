package jp.co.willwave.aca.dto.report;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Each dto corresponds to the data of a sheet of the excel file
 */
@Data
public class DeviceForMonthlyReportDTO {
    private Long deviceId;
    private String companyName;
    private String divisionName;
    private String loginId;
    private String carMaker;
    private String carType;
    private String plateNumber;
    private List<RouteRowDTO> routes;

    /**
     * Each object corresponds to the data of a row in a sheet of the excel file
     */
    @Data
    public static class RouteRowDTO {
        private Date actualStartDate;
        private String actualDriverName;
        private Float totalDistance;
        private List<String> visitedPlaces;

        /**
         * format visited places
         * @return
         */
        public String formatVisitedPlaces() {
            return String.join("→", this.visitedPlaces);
        }
    }
}
