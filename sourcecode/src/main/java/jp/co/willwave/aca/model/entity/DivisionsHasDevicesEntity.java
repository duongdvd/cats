package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "divisions_has_devices")
@Where(clause = "delete_flg = 0")
public class DivisionsHasDevicesEntity extends BaseEntity {
    @Column(name = "divisions_id")
    private Long divisionsId;
    @Column(name = "description")
    private String description;
    @Column(name = "devices_id")
    private Long devicesId;
}
