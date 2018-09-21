package jp.co.willwave.aca.model.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.validation.Valid;

/**
 * Usersテーブルエンティティ
 *
 * @author p-khanhnv.
 */
@Entity
@Table(name = "users")
@Where(clause = "delete_flg = 0")
public class UsersEntity extends BaseEntity {
    @Column(name = "login_id")
    @Valid
    private String loginId;

    @Column(name = "passwd", nullable = false)
    private String passwd;

    @Column(name = "division_id")
    private Long divisionsId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "address")
    private String userAddress;

    @Column(name = "mail")
    private String userEmail;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "fixed_phone")
    private String fixedPhone;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "fax_number")
    private String faxNumber;

    @Column(name = "status")
    private Boolean status = Boolean.TRUE;

    @Column(name = "call_username")
    private String callName;

    @Column(name = "call_password")
    private String callPassword;

    @Column(name = "user_tags")
    private String userTags;

    @Column(name = "call_id")
    private Long callId;

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

    public Long getDivisionsId() {
        return divisionsId;
    }

    public void setDivisionsId(Long divisionsId) {
        this.divisionsId = divisionsId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getCallName() {
        return callName;
    }

    public void setCallName(String callName) {
        this.callName = callName;
    }

    public String getCallPassword() {
        return callPassword;
    }

    public void setCallPassword(String callPassword) {
        this.callPassword = callPassword;
    }

    public String getUserTags() {
        return userTags;
    }

    public void setUserTags(String userTags) {
        this.userTags = userTags;
    }

    public Long getCallId() {
        return callId;
    }

    public void setCallId(Long callId) {
        this.callId = callId;
    }
}
