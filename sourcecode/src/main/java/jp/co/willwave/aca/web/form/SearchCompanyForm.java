package jp.co.willwave.aca.web.form;

import lombok.Data;

@Data
public class SearchCompanyForm {
    private Long id;
    private String divisionName;
    private String divisionMail;
    private String description;
    private String fromCreateDate;
    private String toCreateDate;
    private String fromUpdateDate;
    private String toUpdateDate;
    private Integer status;

}
