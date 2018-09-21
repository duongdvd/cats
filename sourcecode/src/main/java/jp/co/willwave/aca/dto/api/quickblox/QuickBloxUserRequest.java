package jp.co.willwave.aca.dto.api.quickblox;

import lombok.Data;

@Data
public class QuickBloxUserRequest {
    private String login;
    private String password;
    private String tag_list;
}
