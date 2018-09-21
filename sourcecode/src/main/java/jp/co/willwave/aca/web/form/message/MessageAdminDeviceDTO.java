package jp.co.willwave.aca.web.form.message;

import jp.co.willwave.aca.model.entity.MessagesEntity;
import lombok.Data;

import java.util.Date;

@Data
public class MessageAdminDeviceDTO {
    private Long id;
    private Date time;
    private String adminName;
    private MessagesEntity adminMessage;
    private MessagesEntity devicesMessage;
    private String devicesName;
}
