package jp.co.willwave.aca.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class ForgotPasswordForm {

    @NotEmpty(message = "E0001,resetPassword.systemAdmin.emailUser")
    @Email(message = "E0015,resetPassword.systemAdmin.emailUser")
    private String email;
}
