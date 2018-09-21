package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * @RELIPA 2018
 */
@Data
@Entity
@Table(name = "cars")
@Where(clause = "delete_flg = 0")
public class CarsEntity extends BaseEntity {

    @Column(name = "device_id")
    private Long devicesId;
    @Column(name = "driver_name")
    private String driverName;
    @Column(name = "plate_number")
    private String plateNumber;
    @Column(name = "car_type")
    private String carType;
    @Column(name = "car_maker")
    private String carMaker;
    @Column(name = "latest_flg")
    private Integer latestFlg = 1;
}
