package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.contraints.ValidPassword;
import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.SafeHtml;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CompanyForm {
    @NotEmpty(message = "E0001,company.label.firstname")
    @SafeHtml(message = "E0015,company.label.firstname")
    private String firstName;

    @NotEmpty(message = "E0001,company.label.lastName")
    @SafeHtml(message = "E0015,company.label.lastName")
    private String lastName;

    @NotEmpty(message = "E0001,company.label.loginId")
    @SafeHtml(message = "E0015,company.label.loginId")
    @Length(max = 200, message = "E0074,company.label.loginId,200")
    @Pattern(regexp = "^[a-zA-Z0-9//!#//$%&'//(//)//*//+\\-//.///:;//<=//>//?@\\[\\]//^_`//{//|//}~]{1,200}$", message = "E0015, company.label.loginId")

    private String loginId;

    @NotEmpty(message = "E0001,company.label.userMail")
    @Email(message = "E0015,company.label.userMail")
    private String userEmail;

    @ValidPassword()
    private String passwd;

    private String confirmPasswd;

    @NotEmpty(message = "E0001,company.label.companyName")
    @SafeHtml(message = "E0015,company.label.companyName")
    private String divisionName;

    @NotEmpty(message = "E0001,company.label.companyAddress")
    @SafeHtml(message = "E0015,company.label.companyAddress")
    private String divisionAddress;

    @SafeHtml(message = "E0015,company.label.description")
    private String description;

    @NotNull(message = "E0001,company.label.status")
    private Integer status;
}
