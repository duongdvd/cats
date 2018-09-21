package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @author KhanhNV
 */

@Data
@Entity
@Table(name = "route_change_history")
@Where(clause = "delete_flg = 0")
public class RouteChangeHistoryEntity extends BaseEntity {
    @Column(name = "operator_id")
    private Long operatorId;
    @Column(name = "actual_route_id")
    private Long actualRouteId;
    @Column(name = "previous_plan_routeId")
    private Long previousPlanRouteId;
    @Column(name = "current_plan_route_id")
    private Long currentPlanRouteId;
    @Column(name = "note")
    private String note;
}
