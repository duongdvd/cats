package jp.co.willwave.aca.dto.api;

import lombok.Data;

/* device for web */
@Data
public class AssignDeviceDTO {
    private Long deviceId;
    private String deviceLoginId;

    private Long divisionId;
    private String divisionName;
    private String driverName;

    private String plateNumber;

    private String deviceType;

}
