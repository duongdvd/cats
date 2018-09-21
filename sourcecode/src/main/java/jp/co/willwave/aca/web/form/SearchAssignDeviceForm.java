package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class SearchAssignDeviceForm {

    private Long deviceId;
    private String divisionName;
    private String driverName;
    private String deviceLoginId;
    private String plateNumber;
    private Long divisionId;
    private String deviceType;
}
