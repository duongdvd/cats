package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class EditDeviceDTO {

    private Long id;

    private String loginId;

    private Integer status;

    private String carMaker;

    private String carType;

    private String driverName;

    private String plateNumber;

    private String deviceType;

    private String currentImage;
}
