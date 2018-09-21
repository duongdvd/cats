package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;


@Data
public class EmployeeForm {
    private Long id;
    @NotEmpty(message = "E0001,employee.LoginId")
    @Length(max = 200, message = "E0074,employee.LoginId,200")
    @Pattern(regexp = "^[a-zA-Z0-9//!#//$%&'//(//)//*//+\\-//.///:;//<=//>//?@\\[\\]//^_`//{//|//}~]{1,200}$", message = "E0015, employee.LoginId")
    private String loginId;
    private String passwd;
    private Long divisionsId;
    private String divisionName;
    private Integer roleId;
    private String roleName;
    private String userAddress;
    @Email(message = "E0015,employee.email")
    @NotEmpty(message = "E0001,employee.email")
    private String userEmail;
    @NotEmpty(message = "E0001,employee.firstName")
    private String firstName;
    @NotEmpty(message = "E0001,employee.lastName")
    private String lastName;
    private String fixedPhone;
    private String mobilePhone;
    private String faxNumber;
    @NotNull(message = "E0001,employee.status")
    private Boolean status;

    private Boolean modeEdit;
}
