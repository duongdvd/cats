package jp.co.willwave.aca.model.entity;

import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * DivisionsHasCustomersテーブルエンティティ
 *
 * @author p-khanhnv.
 */
@Entity
@Table(name = "divisions_has_customers")
@Where(clause = "delete_flg = false")
public class DivisionsHasCustomersEntity extends BaseEntity {

    @Column(name = "divisions_id")
    private Long divisionsId;

    @Column(name = "customers_id")
    private Long customersId;

    public Long getDivisionsId() {
        return divisionsId;
    }

    public void setDivisionsId(Long divisionsId) {
        this.divisionsId = divisionsId;
    }

    public Long getCustomersId() {
        return customersId;
    }

    public void setCustomersId(Long customersId) {
        this.customersId = customersId;
    }
}
