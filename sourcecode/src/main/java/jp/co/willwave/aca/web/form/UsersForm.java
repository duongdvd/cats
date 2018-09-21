package jp.co.willwave.aca.web.form;

import jp.co.willwave.aca.contraints.ValidPassword;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.utilities.CommonUtil;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class UsersForm {
    private Long id;

    @NotEmpty(message = "E0001,profiles.title.loginId")
    private String loginId;

//    	@NotEmpty(message = "Password not null")
    private String passwd;

    private String newPassword;
    private String confirmPassword;

    @NotEmpty(message = "E0001,profiles.title.firstName")
    private String firstName;

    @NotEmpty(message = "E0001,profiles.title.lastName")
    private String lastName;

    @NotEmpty(message = "E0001,profiles.title.Address")
    private String userAddress;

    @NotEmpty(message = "E0001,profiles.title.mail")
    @Email(message = "E0067,profiles.title.mail")
    private String userEmail;

    private String fixedPhone;

    private String mobilePhone;

    private String faxNumber;

    private String createDate;

    private Long roleId;

    @ValidPassword
    private String getValidateNewPassword() {
        if (CommonUtil.isEmpty(this.newPassword) && CommonUtil.isEmpty(this.confirmPassword)) {
            return Constant.VALID_PASSWORD;
        }
        return this.newPassword;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

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

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFixedPhone() {
        return fixedPhone;
    }

    public void setFixedPhone(String fixedPhone) {
        this.fixedPhone = fixedPhone;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFaxNumber() {
        return faxNumber;
    }

    public void setFaxNumber(String faxNumber) {
        this.faxNumber = faxNumber;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
