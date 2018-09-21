package jp.co.willwave.aca.dto.api;

import lombok.Data;
/* DTO for smp*/
@Data
public class DevicesDTO {
    private Long id;
    private String loginId;
    private String password;
    private String loginToken;
    private String carStatus;
    private String uuid;

}
