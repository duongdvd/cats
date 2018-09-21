package jp.co.willwave.aca.dto.report;

import lombok.Data;

import java.util.List;

@Data
public class RouteForMonthlyReportScreenDTO {
    private Long routeId;
    private String driverName;
    private String plateNumber;
    private String loginId;
    private Float distance;
    private String actualRouteName;
    private List<String> visitedPlaces;

    /**
     * format visited places
     * @return
     */
    public String formatVisitedPlaces() {
        return String.join("→", this.visitedPlaces);
    }
}
