package jp.co.willwave.aca.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "location_logs")
public class LocationLogsEntity extends BaseEntity {
    @Column(name = "log_time")
    private Date logTime;
    @Column(name = "longitude")
    private String longitude;
    @Column(name = "latitude")
    private String latitude;
    @Column(name = "speed")
    private Double speed;
    @Column(name = "route_detail_id")
    private Long routeDetailId;
}
