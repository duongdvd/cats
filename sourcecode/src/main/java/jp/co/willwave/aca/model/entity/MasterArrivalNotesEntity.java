package jp.co.willwave.aca.model.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "master_arrival_notes")
public class MasterArrivalNotesEntity extends BaseEntity {
    @Column(name = "companies_id")
    private Long companiesId;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
}
