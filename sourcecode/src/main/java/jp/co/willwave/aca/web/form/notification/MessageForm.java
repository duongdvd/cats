package jp.co.willwave.aca.web.form.notification;

import lombok.Data;

import java.util.Date;

@Data
public class MessageForm {
    private Long id;
    private Long routeDetailId;
    private Long devicesId;
    private String deviceName;
    private String message;
    private Date time;
}
