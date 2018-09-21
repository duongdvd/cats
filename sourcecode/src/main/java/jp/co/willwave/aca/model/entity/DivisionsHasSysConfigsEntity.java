package jp.co.willwave.aca.model.entity;

import jp.co.willwave.aca.constants.ConfigEnum;
import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@Entity
@Table(name = "divisions_has_sys_configs")
@Where(clause = "delete_flg = 0")
public class DivisionsHasSysConfigsEntity extends BaseEntity {
    @Column(name = "divisions_id")
    private Long divisionsId;
    @Enumerated(EnumType.STRING)
    @Column(name = "key")
    private ConfigEnum key;
    @Column(name = "value")
    private String value;

    public DivisionsHasSysConfigsEntity(Long divisionsId) {
        this.divisionsId = divisionsId;
    }

    public DivisionsHasSysConfigsEntity() {
    }
}
