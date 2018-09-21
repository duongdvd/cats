package jp.co.willwave.aca.web.form.message;

import lombok.Data;

@Data
public class FormSearch {
    private String fromDate;
    private String toDate;
    private Long devicesId;
    private String routesName;
    private String textMessage;
    private Long usersId;
}
