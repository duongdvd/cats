package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class SearchUsageForm {

    private Long companyId;
    private String companyName;
    private String reportType;
    private String reportMonth;
}
