package jp.co.willwave.aca.dto.api.quickblox;

import lombok.Data;

@Data
public class SessionRequestDTO {
    private String application_id;
    private String auth_key;
    private Integer nonce;
    private Long timestamp;
    private String signature;
}
