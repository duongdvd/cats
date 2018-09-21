package jp.co.willwave.aca.model.entity;

import lombok.Data;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

@Data
@Entity
@Table(name = "company_usage_status")
@Where(clause = "delete_flg = 0")
public class CompanyUsageStatusEntity extends BaseEntity {

    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "division_active_count")
    private Long divisionActiveCount;
    @Column(name = "user_active_count")
    private Long userActiveCount;
    @Column(name = "device_active_count")
    private Long deviceActiveCount;
    @Column(name = "month_report")
    private Date monthReport;
    @Column(name = "usage_type")
    private String usageType;
}
