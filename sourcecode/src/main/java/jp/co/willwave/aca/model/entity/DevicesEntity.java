package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.constants.CarStatus;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "devices")
@Where(clause = "delete_flg = 0")
public class DevicesEntity extends BaseEntity {
    @Column(name = "login_id")
    private String loginId;
    @Column(name = "password")
    private String password;
    @Column(name = "login_token")
    private String loginToken;
    @Column(name = "salt")
    private String salt;
    @Column(name = "status_car")
    @Enumerated(EnumType.STRING)
    private CarStatus carStatus;
    @Column(name = "status")
    private Integer status;
    @Column(name = "device_type")
    private String deviceType;
    @Column(name = "time_location")
    private Timestamp timeLocation;
    @Column(name = "list_division_send")
    private String listDivisionSend;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "speed")
    private Double speed;
    @Column(name = "routeDetailFinished")
    private Long routeDetailFinished;
    @Column(name = "uuid")
    private String uuid;
    @Column(name = "call_username")
    private String callUserName;
    @Column(name = "call_password")
    private String callPassword;
    @Column(name = "user_tags")
    private String userTags;
    @Column(name = "call_id")
    private Long callId;
    @Column(name = "icon_path")
    private String iconPath;

}
