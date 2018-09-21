package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * UsersManageDeviceテーブルエンティティ
 *
 * @author p-khanhnv.
 */
@Data
@Entity
@Table(name = "users_manage_devices")
@Where(clause = "delete_flg = 0")
public class UsersManageDevicesEntity extends BaseEntity {
    @Column(name = "user_id")
    private Long usersId;
    @Column(name = "devices_id")
    private Long devicesId;
    @Column(name = "description")
    private String description;
}
