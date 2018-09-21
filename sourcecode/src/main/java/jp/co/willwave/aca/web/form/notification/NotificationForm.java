package jp.co.willwave.aca.web.form.notification;

import jp.co.willwave.aca.model.enums.LogsType;
import lombok.Data;

import java.util.Date;

@Data
public class NotificationForm {
    private Long id;
    private String message;
    private LogsType type;
    private Date time;
    private Long routeDetailId;
}
