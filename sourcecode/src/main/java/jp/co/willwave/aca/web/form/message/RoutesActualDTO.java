package jp.co.willwave.aca.web.form.message;

import lombok.Data;

import java.util.Date;

@Data
public class RoutesActualDTO {
    private Long id;
    private Long usersId;
    private String usersName;
    private Date date;
    private Long devicesId;
    private String devicesName;
    private String routesName;
}
