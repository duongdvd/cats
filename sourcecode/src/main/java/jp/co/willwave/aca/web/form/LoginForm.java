package jp.co.willwave.aca.web.form;

import org.hibernate.validator.constraints.NotEmpty;

public class LoginForm {
    @NotEmpty(message = "E0001,loginId")
    private String loginId;

    @NotEmpty(message = "E0001,password")
    private String passwd;

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
