package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class ConfigDeviceDTO {
    private String distanceFinished;
    private ColorConfig color;
    private String timeMessage;
    private String travelTimeAlert;
    private String maxLengthInputText;

}
