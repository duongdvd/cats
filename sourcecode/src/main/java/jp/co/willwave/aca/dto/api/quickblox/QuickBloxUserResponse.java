package jp.co.willwave.aca.dto.api.quickblox;

import lombok.Data;

import java.util.Date;

@Data
public class QuickBloxUserResponse {

    private Long id;
    private String full_name;
    private String email;
    private String login;
    private String phone;
    private String website;
    private Date created_at;
    private Date updated_at;
    private String last_request_at;
    private String external_user_id;
    private String facebook_id;
    private String twitter_id;
    private String blob_id;
    private String custom_data;
    private String user_tags;
    private String password;
    private String errorMessage;
}
