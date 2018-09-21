package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "emergency_logs")
@Where(clause = "delete_flg = false")
public class EmergencyLogsEntity extends BaseEntity {
    @Column(name = "notification_time")
    private Date notificationTime;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "routeDetailId")
    private Long routeDetailId;
    @Column(name = "message")
    private String message;
    @Column(name = "isRead")
    private Boolean isRead = Boolean.FALSE;
    @Column(name = "user_id")
    private Long usersId;
    @Column(name = "device_id")
    private Long devicesId;
}
