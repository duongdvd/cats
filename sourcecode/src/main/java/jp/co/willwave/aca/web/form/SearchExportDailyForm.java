package jp.co.willwave.aca.web.form;

import lombok.Data;

import java.util.List;

@Data
public class SearchExportDailyForm {

    private Long divisionId;
    private String divisionName;
    private String driverName;
    private String plateNumber;
    private String startPoint;
    private String endPoint;
    private String actualDate;
    private String loginId;

    /**
     * list of selected route for export<br/>
     * Manually set value in Controller
     */
    private List<Integer> selectedRouteIds;
}
