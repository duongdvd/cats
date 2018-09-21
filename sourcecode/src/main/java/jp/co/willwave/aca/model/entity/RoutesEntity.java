package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.constants.RunningStatus;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "routes")
@Where(clause = "delete_flg = false")
public class RoutesEntity extends BaseEntity {
    @Column(name = "devices_id")
    private Long devicesId;
    @Column(name = "name")
    private String name;
    @Column(name = "planed_routes_id")
    private Long planedRoutesId;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "actual_date")
    private Date actualDate;
    @Column(name = "active")
    private Boolean active;
    @Column(name = "route_memo")
    private String routeMemo;
    @Column(name = "distance")
    private Float distance;
    @Column(name = "cars_id")
    private Long carsId;
    @Column(name = "finished_time")
    private Date finishedTime;
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "running_status", length = 1)
    private RunningStatus runningStatus = RunningStatus.NOT_YET_STARTED;

}
