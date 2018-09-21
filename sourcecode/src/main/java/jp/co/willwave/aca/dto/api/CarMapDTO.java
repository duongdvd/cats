package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class CarMapDTO {
    private Long deviceId;
    private String latitude;
    private String longitude;
    private String iconPath;
    private String carName;
    private Double speed;
    private String plateNumber;
}
