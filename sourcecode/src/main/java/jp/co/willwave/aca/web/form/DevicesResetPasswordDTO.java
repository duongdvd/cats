package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class DevicesResetPasswordDTO {
    private Long deviceId;
    private String password;
}
