package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class SearchDeviceForm {

    private Long deviceId;
    private String divisionName;
    private String driverName;
    private String deviceLoginId;
    private Integer status;
    private String plateNumber;
    private Long divisionId;
    private String deviceType;


}
