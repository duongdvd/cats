package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "router_detail_maps")
public class RouterDetailMapsEntity extends BaseEntity {
    @Column(name = "detail_maps")
    @Type(type="text")
    private String detailMaps;
    @Column(name = "routes_id")
    private Long routesId;
}
