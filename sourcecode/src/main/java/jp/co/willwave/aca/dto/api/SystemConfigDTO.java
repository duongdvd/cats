package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class SystemConfigDTO {
    private Long divisionId;
    private String divisionName;
    private String parentDivisionName;
    private String notificationTime;
    private String notificationEmail;
    private String mobileIcon;
    private String customerIcon;
    private String timeMessage;
    private String travelTimeAlert;
    private String distanceFinished;
    private String startEndPointColor;
    private String arrivedPoint;
    private String notArrivedPoint;

}
