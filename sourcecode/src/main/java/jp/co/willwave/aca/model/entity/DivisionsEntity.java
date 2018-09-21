package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Divisionsテーブルエンティティ
 *
 * @author p-khanhnv.
 */
@Data
@Entity
@Table(name = "divisions")
@Where(clause = "delete_flg = false")
public class DivisionsEntity extends BaseEntity {
    @Column(name = "address")
    private String divisionAddress;

    @Column(name = "name")
    private String divisionName;

    @Column(name = "mail")
    private String divisionMail;

    @Column(name = "parent_divisions_id")
    private Long parentDivisionsId;

    @Column(name = "users_id")
    private Long usersId;

    @Column(name = "code")
    private String divisionCode;

    @Column(name = "description")
    private String description;
    @Column(name = "status")
    private Integer status;

    public String getDivisionAddress() {
        return divisionAddress;
    }
}
