package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class LocationTempDTO {
    private String longtitude;
    private String latitude;
    private Double speed;
    private Long time;
}
