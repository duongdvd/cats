package jp.co.willwave.aca.web.form.notification;

import jp.co.willwave.aca.model.enums.LogsType;
import lombok.Data;

@Data
public class NotificationDTO {
    private Long id;
    private LogsType type;
}
