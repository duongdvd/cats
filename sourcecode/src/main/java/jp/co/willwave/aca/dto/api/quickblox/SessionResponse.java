package jp.co.willwave.aca.dto.api.quickblox;

import lombok.Data;

import java.util.Date;

@Data
public class SessionResponse {
    public Integer application_id;
    public Date created_at;
    public Integer id;
    public Integer nonce;
    public String token;
    public Integer ts;
    public Date updated_at;
    public Integer user_id;
    public String _id;
}
