package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.Date;

@Data
public class CompanyUsageDTO {
    private Long companyId;
    private String companyName;
    private Long divisionActiveCount;
    private Long userActiveCount;
    private Long deviceActiveCount;
    private Date monthReport;
}
