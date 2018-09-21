package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.Date;

@Data
public class MessageDTO {
    private Long id;
    private Long devicesId;
    private String content;
    private Long routeDetailId;
    private String routeDetailName;
    private Long routesId;
    private Boolean isFromDevice;
    private Long parentMessageId;
    private Date createTime;
    private Boolean isRead;
    private Boolean isReply;

}
