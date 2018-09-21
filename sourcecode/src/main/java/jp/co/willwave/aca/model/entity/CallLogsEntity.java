package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.model.enums.CallType;
import jp.co.willwave.aca.web.form.CallLogForm;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "call_logs")
@Where(clause = "delete_flg = false")
public class CallLogsEntity extends BaseEntity {
    @Column(name = "is_from_device")
    private Boolean isFromDevice;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CallType type;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "route_detail_id")
    private Long routeDetailId;
    @Column(name = "start_time")
    private Date startTime;
    @Column(name = "end_time")
    private Date endTime;
    @Column(name = "device_id")
    private Long deviceId;
    @Column(name = "operator_id")
    private Long operatorId;

    CallLogsEntity() {}

    public CallLogsEntity(CallLogForm form) {
        this.isFromDevice = form.getIsFromDevice();
        this.type = form.getType();
        this.latitude = form.getLatitude();
        this.longitude = form.getLongitude();
        this.routeDetailId = form.getRouteDetailId();
        this.startTime = form.getStartTime();
        this.endTime = form.getEndTime();
        this.deviceId = form.getDeviceId();
        this.operatorId = form.getOperatorId();
    }
}
