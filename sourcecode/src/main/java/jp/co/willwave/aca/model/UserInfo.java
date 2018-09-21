package jp.co.willwave.aca.model;

import jp.co.willwave.aca.model.enums.UserRole;
import lombok.Data;

@Data
public class UserInfo {
    private String loginId;
    private Long id;
    private Long callId;
    private String callName;
    private String callPassword;
    private Integer roleId;
    private Long divisionsId;
    private String divisionAddress;
    private String companyName;
    private String firstName;
    private String lastName;
    private Integer configNotification;
    public boolean isSystemAdmin() {
        return UserRole.getUserRole(this.roleId).isSystemAdmin();
    }
    public boolean isCompanyDirector() {
        return UserRole.getUserRole(this.roleId).isCompanyDirector();
    }
    public boolean isDivisionDirector() {
        return UserRole.getUserRole(this.roleId).isDivisionDirector();
    }
    public boolean isOperator() {
        return UserRole.getUserRole(this.roleId).isOperator();
    }
    public boolean isViewer() {
        return UserRole.getUserRole(this.roleId).isViewer();
    }
}
