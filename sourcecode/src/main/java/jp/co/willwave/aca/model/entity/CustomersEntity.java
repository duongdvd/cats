package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.model.enums.CustomerType;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Customersテーブルエンティティ
 *
 * @author p-khanhnv.
 */
@Entity
@Table(name = "customers")
@Data
public class CustomersEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "building_name", nullable = false)
    private String buildingName;

    @Column(name = "longitude", nullable = false)
    private String longitude;

    @Column(name = "latitude", nullable = false)
    private String latitude;

    @Column(name = "description")
    private String description;

    @Column(name = "icon_marker")
    private String iconMarker;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private CustomerType customerType;

}
