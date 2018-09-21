package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class EmergencyDTO {
    private String latitude;
    private String longitude;
    private String message;
}
