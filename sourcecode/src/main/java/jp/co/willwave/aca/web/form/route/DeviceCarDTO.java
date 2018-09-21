package jp.co.willwave.aca.web.form.route;

import lombok.Data;

@Data
public class DeviceCarDTO {
    private Long deviceId;
    private String loginId;
    private String plateNumber;
    private Boolean status;
}
