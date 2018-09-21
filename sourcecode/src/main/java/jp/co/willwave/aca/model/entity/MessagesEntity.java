package jp.co.willwave.aca.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "messages")
public class MessagesEntity extends BaseEntity {
    @Column(name = "users_id")
    private Long usersId;
    @Column(name = "devices_id")
    private Long devicesId;
    @Column(name = "content")
    private String content;
    @Column(name = "is_from_device")
    private Boolean isFromDevice = Boolean.FALSE;
    @Column(name = "parent_message_id")
    private Long parentMessageId;
    @Column(name = "created_time")
    private Date createdTime;
    @Column(name = "route_detail_id")
    private Long routeDetailId;
    @Column(name = "is_read")
    private Boolean isRead = Boolean.FALSE;
    @Column(name = "is_reply")
    private Boolean isReply = Boolean.FALSE;

}
