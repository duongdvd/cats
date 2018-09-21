package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "route_detail")
@Where(clause = "delete_flg = false")
public class RouteDetailEntity extends BaseEntity {
    @Column(name = "routes_id")
    private Long routesId;

    @Column(name = "visit_order")
    private Long visitOrder;

    @Column(name = "arrival_time")
    private Date arrivalTime;

    @Column(name = "arrival_note")
    private String arrivalNote;

    @Column(name = "re-depart_time")
    private Date reDepartTime;

    @Column(name = "arrival_notes_id")
    private Long arrivalNotesId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customers_id", nullable = false)
    private CustomersEntity customers;

    public RouteDetailEntity() {
    }

    public RouteDetailEntity(Long routesId, CustomersEntity customersEntity, Long visitOrder) {
        this.routesId = routesId;
        this.visitOrder = visitOrder;
        this.customers = customersEntity;
    }

    public RouteDetailEntity(CustomersEntity customersEntity, Long visitOrder) {
        this.visitOrder = visitOrder;
        this.customers = customersEntity;
    }
}
