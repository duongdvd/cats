package jp.co.willwave.aca.web.form.division;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;


@Data
public class DivisionForm {
    private Long id;
    @NotEmpty(message = "E0001,division.divisionAddress")
    private String divisionAddress;
    @NotEmpty(message = "E0001,division.divisionName")
    private String divisionName;
    @NotNull(message = "E0001,division.parentDivision")
    private Long parentDivisionsId;
    private String parentDivisionName;
    private String divisionCode;
    private Long usersId;
    private Integer divisionStatus;

    private String loginId;
    private String passwd;
    private Long divisionsId;
    private Long roleId;
    private String userEmail;
    private String firstName;
    private String lastName;
    private String fixedPhone;
    private String mobilePhone;
    private String faxNumber;

    private Boolean modeEdit = Boolean.FALSE;

}
