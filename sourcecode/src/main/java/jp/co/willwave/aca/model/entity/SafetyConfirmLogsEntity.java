package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "safety_confirm_logs")
@Where(clause = "delete_flg = false ")
@Data
public class SafetyConfirmLogsEntity extends BaseEntity {
    @Column(name = "notification_time")
    private Date notificationTime;
    @Column(name = "action")
    private String action;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "route_detail_id")
    private Long routeDetailId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "division_id", nullable = false)
    private DivisionsEntity division;
    @Column(name = "notification_mail")
    private String notificationMail;
    @Column(name = "stop_time")
    private Long stopTime;
    @Column(name = "isRead")
    private Boolean isRead = Boolean.FALSE;
    @Column(name = "users_id")
    private Long usersId;
    @Column(name = "devices_id")
    private Long devicesId;
}
