package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.constants.ConfigEnum;
import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "master_sys_configs")
public class MasterSysConfigsEntity extends BaseEntity {
    @Column(name = "key")
    @Enumerated(EnumType.STRING)
    private ConfigEnum key;
    @Column(name = "value")
    private String value;
    @Column(name = "description")
    private String description;
}
